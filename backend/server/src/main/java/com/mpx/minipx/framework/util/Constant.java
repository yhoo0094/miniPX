package com.mpx.minipx.framework.util;

//시스템 전반에 사용되는 상수 값 모음
public class Constant
{
	//토큰 관련
	public static final long   REFRESH_TOKEN_VALIDITY 		  = 1000L * 60 * 60 * 24 * 30; 	// 30일
//	public static final long   ACCESS_TOKEN_VALIDITY 		  = 1000L * 60 * 15; 			// 15분
	public static final long   ACCESS_TOKEN_VALIDITY 		  = 1000L * 30 * 1; 			// 30초(테스트용)
	
	//request, response 관련
	public static final String OUT_DATA 		= "OUT_DATA";			    //리턴할 데이터
	public static final String RESULT 			= "RESULT";				    //결과(RESULT_SUCCESS 혹은 RESULT_FAILURE)
	public static final String RESULT_SUCCESS 	= "RESULT_SUCCESS";		    //성공
	public static final String RESULT_FAILURE 	= "RESULT_FAILURE";		    //실패
	public static final String OUT_RESULT_MSG 	= "OUT_RESULT_MSG";		    //결과에 대한 메세지
	public static final String RESULT_DETAIL 	= "RESULT_DETAIL";		    //결과 상세(결과에 대한 구체적인 분류 - DB반영 실패, 입력 형식 불일치 등)
	public static final String IN_DATA_JOSN		= "IN_DATA_JOSN";		    //전송할 데이터(json 형식)
	public static final String IN_LOG_STR		= "IN_LOG_STR";			    //request 내의 log 생성을 위한 logbuilder가 저장된 key
	                                                                        
	//RESULT_DETAIL 관련                                                    
	public static final String PSWD_LIM_ISSUE 	= "PSWD_LIM_ISSUE";		    //비밀번호 변경 주기 문제
	                                                                        
	//로그인 관련                                                           
	public static final String LOGIN_INFO 		= "LOGIN_INFO";			    //로그인 정보
	public static final String SESSION_TIME		= "SESSION_TIME";		    //세션 유지 시간
	public static final String AUTH_LIST		= "AUTH_LIST";			    //권한 목록
	public static final String FREE_AUTH_LIST	= "FREE_AUTH_LIST";		    //권한이 필요없는 목록
	                                                                        
	//엑셀 관련                                                             
	public static final String EXCEL_FILENM 	= "EXCEL_FILENM";		    //파일명
	public static final String EXCEL_SHEETNM 	= "EXCEL_SHEETNM";		    //시트명	
	public static final String EXCEL_COLUMN 	= "EXCEL_COLUMN";		    //컬럼 정보
	public static final String EXCEL_DATA 		= "EXCEL_DATA";			    //입력 데이터
	public static final String EXCEL_UPLOAD_OPTION = "EXCEL_UPLOAD_OPTION";	//엑셀 업로드 옵션
	
}
