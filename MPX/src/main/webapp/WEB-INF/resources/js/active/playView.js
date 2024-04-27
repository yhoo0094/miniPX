/**
 * @화면명: 놀이
 * @작성자: KimSangMin
 * @생성일: 2023. 11. 29. 오후 8:12:18
 * @설명: 아이와 함께할 수 있는 놀이 정보를 나누는 메뉴입니다.
**/

$(()=>{
	$editorUtil.createEditor('cn');
	
	pageInit();			//권한 및 조회/등록 목적에 따른 버튼, 내용 표시 여부 조정
	
	thumbnailImgHeight();	//썸네일 이미지 높이 조정
})

//썸네일 이미지 높이 조정
function thumbnailImgHeight() {
    var thumbnailImg = document.getElementById('thumbnailImg');
    var width = thumbnailImg.offsetWidth; // 너비를 구합니다
    thumbnailImg.style.height = width + 'px'; // 높이를 너비와 동일하게 설정합니다
}

// 창 크기가 변경될 때마다 높이 조정
window.addEventListener('resize', thumbnailImgHeight);

//썸네일 이미지 클릭
function clickThumbnail(){
	$('#thumbnailImg, #cardImgMsg').on({
		click: function(){
			$('#thumbnail').click()
		}
	})	
}

//등록 이미지 미리보기
function thumbnailPreview(){
	document.getElementById('thumbnail').addEventListener('change', function(event) {
		$('#cardImgMsg').hide();	//'썸네일을 선택하세요.' 숨기기
		
	    const file = event.target.files[0];
	    if (file) {
	        const reader = new FileReader();
	
	        reader.onload = function(e) {
	            const img = document.getElementById('thumbnailImg');
	            img.src = e.target.result;
	            img.style.display = 'block';
	        };
	
	        reader.readAsDataURL(file);
	    }
	});
}

//권한 및 조회/등록 목적에 따른 버튼, 내용 표시 여부 조정 
function pageInit(){
	if($util.isEmpty(param)){
		//param 변수가 null이면 신규 등록
		chViewMode('01');		//화면 모드 바꾸기(01: 등록, 02: 수정, 03: 조회)
		thumbnailPreview();		//등록 이미지 미리보기
		clickThumbnail();		//썸네일 이미지 클릭
	} else {
		//데이터 조회해서 작성자와 id가 같으면 수정
		$com.loadingStart();	
		$.ajax({
	        url: '/play/selectPlay.do',
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
				let src = '/common/getImage.do?boardSeq=' + data.boardSeq;
				$('#thumbnailImg').attr('src', src);
				
				if($com.getUserInfo('userId') == data.fstRegId){	
					//작성자ID와 조회ID가 같을 경우 -> 수정
					chViewMode('02');		//화면 모드 바꾸기(01: 등록, 02: 수정, 03: 조회)
					thumbnailPreview();		//등록 이미지 미리보기
					clickThumbnail();		//썸네일 이미지 클릭
				} else {
					chViewMode('03');		//화면 모드 바꾸기(01: 등록, 02: 수정, 03: 조회)
					$('.thumbnailImg').css('cursor','default')
				}
				$com.loadingEnd();
	        },
	  	    error: function(textStatus, jqXHR, thrownError){
				$com.loadingEnd();
			}  
	    });
	}
}

//화면 모드 바꾸기
function chViewMode(type){
	switch(type){ 
		case('01'):	//등록
			$('#saveBtn').css('display','inline-block');
			$('#delBtn').css('display','none');
			$('#cnByteDisplay').css('display','inline-block');
			$('#cardImgMsg').show();	//'썸네일을 선택하세요.' 보이기
			break;
		case('02'):	//수정
			$('#saveBtn').css('display','inline-block');
			$('#delBtn').css('display','inline-block');
			$('#cnByteDisplay').css('display','inline-block');	 
			break;
		case('03'):	//조회
			$('#saveBtn').css('display','none');
			$('#modifyBtn').css('display','none');
			$('#delBtn').css('display','none');	 
			
			$('#viewForm .form-control').attr('readonly','readonly');		//readonly 적용
			$('#viewForm select').attr('disabled',true);					//disabled 적용
			edit.enableReadOnlyMode('Y');										//에디터 readonly 적용
			break;		
	}
}

//저장
function saveBtnClick(){
	//유효성 검사
	if(!$util.checkRequired({group:["all1"]})){return;};
	if($util.isEmpty(edit.getData())){
		alert("내용이 입력되지 않았습니다.");
		$('#cn').focus();
		return;
	}; 
	
	var formData = new FormData($("#viewForm")[0]);
	
	// input 태그에서 파일을 가져와서 FormData 객체에 추가
    var fileData = $('#thumbnail').prop('files')[0];
    formData.append('files', fileData);	
	
	//에디터 내용 저장
	formData.set('cn',edit.getData());	
	
	var url;    
    if($util.isEmpty(param)){
		if(fileData == undefined){
			alert("썸네일이 선택되지 않았습니다.");
			clickThumbnail()
			return;
		}
		
		url = '/play/insertPlay.do';
	} else {
		url = '/play/updatePlay.do';
	}
	
    $.ajax({
		type: 'POST',
		enctype: 'multipart/form-data',
        url: url,
        data: formData,
        processData: false,	// 데이터를 문자열로 변환하지 않음
        contentType: false,	// 기본 컨텐트 타입 설정 해제
        cache: false, 
        success: function (result) {
	        if (result.RESULT == Constant.RESULT_SUCCESS){
	            alert("완료되었습니다.");
	            
	            //수정 화면으로 이동
			    var $form = $('<form></form>')
			        .attr("action", "/active/play/playView")
			        .attr("method", "post");
			
			    $('<input>').attr({
			        type: "hidden",
			        name: "param",
			        value: result.OUT_DATA.boardSeq,
			    }).appendTo($form);
			
			    $form.appendTo('body').submit();				
	        } else {
				alert(result[Constant.OUT_RESULT_MSG])
			}	
        }
    });	
}

//삭제
function deleteBtnClick(){
	if(!confirm('정말로 삭제하시겠습니까?')){return false;}
	
	$.ajax({
        url: '/play/deletePlay.do',
        type: 'POST',
        data: {boardSeq: param},
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8', 
        dataType: 'json',
        success: function (result) {
			window.location.href = contextPath + '/active/play';
        }
    });	
}

//돌아가기
function moveList(){
	window.location.href = '/active/play';
}