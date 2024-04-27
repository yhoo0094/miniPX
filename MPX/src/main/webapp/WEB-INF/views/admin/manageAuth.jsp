<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<% 
/**
 * @화면명: 권한 관리
 * @작성자: KimSangMin
 * @생성일: 2023. 5. 23. 오후 7:18:19
 * @설명: 관리자가 권한 정보를 조회 및 관리할 수 있는 메뉴입니다.
**/
%>

<div>
	<%@include file="/WEB-INF/views/com/menuInfo.jsp" %>
	<div class="papang-search-div">
		<form id="searchForm" action="#">
			<table class="papang-search-table">
				<colgroup>
					<col width="100px">	
					<col width="230px">	
					<col width="100px">	
					<col width="*">	
				</colgroup>
				<tbody>
				<tr>
					<th>메뉴:</th>
					<td>
						<select id="url" name="url" class="form-select" style="width: 230px;" onchange="javaScript:doSearch()">
							<option value="">전체
						</select>
					</td>
					<th>권한그룹:</th>
					<td>
						<select id="roleSeq" name="roleSeq" class="form-select" style="width: 230px;" onchange="javaScript:doSearch()"></select>
					</td>	
				</tr>
				</tbody>
			</table>
		</form>
	</div>
	
	<div class="papang-content-div">
		<form id="writeForm" action="#">
			<table id="mainTable" style="width:100%;"></table>
		</form>
		<div class="table_btn_wrapper">
			<button type="button" class="papang-create-btn papang_btn paginate_button" onclick="updateAuth()">저장</button>
			<button type="button" class="papang-close-btn papang_btn paginate_button" onclick="resetMainTable()">되돌리기</button>
		</div>
	</div>
</div>	