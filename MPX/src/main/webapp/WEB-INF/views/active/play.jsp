<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
 <% 
/**
 * @화면명: 놀이
 * @작성자: KimSangMin
 * @생성일: 2023. 11. 29. 오후 8:12:18
 * @설명: 아이와 함께할 수 있는 놀이 정보를 나누는 메뉴입니다.
**/
 %>

<div>
	<%@include file="/WEB-INF/views/com/menuInfo.jsp" %>
	<div class="papang-search-div lh50px">
		<form id="searchForm" action="#">
			<table class="papang-search-table">
				<colgroup>
					<col width="*">
					<col width="60rem">	
					<col width="20%">	
					<col width="60rem">	
					<col width="14%">	
					<col width="14%">
				</colgroup>
				<tbody>
				<tr>
					<td></td>
					<th>제목:</th>
					<td><input id="title" name="title" class="w100 form-control forSearch" title="제목" type="text" maxlength="20"></td>	
					<th>정렬:</th>
					<td>
						<select id="playOrderCode" name="playOrderCode" class="form-select w100 makeSelectTag" title="정렬"></select>
					</td>
					<td class="tc">
						<button type="button" class="papang-search-btn papang_btn lh30px" onclick="doSearch()">검색</button>
					</td>
				</tr>
				</tbody>
			</table>
		</form>
	</div>	
	<div id="cardWrap" class="cardWrap"></div>
	<div class="table_btn_wrapper">
		<button type="button" id="createBtn" class="papang-create-btn papang_btn lh30px display-n" onclick="mvCreatePlay()">신규</button>
	</div>
</div>	