package com.mpx.minipx.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "tb_log_req")
@Data
public class TbLogReq {

    /* =========================
     * PK
     * ========================= */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REQ_SEQ", nullable = false)
    @JsonProperty("reqSeq")
    private Long reqSeq;   // 요청일련번호 (AUTO_INCREMENT)

    /* =========================
     * 요청 정보
     * ========================= */
    @Column(name = "REQ_DTTI")
    @JsonProperty("reqDtti")
    private LocalDateTime reqDtti; // 요청일시 (DEFAULT NOW())

    @Column(name = "USER_SEQ")
    @JsonProperty("userSeq")
    private Long userSeq;  // 사용자일련번호

    @Column(name = "IP", length = 39)
    @JsonProperty("ip")
    private String ip;     // 사용자아이피 (IPv6 고려)

    @Column(name = "URI", length = 100)
    @JsonProperty("uri")
    private String uri;    // 요청경로

    @Lob
    @Column(name = "PARAM", columnDefinition = "LONGTEXT")
    @JsonProperty("param")
    private String param;  // 요청파라미터 (query/body 등)

    @Column(name = "REQ_TYPE_CODE", length = 100)
    @JsonProperty("reqTypeCode")
    private String reqTypeCode; // 요청타입코드 (예: GET/POST 등)

    /* =========================
     * 기본값 처리
     * ========================= */
    @PrePersist
    public void prePersist() {
        // DB 기본값(NOW())을 기대하더라도 JPA가 null을 넣는 케이스를 피하려고
        // 안전하게 애플리케이션에서도 채워줌
        if (reqDtti == null) {
            reqDtti = LocalDateTime.now();
        }
    }
}
