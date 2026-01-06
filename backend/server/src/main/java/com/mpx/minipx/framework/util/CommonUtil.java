package com.mpx.minipx.framework.util;

import java.net.InetAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @파일명: CommonUtil.java
 * @패키지: com.mpx.minipx.framework.util
 * @작성자: KimSangMin
 * @생성일: 2026. 1. 6.
 * @설명: 공용 유틸 모음
 */
public class CommonUtil {
	private static transient Logger logger = LogManager.getLogger("Application");
	
	/**
	 * @메소드명: getClientIp
	 * @작성자: KimSangMin
	 * @생성일: 2026. 1. 6.
	 * @설명: 클라이언트 ip 조회
	 */
	public static String getClientIp(HttpServletRequest req) {
        // 프록시/로드밸런서 환경 고려
        String xff = req.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            // 첫 번째가 실제 클라이언트 IP인 경우가 많음
            return xff.split(",")[0].trim();
        }
        String xri = req.getHeader("X-Real-IP");
        if (xri != null && !xri.isBlank()) return xri.trim();
        return req.getRemoteAddr();
    }	
	
	/**
	 * @메소드명: getLocalIp
	 * @작성자: KimSangMin
	 * @생성일: 2026. 1. 6.
	 * @설명: 로컬 ip 구하기
	 */
	public static String getLocalIp() {
	    try {
	        return InetAddress.getLocalHost().getHostAddress();
	    } catch (Exception e) {
	        return null;
	    }
	}		
}
