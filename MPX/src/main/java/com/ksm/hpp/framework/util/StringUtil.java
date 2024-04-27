package com.ksm.hpp.framework.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @작성자: 김상민
 * @생성일: 2022. 11. 10. 오전 8:56:10
 * @설명: JAVA 유틸 모음
 */
public class StringUtil {
	private static transient Logger logger = LogManager.getLogger("Application");
	
	public static String getSHA256(String str) {
		String SHA = "";
		MessageDigest md;
		char[] ch = str.toCharArray();
		try {
			md = MessageDigest.getInstance("SHA-256");
			md.update(new String(ch).getBytes("UTF-8"));
			byte[] by = md.digest();
			by = new Base64().encodeBase64(by);			
			SHA = new String(by); //byte[]을 String으로 변환
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SHA;
	}
	
	/**
	* @메소드명: XssReplace
	* @작성자: KimSangMin
	* @생성일: 2023. 1. 31. 오후 4:35:35
	* @설명: XXS 방지를 위한 텍스트 변환
	*/
	public static String XssReplace(String param) {
		
		param = param.replaceAll("&", "&amp;");
		param = param.replaceAll("\"", "&quot;");
		param = param.replaceAll("'", "&apos;");
		param = param.replaceAll("<", "&lt;");
		param = param.replaceAll(">", "&gt;");
		param = param.replaceAll("\r", "<br>");
		param = param.replaceAll("\n", "<p>");

		return param;
	}

	/**
	* @메소드명: XssReplaceInData
	* @작성자: KimSangMin
	* @생성일: 2023. 1. 31. 오후 4:25:18
	* @설명: inData에 담긴 값들에 대해 XssReplace 실행
	*/
	public static Map<String, Object> XssReplaceInData(Map<String, Object> inData) {
		for (String key: inData.keySet()) {
			if(inData.get(key) instanceof String) {
				inData.put(key, XssReplace((String) inData.get(key))) ;
			}
		}
		return inData;
	}		
	
	/**
	* @메소드명: XssReverse
	* @작성자: KimSangMin
	* @생성일: 2023. 5. 29. 오후 4:43:04
	* @설명: XXS 위험이 있는 문자 대체한 것 되돌리기
	*/
	public static String XssReverse(String value) {
		value = value.replaceAll("&amp;", "&");
		value = value.replaceAll("&#x2F;", "/");
		value = value.replaceAll("&quot;", "\"");
		value = value.replaceAll("&#x27;", "'");
		value = value.replaceAll("&lt;", "<");
		value = value.replaceAll("&gt;", ">");
		value = value.replaceAll("&nbsp;", " ");
		value = value.replaceAll("<br>", "\r");
		value = value.replaceAll("<p>", "\n");
		return value;
	}		
}

	