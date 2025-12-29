package com.mpx.minipx.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "tb_log_login")
@Data
public class TbLogLogin {

    /* =========================
     * PK
     * ========================= */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LOGIN_SEQ", nullable = false)
    @JsonProperty("loginSeq")
    private Long loginSeq;   // 로그인일련번호 (AUTO_INCREMENT)

    /* =========================
     * 로그인 정보
     * ========================= */
    @Column(name = "LOGIN_DTTI")
    @JsonProperty("loginDtti")
    private LocalDateTime loginDtti; // 로그인일시 (TIMESTAMP, 기본값 NOW())

    @Column(name = "USER_SEQ")
    @JsonProperty("userSeq")
    private Long userSeq; // 사용자일련번호

    @Column(name = "USER_ID", length = 20)
    @JsonProperty("userId")
    private String userId; // 사용자아이디

    @Column(name = "IP", length = 39)
    @JsonProperty("ip")
    private String ip; // 사용자아이피 (IPv4/IPv6)

    @Column(name = "LOGIN_CODE", length = 10)
    @JsonProperty("loginCode")
    private String loginCode; // 로그인코드
}
