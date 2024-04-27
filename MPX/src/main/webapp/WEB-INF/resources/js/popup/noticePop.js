/**
 * @화면명: 로그인 모달 
 * @작성자: 김상민
 * @생성일: 2022. 11. 8. 오전 7:53:55
 * @설명: 로그인 정보 입력 모달
**/

var edit;
$(()=>{
	//calWrapHeight();
	createEditor('.editor').then( newEditor => {edit = watchdog.editor});	//에디터 생성
	selectNotice();	//공지사항 조회
})

//wrap 높이 조정
function calWrapHeight(){
	let viewportHeight = parseInt(window.innerHeight);
	let wrapHeight = parseInt($('.wrap').css('height').replace('px',''));
	if(viewportHeight > wrapHeight){
		$('.wrap').css('height', viewportHeight);
	}
}

//하루 동안 표시하지 않음 클릭
function noticePopX(){
	var boardSeq = $util.getParameterByName('boardSeq');
	$util.addToCookie('noticePopX', boardSeq, 1);
	window.close();
}

//공지사항 조회
function selectNotice(){
	var boardSeq = $util.getParameterByName('boardSeq');
	
    $.ajax({
        url: '/notice/selectNotice.do',
        type: 'POST',
        data: {boardSeq : boardSeq},
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8', 
        dataType: 'json',
        success: function (result) {
			var data = $util.XssReverseObj(result.OUT_DATA[0]);	//XSS방지를 위한 문자열 변환 되돌리기
			
			$('#title').val(data.title);
            $('#cn').html(data.cn);
            $('#strDt').val(data.strDt.replace(/(\d{4})(\d{2})(\d{2})/g, '$1-$2-$3'));	//공지사항게시시작일
			$('#endDt').val(data.endDt.replace(/(\d{4})(\d{2})(\d{2})/g, '$1-$2-$3'));	//공지사항게시종료일
			edit.setData(data.cn);								//공지사항내용
			edit.enableReadOnlyMode('noticeModal');				//내용
			
			data.boardCode = '01';								//게시판구분코드(01:공지사항,02:자유게시판,03:질문게시판,04:지역게시판)
			data.readonly = 'Y'									//파일 제거 버튼 출력 여부
			$fileUtil.selectFile(data);							//첨부파일 조회
        }
    });	
}

//공지사항 팝업 열기
function noticeModalOpen(data){
	data = $util.XssReverseObj(data);	//XSS방지를 위한 문자열 변환 되돌리기
	resetModal();

	if(data != null){	//조회
		//데이터 입력
		$('#boardSeq').val(data.boardSeq);			//공지사항일련번호
		$('#title').val(data.title);				//공지사항제목
		edit.setData(data.cn);						//공지사항내용
		
//		$('#cnRead').html(data.cn);					//공지사항내용
		$('#strDt').val(data.strDt.replace(/(\d{4})(\d{2})(\d{2})/g, '$1-$2-$3'));	//공지사항게시시작일
		$('#endDt').val(data.endDt.replace(/(\d{4})(\d{2})(\d{2})/g, '$1-$2-$3'));	//공지사항게시종료일
		(data.popYn == 'Y')? $('#popY').prop('checked','checked') : $('#popN').prop('checked','checked');	//공지사항팝업여부
		(data.popYn == 'Y')? $('#popYnRead').text('Y') : $('#popYnRead').text('N');	//공지사항팝업여부
		
		//요소 출력 여부
		$('#noticeForm .form-control').attr('readonly','readonly');		//readonly 적용
		$('#popYnWrite').css('display','none');				//공지사항팝업여부
		$('#popYnRead').css('display','inline-block');		//공지사항팝업여부
		edit.enableReadOnlyMode('noticeModal');				//내용
		$('#fileAttachBtn').css('display','none');			//첨부파일 버튼
		
		$('#modalSaveBtn').css('display','none');
		if(authGrade > 1){
			//쓰기 권한이 있을 떄
			$('#modalModifyBtn').css('display','inline-block');
			$('#modalDelBtn').css('display','inline-block');
		} else {
			$('#modalModifyBtn').css('display','none');
			$('#modalDelBtn').css('display','none');
			$('.popYn').css('visibility','hidden');
		}
		
		data.boardCode = '01';								//게시판구분코드(01:공지사항,02:자유게시판,03:질문게시판,04:지역게시판)
		data.boardSeq = data.boardSeq;						//게시글일련번호
		data.readonly = 'Y'									//파일 제거 버튼 출력 여부
		$fileUtil.selectFile(data);							//첨부파일 조회
		
		//조회수 +1
		increaseHit();
	} else {	//신규 입력
		$('#noticeForm .form-control').removeAttr('readonly');			//readonly 제거	
		$('#popYnWrite').css('display','inline-block');		//공지사항팝업여부
		$('#popYnRead').css('display','none');				//공지사항팝업여부
		edit.disableReadOnlyMode('noticeModal');				//내용
		$('#fileAttachBtn').css('display','inline-block');	//첨부파일 버튼
		
		$('#modalSaveBtn').css('display','inline-block');
		$('#modalModifyBtn').css('display','none');
		$('#modalDelBtn').css('display','none');
	}
	
	$('#noticeModal').modal({
		clickClose: false,
		showClose: false,	//닫기 버튼 제거
	});	
}

//수정 모드로 바꾸기
function setModifyMode(){
	$('#modalModifyBtn').css('display','none');						//수정 버튼
	$('#modalSaveBtn').css('display','inline-block');				//저장 버튼
	$('#noticeForm .form-control').removeAttr('readonly');			//readonly 제거		
	$('#popYnWrite').css('display','inline-block');					//공지사항팝업여부
	$('#popYnRead').css('display','none');							//공지사항팝업여부
	edit.disableReadOnlyMode('noticeModal');							//내용
	$('#fileAttachBtn').css('display','inline-block');				//첨부파일 버튼
	$('.deleteFileBtn').css('display','inline-block');				//파일 제거 버튼
}

//모달 닫기
function closeModal(){
	$.modal.close();
	
	//모달 내용 초기화
	resetModal();
	$fileUtil.resetFile();
}

