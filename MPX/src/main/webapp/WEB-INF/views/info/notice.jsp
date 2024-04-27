<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
/**
 * @화면명: 공지사항 목록
 * @작성자: 김상민
 * @생성일: 2022. 11. 10. 오후 6:12:31
 * @설명: 공지사항 목록 조회 페이지
**/
%>

<%@ include file="/WEB-INF/views/info/noticeModal.jsp" %><!-- 공지사항 모달 -->

<div>
	<%@include file="/WEB-INF/views/com/menuInfo.jsp" %>
	<form id="searchForm" action="#">
		<div class="form-check form-switch lh50px" style="display: inline-block; float: right;">
			<label for="periodToggle" class="form-check-label"><b>게시중인 공지</b></label>
			<input type="checkbox" id="periodToggle" name="periodToggle" class="form-check-input" onChange="javascript:doSearch()" checked style="margin: 15px 0px; height: 22px; width: 40px; float: none;">
		</div>
	</form>
	<table id="mainTable" class="display" style="width:100%"></table>
</div>	