package com.ksm.hpp.framework.util;

/**
 * @작성자: 김상민 
 * @생성일: 2022. 12. 14 오후 01:23:30
 * @설명: OS타입 정의
 */
public enum OS_Type {
	UNKNOWN(0),
	WINDOWS(1),
	LINUX(2),
	MAC(3),
	SOLARIS(4);
	
	private int value = -1;

	private OS_Type(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public static OS_Type fromInteger(int value) {
		switch(value) {
			case -1 :
				return OS_Type.UNKNOWN;
			case 1 :
				return OS_Type.WINDOWS;
			case 2 :
				return OS_Type.LINUX;
			case 3 :
				return OS_Type.MAC;	
			case 4 :
				return OS_Type.SOLARIS;					
		}
		return null;
	}
}

	