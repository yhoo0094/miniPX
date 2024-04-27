<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<% 
/**
 * @화면명: 기업장터
 * @작성자: KimSangMin
 * @생성일: 2023. 12. 13. 오전 9:53:47
 * @설명: 기업에서 생산한 물품을 사고 팔 수 있는 메뉴입니다.
**/
 %>
<div>
	<%@include file="/WEB-INF/views/com/menuInfo.jsp" %>
	<div class="papang-search-div lh50px">
		<form id="searchForm" action="#">
			<table class="papang-search-table">
				<colgroup>
					<col width="*">
					<col width="90rem">	
					<col width="20%">	
					<col width="60rem">	
					<col width="14%">
					<col width="60rem">	
					<col width="14%">
					<col width="10%">
				</colgroup>
				<tbody>
				<tr>
					<td></td>
					<th>제품명:</th>
					<td><input id="proName" name="proName" class="w100 form-control forSearch" title="제품명" type="text" maxlength="20"></td>
					<th>분류:</th>
					<td>
						<select id="marketTypeCode" name="marketTypeCode" class="form-select w100 makeSelectTag" title="분류" required="all1">
							<option value="">선택하세요
						</select>
					</td>	
					<th>정렬:</th>
					<td>
						<select id="marketOrderCode" name="marketOrderCode" class="form-select w100 makeSelectTag" title="정렬"></select>
					</td>
					<td class="tc">
						<button type="button" class="papang-search-btn papang_btn lh30px" onclick="doSearch()">검색</button>
					</td>
				</tr>
				</tbody>
			</table>
		</form>
	</div>	
	<div id="gridCardWrap" class="entCardWrap container"></div>
	<div class="table_btn_wrapper">
		<button type="button" id="createBtn" class="papang-create-btn papang_btn lh30px display-n" onclick="window.location.href = '/market/enterprise/enterpriseEdit';">신규</button>
	</div>
</div>	