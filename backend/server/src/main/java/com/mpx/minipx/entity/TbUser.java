package com.mpx.minipx.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "tb_user")
@Data
public class TbUser {
    
	@Id
    @Column(name = "USER_SEQ") // 생략 가능, 컬럼명이 같다면 자동 매핑
	@JsonProperty("userSeq")
    private String userSeq;

    @Column(name = "USER_ID")
    @JsonProperty("userId")
    private String userId;
    
    @Column(name = "USER_PW")
    @JsonProperty("userPw")
    private String userPw;

    @Column(name = "USER_NM")
    @JsonProperty("userNm")
    private String userNm;
    
    @Column(name = "ROLE_SEQ")
    @JsonProperty("roleSeq")
    private String roleSeq;
    
}