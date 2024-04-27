/**
 * @화면명: 관리자홈
 * @작성자: 김상민
 * @생성일: 2023. 1. 12. 오후 2:46:04
 * @설명: 관리자가 사이트 환황을 조회할 수 있는 메뉴입니다.
**/
      
$(()=>{
	google.charts.load('current', {'packages':['corechart']});
	loginLogChart();	//접속기록 그래프 그리기 실행
	requestLogChart();	//요청기록 그래프 그리기 실행
})

//접속기록 그래프 그리기 실행
function loginLogChart(){
	google.charts.setOnLoadCallback(drawLoginLogChart);  	
}

//요청기록 그래프 그리기 실행
function requestLogChart(){
	google.charts.setOnLoadCallback(drawRequestLogChart);  	
}

//요청기록 그래프 그리기
function drawRequestLogChart(){
	let reqTypeCodeNm = $('#reqTypeCode option:selected').text();
	let chartData = [
		['날짜', reqTypeCodeNm],
	]
	
    let options = {
		width:'100%',
		height: 500,	//차트 전체 높이
		chartArea: {
		    height: '75%', // 이 값을 조정하여 위의 공간을 줄일 수 있습니다.
		    top: 24,       // 이 값을 줄여서 차트 제목을 더 위로 올릴 수 있습니다.
		    left: '10%',   // 여백
    		right: '10%',  // 여백
	    },
		curveType: 'function',			//꺽은선 -> 곡선
		legend: 'none',	//범례 위치
		vAxis : {	//y축 옵션
			viewWindow: {
				min: 0	//y축 최소값
			},
		},
		hAxis: { //x축 옵션
	    	showTextEvery: 2,  // 레이블을 매 2개 포인트마다 표시합니다.
	    	maxTextLines: 1 // 최대 텍스트 줄 수를 1로 제한합니다.
		},
    };

    let chart = new google.visualization.LineChart(document.getElementById('requestLogChart'));
    let reqTypeCode = $('#reqTypeCode').val();
	
	$com.loadingStart();	
	$.ajax({
        url: '/admin/drawRequestLogChart.do',
        type: 'POST',
        data: {reqTypeCode : reqTypeCode},
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8', 
        dataType: 'json',
        success: function (result) {
            if (result.RESULT == Constant.RESULT_SUCCESS){
				for(let i in result.OUT_DATA){
					chartData.push([result.OUT_DATA[i].reqDt, result.OUT_DATA[i].cnt]);
				}
				let arrayToDataTable = google.visualization.arrayToDataTable(chartData);	
				chart.draw(arrayToDataTable, options);
            } else {
				alert(result[Constant.OUT_RESULT_MSG])
			}			
			$com.loadingEnd();
        },
  	    error: function(textStatus, jqXHR, thrownError){
			$com.loadingEnd();
		}       
    });		
}

//접속기록 그래프 그리기
function drawLoginLogChart() {
	let loginCodeNm = $('#loginCode option:selected').text();
	let chartData = [
		['날짜', loginCodeNm],
	]
	
    let options = {
		width:'100%',
		height: 500,	//차트 전체 높이
		chartArea: {
		    height: '75%', // 이 값을 조정하여 위의 공간을 줄일 수 있습니다.
		    top: 24,       // 이 값을 줄여서 차트 제목을 더 위로 올릴 수 있습니다.
		    left: '10%',   // 여백
    		right: '10%',  // 여백
	    },
		curveType: 'function',			//꺽은선 -> 곡선
		legend: 'none',	//범례 위치
		vAxis : {	//y축 옵션
			viewWindow: {
				min: 0	//y축 최소값
			},
		},
		hAxis: { //x축 옵션
	    	showTextEvery: 2,  // 레이블을 매 2개 포인트마다 표시합니다.
	    	maxTextLines: 1 // 최대 텍스트 줄 수를 1로 제한합니다.
		},
    };

    let chart = new google.visualization.LineChart(document.getElementById('loginLogChart'));
    let loginCode = $('#loginCode').val();
	
	$com.loadingStart();	
	$.ajax({
        url: '/admin/drawLoginLogChart.do',
        type: 'POST',
        data: {loginCode : loginCode},
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8', 
        dataType: 'json',
        success: function (result) {
            if (result.RESULT == Constant.RESULT_SUCCESS){
				for(let i in result.OUT_DATA){
					chartData.push([result.OUT_DATA[i].loginDt, result.OUT_DATA[i].cnt]);
				}
				let arrayToDataTable = google.visualization.arrayToDataTable(chartData);	
				chart.draw(arrayToDataTable, options);
            } else {
				alert(result[Constant.OUT_RESULT_MSG])
			}			
			$com.loadingEnd();
        },
  	    error: function(textStatus, jqXHR, thrownError){
			$com.loadingEnd();
		}       
    });	
}
	