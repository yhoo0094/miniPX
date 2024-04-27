/**
 * @작성자: 김상민
 * @생성일: 2022. 11. 8. 오후 6:51:35
 * @설명: date 유틸
**/

var $dateUtil = {};
 
/**
 * 날짜 계산
 * @param strDate 날짜(yyyymmdd)
 * @param year 연도
 * @param month 월
 * @param day 일
 * @return yyyymmdd 계산 결과(yyyymmdd)
 */
$dateUtil.addDate = function(strDate, year, month, day){
	//strDate = strDate.replace(/(\d{4})(\d{2})(\d{2})/g, '$1-$2-$3');
	
	var originYear = strDate.substr(0, 4);
	var originMonth = strDate.substr(4, 2);
	var originDay = strDate.substr(6, 2);
	
	var date = new Date(parseInt(originYear) + parseInt(year)
						, parseInt(originMonth) + parseInt(month) - 1
						, parseInt(originDay) + parseInt(day)
						);

    var year = date.getFullYear();
    var month = ("0" + (1 + date.getMonth())).slice(-2);
    var day = ("0" + date.getDate()).slice(-2);						
	return year + month + day;
};
 
/**
 * 날짜 계산
 * @param strDate 날짜(yyyymmddhhmm)
 * @param year 연도
 * @param month 월
 * @param day 일
 * @param hour 시
 * @param minute 분
 * @return yyyymmdd 계산 결과(yyyymmdd)
 */
$dateUtil.addDate = function(strDate, year, month, day, hour, minute){
	var originYear = strDate.substr(0, 4);
	var originMonth = strDate.substr(4, 2);
	var originDay = strDate.substr(6, 2);	
	var originHour = strDate.substr(8, 2);
	var originMinute = strDate.substr(10, 2);
	
	var date = new Date(parseInt(originYear) + parseInt(year)
						, parseInt(originMonth) + parseInt(month) - 1
						, parseInt(originDay) + parseInt(day)
						, parseInt(originHour) + parseInt(hour)
						, parseInt(originMinute) + parseInt(minute)
						);

    var year = date.getFullYear();
    var month = ("0" + (1 + date.getMonth())).slice(-2);
    var day = ("0" + date.getDate()).slice(-2);	
    var hours = ('0' + date.getHours()).slice(-2); 
    var minutes = ('0' + date.getMinutes()).slice(-2);    					
	return year + month + day + hours + minutes;
};

/**
 * 오늘 날짜(yyyymmdd)
 * @return yyyymmdd 오늘 날짜
 */
$dateUtil.todayYYYYMMDD = function(){
	var date = new Date();
    var year = date.getFullYear();
    var month = ("0" + (1 + date.getMonth())).slice(-2);
    var day = ("0" + date.getDate()).slice(-2);						
	return year + month + day;	
}

/**
 * 오늘 날짜(yyyymmdd)
 * @return yyyymmdd 오늘 날짜
 */
$dateUtil.todayYYYYMMDDHHMM = function(){
	var date = new Date();
    var year = date.getFullYear();
    var month = ("0" + (1 + date.getMonth())).slice(-2);
    var day = ("0" + date.getDate()).slice(-2);	
    var hours = ('0' + date.getHours()).slice(-2); 
    var minutes = ('0' + date.getMinutes()).slice(-2);      					
	return year + month + day + hours + minutes;	
}

/**
 * 오늘 날짜(yyyy-mm-dd)
 * @return yyyy-mm-dd 오늘 날짜
 */
$dateUtil.todayYYYY_MM_DD = function(){
	var date = new Date();
    var year = date.getFullYear();
    var month = ("0" + (1 + date.getMonth())).slice(-2);
    var day = ("0" + date.getDate()).slice(-2);						
	return year + '-' + month + '-' + day;	
}

/**
 * 날짜(yyyy-mm-dd hh:mm)
 * @param paramD 임의 날짜(yyyy-mm-dd) 
 * @return yyyy-mm-dd hh:mm 오늘 날짜
 */
$dateUtil.todayYYYY_MM_DD_HHMM = function(paramD){
	var date = new Date(paramD);
    var year = date.getFullYear();
    var month = ("0" + (1 + date.getMonth())).slice(-2);
    var day = ("0" + date.getDate()).slice(-2);	
    var hours = ('0' + date.getHours()).slice(-2); 
    var minutes = ('0' + date.getMinutes()).slice(-2);
    					
	return year + '-' + month + '-' + day + ' ' + hours + ':' + minutes;	
}

/**
 * 초 -> 분으로 변환
 * @param origSecond 초
 * @return 시간(hh:mm:ss)
 */
$dateUtil.secondToHour = function(origSecond){
	var hour = parseInt(origSecond/3600);
	var minute = parseInt((origSecond%3600)/60);
	var second = parseInt((origSecond%3600)%60);
	var result = ("0" + hour).slice(-2) + ":" + ("0" + minute).slice(-2) + ":" + ("0" + second).slice(-2)
	return result;
}

/**
 * yyyymmdd를 yyyy-mm-dd로 변경
 * @param strDate 날짜(yyyymmdd)
 * @return yyyy-mm-dd
 */
$dateUtil.dateHyphen = function(strDate){
	return strDate.replace(/(\d{4})(\d{2})(\d{2})/g, '$1-$2-$3');
}

/**
 * yyyymmddhhMM를 yyyy-mm-dd hh:MM로 변경
 * @param strDate 날짜(yyyymmddhhMM)
 * @return yyyy-mm-dd hh:MM
 */
$dateUtil.dateHyphenTime = function(strDate){
	return strDate.replace(/(\d{4})(\d{2})(\d{2})(\d{2})(\d{2})/g, '$1-$2-$3 $4:$5');
}

