/**
 * @화면명: 헤더 템플릿
 * @작성자: KimSangMin
 * @생성일: 2023. 11. 23. 오후 6:49:10
 * @설명: 헤더 메뉴에 대한 tiles 템플릿
**/

$(document).ready(function() {
//	selectNavMnuList();	//네비게이션 메뉴 그리기
	
	$('#sessionUserId').text(loginInfo.userId);
});

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
	
//네비게이션 메뉴 그리기	
function selectNavMnuList() {
	$com.loadingStart();
    $.ajax({
        url: '/common/selectNavMnuList.do',
        type: 'POST',
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8', 
        dataType: 'json',
        success: function (result) {
			$com.loadingEnd();
	        if (result.RESULT == Constant.RESULT_SUCCESS){
				for(let menu of result.OUT_DATA){
				    if(menu.upperUrl == '#'){	//최상위 메뉴 일 때
				    	//관리자 메뉴는 관리자 권한(3), 게스트 권한(2)이 있을 때만 노출
				    	if(menu.url == 'admin'){
							if(roleSeq != 3 && roleSeq != 2){continue;}
						}
				    
				    	let $ul = $('<ul></ul>');
				    	$ul.attr('id', menu.url);
				    	$ul.addClass('navMnuCol');
				    	
				    	let $li = $('<li></li>');
				    	$li.addClass('navMnuThDiv');
				    	$ul.append($li);
				    	
				    	let $a = $('<a></a>');
				    	$a.attr('href', '/' + menu.topUrl);
				    	$a.addClass('navMnu remove-a navMnu-color');
				    	$li.append($a);
				    	
				    	let $i = $('<i></i>');
				    	$i.addClass('fa ' + menu.mnuIcon);
				    	$a.append($i);
				    	
				    	let $span = $('<span></span>'); 
				    	$span.text(' ' + menu.mnuNm);
				    	$a.append($span);
				    	
						$('#navMnuDiv').append($ul)
					} else {
						let $ul = $('#' + menu.upperUrl);
						
						let $li = $('<li></li>');
				    	$li.addClass('navMnuTdDiv');
				    	$ul.append($li);
				    	
				    	let $a = $('<a></a>');
				    	$a.attr('href', '/' + menu.url);
				    	$a.addClass('navMnu remove-a navMnu-color');
				    	$li.append($a);
				    	
				    	let $span = $('<span></span>'); 
				    	$span.text(menu.mnuNm);
				    	$a.append($span);
					}
					navMnuHover();	//네비게이션 메뉴 호버
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

//네비게이션 메뉴 호버
var isHovered = false;
function navMnuHover() {
	$('.navMnuThDiv').hover(
	    function() {
			$('.navMnuTdDiv').css('display', 'none');
			
	        // 현재 요소의 위치를 기준으로 드롭다운 메뉴의 위치를 설정합니다.
	        var topPosition = 100;
	        $(this).siblings('.navMnuTdDiv').each(function(index) {
	            $(this).css('top', topPosition + (index * 100) + '%'); // 예시로 100%씩 증가
	            $(this).css('display', 'block');
	        });
	    },
	    function() {}
	);
	
	$('.navMnuThDiv, .navMnuTdDiv').hover(
	    function() {
			isHovered = true;
		},
	    function() {
			isHovered = false;
			setTimeout( function(){
				if(!isHovered){
					$('.navMnuTdDiv').css('display', 'none');	
				}	
			}, 0);
	    }		
	);	
}

	