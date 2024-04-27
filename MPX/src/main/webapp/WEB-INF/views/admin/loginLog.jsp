<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
/**
 * @화면명: 사용자 접속 기록 조회
 * @작성자: 김상민
 * @생성일: 2023. 1. 12. 오후 2:46:04
 * @설명: 관리자가 사용자 접속 기록을 조회할 수 있는 페이지
**/
%>
<div>
	<%@include file="/WEB-INF/views/com/menuInfo.jsp" %>
	<div class="papang-search-div lh50px">
		<form id="searchForm" action="#">
			<table class="papang-search-table">
				<colgroup>
					<col width="10%">	
					<col width="*">	
					<col width="8%">
					<col width="10%">
					<col width="8%">
					<col width="10%">	
					<col width="8%">	
					<col width="14%">	
					<col width="8%">	
				</colgroup>
				<tbody>
				<tr>
					<th>발생일시:</th>
					<td>
						<input id="dttiStr" name="dttiStr" class="datetimepicker w45 form-control" title="발생일시" type="text"> ~ 
						<input id="dttiEnd" name="dttiEnd" class="datetimepicker w45 form-control" title="발생일시" type="text">
					</td>	
					<th>아이디:</th>
					<td><input id="userId" name="userId" class="w100 form-control forSearch" title="아이디" type="text" maxlength="20"></td>
					<th>아이피:</th>
					<td><input id="ip" name="ip" class="w100 form-control forSearch" title="아이피" type="text" maxlength="39"></td>						
					<th>유형:</th>
					<td>
						<select id="loginCode" name="loginCode" class="form-select w100 makeSelectTag" title="유형">
							<option value="">전체
						</select>
					</td>
					<td class="tc">
						<button type="button" class="papang-search-btn papang_btn lh30px" onclick="doSearch()">검색</button>
					</td>	
				</tr>
				</tbody>
			</table>
		</form>
	</div>	
	<table id="mainTable" class="display" style="width:100%;"></table>
</div>	