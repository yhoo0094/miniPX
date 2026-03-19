"""
ETL migration template: MySQL -> MySQL

Usage:
  python etl_template.py --config config.yaml

"""

from __future__ import annotations

import argparse
import logging
import os
import sys
from dataclasses import dataclass
from typing import Any, Dict, Iterable, List, Optional, Tuple

import pymysql
import yaml
from sshtunnel import SSHTunnelForwarder

# ---------------------------------------------------------------------------
# 설정 (Configuration)
# ---------------------------------------------------------------------------

@dataclass
class MysqlConfig:
    host: str
    port: int
    user: str
    password: str
    database: str
    charset: str = "utf8mb4"


@dataclass
class SshTunnelConfig:
    # SSH 바스티온/점프 호스트
    ssh_host: str
    ssh_user: str
    remote_host: str
    ssh_port: int = 22
    # 인증에 사용할 개인키 또는 패스워드 (둘 중 하나 사용)
    ssh_pkey_path: Optional[str] = None
    ssh_password: Optional[str] = None
    remote_port: int = 3306
    local_port: int = 3307


# 설정 레이어: 커넥션 및 마이그레이션 설정을 정리합니다.
@dataclass
class MigrationJob:
    # 선택적 이름 (로그용)
    name: Optional[str] = None
    extract_sql: str = ""
    destination_table: str = ""
    # 목적지 INSERT 시 사용할 컬럼 순서 (생략 시 쿼리 결과 컬럼 순서에 따름)
    destination_columns: Optional[List[str]] = None
    # 소스 테이블 이름 (테이블 생성 시 사용, 생략 시 extract_sql에서 추출 시도)
    source_table: Optional[str] = None


@dataclass
class MigrationConfig:
    source: MysqlConfig
    destination: MysqlConfig
    # 출발지 DB가 private 네트워크에 있을 경우 사용할 SSH 터널 설정 (없어도 됨)
    source_ssh_tunnel: Optional[SshTunnelConfig] = None
    # 목적지 DB가 private 네트워크에 있을 경우 사용할 SSH 터널 설정 (없어도 됨)
    destination_ssh_tunnel: Optional[SshTunnelConfig] = None
    # 여러 테이블을 순차적으로 마이그레이션할 수 있도록 목록으로 관리
    migrations: List[MigrationJob] = None
    # 페치/적재 시 한 번에 처리할 행 수
    batch_size: int = 1000


# config.yaml 로드
def load_config(path: str) -> MigrationConfig:
    with open(path, "r", encoding="utf-8") as f:
        raw = yaml.safe_load(f)

    def cfg(obj: Dict[str, Any]) -> MysqlConfig:
        return MysqlConfig(
            host=obj.get("host", "localhost"),
            port=int(obj.get("port", 3306)),
            user=obj["user"],
            password=obj.get("password", ""),
            database=obj["database"],
            charset=obj.get("charset", "utf8mb4"),
        )

    def ssh_cfg(obj: Dict[str, Any]) -> SshTunnelConfig:
        return SshTunnelConfig(
            ssh_host=obj["ssh_host"],
            ssh_port=int(obj.get("ssh_port", 22)),
            ssh_user=obj["ssh_user"],
            ssh_pkey_path=obj.get("ssh_pkey_path"),
            ssh_password=obj.get("ssh_password"),
            remote_host=obj["remote_host"],
            remote_port=int(obj.get("remote_port", 3306)),
            local_port=int(obj.get("local_port", 3307)),
        )

    source_ssh_tunnel = None
    if raw.get("source_ssh_tunnel"):
        source_ssh_tunnel = ssh_cfg(raw["source_ssh_tunnel"])

    destination_ssh_tunnel = None
    if raw.get("destination_ssh_tunnel"):
        destination_ssh_tunnel = ssh_cfg(raw["destination_ssh_tunnel"])

    migrations: List[MigrationJob] = []

    for job in raw.get("migrations", []):
        migrations.append(
            MigrationJob(
                name=job.get("name"),
                extract_sql=job["extract_sql"],
                destination_table=job["destination_table"],
                destination_columns=job.get("destination_columns"),
                source_table=job.get("source_table"),
            )
        )

    # 이전 방식 (extract_sql + destination_table) 호환성 유지
    if not migrations and raw.get("extract_sql") and raw.get("destination_table"):
        migrations.append(
            MigrationJob(
                extract_sql=raw["extract_sql"],
                destination_table=raw["destination_table"],
                destination_columns=raw.get("destination_columns"),
            )
        )

    return MigrationConfig(
        source=cfg(raw["source"]),
        destination=cfg(raw["destination"]),
        source_ssh_tunnel=source_ssh_tunnel,
        destination_ssh_tunnel=destination_ssh_tunnel,
        migrations=migrations,
        batch_size=int(raw.get("batch_size", 1000)),
    )


# ---------------------------------------------------------------------------
# ETL helpers
# ---------------------------------------------------------------------------
# MySQL 커넥션 생성 (autocommit=False로 트랜잭션을 수동 제어)
def connect_mysql(cfg: MysqlConfig) -> pymysql.connections.Connection:
    return pymysql.connect(
        host=cfg.host,
        port=cfg.port,
        user=cfg.user,
        password=cfg.password,
        database=cfg.database,
        charset=cfg.charset,
        cursorclass=pymysql.cursors.Cursor,
        autocommit=False,
    )

# SSH 터널(바스티온/점프 호스트)을 생성합니다.
# 호출 측에서 tunnel.start()/tunnel.stop()으로 수명 관리를 해야 합니다.
# 로컬 포트(local_port)를 통해 원격 DB에 접속할 수 있게 됩니다.
def open_ssh_tunnel(cfg: SshTunnelConfig) -> SSHTunnelForwarder:
    """SSH 터널을 열어 로컬 포트에서 원격 DB에 접속할 수 있도록 합니다."""
    return SSHTunnelForwarder(
        (cfg.ssh_host, cfg.ssh_port),
        ssh_username=cfg.ssh_user,
        ssh_pkey=cfg.ssh_pkey_path,
        ssh_password=cfg.ssh_password,
        remote_bind_address=(cfg.remote_host, cfg.remote_port),
        local_bind_address=("localhost", cfg.local_port),
    )

# 소스 DB에서 배치 단위로 데이터를 읽어 메모리 과부하를 방지합니다.
def extract_rows(
    conn: pymysql.connections.Connection,
    sql: str,
    batch_size: int,
) -> Iterable[List[Tuple[Any, ...]]]:
    """Yield batches of rows from the source database."""
    with conn.cursor() as cur:
        cur.execute(sql)
        while True:
            batch = cur.fetchmany(batch_size)
            if not batch:
                break
            yield batch

# 로드 전에 필요한 변환(클렌징, 매핑, 타입 변환 등)을 수행합니다.
# 마이그레이션 요구에 맞게 이 함수를 수정하세요.
def transform_batch(rows: List[Tuple[Any, ...]]) -> List[Tuple[Any, ...]]:
    """Transform rows before loading.

    Modify this function to implement any required data cleansing / type conversion.
    """
    # Example: no-op
    return rows

# 목적지 테이블에 사용할 INSERT SQL을 구성합니다. (sql, placeholder_count) 반환.
def make_insert_sql(table: str, columns: Optional[List[str]], row_len: int) -> Tuple[str, int]:
    """Build an INSERT statement for a given destination table.

    Returns (sql, placeholder_count).
    """
    if columns:
        col_list = ", ".join(f"`{c}`" for c in columns)
        placeholder_count = len(columns)
    else:
        # If columns are not provided, assume caller delivers row tuples with correct length
        col_list = None
        placeholder_count = row_len

    values_clause = ", ".join(["%s"] * placeholder_count)

    if col_list:
        return f"INSERT INTO `{table}` ({col_list}) VALUES ({values_clause})", placeholder_count

    return f"INSERT INTO `{table}` VALUES ({values_clause})", placeholder_count

# 배치 단위로 목적지 테이블에 데이터를 적재합니다. executemany를 사용해 효율성을 높입니다.
def load_batch(
    conn: pymysql.connections.Connection,
    table: str,
    columns: Optional[List[str]],
    rows: List[Tuple[Any, ...]],
) -> int:
    """Insert a batch of rows into the destination table."""
    if not rows:
        return 0

    sql, placeholder_count = make_insert_sql(table, columns, len(rows[0]))

    with conn.cursor() as cur:
        # Use executemany for batch insert
        cur.executemany(sql, rows)
    conn.commit()
    return len(rows)

# 간단한 검증을 위해 소스 쿼리 결과와 목적지 테이블의 행 수를 비교합니다.
def validate_counts(
    source_conn: pymysql.connections.Connection,
    dest_conn: pymysql.connections.Connection,
    extract_sql: str,
    dest_table: str,
) -> Tuple[int, int]:
    """Optionally validate row counts between source and destination."""
    # NOTE: This is a simplistic count validation. Adjust as needed.
    src_count_sql = f"SELECT COUNT(*) FROM ({extract_sql}) AS _src"
    with source_conn.cursor() as cur:
        cur.execute(src_count_sql)
        src_count = cur.fetchone()[0]

    dest_count_sql = f"SELECT COUNT(*) FROM `{dest_table}`"
    with dest_conn.cursor() as cur:
        cur.execute(dest_count_sql)
        dest_count = cur.fetchone()[0]

    return src_count, dest_count


# 소스 테이블의 스키마를 가져와 목적지 테이블을 생성합니다.
def create_destination_table(
    source_conn: pymysql.connections.Connection,
    dest_conn: pymysql.connections.Connection,
    source_table: str,
    dest_table: str,
) -> None:
    """Create destination table based on source table schema."""
    # 소스 테이블의 CREATE TABLE DDL 가져오기
    with source_conn.cursor() as cur:
        cur.execute(f"SHOW CREATE TABLE `{source_table}`")
        create_table_sql = cur.fetchone()[1]

    # DDL에서 테이블 이름 변경 (소스 → 목적지)
    create_table_sql = create_table_sql.replace(f"`{source_table}`", f"`{dest_table}`", 1)

    # 목적지에서 테이블 생성 (기존 테이블이 있으면 삭제 후 생성)
    with dest_conn.cursor() as cur:
        cur.execute(f"DROP TABLE IF EXISTS `{dest_table}`")
        cur.execute(create_table_sql)
    dest_conn.commit()
    logging.info("Created destination table: %s", dest_table)


# ---------------------------------------------------------------------------
# CLI / entrypoint
# ---------------------------------------------------------------------------

# 커맨드라인 인자를 파싱합니다. (--config 로 YAML 경로 지정)
def parse_args(argv: List[str]) -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="ETL: MySQL to MySQL migration template")
    parser.add_argument(
        "--config",
        required=True,
        help="Path to YAML config file describing source/destination and query",
    )
    return parser.parse_args(argv)

# 프로그램 시작점: 설정을 로드하고, 필요 시 SSH 터널을 열어 ETL 작업을 실행합니다.
def main(argv: Optional[List[str]] = None) -> int:
    args = parse_args(argv or sys.argv[1:])
    cfg = load_config(args.config)

    logging.basicConfig(
        level=logging.INFO,
        format="%(asctime)s [%(levelname)s] %(message)s",
    )

    logging.info("Starting migration")

    source_tunnel: Optional[SSHTunnelForwarder] = None
    dest_tunnel: Optional[SSHTunnelForwarder] = None

    # 출발지 DB SSH 터널 (optional)
    if cfg.source_ssh_tunnel:
        source_tunnel = open_ssh_tunnel(cfg.source_ssh_tunnel)
        source_tunnel.start()
        logging.info(
            "Opened SSH tunnel (source): localhost:%d -> %s:%d via %s",
            cfg.source_ssh_tunnel.local_port,
            cfg.source_ssh_tunnel.remote_host,
            cfg.source_ssh_tunnel.remote_port,
            cfg.source_ssh_tunnel.ssh_host,
        )
        cfg.source.host = "localhost"
        cfg.source.port = cfg.source_ssh_tunnel.local_port

    # 목적지 DB SSH 터널 (optional)
    if cfg.destination_ssh_tunnel:
        dest_tunnel = open_ssh_tunnel(cfg.destination_ssh_tunnel)
        dest_tunnel.start()
        logging.info(
            "Opened SSH tunnel (destination): localhost:%d -> %s:%d via %s",
            cfg.destination_ssh_tunnel.local_port,
            cfg.destination_ssh_tunnel.remote_host,
            cfg.destination_ssh_tunnel.remote_port,
            cfg.destination_ssh_tunnel.ssh_host,
        )
        cfg.destination.host = "localhost"
        cfg.destination.port = cfg.destination_ssh_tunnel.local_port

    src_conn = connect_mysql(cfg.source)
    dest_conn = connect_mysql(cfg.destination)

    try:
        for job in cfg.migrations:
            logging.info("Starting job: %s (dest=%s)", job.name or "<unnamed>", job.destination_table)

            # 목적지 테이블 생성 (source_table이 제공된 경우)
            if job.source_table:
                create_destination_table(src_conn, dest_conn, job.source_table, job.destination_table)
            else:
                logging.warning("source_table not provided for job %s, skipping table creation", job.name or "<unnamed>")

            total_inserted = 0

            for batch in extract_rows(src_conn, job.extract_sql, cfg.batch_size):
                transformed = transform_batch(batch)
                inserted = load_batch(dest_conn, job.destination_table, job.destination_columns, transformed)
                total_inserted += inserted
                logging.info("Inserted %d rows (total %d)", inserted, total_inserted)

            src_count, dest_count = validate_counts(src_conn, dest_conn, job.extract_sql, job.destination_table)
            logging.info("Validation (job=%s): source count=%d, destination count=%d", job.name or "<unnamed>", src_count, dest_count)

            if src_count != dest_count:
                logging.warning("Row count mismatch detected for job %s; investigate further.", job.name or "<unnamed>")

        logging.info("Migration completed")
        return 0

    except Exception:
        logging.exception("Migration failed")
        return 1

    finally:
        src_conn.close()
        dest_conn.close()
        if source_tunnel:
            source_tunnel.stop()
        if dest_tunnel:
            dest_tunnel.stop()


if __name__ == "__main__":
    raise SystemExit(main())
