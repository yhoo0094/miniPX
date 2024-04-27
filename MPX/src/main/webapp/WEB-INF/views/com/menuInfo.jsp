<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    
<% 
/**
 * @화면명: 메뉴 경로
 * @작성자: KimSangMin
 * @생성일: 2023. 9. 11. 오후 1:27:30
 * @설명: 메뉴 접속 경로를 보여준다
**/
 %>

    
<script>
	var infoMnuUpperNm = '<%= request.getAttribute("mnuUpperNm") %>';
	var infoMnuNm = '<%= request.getAttribute("mnuNm") %>';
	var infoTopUrl = '<%= request.getAttribute("topUrl") %>';
	var infoUrl = '<%= request.getAttribute("url") %>';
	var infoTxt = '<%= request.getAttribute("info") %>';
	
	$(document).ready(function () {
		$('#mnuTitle').text(infoMnuNm);
		$('#infoMnuUpperNm').text(infoMnuUpperNm);
		$('#infoMnuNm').text(infoMnuNm);
		$('#infoTopUrl').attr('href', '/' + infoTopUrl);
		$('#infoUrl').attr('href', '/' + infoUrl);
		$('#infoTxt').text(infoTxt);
	})
</script>

	
<div class="menuNmDiv">
	<span id='mnuTitle'></span>
</div>
<div class="menuPathDiv">
	<a href="/" class="remove-a"><span>Hello Papang</span></a> &gt
	<a href="/" id='infoTopUrl' class="remove-a"><span id='infoMnuUpperNm'></span></a> &gt
	<a href="/" id='infoUrl' class="remove-a"><span id='infoMnuNm'></span></a> -
	<span id='infoTxt'></span>
</div>
