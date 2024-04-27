/**
 * @화면명: 로그인 모달 
 * @작성자: 김상민
 * @생성일: 2022. 11. 8. 오전 7:53:55
 * @설명: 로그인 정보 입력 모달
**/

//var edit;
$(()=>{
	$editorUtil.createEditor('cn');
})

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
		//data.boardSeq = data.boardSeq;						//게시글일련번호
		data.readonly = 'Y'									//파일 제거 버튼 출력 여부
		$fileUtil.selectFile(data);							//첨부파일 조회
		
		//조회수 +1
		increaseHit();
	} else {	//신규 입력
		$('#noticeForm .form-control').removeAttr('readonly');			//readonly 제거	
		$('#popYnWrite').css('display','inline-block');		//공지사항팝업여부
		$('#popYnRead').css('display','none');				//공지사항팝업여부
		edit.disableReadOnlyMode('noticeModal');			//내용
		$('#cnByteDisplay').css('display','inline-block');
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
	edit.disableReadOnlyMode('noticeModal');						//내용
	$('#cnByteDisplay').css('display','inline-block');
	$('#fileAttachBtn').css('display','inline-block');				//첨부파일 버튼
	$('.deleteFileBtn').css('display','inline-block');				//파일 제거 버튼
}

//공지사항 저장
function saveNotice(){
	//유효성 검사
	if(!$util.checkRequired({group:["all1"]})){return;};
	if($util.isEmpty(edit.getData())){
		alert("내용이 입력되지 않았습니다.");
		$('#cn').focus();
		return;
	}; 	
	
	var formData = new FormData($("#noticeForm")[0]);
	
	//날짜 하이픈(-) 제거
	formData.set('strDt',$('#strDt').val().replace(/-/g,''));
	formData.set('endDt',$('#endDt').val().replace(/-/g,''));
	
	//에디터 내용 저장
	formData.set('cn',edit.getData());
	
	var url;    
    if($util.isEmpty($('#boardSeq').val())){
		url = '/notice/insertNotice.do';
	} else {
		url = '/notice/updateNotice.do';
	}
	
	//파일첨부가 포함된 글 저장
	$fileUtil.saveFile(url, formData).then(function(result){
        if (result.RESULT == Constant.RESULT_SUCCESS){
            // 데이타 성공일때 이벤트 작성
            closeModal();
            mainTable.ajax.reload(null,false);
            alert("완료되었습니다.");
        } else {
			alert(result[Constant.OUT_RESULT_MSG])
		}		
	});
}	

//모달 닫기
function closeModal(){
	$.modal.close();
	
	//모달 내용 초기화
	resetModal();
	$fileUtil.resetFile();
}

//모달 내용 초기화
function resetModal(){
	var today = new Date();
	document.getElementById('strDt').valueAsDate = today	//게시시작일 기본값 = 오늘 날짜
	document.getElementById('endDt').valueAsDate = new Date(today.setDate(today.getDate() + 7));	//게시종료일 기본값 = 일주일 후

	var el = $('#noticeModal');
	$util.inputTypeEmpty(el, 'text');
	$('#boardSeq').val('');
	edit.setData('');			//공지사항내용
}

//게시글 삭제
function deleteBoard(){
	if(!confirm('정말로 삭제 하시겠습니까?')){
		return;
	};
	
	var formData = new FormData($("#noticeForm")[0]);

    $.ajax({
		type: 'POST',
		enctype: 'multipart/form-data',
        url: '/notice/deleteNotice.do',
        data: formData,
        processData: false,
        contentType: false,
        cache: false, 
        success: function (result) {
            if (result.RESULT == Constant.RESULT_SUCCESS){
				closeModal();
				mainTable.ajax.reload(null,false);
                alert("삭제가 완료되었습니다.")
            } else {
				alert(result[Constant.OUT_RESULT_MSG])
			}
        }
    });  	
}

//조회수 증가
function increaseHit(){
	var formData = new FormData($("#noticeForm")[0]);
	
    $.ajax({
		type: 'POST',
		enctype: 'multipart/form-data',
        url: '/notice/increaseHit.do',
        data: formData,
        processData: false,
        contentType: false,
        cache: false, 
        success: function (result) {
            if (result.RESULT == Constant.RESULT_SUCCESS){
                
            } else {
				alert(result[Constant.OUT_RESULT_MSG])
			}
        }
    });	
}
