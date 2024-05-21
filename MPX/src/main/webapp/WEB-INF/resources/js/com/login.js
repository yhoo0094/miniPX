/**
 * @화면명: 로그인 
 * @작성자: KimSangMin
 * @생성일: 2024. 4. 25. 오전 10:50:08
 * @설명: 로그인 화면
**/

$(()=>{
	rememberIdChk();	//아이디 저장 여부 확인
})

//아이디 저장 여부 확인
function rememberIdChk(){
	if($util.getCookie('mpx_save_id') == 'Y'){
		$('#rememberIdChk').val($util.getCookie('mpx_user_id'));
		$('#rememberIdChk').prop("checked", true);
	}	
}

//로그인
function login(){
	//필수입력 검증
	if(!$util.checkRequired({group:["all1"]})){return;};
	
	//아이디 저장 여부 확인
	if($('#rememberIdChk').is(":checked")){
		$util.setCookie('mpx_user_id', $('#userId').val());
		$util.setCookie('mpx_save_id', 'Y');
	} else {
		$util.setCookie('mpx_user_id', '');
		$util.setCookie('mpx_save_id', 'N');		
	};
	
	var formData = $('#loginForm').serialize();
	$com.loadingStart();
    $.ajax({
        url: '/user/login.do',
        type: 'POST',
        data: formData,
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8', 
        dataType: 'json',
        success: function (res, textStatus, jqXHR) {
			$com.loadingEnd();
	        if (res.RESULT == Constant.RESULT_SUCCESS){
				location.href = "/";
	        } else {
				alert(res[Constant.OUT_RESULT_MSG]);
			}
        },
        error: function(textStatus, jqXHR, thrownError){
			$com.loadingEnd();
		}
    });		
}