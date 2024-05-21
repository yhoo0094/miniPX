$(() => {
	selectItemSortCode();	//상품 정렬 코드 조회
	setBtn();	//버튼 표시여부 설정
})

//버튼 표시여부 설정
function setBtn(){
	if (loginInfo.roleSeq == 3) {			//권한그룹이 관리자 일 때
		$('#createBtn').css('display', 'inline-block');
	}
}

//상품 정렬 코드 조회
function selectItemSortCode(){
	$com.loadingStart();	

	$.ajax({
        url: '/common/selectCodeList.do',
        type: 'POST',
        data: {codeGroup: 'ITEM_SORT_CODE',},
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8', 
        dataType: 'json',
        success: function (result) {
			let totalTag = '';
			for(let item of result.OUT_DATA){
				let isActive = (totalTag == '')?' active':'';
				let tag = '<li class="' + isActive + '" itemSortCode="' + item.codeDetail + '">' + item.codeDetailNm + '</li>';
				totalTag += tag;		
			}
			
			if(totalTag == ''){
				totalTag = '<div class="noItemMsgBox">정렬 기준이 존재하지 않습니다.</div>';
			}
			
			$('#sorting').html(totalTag);				
			
			$com.loadingEnd();
			
			setSchItemSortCode()	//상품 정렬 코드 선택 이벤트
			selectDtlTypeList();	//상세 분류 목록 조회
        },
	    error: function(textStatus, jqXHR, thrownError){
			$com.loadingEnd();
		} 
    });		
}

//상품 정렬 코드 선택 이벤트
function setSchItemSortCode(){
	$('#sorting > li').on({
		click : function(){
			$('#sorting > li').removeClass('active');
			$(this).addClass('active');
			selectItemList();	//상품 리스트 조회
		}
	})
}

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
			selectItemList();	//상품 리스트 조회
		}
	})
}

//상품 리스트 조회
let currentPage = 1;
const pageSize = 10;
function selectItemList(page = 1) {
    currentPage = page;
	$com.loadingStart();	
	
	let formData = new FormData($("#navSearchForm")[0]);
	let formObject = {};
	formData.forEach(function(value, key) {
	    formObject[key] = value;
	});
	formObject.useYn = 'Y';
	let itemDtlTypeCode = $('.schCategoryList.active').attr('itemDtlTypeCode');
	formObject.itemDtlTypeCode = itemDtlTypeCode;
	
	formObject.itemSortCode = $('#sorting > .active').attr('itemSortCode');
    formObject.page = currentPage;
    formObject.pageSize = pageSize;
    formObject.offset = (currentPage - 1) * pageSize;

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
									+ '<button type="button" class="btn btn-dark font-bold" onClick="insertBasket(this)" data-item-Seq="' + item.itemSeq + '">장바구니 담기</button>'
								+ '</div>'
							+ '</div>'
						+ '</div>';
				totalTag += tag;		
			}
			
			if(totalTag == ''){
				totalTag = '<div class="noItemMsgBox">일치하는 상품이 존재하지 않습니다.</div>';
			}
			
			$('#itemBox').html(totalTag);				
			updatePagination(result.TOTAL_COUNT, pageSize, currentPage);
			
			$com.loadingEnd();
        },
	    error: function(textStatus, jqXHR, thrownError){
			$com.loadingEnd();
		} 
    });		
}

//페이지네이션
function updatePagination(totalCount, pageSize, currentPage) {
    let totalPages = Math.ceil(totalCount / pageSize);
    let paginationHTML = '';

    for (let i = 1; i <= totalPages; i++) {
        paginationHTML += `<button class="${i === currentPage ? 'active' : ''}" onclick="selectItemList(${i})">${i}</button>`;
    }

    $('#pagination').html(paginationHTML);
}

//장바구니 담기
function insertBasket(target){
	let itemSeq = $(target).data('itemSeq');
}

//신규 상품 등록 페이지 이동
function mvCreateItem(){
	location.href = "/mart/item/itemEdit"
}
