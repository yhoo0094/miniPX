/**
 * @작성자: 김상민
 * @생성일: 2022. 11. 8. 오후 6:51:35
 * @설명: hello papang 프로젝트의 페이지 공통 js(타 프레임워크와 공유 목적 X)
**/
var $com = {};

//로그인 사용자 정보 조회
$com.getUserInfo = function(itemKey){
	return (loginInfo)? loginInfo[itemKey] : loginInfo;
};

//로딩패널 보이기
$com.loadingStart = function(){
	$('#loadingPanel').css('display','flex');	
};

//로딩패널 숨기기
$com.loadingEnd = function(){
	$('#loadingPanel').css('display','none');	
};

//datetimepicker
$com.datetimepicker = function(id, defVal){
	if($util.isEmpty(defVal)){
		defVal = $dateUtil.todayYYYY_MM_DD();
	}
	
	if($util.isEmpty(id)){
		id = 'datetimepicker';
	}	
	
	jQuery('#' + id).datetimepicker({
		format:'Y-m-d H:i',
		value: defVal,
	});
	$.datetimepicker.setLocale('ko');	
};

//datepicker
$com.datepicker = function(id, defVal){
	if($util.isEmpty(defVal)){
		defVal = $dateUtil.todayYYYY_MM_DD();
	}
	
	if($util.isEmpty(id)){
		id = 'datetimepicker';
	}	
	
	jQuery('#' + id).datetimepicker({
		format:'Y-m-d',
		timepicker:false,
		value: defVal,
	});
	$.datetimepicker.setLocale('ko');	
};

