package com.mpx.minipx.dto.common;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

@JsonClassDescription("상품 DB에서 읽기 전용 SELECT 쿼리를 실행합니다. TB_ITEM 테이블만 사용하세요.")
public class RunProductsQueryArgs {

    @JsonPropertyDescription("실행할 SELECT SQL (SELECT 전용)")
    public String sql;

    // 기본 생성자 필수 (Jackson용)
    public RunProductsQueryArgs() {}

    public RunProductsQueryArgs(String sql) {
        this.sql = sql;
    }
}
