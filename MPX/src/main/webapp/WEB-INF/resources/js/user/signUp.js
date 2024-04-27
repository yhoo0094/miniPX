$(()=>{
	alert('죄송합니다. 현재 회원가입 기능은 지원하지 않고 있습니다. \n계정이 필요한 경우 개발자에게 문의해주세요.')
	location.href = "/home";
	
	$('#userId').on('change',function(){
		$('#chkUniqIdBtn').removeClass('papang_disabled');
		$('#idUniChkMsg').attr('class','idUniChkBf');
	})
})

//회원가입
function insertUser(){
	if(!$util.checkRequired({group:["all1"]})){return;};
	
	//아이디 유효성 검증
	if(!idValidation()){return;}
	
	//아이디 중복확인 했는지 확인
	if(!$('#chkUniqIdBtn').hasClass('papang_disabled')){
		alert('아이디 중복확인이 필요합니다.');
		return;
	};
	
	//비밀번호 유효성 검증
	if(!pwValidation()){return;}
	
	//비밀번호 유효성 검증
	let userPw = formData.get('userPw');
	let userPwChk = formData.get('userPwChk');	
	if(!$util.pwValidation(userPw, userPwChk)){return;}
	
	var formData = $('#signUpForm').serialize();
    $.ajax({
        url: '/user/insertUser.do',
        type: 'POST',
        data: formData,
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8', 
        dataType: 'json',
        success: function (result) {
            if (result.RESULT == Constant.RESULT_SUCCESS){
                alert("회원가입이 완료되었습니다.")
                location.href = "/"	//홈으로 이동
            } else {
				alert(result[Constant.OUT_RESULT_MSG])
			}
        }
    });
}

//아이디 유효성 검증
function idValidation(){
	var result;	//처리 결과
	var value = $('#userId').val();
	
	//아이디 입력여부 검사
	if($util.isEmpty(value)){
		alert('아이디가 입력되지 않았습니다.')
		return false;
	}

	//조건: 영문과 숫자만으로 20글자 이하(추후 구현)
	result = true;
	
	return result;
}

//비밀번호 유효성 검증
function pwValidation(){
	var result;	//처리 결과
	var value = $('#userPw').val();
	
	//조건: 숫자, 영어, 특수문자를 포함하여 8글자 이상 50글자 이하
	var allChk = /^(?=.*\d)(?=.*[A-Za-z])(?=.*[~!@#\$%\^&\*()_\+\-={}\[\]\\:;"'<>,.\/]).{8,20}$/;
	var numChk = /\d/gim;		//숫자 포함
	var enChk = /[A-Za-z]/gim;	//영어 포함
	var speChaChk = /[~!@#\$%\^&\*()_\+\-={}\[\]\\:;"'<>,.\/]/gim;	//특수문자 포함
	var lenChk = /.{8,}$/gim;	//최소 길이
	var maxLenChk = /.{,50}$/gim;	//최대 길이
	
	if(allChk.test(value)){ //비밀번호 입력 조건 검증
		if(value == $('#pwChk').val()){ //비밀번호와 비밀번호 확인이 같은지 검증
			result = true;
		} else {
			alert('비밀번호와 비밀번호 확인이 일치하지 않습니다.');
			result = false;
		}
	} else {
		if(!numChk.test(value)){
			alert('비밀번호에 숫자가 포함되지 않았습니다.');
		} else if(!enChk.test(value)) {
			alert('비밀번호에 영어가 포함되지 않았습니다.');
		} else if(!speChaChk.test(value)) {
			alert('비밀번호에 특수문자가 포함되지 않았습니다.');
		} else if(!lenChk.test(value)) {
			alert('비밀번호의 길이가 8글자보다 길어야합니다.');
		} else if(!maxLenChk.test(value)) {
			alert('비밀번호의 최대 길이는 50글자입니다.');
		} 
		result = false;				
	}
	
	return result;
}

//아이디 중복 확인
function chkUniqId(){
	//아이디 유효성 검증
	if(!idValidation()){return;}
	
	var userId = $('#userId').val();
	
    $.ajax({
        url: '/user/chkUniqId.do',
        type: 'POST',
        data: {userId : userId},
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8', 
        dataType: 'json',
        success: function (result) {
            if (result.RESULT == Constant.RESULT_SUCCESS){
				if(result.data == null){
					alert("사용 가능한 아이디입니다.");
					$('#chkUniqIdBtn').addClass('papang_disabled');
					$('#idUniChkMsg').attr('class','idUniChkAf');
				} else {
					alert("중복된 아이디입니다.");
				}
            } else {
				alert(result[Constant.OUT_RESULT_MSG])
			}
        }
    });	
}
