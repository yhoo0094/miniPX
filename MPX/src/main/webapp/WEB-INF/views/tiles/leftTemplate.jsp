<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<% 
/**
 * @화면명: 왼쪽 사이드바
 * @작성자: KimSangMin
 * @생성일: 2023. 11. 30. 오전 8:07:19
 * @설명: 화면 왼쪽의 메뉴 바
**/
 %>
 <script>
 	var url = '<%= request.getAttribute("url") %>';
	var topUrl = '<%= request.getAttribute("topUrl") %>';
	var mnuUpperNm = '<%= request.getAttribute("mnuUpperNm") %>';
	var upperUrl = '<%= request.getAttribute("upperUrl") %>';
	
	$(document).ready(function () {
		$('#sideTopUrl').attr('href', '/' + topUrl);
		$('#sideMnuUpperNm').text(mnuUpperNm);
		
		selectSideBarList();
	});
	
	//사이드바 메뉴 목록 조회
	function selectSideBarList(){
		$.ajax({
	        url: '/common/selectSideBarList.do',
	        type: 'POST',
	        data: {url: upperUrl},
	        contentType: 'application/x-www-form-urlencoded; charset=UTF-8', 
	        dataType: 'json',
	        success: function (result) {
	            if (result.RESULT == Constant.RESULT_SUCCESS){
	            	let menuList = result.OUT_DATA;
	            	menuList.forEach(function(menu) {
	                    var menuItemHtml = '<div><a href="/' + menu.url + '" class="remove-a">' + menu.mnuNm + '</a></div>';
	                    $('#sideMnuListDiv').append(menuItemHtml);
	                });
	            } else {
					alert(Constant.OUT_RESULT_MSG)
				}
	        }
	    });	
	}
</script>
 
<div id="leftCon">
    <div class="sideMnuTop"><a id='sideTopUrl' href="/" class="remove-a"><i class="fa fa-gear" aria-hidden="true"></i> <span id='sideMnuUpperNm'></span></a></div>
    <div id="sideMnuListDiv"></div>
</div>