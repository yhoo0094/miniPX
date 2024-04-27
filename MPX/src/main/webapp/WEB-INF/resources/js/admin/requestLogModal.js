/**
 * @화면명: 요청 로그 모달
 * @작성자: KimSangMin
 * @생성일: 2023. 4. 13. 오전 8:29:53
 * @설명: 요청 로그 상세 정보 조회 
**/

//요청 로그 모달 열기
function requestLogModalOpen(data){
	//모달 값 입력
	for(var i in data){
		$('#' + i + "Modal").val(data[i]);
	}
	
	//요청 로그 모달 열기
	$('#requestLogModal').modal({
		clickClose: false
	});		
}	

//모달 닫기
function requestLogModalClose(){
	$.modal.close();
}