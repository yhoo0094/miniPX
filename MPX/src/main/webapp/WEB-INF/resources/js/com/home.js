$(() => {
	selectDtlTypeList();//상세 분류 목록 조회
})

//상세 분류 목록 조회
function selectDtlTypeList(){
	$com.loadingStart();
    $.ajax({
        url: '/mart/selectDtlTypeList.do',
        type: 'POST',
        data: {'navSearchType' : $('#navSearchType').val()},
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8', 
        dataType: 'json',
        success: function (result) {
	        if (result.RESULT == Constant.RESULT_SUCCESS){
				$('#schCategory').html('<li class="schCategoryList active">전체</li>');
				for(let item of result.OUT_DATA){
					$('#schCategory').append('<li class="schCategoryList" itemDtlTypeCode="' + item.itemDtlTypeCode + '">' + item.itemDtlTypeCodeNm + '</li>');
				}
				
				setSchCategoryList();	//상세 분류 선택 이벤트
				selectItemList();	//상품 리스트 조회
	        } else {
				alert(result[Constant.OUT_RESULT_MSG])
			}
			$com.loadingEnd();
        },
        error: function(textStatus, jqXHR, thrownError){
			$com.loadingEnd();
		}        
    });			
};

//상세 분류 선택 이벤트
function setSchCategoryList(){
	$('.schCategoryList').on({
		click : function(){
			$('.schCategoryList').removeClass('active');
			$(this).addClass('active');

			selectItemList();
		}
	})
}

//상품 리스트 조회
function selectItemList() {
	$com.loadingStart();	
	
	let formData = new FormData($("#navSearchForm")[0]);
	let formObject = {};
	formData.forEach(function(value, key) {
	    formObject[key] = value;
	});
	formObject.useYn = 'Y';
	let itemDtlTypeCode = $('.schCategoryList.active').attr('itemDtlTypeCode');
	formObject.itemDtlTypeCode = itemDtlTypeCode;

	$.ajax({
        url: '/mart/selectItemList.do',
        type: 'POST',
        data: formObject,
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8', 
        dataType: 'json',
        success: function (result) {
			let totalTag = '';
			for(let item of result.OUT_DATA){
				let tag = '<div class="itemBlock">'
							+ '<div class="itemCard">'
								+ '<div class="itemImgBox itemInfo">'
									+ '<img src="/resources/images/temp/' + item.img + '" class="itemImg">'
								+ '</div>'
								+ '<div class="itemName itemInfo">' + item.itemNm + '(' + item.unit + '개)</div>'
								+ '<div class="itemPrice itemInfo">' + $util.numberWithCommas(item.price * item.unit )+ '원</div>'
								+ '<div class="itemBtn itemInfo">'
									+ '<button type="button" class="btn btn-dark font-bold" onClick="copyNm(this)" data-nm="' + item.itemNm + '">장바구니 담기</button>'
								+ '</div>'
							+ '</div>'
						+ '</div>';
				totalTag += tag;		
			}
			
			if(totalTag == ''){
				totalTag = '<div class="noItemMsgBox">일치하는 상품이 존재하지 않습니다.</div>';
			}
			
			$('#itemBox').html(totalTag);				
			
			$com.loadingEnd();
        },
    error: function(textStatus, jqXHR, thrownError){
		$com.loadingEnd();
	} 
        
    });		
}
