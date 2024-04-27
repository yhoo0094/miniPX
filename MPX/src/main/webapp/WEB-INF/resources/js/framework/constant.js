/*
 * @작성자: 김상민
 * @생성일: 2022. 11. 10. 오전 8:56:10
 * @설명: JS 공통 변수 모음
 */
 
 var Constant = {
	//request, response 관련
	  OUT_DATA			: "OUT_DATA"			//리턴할 데이터
	, RESULT			: "RESULT"				//결과(RESULT_SUCCESS 혹은 RESULT_FAILURE)
	, RESULT_SUCCESS 	: "RESULT_SUCCESS"		//성공
	, RESULT_FAILURE 	: "RESULT_FAILURE"		//실패
	, OUT_RESULT_MSG 	: "OUT_RESULT_MSG"		//결과에 대한 메세지
	, RESULT_DETAIL		: "RESULT_DETAIL"		//결과 상세(결과에 대한 구체적인 분류 - DB반영 실패, 입력 형식 불일치 등)   
	, IN_DATA_JOSN		: "IN_DATA_JOSN"		//전송할 데이터(json 형식)
	, IN_LOG_STR		: "IN_LOG_STR"			//request 내의 log 생성을 위한 logbuilder가 저장된 key
   
	//RESULT_DETAIL 관련
	, PSWD_LIM_ISSUE	: "PSWD_LIM_ISSUE"		//비밀번호 변경 주기 문제  
   
	//로그인 관련
	, LOGIN_INFO		: "LOGIN_INFO"			//로그인 정보
	, SESSION_TIME		: "SESSION_TIME"		//세션 유지 시간
	, AUTH_LIST			: "AUTH_LIST"			//권한 목록
	, FREE_AUTH_LIST	: "FREE_AUTH_LIST"		//권한이 필요없는 목록
	
	//엑셀 관련
	, EXCEL_FILENM		: "EXCEL_FILENM"		//파일명
	, EXCEL_SHEETNM		: "EXCEL_SHEETNM"		//시트명	
	, EXCEL_COLUMN		: "EXCEL_COLUMN"		//컬럼 정보
	, EXCEL_DATA		: "EXCEL_DATA"			//입력 데이터   
	, EXCEL_UPLOAD_OPTION : "EXCEL_UPLOAD_OPTION"	//엑셀 업로드 옵션
	
 }
