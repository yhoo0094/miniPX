$(() => {
	setData();
})

//데이터 세팅
function setData() {
	$com.loadingStart();
	
	let totalTag = '';
	
	for(let item of data){
		let tag = '<div class="itemBlock">'
					+ '<div class="itemCard">'
						+ '<div class="itemImgBox itemInfo">'
							+ '<img src="/resources/images/temp/' + item.IMG + '" class="itemImg">'
						+ '</div>'
						+ '<div class="itemName itemInfo">' + item.ITM_NM + '(' + item.UNIT + '개)</div>'
						+ '<div class="itemPrice itemInfo">' + $util.numberWithCommas(item.PRICE * item.UNIT )+ '원</div>'
						+ '<div class="itemBtn itemInfo">'
							+ '<button type="button" class="btn btn-dark font-bold" onClick="copyNm(this)" data-nm="' + item.ITM_NM + '">장바구니 담기</button>'
						+ '</div>'
//						+ '<div class="itemBtn itemInfo">'
//							+ '<img src="/resources/images/etc/coupang2.jpg" class="searchLogoBtn">'
//							+ '<img src="/resources/images/etc/danawa.jpg" class="searchLogoBtn">'
//						+ '</div>'
					+ '</div>'
				+ '</div>';
		totalTag += tag;		
	}
	$('#itemBox').html(totalTag);
	
	setTimeout(() => {
	  $com.loadingEnd();
	}, "500");
}

const data = [
{"ITM_SEQ":"1","TYPE":"기타","TYPE_DTL":"디퓨저","ITM_NM":"캘리포니아센트 스필프루프 코로나도","PRICE":"2970","UNIT":"1","":"2970","IMG":"캘리포니아센트 스필프루프 코로나도.jpg"},
{"ITM_SEQ":"2","TYPE":"기타","TYPE_DTL":"디퓨저","ITM_NM":"젠틀 디퓨저 블랙로즈","PRICE":"6100","UNIT":"1","":"6100","IMG":"젠틀 디퓨저 블랙로즈.jpg"},
{"ITM_SEQ":"3","TYPE":"기타","TYPE_DTL":"디퓨저","ITM_NM":"에멜시 차량용 방향제 170ml 헤블린 오션","PRICE":"8760","UNIT":"1","":"8760","IMG":"에멜시 차량용 방향제 170ml 헤블린 오션.jpg"},
{"ITM_SEQ":"4","TYPE":"기타","TYPE_DTL":"디퓨저","ITM_NM":"그라스 디퓨저 105ml 블랙베리&체리","PRICE":"8990","UNIT":"1","":"8990","IMG":"그라스 디퓨저 105ml 블랙베리&체리.jpg"},
{"ITM_SEQ":"5","TYPE":"기타","TYPE_DTL":"세재","ITM_NM":"다우니 프리미엄 세탁세제 액체형 실내건조","PRICE":"6150","UNIT":"1","":"6150","IMG":"다우니 프리미엄 세탁세제 액체형 실내건조.jpg"},
{"ITM_SEQ":"6","TYPE":"기타","TYPE_DTL":"안마기","ITM_NM":"비트 마사지 건 HMS-12","PRICE":"25280","UNIT":"1","":"25280","IMG":"비트 마사지 건 HMS-12.jpg"},
{"ITM_SEQ":"7","TYPE":"기타","TYPE_DTL":"면도날","ITM_NM":"질레트 프로쉴드 옐로우 면도날 4입","PRICE":"13280","UNIT":"1","":"13280","IMG":"질레트 프로쉴드 옐로우 면도날 4입.jpg"},
{"ITM_SEQ":"8","TYPE":"식품","TYPE_DTL":"과자","ITM_NM":"프링글스양파맛","PRICE":"1250","UNIT":"12","":"15000","IMG":"프링글스양파맛.jpg"},
{"ITM_SEQ":"9","TYPE":"식품","TYPE_DTL":"과자","ITM_NM":"프링글스 리치치즈갈릭","PRICE":"1400","UNIT":"12","":"16800","IMG":"프링글스 리치치즈갈릭.jpg"},
{"ITM_SEQ":"10","TYPE":"식품","TYPE_DTL":"과자","ITM_NM":"프링글스 버터카라멜","PRICE":"1250","UNIT":"12","":"15000","IMG":"프링글스 버터카라멜.jpg"},
{"ITM_SEQ":"11","TYPE":"식품","TYPE_DTL":"과자","ITM_NM":"프링글스 핫앤스파이시","PRICE":"1250","UNIT":"12","":"15000","IMG":"프링글스 핫앤스파이시.jpg"},
{"ITM_SEQ":"12","TYPE":"식품","TYPE_DTL":"과자","ITM_NM":"키커바","PRICE":"450","UNIT":"15","":"6750","IMG":"키커바.png"},
{"ITM_SEQ":"12","TYPE":"식품","TYPE_DTL":"과자","ITM_NM":"코주부 CLASSIC 육포 Mild 130g","PRICE":"7130","UNIT":"1","":"7130","IMG":"코주부 CLASSIC 육포 Mild 130g.jpg"},
{"ITM_SEQ":"12","TYPE":"식품","TYPE_DTL":"과자","ITM_NM":"코주부 CLASSIC 육포 HOT & SPICY 130g","PRICE":"6040","UNIT":"1","":"6040","IMG":"코주부 CLASSIC 육포 HOT & SPICY 130g.jpg"},
{"ITM_SEQ":"13","TYPE":"식품","TYPE_DTL":"음료","ITM_NM":"펩시제로슈거라임 500ml","PRICE":"680","UNIT":"20","":"13600","IMG":"펩시제로슈거라임 500ml.jpg"},
{"ITM_SEQ":"14","TYPE":"식품","TYPE_DTL":"음료","ITM_NM":"핫식스더킹러쉬 355ml","PRICE":"710","UNIT":"24","":"17040","IMG":"핫식스더킹러쉬 355ml.jpg"},
{"ITM_SEQ":"15","TYPE":"식품","TYPE_DTL":"음료","ITM_NM":"핫식스더킹파워 355ml","PRICE":"740","UNIT":"24","":"17760","IMG":"핫식스더킹파워 355ml.jpg"},
{"ITM_SEQ":"16","TYPE":"식품","TYPE_DTL":"음료","ITM_NM":"몬스터에너지 울트라 355ml","PRICE":"1420","UNIT":"24","":"34080","IMG":"몬스터에너지 울트라 355ml.jpg"},
{"ITM_SEQ":"17","TYPE":"식품","TYPE_DTL":"즉석밥","ITM_NM":"햇반 130g","PRICE":"620","UNIT":"36","":"22320","IMG":"햇반 130g.jpg"},
{"ITM_SEQ":"18","TYPE":"식품","TYPE_DTL":"즉석밥","ITM_NM":"햇반 210g","PRICE":"1100","UNIT":"24","":"26400","IMG":"햇반 210g.jpg"},
{"ITM_SEQ":"19","TYPE":"식품","TYPE_DTL":"즉석밥","ITM_NM":"맛있는 오뚜기밥 210g","PRICE":"850","UNIT":"24","":"20400","IMG":"맛있는 오뚜기밥 210g.png"},
{"ITM_SEQ":"20","TYPE":"식품","TYPE_DTL":"즉석밥","ITM_NM":"햇반흑미밥 130g","PRICE":"800","UNIT":"24","":"19200","IMG":"햇반흑미밥 130g.jpg"},
{"ITM_SEQ":"21","TYPE":"식품","TYPE_DTL":"즉석밥","ITM_NM":"햇반 매일잡곡밥 210g","PRICE":"1010","UNIT":"24","":"24240","IMG":"햇반 매일잡곡밥 210g.jpg"},
{"ITM_SEQ":"22","TYPE":"식품","TYPE_DTL":"라면","ITM_NM":"오뚜기 컵누들 매콤한 맛","PRICE":"530","UNIT":"15","":"7950","IMG":"오뚜기 컵누들 매콤한 맛.jpg"},
{"ITM_SEQ":"23","TYPE":"식품","TYPE_DTL":"차","ITM_NM":"쟈뎅 아워티 콤푸차 레몬 10p","PRICE":"2550","UNIT":"1","":"2550","IMG":"쟈뎅 아워티 콤푸차 레몬 10p.jpg"},
{"ITM_SEQ":"24","TYPE":"식품","TYPE_DTL":"차","ITM_NM":"쟈뎅 아워티 콤푸차 베리 10p","PRICE":"2550","UNIT":"1","":"2550","IMG":"쟈뎅 아워티 콤푸차 베리 10p.jpg"},
{"ITM_SEQ":"25","TYPE":"식품","TYPE_DTL":"차","ITM_NM":"쟈뎅 아워티 콤푸차 자몽 10p","PRICE":"2550","UNIT":"1","":"2550","IMG":"쟈뎅 아워티 콤푸차 자몽 10p.png"},
{"ITM_SEQ":"26","TYPE":"식품","TYPE_DTL":"캔디","ITM_NM":"박카스맛젤리","PRICE":"570","UNIT":"8","":"4560","IMG":"박카스맛젤리.jpg"},
{"ITM_SEQ":"27","TYPE":"식품","TYPE_DTL":"캔디","ITM_NM":"이클립스 페퍼민트향 34g","PRICE":"960","UNIT":"8","":"7680","IMG":"이클립스 페퍼민트향 34g.jpg"},
{"ITM_SEQ":"28","TYPE":"식품","TYPE_DTL":"캔디","ITM_NM":"이클립스 피치향 34g","PRICE":"990","UNIT":"8","":"7920","IMG":"이클립스 피치향 34g.jpg"},
{"ITM_SEQ":"29","TYPE":"식품","TYPE_DTL":"영양제","ITM_NM":"맥스빌더","PRICE":"36960","UNIT":"1","":"36960","IMG":"맥스빌더.jpg"},
{"ITM_SEQ":"30","TYPE":"식품","TYPE_DTL":"소세지","ITM_NM":"롯데햄 키스틱 더블치즈 소시지","PRICE":"440","UNIT":"12","":"5280","IMG":"롯데햄 키스틱 더블치즈 소시지.jpg"},
{"ITM_SEQ":"31","TYPE":"식품","TYPE_DTL":"소세지","ITM_NM":"키스틱","PRICE":"660","UNIT":"12","":"7920","IMG":"키스틱.jpg"},
{"ITM_SEQ":"32","TYPE":"식품","TYPE_DTL":"쉐이크","ITM_NM":"퀘이커 마시는오트밀 오트&그레인, 50g","PRICE":"960","UNIT":"10","":"9600","IMG":"퀘이커 마시는오트밀 오트&그레인, 50g.jpg"},
{"ITM_SEQ":"33","TYPE":"식품","TYPE_DTL":"쉐이크","ITM_NM":"퀘이커 마시는 오트밀 오트&우리쌀, 50g","PRICE":"940","UNIT":"10","":"9400","IMG":"퀘이커 마시는 오트밀 오트&우리쌀, 50g.jpg"},
{"ITM_SEQ":"34","TYPE":"식품","TYPE_DTL":"쉐이크","ITM_NM":"퀘이커 마시는 오트밀 단백질, 80g","PRICE":"1390","UNIT":"10","":"13900","IMG":"퀘이커 마시는 오트밀 단백질, 80g.jpg"},
{"ITM_SEQ":"35","TYPE":"식품","TYPE_DTL":"쉐이크","ITM_NM":"퀘이커 마시는 오트밀 찰옥수수, 50g","PRICE":"1080","UNIT":"10","":"10800","IMG":"퀘이커 마시는 오트밀 찰옥수수, 50g.jpg"},
{"ITM_SEQ":"36","TYPE":"화장품","TYPE_DTL":"마스크팩","ITM_NM":"SNP 골드 콜라겐 앰플 마스크","PRICE":"480","UNIT":"10","":"4800","IMG":"SNP 골드 콜라겐 앰플 마스크.jpg"},
{"ITM_SEQ":"37","TYPE":"화장품","TYPE_DTL":"마스크팩","ITM_NM":"메디힐 디엔에이 프로아틴 마스크","PRICE":"700","UNIT":"10","":"7000","IMG":"메디힐 디엔에이 프로아틴 마스크.jpg"},
{"ITM_SEQ":"38","TYPE":"화장품","TYPE_DTL":"선크림","ITM_NM":"닥터지 메디유브이 울트라 선 50ml","PRICE":"4750","UNIT":"1","":"4750","IMG":"닥터지 메디유브이 울트라 선 50ml.jpg"},
{"ITM_SEQ":"39","TYPE":"화장품","TYPE_DTL":"선크림","ITM_NM":"AHC 내추럴 퍼펙션 프레쉬선스틱","PRICE":"8800","UNIT":"1","":"8800","IMG":"AHC 내추럴 퍼펙션 프레쉬선스틱.jpg"},
{"ITM_SEQ":"40","TYPE":"화장품","TYPE_DTL":"아이크림","ITM_NM":"AHC프라이빗 리얼아이크림기프트세트","PRICE":"5560","UNIT":"1","":"5560","IMG":"AHC프라이빗 리얼아이크림기프트세트.jpg"},
{"ITM_SEQ":"41","TYPE":"화장품","TYPE_DTL":"영양크림","ITM_NM":"마녀공장 비피다 바이옴 수분 장벽 강화 크림 기획세트","PRICE":"4710","UNIT":"1","":"4710","IMG":"마녀공장 비피다 바이옴 수분 장벽 강화 크림 기획세트.jpg"},
{"ITM_SEQ":"42","TYPE":"화장품","TYPE_DTL":"영양크림","ITM_NM":"이니스프리 퍼펙트9인텐시브 크림","PRICE":"7900","UNIT":"1","":"7900","IMG":"이니스프리 퍼펙트9인텐시브 크림.jpg"},
{"ITM_SEQ":"43","TYPE":"화장품","TYPE_DTL":"영양크림","ITM_NM":"닥터지 로얄 블랙스네일 크림","PRICE":"10080","UNIT":"1","":"10080","IMG":"닥터지 로얄 블랙스네일 크림.jpg"},
{"ITM_SEQ":"44","TYPE":"화장품","TYPE_DTL":"영양크림","ITM_NM":"아이소이 탄력크림EX","PRICE":"14090","UNIT":"1","":"14090","IMG":"아이소이 탄력크림EX.jpg"},
{"ITM_SEQ":"45","TYPE":"화장품","TYPE_DTL":"영양크림","ITM_NM":"다이아몬드 리페어 퍼펙트 세트","PRICE":"38930","UNIT":"1","":"38930","IMG":"다이아몬드 리페어 퍼펙트 세트.jpg"},
{"ITM_SEQ":"46","TYPE":"화장품","TYPE_DTL":"폼클렌징","ITM_NM":"닥터지 레드 블레미쉬 모이스처 클렌징 폼","PRICE":"4320","UNIT":"1","":"4320","IMG":"닥터지 레드 블레미쉬 모이스처 클렌징 폼.jpg"},
{"ITM_SEQ":"47","TYPE":"화장품","TYPE_DTL":"폼클렌징","ITM_NM":"뉴트로지나 딥클린 아크네 포밍","PRICE":"4390","UNIT":"1","":"4390","IMG":"뉴트로지나 딥클린 아크네 포밍.jpg"},
]