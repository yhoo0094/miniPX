/**
 * @화면명: 비밀번호 변경 모달 
 * @작성자: KimSangMin
 * @생성일: 2024. 1. 18. 오전 10:33:18
 * @설명: 비밀번호 변경 모달
**/

//비밀번호 변경 모달 열기
function chngPwModalOpen(){
	$com.loadingEnd();
	chngPwModalReset();	//모달 내용 초기화
	
	//비밀번호 변경 모달 열기
	$('#chngPwModal').modal({
		clickClose: false
	});		
}	

//모달 닫기
function chngPwModalClose(){
	chngPwModalReset();	//모달 내용 초기화
	$.modal.close();
}

//모달 내용 초기화
function chngPwModalReset(){
	var el = $('#chngPwModal');
	$util.inputTypeEmpty(el, 'password');
}

//비밀번호 변경
function chngUserPw(){
	//필수입력 검증
	if(!$util.checkRequired({group:["allM2"]})){return;};	
	
	let formData = new FormData($("#chngPwForm")[0]);
	let formObject = {};
	formData.forEach(function(value, key) {
	    formObject[key] = value;
	});

	//비밀번호 유효성 검증
	let userPw = formObject.userPw;
	let userPwChk = formObject.chngUserPwModalChk;	
	if(!$util.pwValidation(userPw, userPwChk)){return;}
	
	$com.loadingStart();	
    $.ajax({
        url: '/user/chngUserPw.do',
        type: 'POST',
        data: formObject,
        dataType: 'json',
        success: function (result) {
            if (result.RESULT == Constant.RESULT_SUCCESS){
                alert("비밀번호 변경이 완료되었습니다.");
                location.reload();
            } else {
				alert(result[Constant.OUT_RESULT_MSG])
			};
			$com.loadingEnd();
        }
    });	
}