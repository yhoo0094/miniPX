/**
 * @화면명: 기업장터
 * @작성자: KimSangMin
 * @생성일: 2023. 12. 13. 오전 9:53:47
 * @설명: 기업에서 생산한 물품을 사고 팔 수 있는 메뉴입니다.
**/

$(()=>{
	selectEnterprise();			//기압징터 조회
})

//검색
function doSearch(){
	selectEnterprise();
};

//기압징터 조회
function selectEnterprise(){
		$com.loadingStart();	
		$('#gridCardWrap').html('');	//기존 데이터 초기화
		
		let colCnt = 4;	//한 줄에 표시하는 상품 개수
		let formData = new FormData($("#searchForm")[0]);
		let formObject = {};
		formData.forEach(function(value, key) {
		    formObject[key] = value;
		});
		formObject.useYn = 'Y';
	
		$.ajax({
	        url: '/enterprise/selectEnterprise.do',
	        type: 'POST',
	        data: formObject,
	        contentType: 'application/x-www-form-urlencoded; charset=UTF-8', 
	        dataType: 'json',
	        success: function (result) {

				//데이터 조회
				let index = 0;
				let gridCardWrap = $('#gridCardWrap');
				let row;
				for (let data of result.OUT_DATA) {
					if(index % colCnt == 0){
						row = $('<div class="row card-deck"></div>');	
					}
					
					let col = $('<div class="col"></div>');
				    let card = $('<div class="card"><div>');
				    card.attr('title', data.proName);
				    card.attr('onClick', 'mvEnterpriseView(' + data.entMarketSeq + ')');
				    
				    let gridCardImg = $('<img src="" class="gridCardImg card-img-top" alt="...">');
				    let src = '/common/images/enterprise/' + data.thumbnail;
				    gridCardImg.attr('src',src);
				    
				    let gridCardBody = $('<div class="gridCardBody"></div>');
				    
				    let proName = $('<p class="card-title"></p>');
				    proName.text(data.proName);
				    
				    let price = $('<p class="card-text"></p>');
				    price.text(data.price.toLocaleString() + '원');
				    
				    card.append(gridCardImg);
				    gridCardBody.append(proName);
				    gridCardBody.append(price);
				    card.append(gridCardBody);
				    col.append(card);
				    row.append(col);
				    
				    if(index % colCnt == colCnt - 1){
						gridCardWrap.append(row);	
					} else if(result.OUT_DATA.length - 1 == index) {
						//마지막 데이터 출력
						gridCardWrap.append(row);	
					}
					index++;
				}
				
				//신규 버튼 노출 여부
				if (authGrade > 1) {			//쓰기 권한이 있을 떄
					$('#createBtn').css('display', 'inline-block');
				}
				
				$com.loadingEnd();
	        },
	    error: function(textStatus, jqXHR, thrownError){
			$com.loadingEnd();
		} 
    });
}

//놀이 조회 화면 이동
function mvEnterpriseView(entMarketSeq){
	window.location.href = '/market/enterprise/enterpriseView?entMarketSeq=' + entMarketSeq;
}

//놀이 신규 등록 화면으로 이동 
function mvCreatePlay(){
	window.location.href = '/active/play/playView';
}	

//썸네일 이미지 높이 조정
function thumbnailImgHeight() {
	let thumbnailImgList = $('.thumbnailImg')
	for(let thumbnailImg of thumbnailImgList){
		let width = $(thumbnailImg).width();
		$(thumbnailImg).height(width);
	}
}