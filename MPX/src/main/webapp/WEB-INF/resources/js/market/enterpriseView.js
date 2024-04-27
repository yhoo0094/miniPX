/**
 * @화면명: 자유게시판 조회
 * @작성자: KimSangMin
 * @생성일: 2023. 11. 23. 오후 7:01:06
 * @설명: 자유게시판 등록/조회/수정 화면 
**/

const curUrl = new URL(window.location.href);
const curParams = new URLSearchParams(curUrl.search);
const entMarketSeq = curParams.get('entMarketSeq');

$(()=>{
	$editorUtil.createEditor('cn');
	
	pageInit();	//권한 및 조회/등록 목적에 따른 버튼, 내용 표시 여부 조정
})

//썸네일 이미지 높이 조정
function thumbnailImgHeight() {
	let width = $('#marketThumbnail').width() - 100;
	$('#marketThumbnail').height(width);
}

//권한 및 조회/등록 목적에 따른 버튼, 내용 표시 여부 조정 
function pageInit(){
	$.ajax({
        url: '/enterprise/selectEnterprise.do',
        type: 'POST',
        data: {entMarketSeq: entMarketSeq},
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8', 
        dataType: 'json',
        success: function (result) {
			let data = result.OUT_DATA[0];
			data = $util.XssReverseObj(data);	//XSS방지를 위한 문자열 변환 되돌리기

			//데이터 조회
			for (let key in data) {
			    let tag = document.getElementById(key);
			    if (tag) {
					if(['price'].includes(key)){
						data[key] = data[key].toLocaleString();
					}
			        $(tag).text(data[key])
			    }
			}
			edit.setData(data.cn); //에디터 조회
			$('#marketThumbnail').attr('src', '/common/images/enterprise/' + data.thumbnail)	//썸네일 이미지
			
			//재고 수량 최대 값
			let cntMax = data.cnt
			$('#cnt').attr('max', cntMax);
			
			if($com.getUserInfo('userId') == data.fstRegId){	
				//작성자ID와 조회ID가 같을 경우 -> 수정, 삭제 버튼 보이기
				$('#modifyBtn').css('display','inline-block');
				$('#delBtn').css('display','inline-block');
			} 
			
			edit.enableReadOnlyMode('Y');										//에디터 readonly 적용
			
			thumbnailImgHeight();	//썸네일 이미지 높이 조정
        }
    });
}

//삭제
function deleteBtnClick(){
	if(!confirm('정말로 삭제하시겠습니까?')){return false;}
	
	$.ajax({
        url: '/enterprise/deleteEnterprise.do',
        type: 'POST',
        data: {entMarketSeq: entMarketSeq},
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8', 
        dataType: 'json',
        success: function (result) {
			window.location.href = contextPath + '/market/enterprise';
        }
    });	
}

//수정 페이지 이동
function modifyBtnClick(){
	window.location.href = '/market/enterprise/enterpriseEdit?entMarketSeq=' + entMarketSeq;
}

//돌아가기
function moveList(){
	window.location.href = '/market/enterprise';
}