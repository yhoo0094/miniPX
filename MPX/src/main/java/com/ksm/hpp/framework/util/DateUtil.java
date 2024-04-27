package com.ksm.hpp.framework.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @작성자: 김상민
 * @생성일: 2023. 1. 9. 오후 3:14:18
 * @설명: Date 유틸 모음
 */
public class DateUtil {
	private static transient Logger log = LogManager.getLogger("Application");
	
	/**
	 * @메소드명: addDate
	 * @작성자: 김상민
	 * @생성일: 2023. 1. 9. 오후 3:14:18
	 * @설명: 날짜 계산하여 String 타입으로 반환
	 * @param strDate - yyyyMMdd
	 */
	public static String addDate(String strDate, int year, int month, int day) throws Exception {
		
        SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMdd");
		Calendar cal = Calendar.getInstance();
		Date dt = dtFormat.parse(strDate);
        
		cal.setTime(dt);
		cal.add(Calendar.YEAR,  year);
		cal.add(Calendar.MONTH, month);
		cal.add(Calendar.DATE,  day);
        
		return dtFormat.format(cal.getTime());
	}	
	
	/**
	 * @메소드명: isBefore
	 * @작성자: 김상민
	 * @생성일: 2023. 1. 9. 오후 3:24:56
	 * @설명: 첫 번째 날짜가 두 번째 날짜 보다 이른 날짜인지 비교
	 * @param firstDate - yyyyMMdd
	 * @param secondDate - yyyyMMdd
	 */
	public static Boolean isBefore(String firstDate, String secondDate) throws Exception {
		Boolean result;
		SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMdd");
		Date date1 = dtFormat.parse(firstDate);
		Date date2 = dtFormat.parse(secondDate);
		
		if(date1.compareTo(date2) < 0) {
			log.info(firstDate + "는 " + secondDate + "보다 이른 날짜입니다.(true)");
			result = true;
		} else {
			result = false;
			log.info(firstDate + "는 " + secondDate + "보다 늦거나 같은 날짜입니다.(false)");
		}
		return result;
	}
}

	