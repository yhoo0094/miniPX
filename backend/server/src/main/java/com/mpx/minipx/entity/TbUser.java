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
@Table(name = "tb_user")
@Data
public class TbUser {

    /* =========================
     * PK
     * ========================= */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_SEQ", nullable = false)
    @JsonProperty("userSeq")
    private Long userSeq;   // 사용자일련번호 (AUTO_INCREMENT)

    /* =========================
     * 기본 정보
     * ========================= */
    @Column(name = "USER_ID", length = 20)
    @JsonProperty("userId")
    private String userId;  // 사용자아이디

    @Column(name = "USER_PW", length = 100)
    @JsonProperty("userPw")
    private String userPw;  // 사용자비밀번호 (BCrypt 해시)

    @Column(name = "USER_NM", length = 30)
    @JsonProperty("userNm")
    private String userNm;  // 사용자명

    /* =========================
     * 상태 / 권한
     * ========================= */
    @Column(name = "AI_OPEN_YN", length = 1)
    @JsonProperty("aiOpenYn")
    private String aiOpenYn; // AI사용여부
    
    @Column(name = "CREDIT")
    @JsonProperty("credit")
    private Long credit; // 크레딧  
    
    @Column(name = "USE_YN", length = 1)
    @JsonProperty("useYn")
    private String useYn;

    @Column(name = "ROLE_SEQ")
    @JsonProperty("roleSeq")
    private Integer roleSeq; // 권한그룹일련번호

    /* =========================
     * 비밀번호 관리
     * ========================= */
    @Column(name = "PW_CH_DTTI")
    @JsonProperty("pwChDtti")
    private LocalDateTime pwChDtti; // 비밀번호 최종수정일시

    @Column(name = "PW_BF", length = 50)
    @JsonProperty("pwBf")
    private String pwBf; // 이전 비밀번호

    @Column(name = "PW_ERR_CNT", length = 1)
    @JsonProperty("pwErrCnt")
    private Integer pwErrCnt; // 비밀번호 오류입력 횟수

    @Column(name = "PW_INIT_YN", length = 1)
    @JsonProperty("pwInitYn")
    private String pwInitYn; // 비밀번호 초기화 여부 (Y/N)

    /* =========================
     * 비고
     * ========================= */
    @Column(name = "RMRK", length = 3000)
    @JsonProperty("rmrk")
    private String rmrk; // 비고

    /* =========================
     * 등록 / 수정 이력
     * ========================= */
    @Column(name = "FST_REG_SEQ", nullable = false)
    @JsonProperty("fstRegSeq")
    private Integer fstRegSeq; // 최초등록자일련번호

    @Column(name = "FST_REG_DTTI", nullable = false)
    @JsonProperty("fstRegDtti")
    private LocalDateTime fstRegDtti; // 최초등록일시

    @Column(name = "LST_UPD_SEQ", nullable = false)
    @JsonProperty("lstUpdSeq")
    private Integer lstUpdSeq; // 최종수정자일련번호

    @Column(name = "LST_UPD_DTTI", nullable = false)
    @JsonProperty("lstUpdDtti")
    private LocalDateTime lstUpdDtti; // 최종수정일시
    
    //비밀번호 오입력 횟수 증가
    public void increasePwErrCnt() {
    	pwErrCnt++;
    }
}
