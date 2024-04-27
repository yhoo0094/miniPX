package com.ksm.hpp.framework.exception;

/**
* @파일명: ConfigurationException.java
* @패키지: com.ksm.hpp.framework.exception
* @작성자: KimSangMin
* @생성일: 2023. 1. 25. 오후 2:55:38
* @설명: 사용자 정의 오류 호출을 위한 클래스
 */
public class ConfigurationException extends Exception {
		
	private static final long serialVersionUID = -7229562509236464873L;

	public ConfigurationException() {
		super();
	}

	public ConfigurationException(String s) {
		super(s);
	}
}
