package com.mpx.minipx.dto.common;

//lombok 사용 예시
import lombok.Data;

@Data
public class ReqLog {
 private Long reqSeq;        // auto
 private String reqDtti;     // DB default 사용해도 됨
 private Long userSeq;
 private String ip;
 private String uri;
 private String param;
 private String reqTypeCode;
}

