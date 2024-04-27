/**
* @파일명: manageRole.js
* @작성자: KimSangMin
* @생성일: 2023. 5. 3. 오후 8:20:50
* @설명: 권한그룹 관리
*/

$(document).ready(function () {
	selectData();
	makeGroupUserDataTable();
});	

//권한그룹 목록 조회
function selectData(){
    $.ajax({
        url: '/admin/selectRoleList.do',
        type: 'POST',
        data: $('#searchForm').serialize(),
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8', 
        dataType: 'json',
        success: function (result) {
            makeDataTable(result.data);
        }
    });	
}

var columInfo = [
	{ title: "표시순서"	, data: "roleOrder"		, width: "100px"		, className: "text_align_center"},
	{ title: "그룹명"		, data: "roleNm"		, width: "100px"		, className: "text_align_center"},
	{ title: "비고"		, data: "rmrk"			, width: "*"			, className: "text_align_left"		, defaultContent: ""},
]

//DataTable 만들기
var mainTable;
function makeDataTable(data) {
    mainTable = $('#mainTable').DataTable({
        data: data,
        columns: columInfo,		
        paging: false,
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
        rowReorder: {							//행 드래그해서 순서 바꾸기
            selector: 'td:first-child'
        },
    });	
    
    //테이블 클릭 이벤트
	$('#mainTable tr').on('click', function () {
	    var data = mainTable.row(this).data();
	    $('#roleNmSpan').text(data.roleNm);
	    
	    //게스트 권한인 경우 사용자 그룹만 조회 가능
	    if(loginInfo.roleNm == '게스트' && data.roleNm != '사용자'){
			alert('게스트 권한인 경우 사용자 그룹만 조회 가능합니다.');	
			$('#mainTable tr').eq(1).click();
		} else {
			param.roleSeq = data.roleSeq;
	    	groupUserTable.ajax.reload();	
		}
	});	    
	
	$('#mainTable tr').eq(1).click();
}

var groupUserCol = [
	{ 
		title: '<input type="checkBox" onclick="checkAll(this)">'		
		, data: 'userId'		
		, width: '25px'		
		, className: 'text_align_center'
	    , render: function (data, type, row, meta) {
	    	return '<input type="checkBox" name="userChk" data-userId="' + data + '">';
	    }		
	},
	{ 
		title: "ID"		
		, data: "userId"		
		, width: "100px"		
		, className: "text_align_center"
	},
	{ 
		title: "이름"		
		, data: "userName"		
		, width: "100px"		
		, className: "text_align_center"
	},
];

//체크박스 전체 선택
function checkAll(obj){
	$('#groupUserTable').find('input[type="checkBox"]').prop('checked',$(obj).prop('checked'));
}

//권한그룹에 속한 사용자 목록 테이블 만들기
var param = {
	pageLength : '10',
	roleSeq : '1',
}

var groupUserTable;
function makeGroupUserDataTable(){
    groupUserTable = $('#groupUserTable').DataTable({
		serverSide: true,						//페이징 처리 서버에서 수행
		ajax: {
			url: '/admin/selectGroupUser.do',
        	type: 'POST',
			data: function(){
				if($util.isEmpty(groupUserTable)){
					param.strIdx = 0;
				} else {
					param.strIdx = 0 + (param.pageLength * parseInt(groupUserTable.page()));		//시작 레코드 인덱스 
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
        columns: groupUserCol,
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
    
    var $createBtn = $('<div class="table_btn_wrapper"><button type="button" class="papang-del-btn papang_btn paginate_button">삭제</button></div>');
    $createBtn.on('click', function(){
		deleteGroupUser();
	})
    $('#groupUserTable_paginate').after($createBtn);   
}

//권한그룹 사용자 제거
function deleteGroupUser(){
	if(!confirm('정말 삭제하시겠습니까?')){return false;}
	
	var userIdList = [];
	$('input[name="userChk"]:checked').each(function(idx, itm){
		userIdList.push(itm.dataset.userid);
	});

    $.ajax({
        url: '/admin/deleteGroupUser.do',
        type: 'POST',
        data: {userIdList : JSON.stringify(userIdList)},
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8', 
        dataType: 'json',
        success: function (result) {
	        if (result.RESULT == Constant.RESULT_SUCCESS){
	            groupUserTable.ajax.reload();
	        } else {
				alert(result[Constant.OUT_RESULT_MSG])
			}	
        }
    });		
}
