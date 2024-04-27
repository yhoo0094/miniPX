package com.ksm.hpp.framework.util;

import java.util.HashMap;

import org.springframework.jdbc.support.JdbcUtils;

/**
 * @작성자: 김상민
 * @생성일: 2023. 1. 12. 오후 1:35:47
 * @설명: 마이바티스에서 map으로 반환할 때 key 값을 CamelCase로 바꿔준다
 */
public class CamelCaseMap extends HashMap<String, Object> {

	private static final long serialVersionUID = 1L;

	@Override
	public Object put(String key, Object value) {
		
		//ibatis에서 호출한 경우에만 CamelCase로 변환
		StackTraceElement[] ste = new Throwable().getStackTrace();
		if(ste[2].toString().indexOf("ibatis") != -1) {
			key = JdbcUtils.convertUnderscoreNameToPropertyName(key);
		}
		return super.put(key, value);
	}

}

	