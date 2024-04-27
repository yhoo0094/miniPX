/**
 * @화면명: 놀이
 * @작성자: KimSangMin
 * @생성일: 2023. 11. 29. 오후 8:12:18
 * @설명: 아이와 함께할 수 있는 놀이 정보를 나누는 메뉴입니다.
**/

$(()=>{
	selectPlay();			//놀이 조회
})

//검색
function doSearch(){
	selectPlay();
};

//놀이 조회
function selectPlay(){
		$com.loadingStart();	
		$('#cardWrap').html('');	//기존 데이터 초기화
		
		let formData = new FormData($("#searchForm")[0]);
		let formObject = {};
		formData.forEach(function(value, key) {
		    formObject[key] = value;
		});
		formObject.useYn = 'Y';
	
		$.ajax({
	        url: '/play/selectPlay.do',
	        type: 'POST',
	        data: formObject,
	        contentType: 'application/x-www-form-urlencoded; charset=UTF-8', 
	        dataType: 'json',
	        success: function (result) {

				//데이터 조회
				for (let data of result.OUT_DATA) {
				    let cardWrap = $('#cardWrap');
				    
				    let cardItem = $('<div class="cardItem"></div>');
				    
				    let cardImg = $('<div class="cardImg"></div>')
				    let thumbnailImg = $('<img class="thumbnailImg" alt="no image"/>');
				    let src = '/common/getImage.do?boardSeq=' + data.boardSeq;
				    thumbnailImg.attr('src',src);
				    thumbnailImg.attr('onClick','mvPlayView(' + data.boardSeq +')');
				    cardImg.append(thumbnailImg);
				    
				    let cardContent = $('<div class="cardContent"></div>');
				    
				    let cardBtn = $('<div class="cardBtn"></div>');
				    let searchBtn = $('<img src="/resources/images/etc/search.png" alt="no image" class="cardBtnImg"/>');
				    searchBtn.attr('onClick','mvPlayView(' + data.boardSeq +')');
		            let likeBtn = $('<img src="/resources/images/etc/like.png" alt="no image" class="cardBtnImg"/>');	
		            let cardTitle = $('<div class="cardTitle"></div>');
		            cardTitle.text(data.title);
		            let cardIntro = $('<div class="cardIntro"></div>');
		            let intro = $util.XssReverse(data.intro);
		            cardIntro.text(intro);
		            
				    cardBtn.append(searchBtn);
		            cardBtn.append(likeBtn);
		            cardContent.append(cardBtn);
		            cardContent.append(cardTitle);
		            cardContent.append(cardIntro);
		            
		            cardItem.append(cardImg);
				    cardItem.append(cardContent);
				    
				    cardWrap.append(cardItem);
				}
				thumbnailImgHeight();	//썸네일 이미지 높이 조정
				
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
function mvPlayView(boardSeq){
	 // form 태그 생성
    var $form = $('<form></form>')
        .attr("action", "/active/play/playView")
        .attr("method", "post");

    // input 태그 생성 및 form 태그에 추가
    $('<input>').attr({
        type: "hidden",
        name: "param",
        value: boardSeq
    }).appendTo($form);

    // form 태그를 body에 추가하고 제출
    $form.appendTo('body').submit();
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