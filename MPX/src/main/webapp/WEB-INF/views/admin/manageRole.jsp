<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<% 
/**
 * @화면명: 권한그룹 관리
 * @작성자: KimSangMin
 * @생성일: 2023. 5. 3. 오후 8:18:17
 * @설명:
**/
%>

<div>
	<%@include file="/WEB-INF/views/com/menuInfo.jsp" %>
	<div style="display: inline-block; width: 50%;">
		<div class="titleDiv">
			<img src="<%=request.getContextPath()%>/resources/images/title-logo/arrow.png" style="width: 30px; margin-bottom: 4px; margin-right: -10px;">
			권한그룹 목록
		</div>		
		<table id="mainTable" class="display" style="width:100%;"></table>
		<div class="table_btn_wrapper"><button type="button" class="papang-create-btn papang_btn paginate_button" onclick="$util.notYet()">신규</button></div>
	</div>
	
	<div id="groupUserTableDiv" style="display: inline-block; width: 47%; float: right;">
		<div class="titleDiv">
			<img src="<%=request.getContextPath()%>/resources/images/title-logo/arrow.png" style="width: 30px; margin-bottom: 4px; margin-right: -10px;">
			권한그룹 사용자 목록[<span id="roleNmSpan"></span>]
		</div>	
		<table id="groupUserTable" class="display" style="width:100%;"></table>
	</div>
</div>	