/**
 * @화면명: 헤더 템플릿
 * @작성자: KimSangMin
 * @생성일: 2023. 11. 23. 오후 6:49:10
 * @설명: 헤더 메뉴에 대한 tiles 템플릿
**/

$(document).ready(function() {
	selectTypeList();	//분류 목록 조회
	
	$('#sessionUserId').text(loginInfo.userId);
});

//상품 검색
function searchItem(event){
	// 폼의 submit 동작을 막습니다.
	 event.preventDefault();
	
	if(window.location.pathname.includes('/home')){
		selectDtlTypeList();	//상세 분류 목록 조회
	} else {
		let navSearchItemNm = $('#navSearchItemNm').val();
		let navSearchType = $('#navSearchType').val();
//		window.location.href = '/home?navSearchItemNm =' + navSearchItemNm + '&navSearchType = ' + navSearchType;
	}
}

//로그아웃
function loginOut() {
	if(!confirm("정말 로그아웃 하시겠습니까?")){
		return;
	}
	
	$com.loadingStart();
    $.ajax({
        url: '/user/logout.do',
        type: 'POST',
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8', 
        dataType: 'json',
        success: function (result) {
			$com.loadingEnd();
	        if (result.RESULT == Constant.RESULT_SUCCESS){
	            location.replace('/');
	        } else {
				alert(result[Constant.OUT_RESULT_MSG])
			}
        },
        error: function(textStatus, jqXHR, thrownError){
			$com.loadingEnd();
		}        
    });		
}	
	
//분류 목록 조회
function selectTypeList() {
	$com.loadingStart();
    $.ajax({
        url: '/mart/selectTypeList.do',
        type: 'POST',
        data: {},
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8', 
        dataType: 'json',
        success: function (result) {
			$com.loadingEnd();
	        if (result.RESULT == Constant.RESULT_SUCCESS){
				for(let item of result.OUT_DATA){
					$('#navSearchType').append('<option value="' + item.itemTypeCode + '">' + item.itemTypeCodeNm + '</option>');
				}
	        } else {
				alert(result[Constant.OUT_RESULT_MSG])
			}
        },
        error: function(textStatus, jqXHR, thrownError){
			$com.loadingEnd();
		}        
    });		
}

	