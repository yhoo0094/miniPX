package com.mpx.minipx.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name = "tb_token")
public class TbToken {
    
	@Id
    @Column(name = "USER_ID")
    @JsonProperty("userId")
    private String userId;

    @Column(name = "TOKEN")
    @JsonProperty("token")
    private String token;

    @Column(name = "CREATE_DTTI")
    @JsonProperty("createDtti")
    private LocalDateTime createDtti;

    @Column(name = "EXPIRY_DTTI")
    @JsonProperty("expiryDtti")
    private LocalDateTime expiryDtti;

    @Column(name = "DEVICE_INFO")
    @JsonProperty("deviceInfo")
    private String deviceInfo;

    @Column(name = "FST_REG_SEQ")
    @JsonProperty("fstRegSeq")
    private String fstRegSeq;

    @Column(name = "FST_REG_DTTI", insertable = false, updatable = false)
    @JsonProperty("fstRegDtti")
    private LocalDateTime fstRegDtti;

    @Column(name = "LST_UPD_SEQ")
    @JsonProperty("lstUpdSeq")
    private String lstUpdSeq;

    @Column(name = "LST_UPD_DTTI")
    @JsonProperty("lstUpdDtti")
    private LocalDateTime lstUpdDtti;
        
}