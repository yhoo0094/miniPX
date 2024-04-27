<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<% 
/**
 * @화면명: 요청 로그 조회 
 * @작성자: KimSangMin
 * @생성일: 2023. 1. 30. 오후 2:25:23
 * @설명: 클라이언트에서 서버로 넘어오는 요청관련 로그 조회
**/
 %>
 
<%@ include file="/WEB-INF/views/admin/requestLogModal.jsp" %><!-- 요청 로그 상세 모달 -->
 
<div>
	<%@include file="/WEB-INF/views/com/menuInfo.jsp" %>
	<div class="papang-search-div">
		<form id="searchForm" action="#">
			<table class="papang-search-table">
				<tr>
					<th>발생일시:</th>
					<td>
						<input id="reqDttiStr" name="reqDttiStr" class="datetimepicker form-control" title="발생일시" type="text" style="width: 150px;"> ~ 
						<input id="reqDttiEnd" class="datetimepicker form-control" name="reqDttiEnd" title="발생일시" type="text" style="width: 150px;">
					</td>	
					<th>아이디:</th>
					<td><input id="userId" class="w100 form-control forSearch" name="userId" title="아이디" type="text" maxlength="20"></td>
					<th>아이피:</th>
					<td><input id="ip" class="w100 form-control forSearch" name="ip" title="아이피" type="text" maxlength="39"></td>
					<td rowspan="3"><button type="button" class="papang-search-btn papang_btn w90" onclick="doSearch()" style="height: 105px;">검색</button></td>
				</tr>
				<tr>
					<th>URI:</th>
					<td><input id="uri" class="w100 form-control forSearch" name="uri" title="URI" type="text" maxlength="100"></td>
					<th>파라미터:</th>
					<td colspan="3"><input id="param" class="w100 form-control forSearch" name="param" title="파라미터" type="text" maxlength="1000"></td>
				</tr>
				<tr>					
					<th>유형:</th>
					<td colspan="5">
						<input id="adminChk" name="reqTypeCode" title="메뉴" type="checkbox" value="admin" checked="checked"><label for="adminChk"> 관리자</label>
						<input id="activeChk" name="reqTypeCode" title="메뉴" type="checkbox" value="active" checked="checked"><label for="activeChk"> 활동</label> 
						<input id="boardChk" name="reqTypeCode" title="메뉴" type="checkbox" value="board" checked="checked"><label for="boardChk"> 게시판</label> 
						<input id="infoChk" name="reqTypeCode" title="메뉴" type="checkbox" value="info" checked="checked"><label for="infoChk"> 정보</label> 
						<input id="marketChk" name="reqTypeCode" title="메뉴" type="checkbox" value="market" checked="checked"><label for="marketChk"> 장터</label> 
						<input id="sitterChk" name="reqTypeCode" title="메뉴" type="checkbox" value="sitter" checked="checked"><label for="sitterChk"> 베이비시터</label> 
						<input id="userChk" name="reqTypeCode" title="메뉴" type="checkbox" value="user" checked="checked"><label for="userChk"> 사용자</label> 
						<input id="etcChk" name="reqTypeCode" title="메뉴" type="checkbox" value="etc" checked="checked"><label for="etcChk"> 기타</label> 
					</td>
				</tr>
			</table>
		</form>
	</div>
	<table id="mainTable" class="display" style="width:100%;"></table>
</div>	