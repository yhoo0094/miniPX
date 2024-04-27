/**
 * @화면명: 자유게시판 
 * @작성자: KimSangMin
 * @생성일: 2023. 11. 23. 오후 7:01:06
 * @설명: 자유롭운 주제로 이야기를 나눌 수 있는 게시판입니다.
**/

$(document).ready(function () {
	setDatetimepicker();		//datetimepicker 설정
	makeDataTableServerSide();
});

//datetimepicker 설정
function setDatetimepicker() {
	var dttiStr = $dateUtil.addDate($dateUtil.todayYYYYMMDDHHMM(),-1,0,0,0,0);	//1년 전
	dttiStr = $dateUtil.dateHyphenTime(dttiStr);
	
	$com.datepicker('dttiStr',$dateUtil.todayYYYY_MM_DD_HHMM(dttiStr));
	$com.datepicker('dttiEnd',$dateUtil.todayYYYY_MM_DD_HHMM());	
}

//검색
function doSearch(){
	mainTable.ajax.reload();
};

//DataTable 만들기(페이지네이션 서버 처리)
var columInfo = [
            { title: "제목"		, data: "title"				, width: "*"		, className: "text_align_left max-w400px"}
          , { title: "작성자"		, data: "fstRegId"			, width: "100px"	, className: "text_align_center"}
          , { title: "분류"		, data: "boardFreeCodeNm"	, width: "150px"	, className: "text_align_center"}
          , { title: "게시일"		, data: "ltUpdDtti"			, width: "150px"	, className: "text_align_center"	, render: function(data){return data.replace(/\//g,'-')}}	
          , { title: "조회수"		, data: "hit"				, width: "50px"		, className: "text_align_center hit"}
]
var mainTable
function makeDataTableServerSide() {
	var url = '/freeBoard/selectFreeBoard.do';
	var param = {useYn : 'Y'};    
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
		window.location.href = '/board/freeBoard/freeBoardView';
	})
	
	if(authGrade > 1){
		//쓰기 권한이 있을 떄
		$('#mainTable_paginate').after($createBtn);
	}
    
    //테이블 클릭 이벤트
	$('#mainTable tbody').on('click', 'tr', function () {
	    var data = mainTable.row(this).data();
	    moveFreeBoardView(data.boardSeq);	//자유게시판 조회 화면으로 이동
	});
}	

//자유게시판 조회 화면으로 이동
function moveFreeBoardView(boardSeq){
	increaseHit(boardSeq); //조회수 증가
	
	 // form 태그 생성
    var $form = $('<form></form>')
        .attr("action", "/board/freeBoard/freeBoardView")
        .attr("method", "post");

    // input 태그 생성 및 form 태그에 추가
    $('<input>').attr({
        type: "hidden",
        name: "param",
        value: boardSeq
    }).appendTo($form);

    // form 태그를 body에 추가하고 제출
    $form.appendTo('body').submit();
}

//조회수 증가
function increaseHit(boardSeq){
    $.ajax({
        url: '/freeBoard/increaseHit.do',
        type: 'POST',
        data: {boardSeq: boardSeq},
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8', 
        dataType: 'json',
        success: function (result) {
            if (result.RESULT == Constant.RESULT_SUCCESS){
                
            } else {
				alert(result[Constant.OUT_RESULT_MSG])
			}
        }
    });	
}
	