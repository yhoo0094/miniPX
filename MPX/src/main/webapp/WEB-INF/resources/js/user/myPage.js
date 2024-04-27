/**
 * @화면명: 마이페이지 
 * @작성자: KimSangMin
 * @생성일: 2024. 1. 18. 오후 5:06:31
 * @설명: 자신의 정보를 조회 및 관리할 수 있는 메뉴입니다.
**/

$(()=>{
	setLoginInfo();
})

//로그인 정보 화면에 뿌리기
function setLoginInfo(){
	for (let key in loginInfo) {
		if(key == 'signupDt' || key == 'birthDt'){
			let value = loginInfo[key].substring(0, 4) + '-' + loginInfo[key].substring(4, 6) + '-' + loginInfo[key].substring(6, 8);
			$('#' + key).val(value);
			continue;
		}
		if(key == 'phone'){
			let value = loginInfo[key].substring(0, 3) + '-' + loginInfo[key].substring(3, 7) + '-' + loginInfo[key].substring(7);
			$('#' + key).val(value);
			continue;
		}
		$('#' + key).val(loginInfo[key]);
	};
}

//사용자 정보 수정
function updateUserInfo(){
	alert('죄송합니다. 해당 기능은 추후 구현 예정입니다.');
}