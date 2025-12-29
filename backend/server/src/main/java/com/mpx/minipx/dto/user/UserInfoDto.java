package com.mpx.minipx.dto.user;

import lombok.Data;

@Data
public class UserInfoDto {
    private Long userSeq;
    private String userId;
    private String userNm;
    private String aiOpenYn;
    private Long credit;      
    private String useYn;
    private String pwInitYn;
    private Integer roleSeq;
}
