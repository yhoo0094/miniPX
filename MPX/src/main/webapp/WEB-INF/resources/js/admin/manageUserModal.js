/**
 * @화면명: manageUserModal.jsp
 * @작성자: KimSangMin
 * @생성일: 2024. 1. 16. 오전 10:19:32
 * @설명: 사용자 관리 모달
**/
 
//사용자 관리 모달 열기
function manageUserModalOpen(data){
	resetModal();
	
	if(data){
		$('#userIdModal').attr('readonly', true);
		//모달 값 입력
		for(var i in data){
			$('#' + i + "Modal").val(data[i]);
		}	
	} else {
		$('#userIdModal').attr('readonly', false);
	}
	
	//요청 로그 모달 열기
	$('#manageUserModal').modal({
		clickClose: false
	});		
}	 

//비밀번호 초기화
function pwReset(){
	if(!confirm('비밀번호를 초기화 하시겠습니까?')){
		return false;
	}
	
	var formData = new FormData($("#writeForm")[0]);
	
	$com.loadingStart();				//로딩패널 보이기
    $.ajax({
		url: '/admin/pwReset.do',
		type: 'POST',
		data: formData,
	    processData: false, // 데이터를 쿼리 문자열로 변환하지 않도록 설정
	    contentType: false, // 자동으로 Content-Type 설정을 하도록 설정
        dataType: 'json',
        success: function (result) {
	        if (result.RESULT == Constant.RESULT_SUCCESS){
	            alert("완료되었습니다.");
	            mainTable.ajax.reload();
	            $com.loadingEnd();					//로딩패널 숨기기
	        } else {
				alert(result[Constant.OUT_RESULT_MSG])
				$com.loadingEnd();					//로딩패널 숨기기
			}	
        }
    });		
}

//모달 내용 리셋
function resetModal(){
	$util.inputTypeEmpty($('#manageUserModal'), 'text');
	$('#rmrkModal').val('');
	
	$('select').each(function() {
	    $(this).find('option:first').prop('selected', true);
	});
}

//사용자 정보 저장
function saveUserInfo(){
	//유효성 검사
	if(!$util.checkRequired({group:["all1"]})){return;};
	var formData = new FormData($("#writeForm")[0]);
	
	let url;
	if($('#userIdModal').attr('readonly')){
		url = '/admin/updateUserInfo.do'
	} else {
		url = '/admin/insertUserInfo.do'
	}
	
	$com.loadingStart();				//로딩패널 보이기
    $.ajax({
		url: url,
		type: 'POST',
		data: formData,
	    processData: false, // 데이터를 쿼리 문자열로 변환하지 않도록 설정
	    contentType: false, // 자동으로 Content-Type 설정을 하도록 설정
        dataType: 'json',
        success: function (result) {
	        if (result.RESULT == Constant.RESULT_SUCCESS){
	            alert("완료되었습니다.");
	            mainTable.ajax.reload();
	            $com.loadingEnd();					//로딩패널 숨기기
	        } else {
				alert(result[Constant.OUT_RESULT_MSG])
				$com.loadingEnd();					//로딩패널 숨기기
			}	
        }
    });			
}