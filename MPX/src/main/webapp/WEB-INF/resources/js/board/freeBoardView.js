/**
 * @화면명: 자유게시판 조회
 * @작성자: KimSangMin
 * @생성일: 2023. 11. 23. 오후 7:01:06
 * @설명: 자유게시판 등록/조회/수정 화면 
**/

$(()=>{
	$editorUtil.createEditor('cn');
	
	pageInit();	//권한 및 조회/등록 목적에 따른 버튼, 내용 표시 여부 조정
})

//권한 및 조회/등록 목적에 따른 버튼, 내용 표시 여부 조정 
function pageInit(){
	if($util.isEmpty(param)){
		//param 변수가 null이면 신규 등록
		chViewMode('01');	//화면 모드 바꾸기(01: 등록, 02: 수정, 03: 조회)
	} else {
		//데이터 조회해서 작성자와 id가 같으면 수정 모드
		$.ajax({
	        url: '/freeBoard/selectFreeBoard.do',
	        type: 'POST',
	        data: {boardSeq: param},
	        contentType: 'application/x-www-form-urlencoded; charset=UTF-8', 
	        dataType: 'json',
	        success: function (result) {
				let data = result.OUT_DATA[0];
				data = $util.XssReverseObj(data);	//XSS방지를 위한 문자열 변환 되돌리기

				//데이터 조회
				for (let key in data) {
				    let input = document.getElementById(key);
				    if (input) {
				        input.value = data[key];
				    }
				}
				edit.setData(data.cn); //에디터 조회
				
				if($com.getUserInfo('userId') == data.fstRegId){	
					//작성자ID와 조회ID가 같을 경우 -> 수정
					chViewMode('02');	//화면 모드 바꾸기(01: 등록, 02: 수정, 03: 조회)
					data.readonly = 'N'	//첨부파일 제거 버튼 출력 여부					
				} else {
					chViewMode('03');	//화면 모드 바꾸기(01: 등록, 02: 수정, 03: 조회)
					data.readonly = 'Y'	//첨부파일 제거 버튼 출력 여부
				}
				
				//첨부파일 조회
				data.boardCode = '02';								//게시판구분코드(01:공지사항,02:자유게시판,03:질문게시판,04:지역게시판)
				$fileUtil.selectFile(data);							//첨부파일 조회
	        }
	    });
	}
}

//화면 모드 바꾸기
function chViewMode(type){
	switch(type){ 
		case('01'):	//등록
			$('#saveBtn').css('display','inline-block');
			$('#modifyBtn').css('display','none');
			$('#delBtn').css('display','none');
			$('#cnByteDisplay').css('display','inline-block');
			$('.fileAttachBtn').css('display','inline-block');
			break;
		case('02'):	//수정
			$('#saveBtn').css('display','none');
			$('#modifyBtn').css('display','inline-block');
			$('#delBtn').css('display','inline-block');
			$('#cnByteDisplay').css('display','inline-block');	 
			break;
		case('03'):	//조회
			$('#saveBtn').css('display','none');
			$('#modifyBtn').css('display','none');
			$('#delBtn').css('display','none');	 
			
			$('#freeBoardForm .form-control').attr('readonly','readonly');		//readonly 적용
			$('#freeBoardForm select').attr('disabled',true);					//disabled 적용
			edit.enableReadOnlyMode('Y');										//에디터 readonly 적용
			break;		
		default:
			break;
	}
}

//저장
function saveFreeBoard(){
	//유효성 검사
	if(!$util.checkRequired({group:["all1"]})){return;};
	if($util.isEmpty(edit.getData())){
		alert("내용이 입력되지 않았습니다.");
		$('#cn').focus();
		return;
	}; 
	
	var formData = new FormData($("#freeBoardForm")[0]);
	//에디터 내용 저장
	formData.set('cn',edit.getData());
	
	var url;    
    if($util.isEmpty(param)){
		url = '/freeBoard/insertFreeBoard.do';
	} else {
		url = '/freeBoard/updateFreeBoard.do';
	}
	
	//파일첨부가 포함된 글 저장
	$fileUtil.saveFile(url, formData).then(function(result){
        if (result.RESULT == Constant.RESULT_SUCCESS){
            alert("완료되었습니다.");
            
            //등록 후 수정 화면으로 이동
            if($util.isEmpty(param)){
			    var $form = $('<form></form>')
			        .attr("action", "/board/freeBoard/freeBoardView")
			        .attr("method", "post");
			
			    $('<input>').attr({
			        type: "hidden",
			        name: "param",
			        value: result.OUT_DATA.boardSeq,
			    }).appendTo($form);
			
			    $form.appendTo('body').submit();				
			}
        } else {
			alert(result[Constant.OUT_RESULT_MSG])
		}		
	});
}

//삭제
function deleteFreeBoard(){
	if(!confirm('정말로 삭제하시겠습니까?')){return false;}
	
	$.ajax({
        url: '/freeBoard/deleteFreeBoard.do',
        type: 'POST',
        data: {boardSeq: param},
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8', 
        dataType: 'json',
        success: function (result) {
			window.location.href = contextPath + '/board/freeBoard';
        }
    });	
}

//돌아가기
function moveList(){
	window.location.href = '/board/freeBoard';
}