/**
 * @작성자: KimSangMin
 * @생성일: 2023. 4. 20. 오후 8:04:50
 * @설명: 모든 페이지 로딩 후 공통으로 적용되는 js
**/
$(()=>{
	$('.forSearch').on({ 
		keydown: function(e) {
			//엔터 눌렀을 때 검색
			if(e.keyCode == 13 ){doSearch();}
		},
	});
})

