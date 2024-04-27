/**
 * @화면명: 사용자 접속 기록 조회
 * @작성자: 김상민
 * @생성일: 2023. 1. 12. 오후 2:46:04
 * @설명: 관리자가 사용자 접속 기록을 조회할 수 있는 페이지
**/

//페이지 전역 변수
var mainTable

$(document).ready(function () {
	setDatetimepicker();		//datetimepicker 설정
	guestChk(); 				//게스트 계정에 대한 제약사항 적용
	makeDataTableServerSide();	//DataTable 만들기(페이지네이션 서버 처리)
});

//게스트 계정에 대한 제약사항 적용
function guestChk(){
	if(loginInfo.roleNm == '게스트'){
		$('#userId').val(loginInfo.userId);
		$('#userId').attr('readonly',true);
		$('#ip').val(loginInfo.ip);
		$('#ip').attr('readonly',true);
	}
}

//datetimepicker 설정
function setDatetimepicker() {
	var dttiStr = $dateUtil.addDate($dateUtil.todayYYYYMMDDHHMM(),-1,0,0,0,0);	//1년 전
	dttiStr = $dateUtil.dateHyphenTime(dttiStr);
	var dttiEnd = $dateUtil.addDate($dateUtil.todayYYYYMMDDHHMM(),0,0,0,1,0);	//1시간 후(로컬과 서버 시간 안 맞는 경우 때문에)
	dttiEnd = $dateUtil.dateHyphenTime(dttiEnd);
	
	$com.datetimepicker('dttiStr',$dateUtil.todayYYYY_MM_DD_HHMM(dttiStr));
	$com.datetimepicker('dttiEnd',$dateUtil.todayYYYY_MM_DD_HHMM(dttiEnd));	
}

//검색
function doSearch(){
	mainTable.ajax.reload();
}

var columInfo = [
        { title: "발생일시"	, data: "loginDtti"		, width: "25%"		, className: "text_align_center"}
      , { title: "아이디"		, data: "userId"		, width: "25%"		, className: "text_align_center"}
      , { title: "아이피"		, data: "ip"			, width: "25%"		, className: "text_align_center"	, defaultContent: ""}
      , { title: "유형"		, data: "loginCodeNm"	, width: "25%"		, className: "text_align_center"	, defaultContent: ""}
]

//엑셀 다운로드 버튼
var excelDownBtn = $('<div class="table_btn_wrapper"><button type="button" class="papang-excel-btn papang_btn paginate_button">Excel</button></div>');

//엑셀 업로드 버튼
var excelUploadBtn = $('<div class="table_btn_wrapper"><button type="button" class="papang-excel-btn papang_btn paginate_button">Excel Upload</button></div>');

//DataTable 만들기
function makeDataTable(data) {
    mainTable = $('#mainTable').DataTable({
        data: data,
        columns: columInfo,		
        pagingType: "numbers",					//v페이지 표시 옵션
        ordering: false,
        info: false,
        searching: false,
        lengthChange: false,
        autoWidth: false,						//자동 열 너비 조정
        
  		scrollY: 600,							//테이블 높이
  		scrollCollapse: true,   				//테이블 최대 높이 고정 여부     
        preDrawCallback : function(settings){	//테이블 그리기 전에 동작
			$com.loadingStart();				//로딩패널 보이기
		},
        drawCallback : function(settings){		//테이블 그리기 후에 동작
			$com.loadingEnd();					//로딩패널 숨기기
		},
		language : {
			paginate : {
				first : '처음',
				last : '마지막',
				next : '다음',
				previous : '이전'
			},
			zeroRecords	: '조회된 결과가 없습니다.',
		},
    });	
    
    //엑셀 작업을 위해 컬럼 정보 추가
    mainTable.columInfo = columInfo;
    
    //엑셀 다운로드 버튼
    excelDownBtn.on('click', function(){
		$excelUtil.downloadData(infoMnuNm, infoMnuNm, mainTable.columInfo, data);
	})
    $('#mainTable_paginate').after(excelDownBtn);
    
    //엑셀 업로드 버튼
    excelUploadBtn.on('click', function(){
		$excelUtil.upload(excelUploadOptions, excelUploadCallBack);
	})
    $('#mainTable_paginate').after(excelUploadBtn);    
    
	var excelUploadOptions = [];
	
	//excel 업로드 옵션 입력(사용할 시트 수 만큼 반복)
	var excelUploadOption={};
	excelUploadOption["rowOffset"] = 4;		//빈 행 개수(테이블 헤드도 빈 행으로 취급)
	excelUploadOption["colOffset"] = 1;		//빈 열 개수
	excelUploadOption["colOptions"] = mainTable.columInfo;
	
	excelUploadOptions.push(excelUploadOption);
}

//DataTable 만들기(페이지네이션 서버 처리)
function makeDataTableServerSide() {
	var url = '/admin/selectLoginLog.do';
	var param = {};    
	param.pageLength = 10;						//페이지당 레코드 수
	
    mainTable = $('#mainTable').DataTable({
		serverSide: true,						//페이징 처리 서버에서 수행
		ajax: {
			url: url,
        	type: 'POST',
			data: function(){
				//검색 조건 object에 담기
			    $.each($('#searchForm').serializeArray(), function() {
			        param[this.name] = this.value;
			    });
				
				if($util.isEmpty(mainTable)){
					param.strIdx = 0;
				} else {
					param.strIdx = 0 + (param.pageLength * parseInt(mainTable.page()));		//시작 레코드 인덱스 
				};
				return param;
			},
			dataSrc: function (json) {
		        if (json.RESULT == Constant.RESULT_FAILURE) {
		             alert(json[Constant.OUT_RESULT_MSG]);
		             location.reload();
		        }
		        return json.data;
		    },
		},
        columns: columInfo,
        pagingType: "numbers",					//v페이지 표시 옵션
        ordering: false,
        info: false,
        searching: false,
        lengthChange: false,
        autoWidth: false,						//자동 열 너비 조정
  		scrollY: 600,							//테이블 높이
  		scrollCollapse: true,   				//테이블 최대 높이 고정 여부     
        preDrawCallback : function(settings){	//테이블 그리기 전에 동작
			$com.loadingStart();				//로딩패널 보이기
		},
        drawCallback : function(settings){		//테이블 그리기 후에 동작
			$com.loadingEnd();					//로딩패널 숨기기
		},
		language : {
			paginate : {
				first : '처음',
				last : '마지막',
				next : '다음',
				previous : '이전'
			},
			zeroRecords	: '조회된 결과가 없습니다.',
		},
    });	
    
    //엑셀 작업을 위해 컬럼 정보 추가
    mainTable.columInfo = columInfo;
    
    //엑셀 다운로드 버튼
    excelDownBtn.on('click', function(){
		$excelUtil.downloadURL(infoMnuNm, infoMnuNm, mainTable.columInfo, url, param);
	})
    $('#mainTable_paginate').after(excelDownBtn); 
    
    //엑셀 업로드 버튼
    excelUploadBtn.on('click', function(){
		$excelUtil.upload(excelUploadOptions, excelUploadCallBack);
	})
    $('#mainTable_paginate').after(excelUploadBtn);    
    
	var excelUploadOptions = [];
	
	//excel 업로드 옵션 입력(사용할 시트 수 만큼 반복)
	var excelUploadOption={};
	excelUploadOption["rowOffset"] = 4;		//빈 행 개수(테이블 헤드도 빈 행으로 취급)
	excelUploadOption["colOffset"] = 1;		//빈 열 개수
	excelUploadOption["colOptions"] = mainTable.columInfo;
	
	excelUploadOptions.push(excelUploadOption);
}

//엑셀 업로드 후 콜백
var excelUploadCallBack = function(result){
	mainTable.destroy();
	makeDataTable(result[Constant.OUT_DATA][0]);
};