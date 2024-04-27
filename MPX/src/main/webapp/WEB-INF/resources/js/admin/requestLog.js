/**
 * @화면명: 요청 로그 조회 
 * @작성자: KimSangMin
 * @생성일: 2023. 1. 30. 오후 2:25:23
 * @설명: 클라이언트에서 서버로 넘어오는 요청관련 로그 조회
**/

$(document).ready(function() {
	setDatetimepicker();
	guestChk();
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
	var reqDttiStr = $dateUtil.addDate($dateUtil.todayYYYYMMDDHHMM(),-1,0,0,0,0);	//1년 전
	reqDttiStr = $dateUtil.dateHyphenTime(reqDttiStr);
	var reqDttiEnd = $dateUtil.addDate($dateUtil.todayYYYYMMDDHHMM(),0,0,0,1,0);	//1시간 후(로컬과 서버 시간 안 맞는 경우 때문에)
	reqDttiEnd = $dateUtil.dateHyphenTime(reqDttiEnd);
	
	$com.datetimepicker('reqDttiStr',$dateUtil.todayYYYY_MM_DD_HHMM(reqDttiStr));
	$com.datetimepicker('reqDttiEnd',$dateUtil.todayYYYY_MM_DD_HHMM(reqDttiEnd));	
}

//검색
function doSearch(){
	mainTable.ajax.reload();
};

var columInfo = [
        { title: "발생일시"	, data: "reqDtti"		, width: "120px"	, className: "text_align_center"}
      , { title: "아이디"		, data: "userId"		, width: "90px"		, className: "text_align_center"	, defaultContent: ""}
      , { title: "아이피"		, data: "ip"			, width: "90px"		, className: "text_align_center"	, defaultContent: ""}
      , { title: "URI"		, data: "uri"			, width: "90px"		, className: "text_align_left"		, defaultContent: ""}      
      , { title: "파라미터"	, data: "param"			, width: "*"		, className: "text_align_left max-w300px"		, defaultContent: ""}      
      , { title: "메뉴"		, data: "reqTypeNm"		, width: "120px"	, className: "text_align_center"	, defaultContent: ""}
]

//엑셀 다운로드 버튼
var excelDownBtn = $('<div class="table_btn_wrapper"><button type="button" class="papang-excel-btn papang_btn paginate_button">Excel</button></div>');

//DataTable 만들기(페이지네이션 서버 처리)
var mainTable;
function makeDataTableServerSide() {
	var url = '/admin/selectRequestLog.do';
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
			    var reqTypeCode = [];
			    $("input:checkbox[name=reqTypeCode]:checked").each(function(){
					reqTypeCode.push(this.value);              
				});
			    param.reqTypeCode = reqTypeCode;				
				
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
	  	createdRow: function( row, data, dataIndex ) {	//행 옵션
			$(row).addClass('pointer');			//행에 pointer css 적용
	  	},       		
        pagingType: "numbers",					//페이지 표시 옵션
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
    
    //테이블 더블 클릭 이벤트
	$('#mainTable tbody').on('dblclick', 'tr', function () {
	    var data = mainTable.row(this).data();
	    requestLogModalOpen(data);
	});	 
}