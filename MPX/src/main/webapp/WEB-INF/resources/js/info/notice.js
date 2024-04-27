/**
 * @화면명: 공지사항 목록
 * @작성자: 김상민
 * @생성일: 2022. 11. 10. 오후 6:12:31
 * @설명: 공지사항 목록 조회 페이지
**/

$(document).ready(function () {
	makeDataTableServerSide();
});

//공지사항 목록 조회
function selectNotice(){
    $.ajax({
        url: '/notice/selectNotice.do',
        type: 'POST',
        data: {},
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8', 
        dataType: 'json',
        success: function (result) {
            mkNoticeTable(result.list);
        }
    });	
}

//검색
function doSearch(){
	mainTable.ajax.reload();
};

//DataTable 만들기(페이지네이션 서버 처리)
var columInfo = [
            { title: "제목"		, data: "title"			, width: "*"		, className: "text_align_left max-w400px"}
          , { title: "작성자"		, data: "fstRegId"		, width: "100px"	, className: "text_align_center"}
          , { title: "게시기간"	, data: "period"		, width: "150px"	, className: "text_align_center"}
          , { title: "게시일"		, data: "fstRegDtti"	, width: "150px"	, className: "text_align_center"	, render: function(data){return data.replace(/\//g,'-')}}	
          , { title: "조회수"		, data: "hit"			, width: "50px"		, className: "text_align_center hit"}
]
var mainTable
function makeDataTableServerSide() {
	var url = '/notice/selectNotice.do';
	var param = {};    
	param.pageLength = 10;						//페이지당 레코드 수
	
    mainTable = $('#mainTable').DataTable({
		serverSide: true,						//페이징 처리 서버에서 수행
		ajax: {
			url: url,
        	type: 'POST',
			data: function(){
				//검색 조건 object에 담기
				param.periodToggle = $('#periodToggle').prop('checked');
				
				if($util.isEmpty(mainTable)){
					param.strIdx = 0;
				} else {
					param.strIdx = 0 + (param.pageLength * parseInt(mainTable.page()));		//시작 레코드 인덱스 
				};
				return param;
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
   
    var $createBtn = $('<div class="table_btn_wrapper"><button type="button" class="papang-create-btn papang_btn paginate_button">신규</button></div>');
    $createBtn.on('click', function(){
		noticeModalOpen();
	})
	
	if(authGrade > 1){
		//쓰기 권한이 있을 떄
		$('#mainTable_paginate').after($createBtn);
	}
    
    //테이블 클릭 이벤트
	$('#mainTable tbody').on('click', 'tr', function () {
	    var data = mainTable.row(this).data();
	    noticeModalOpen(data);
	    $(this).children('.hit').text(++data.hit);
	});
}