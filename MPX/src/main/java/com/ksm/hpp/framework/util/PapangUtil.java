package com.ksm.hpp.framework.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @작성자: 김상민
 * @생성일: 2023. 01. 06. 오전 11:18:10
 * @설명: Papang에서 만든 유틸 모음
 */
public class PapangUtil {
	private static transient Logger logger = LogManager.getLogger("Application");
	
	/**
	 * @메소드명: getMapFromList
	 * @작성자: 김상민
	 * @생성일: 2023. 1. 6. 오전 11:20:14
	 * @설명: List에서 원하는 Map 객체를 가져온다.
	 */
	public static Map<String, Object> getMapFromList(List<Map<String, Object>> list, String key, String value) {
		Map<String, Object> result = new HashMap<String, Object>();
		
		for(Map<String, Object> map : list) {
			if(value.equals(map.get(key))){
				result = map;
			}
		}
		
		return result;
	}
}

	