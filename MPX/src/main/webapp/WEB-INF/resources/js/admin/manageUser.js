/**
* @파일명: manageUser.js
* @작성자: KimSangMin
* @생성일: 2024. 1. 15. 오후 1:23:35
* @설명: 사용자 관리
*/
	
$(document).ready(function () {
	makeRoleSelectTag();		//권한그룹 select태그 만들기
	guestChk();
	makeDataTableServerSide();	//사용자 목록 조회
});	

//게스트 계정에 대한 제약사항 적용
function guestChk(){
	if(loginInfo.roleNm == '게스트'){
		$('#userId').val(loginInfo.userId);
		$('#userId').attr('readonly',true);
	}
}

//검색
function doSearch(){
	mainTable.ajax.reload();
};

//사용자 조회
var columInfo = [
            { title: "아이디"		, data: "userId"				, width: "*"		, className: "text_align_left max-w300px"}
          , { title: "이름"		, data: "userName"				, width: "*"		, className: "text_align_left max-w300px"}
          , { title: "권한그룹"	, data: "roleNm"				, width: "150px"	, className: "text_align_center"}
          , { title: "상태"		, data: "userStatusCodeNm"		, width: "150px"	, className: "text_align_center"}	
          , { title: "가입일"		, data: "fstRegDtti"			, width: "150px"	, className: "text_align_center"}
]
var mainTable
function makeDataTableServerSide() {
	var url = '/admin/selectUser.do';
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
	  	createdRow: function( row, data, dataIndex ) {
			$(row).addClass('pointer');			//행에 pointer css 적용
	  	},       		
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
			zeroRecords	: '조회된 결과가 없습니다.',
		},
    });		
    
    //테이블 더블 클릭 이벤트
	$('#mainTable tbody').on('dblclick', 'tr', function () {
	    var data = mainTable.row(this).data();
	    manageUserModalOpen(data);
	});	     
	
	//사용자 생성 버튼
    createUserBtn.on('click', function(){
		manageUserModalOpen();
	})
	$('#mainTable_paginate').after(createUserBtn); 
}

//사용자 생성 버튼
let createUserBtn = $('<div class="table_btn_wrapper"><button type="button" class="papang-create-btn papang_btn paginate_button">신규</button></div>');

//권한그룹 select태그 만들기
function makeRoleSelectTag(){
	$com.loadingStart();	
	$.ajax({
        url: '/admin/makeRoleSelectTag.do',
        type: 'POST',
        data: {},
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8', 
        dataType: 'json',
        success: function (result) {
			//데이터 조회
			for (let data of result.OUT_DATA) {
				let option = $('<option value="' + data.roleSeq + '">' + data.roleNm + '</option>')
				$('.roleSeqTag').append(option);
			}
			$com.loadingEnd();
        },
	    error: function(textStatus, jqXHR, thrownError){
			$com.loadingEnd();
		} 
    });
}