/**
 * @작성자: 김상민
 * @생성일: 2022. 11. 8. 오후 6:51:35
 * @설명: 다양한 프레임워크에서 큰 제약 없이 사용 가능한 js 기능 모음
**/

var $util = {};

$(()=>{
	makeSelectTag();
})

//해당 기능은 아직 구현되지 않았습니다.
$util.notYet = function notYet(){
	alert('해당 기능은 아직 구현되지 않았습니다.');
}

//select 태그 자동 완성
function makeSelectTag(){
	$('.makeSelectTag').each(function(idx, itm){
		let target = $(itm).attr('name');
		let codeGroup = $util.camelToSnake(target);
		
		$.ajax({
        url: '/common/selectCodeList.do',
        type: 'POST',
        data: {codeGroup : codeGroup},
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8', 
        dataType: 'json',
        async: false,
        success: function (result) {
            $.each(result.OUT_DATA, function(index, item) {
		        var $option = $('<option></option>')
		            .val(item.codeDetail) // value 속성 설정
		            .text(item.codeDetailNm); // 텍스트 설정
//		        $('#' + target).append($option);
		        $(itm).append($option);
	    	});
        }
    	});	
	})
}
 
/**
 * 필수값 입력 여부 점검
 * @param option {group : []} 일 때 - 검사 대상 그룹 목록 정의. "all0", "least1" 등의 형식으로 그룹 지정 가능. all = 해당 그룹 모두 입력 필요, least = 해당 그룹 중 적어도 하나 입력 필요
 				 {target : []} 일 때 - 해당 태그만 검사
 * @return boolean
 */
$util.checkRequired = function(option){
	//해당 객체 없을 경우 빈 값 만들기
	option = option || {};
	option.target = option.target || [];
	option.group = option.group || [];
	
	if(option.group.length > 0){
		for(i = 0; i < option.group.length; i++){
			var grp = option.group[i];
			var selector = '';
			
			//제이쿼리 설렉터
			if(option.target.length > 0){
				for(k = 0; k < option.target.length; i++){
					selector = selector + option.target[k] + "[required=" + grp + "] "
				};				
			} else {
				selector = "[required=" + grp + "]"
			}
			var selectTags = $(selector);
			
			if(grp.indexOf("all") >= 0){	//모두 입력 필요
				for(k = 0; k < selectTags.length; k++){
					if($util.isEmpty($(selectTags[k]).val())){
						alert(selectTags[k].title + "이(가) 입력되지 않았습니다.")
						$(selectTags[k]).focus();
						return false;
					}
				}
				return true;
			} else if (grp.indexOf("least") >= 0){	//적어도 하나 입력 필요
				var tagTitles = "";	//대상 태그 타이틀 변수 저장
				for(k = 0; k < selectTags.length; k++){
					if($util.isEmpty($(selectTags[k]).val())){
						if(tagTitles == ""){
							tagTitles = selectTags[k].title
						} else {
							tagTitles = tagTitles + ", " + selectTags[k].title
						}
					} else {
						return true;
					}
				}
				alert(tagTitles + " 중 적어도 하나는 입력되어야 합니다.")
				$(selectTags[0]).focus();
				return false;				
			};
		};
	}
}	

/**
 * 빈 값 여부 확인(boolean 반환)
 * @param obj 오브젝트
 * @return boolean
 */
$util.isEmpty = function(obj){
	//string이면 공백 제거
	if(typeof(obj) == 'string'){obj = obj.trim();};
	
	if(obj == null || obj == undefined || obj == ""){
		return true;
	} else {
		return false;
	}
};

/**
 * 두 개의 텍스트가 같은지 확인(대소문자 구분 X)
 * @param val1 텍스트
 * @param val2 텍스트 
 * @return boolean
 */
$util.isSameText = function(val1, val2){
	if(val1.trim().toLowerCase() == val2.trim().toLowerCase()){
		return true;
	} else {
		return false;
	}
}

/**
 * element 하위 input의 값 비우기
 * @param el 범위 element
 * @param type 대상 input 타입
 */
$util.inputTypeEmpty = function(el, type){
	$(el).find('input[type="' + type + '"]').val('');
};

/**
 * 쿠키 생성
 * @param name 쿠키 이름
 * @param value 쿠키 값
 * @param days 쿠키 보존 기간
 */
$util.setCookie = function(name, value, days){
	var expires = 360;
	if(days){
		expires = days; 
	}
	var date = new Date();
	date.setTime(date.getTime() + (expires*24*60*60*1000));
	expires = date.toUTCString();
	document.cookie = name + "=" + value + "; expires=" + expires + "; path=/";
}

/**
 * 쿠키 조회
 * @param name 쿠키 이름
 * @return value 쿠키 값
 */
$util.getCookie = function(name){
	var value = document.cookie
	  .split('; ')
	  .find((row) => row.startsWith(name))
	  ?.split('=')[1];	
	return value;
}

/**
 * 쿠키에 값 추가하기
 * @param name string - 쿠키 이름
 * @param value string - 추가할 값
 * @param days number - 쿠키 보존 기간(일)
 */
$util.addToCookie = function(name, value, days) {
    var cookieValue = $util.getCookie(name);
    var cookieArray = cookieValue ? cookieValue.split(',') : [];
    if (cookieArray.indexOf(value) === -1) {
        cookieArray.push(value);
        $util.setCookie(name, cookieArray.join(','), days); // 1일 유지
    }
}

/**
 * 쿠키에 값 제거하기
 * @param name string - 쿠키 이름
 * @param value string - 제거할 값
 * @param days number - 쿠키 보존 기간(일)
 */
$util.rmFromCookie = function(name, value, days) {
    var cookieValue = $util.getCookie(name);
    var cookieArray = cookieValue ? cookieValue.split(',') : [];
    var index = cookieArray.indexOf(value);
    if (index !== -1) {
        cookieArray.splice(index, 1);
        $util.setCookie(name, cookieArray.join(','), days); // 1일 유지
    }
}

/**
 * 배열(Array)에서 객체(object) 가져오기
 * @param arr 배열(array)
 * @param key 키
 * @param val 값(value)
 * @return obj 객체(object)
 */
$util.getObjFromArr = function(arr, key, val){
	var obj;
	
	for(i in arr){
		if(val == (arr[i][key])){
			obj = arr[i];
		}
	}
	
	return obj;
}

/**
 * Xss 방지를 위한 텍스트 변환
 * @param param String
 * @return String
 */
$util.XssReplace = function(param) {
		param = param.replaceAll("&", "&amp;");
		param = param.replaceAll("\"", "&quot;");
		param = param.replaceAll("'", "&apos;");
		param = param.replaceAll("<", "&lt;");
		param = param.replaceAll(">", "&gt;");
		param = param.replaceAll("\r", "<br>");
		param = param.replaceAll("\n", "<p>");

		return param;	
}

/**
 * XSS방지를 위한 문자열 변환 되돌리기
 * @param param String
 * @return String
 */
$util.XssReverse = function(param) {
		param = param.replaceAll("<p>", "\n");
		param = param.replaceAll("<br>", "\r");
		param = param.replaceAll("&gt;", ">");
		param = param.replaceAll("&lt;", "<");
		param = param.replaceAll("&apos;", "'");
		param = param.replaceAll("&quot;", "\"");
		param = param.replaceAll("&amp;", "&");
		return param;	
}

/**
 * object에 대해 XSS방지를 위한 문자열 변환 되돌리기
 * @param param object
 * @return object
 */
$util.XssReverseObj = function(obj) {
	for(key in obj){
		if(typeof(obj[key]) == 'string'){
			obj[key] = $util.XssReverse(obj[key]);
		}	
	}
	return obj;	
}

/**
 * url에 포함된 파라미터 값 가져오기
 * @param name String 파라미터명
 * @return String 파라미터값
 */
$util.getParameterByName = function(name) {
	var url = window.location.href
    name = name.replace(/[\[\]]/g, '\\$&');
    const regex = new RegExp('[?&]' + name + '(=([^&#]*)|&|#|$)'),
          results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, ' '));
}

/**
 * select 태그에 코드를 기반으로 option 자동 생성 
 * @param codeGroup String 코드그룹명
 * @param target String select 태그의 id
 */
$util.getCodeForSelectTag = function(codeGroup, target){
    $.ajax({
        url: '/common/selectCodeList.do',
        type: 'POST',
        data: {codeGroup : codeGroup},
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8', 
        dataType: 'json',
        success: function (result) {
            $.each(result.OUT_DATA, function(index, item) {
		        var $option = $('<option></option>')
		            .val(item.codeDetail) // value 속성 설정
		            .text(item.codeDetailNm); // 텍스트 설정
		        $('#' + target).append($option);
	    	});
        }
    });	
}

/**
 * 테스트 데이터 byte 계산(MySQL의 utf8mb4 문자 집합 기준)
 * @param str String 테스트 데이터
 * @return Bytes number byte크기
 */
$util.calculateBytes = function(str) {
    let bytes = 0;
    for (let i = 0; i < str.length; i++) {
        const charCode = str.charCodeAt(i);
        if (charCode <= 0x7F) {
            bytes += 1; // ASCII 문자: 1바이트
        } else {
            bytes += 3; // 한글 및 기타 유니코드 문자: 3바이트
        }
    }
    return bytes;
}

/**
 * 실수에 대해 천 단위 구분기호 넣기
 * @param num Number 원래 숫자
 * @return Number 천 단위 구분 기호가 적용된 숫자
 */
$util.numberWithCommas = function(num) {
    var parts = num.toString().split(".");
    parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    return parts.join(".");
}

/**
 * Camel case 문자열을 snake case로 변환
 * @param str String Camel case 문자열
 * @return String snake case 문자열
 */
$util.camelToSnake = function(str) {
    return str.replace(/([A-Z])/g, '_$1').toLowerCase();
}

/**
 * 비밀번호 유효성 검증
 * @param userPw String 비밀번호
 * @param userPwChk String 비밀번호 확인(필수 X)
 * @return String snake case 문자열
 */
$util.pwValidation = function(userPw, userPwChk) {
	var result;	//처리 결과
	
	//조건: 숫자, 영어, 특수문자를 포함하여 8글자 이상 50글자 이하
	var allChk = /^(?=.*\d)(?=.*[A-Za-z])(?=.*[~!@#\$%\^&\*()_\+\-={}\[\]\\:;"'<>,.\/]).{8,20}$/;
	var numChk = /\d/gim;		//숫자 포함
	var enChk = /[A-Za-z]/gim;	//영어 포함
	var speChaChk = /[~!@#\$%\^&\*()_\+\-={}\[\]\\:;"'<>,.\/]/gim;	//특수문자 포함
	var lenChk = /.{8,}$/gim;	//최소 길이
	var maxLenChk = /.{,50}$/gim;	//최대 길이
	
	if(allChk.test(userPw)){ //비밀번호 입력 조건 검증
		if(!userPwChk || userPw == userPwChk){ //비밀번호와 비밀번호 확인이 같은지 검증(userPwChk가 undefinded면 true 출력)
			result = true;
		} else {
			alert('비밀번호와 비밀번호 확인이 일치하지 않습니다.');
			result = false;
		}
	} else {
		if(!numChk.test(userPw)){
			alert('비밀번호에 숫자가 포함되지 않았습니다.');
		} else if(!enChk.test(userPw)) {
			alert('비밀번호에 영어가 포함되지 않았습니다.');
		} else if(!speChaChk.test(userPw)) {
			alert('비밀번호에 특수문자가 포함되지 않았습니다.');
		} else if(!lenChk.test(userPw)) {
			alert('비밀번호의 길이가 8글자보다 길어야합니다.');
		} else if(!maxLenChk.test(userPw)) {
			alert('비밀번호의 최대 길이는 50글자입니다.');
		} 
		result = false;				
	}
	
	return result;	
}