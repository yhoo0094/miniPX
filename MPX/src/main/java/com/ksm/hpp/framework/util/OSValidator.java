package com.ksm.hpp.framework.util;

/**
 * @작성자: 김상민 
 * @생성일: 2022. 12. 13 오후 05:42:30
 * @설명: 현재 서버를 실행 중인 OS 판단
 */
public class OSValidator {
	private static String OS = System.getProperty("os.name").toLowerCase();
	
	public static boolean isWindows() {
		return (OS.indexOf("win") >= 0);
	}
	
	public static boolean isUnix() {
		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") >= 0);
	}
	
	public static boolean isMac() {
		return (OS.indexOf("mac") >= 0);
	}	
	
	public static boolean isSolaris() {
		return (OS.indexOf("sunos") >= 0);
	}
	
	public static OS_Type getOS() {
		if(OS.indexOf("win") >= 0) {
			return OS_Type.WINDOWS;
		} else if(OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") >= 0) {
			return OS_Type.LINUX;
		} else if(OS.indexOf("mac") >= 0) {
			return OS_Type.MAC;	
		} else if(OS.indexOf("sunos") >= 0) {
			return OS_Type.SOLARIS;
		} else {
			return OS_Type.UNKNOWN;
		}
	}
}

	