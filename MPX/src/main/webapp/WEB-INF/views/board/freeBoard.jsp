<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<% 
/**
 * @화면명: 자유게시판 
 * @작성자: KimSangMin
 * @생성일: 2023. 11. 23. 오후 7:01:06
 * @설명: 자유게시판 목록 조회 화면
**/
 %>

<div>
	<%@include file="/WEB-INF/views/com/menuInfo.jsp" %>
	<div class="papang-search-div lh50px">
		<form id="searchForm" action="#">
			<table class="papang-search-table">
				<colgroup>
					<col width="60rem">	
					<col width="14%">	
					<col width="90rem">
					<col width="14%">
					<col width="90rem">
					<col width="*">	
					<col width="60rem">	
					<col width="14%">	
					<col width="8%">	
				</colgroup>
				<tbody>
				<tr>
					<th>제목:</th>
					<td><input id="title" name="title" class="w100 form-control forSearch" title="제목" type="text" maxlength="20"></td>	
					<th>작성자:</th>
					<td><input id="userId" name="userId" class="w100 form-control forSearch" title="아이디" type="text" maxlength="20"></td>
					<th>게시일:</th>
					<td>
						<input id="dttiStr" name="dttiStr" class="datetimepicker w45 form-control" title="게시일" type="text"> ~ 
						<input id="dttiEnd" name="dttiEnd" class="datetimepicker w45 form-control" title="게시일" type="text">
					</td>						
					<th>분류:</th>
					<td>
						<!-- 01:잡담,02:정보,03:질문 -->
						<select id="boardFreeCode" name="boardFreeCode" class="form-select w100 makeSelectTag" title="분류">
							<option value="">선택하세요</option>
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
	<table id="mainTable" class="display" style="width:100%"></table>
</div>	