<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<% 
/**
 * @화면명: manageUser.jsp
 * @작성자: KimSangMin
 * @생성일: 2024. 1. 15. 오후 1:22:21
 * @설명: 사용자 관리
**/
 %>

<%@ include file="/WEB-INF/views/admin/manageUserModal.jsp" %><!-- 사용자 관리 모달 -->

<div>
	<%@include file="/WEB-INF/views/com/menuInfo.jsp" %>
	<div class="papang-search-div lh50px">
		<form id="searchForm" action="#">
			<table class="papang-search-table">
				<colgroup>
					<col width="90rem">	
					<col width="150rem">	
					<col width="60rem">
					<col width="150rem">
					<col width="100rem">	
					<col width="120rem">	
					<col width="60rem">	
					<col width="120rem">
					<col width="60rem">	
					<col width="120rem">	
					<col width="90rem">	
				</colgroup>
				<tbody>
				<tr>
					<th>아이디:</th>
					<td><input id="userId" name="userId" class="w100 form-control forSearch" title="아이디" type="text" maxlength="20"></td>
					<th>이름:</th>
					<td><input id="userName" name="userName" class="w100 form-control forSearch" title="이름" type="text" maxlength="30"></td>
					<th>권한그룹:</th>
					<td>
						<select id="roleSeq" name="roleSeq" class="form-select w100 roleSeqTag forSearch" title="권한그룹">
							<option value="">전체
						</select>
					</td>						
					<th>상태:</th>
					<td>
						<select id="userStatusCode" name="userStatusCode" class="form-select w100 makeSelectTag forSearch" title="상태">
							<option value="">전체
						</select>
					</td>
					<th>정렬:</th>
					<td>
						<select id="orderCol" name="orderCol" class="form-select w100 forSearch" title="정렬기준">
							<option value="userId">아이디</option>
							<option value="userName">이름</option>
							<option value="fstRegDtti">등록일</option>
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