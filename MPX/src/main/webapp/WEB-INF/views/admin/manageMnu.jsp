<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<% 
/**
 * @화면명: manageMnu.jsp
 * @작성자: KimSangMin
 * @생성일: 2023. 5. 13. 오전 11:47:10
 * @설명: 메뉴 관리
**/
 %>

<div>
	<%@include file="/WEB-INF/views/com/menuInfo.jsp" %>
	<div style="display: inline-block; width: 30%;">
		<div class="titleDiv">
			<img src="<%=request.getContextPath()%>/resources/images/title-logo/arrow.png" style="width: 30px; margin-bottom: 4px; margin-right: -10px;">
			메뉴목록
		</div>
		<div class="papang-info-div over-auto" style="height:630px;"><div id="jstree"></div></div>
	</div>
	
	<div id="mnuInfoTableDiv" style="display: inline-block; width: 67%; float: right;">
		<div class="titleDiv">
			<img src="<%=request.getContextPath()%>/resources/images/title-logo/arrow.png" style="width: 30px; margin-bottom: 4px; margin-right: -10px;">
			메뉴정보
		</div>
		<div class="papang-info-div" style="height:630px; vertical-align: middle; line-height: 50px;">
			<form id="writeForm" action="#">		
				<table id="mnuInfoTable" class="display papang-info-table" style="width:100%;">
					<colgroup>
						<col width="150px">	
						<col width="*">	
						<col width="150px">	
						<col width="*">	
					</colgroup>		
					<tr>
						<th>메뉴명</th>
						<td>
							<input type="text" id="mnuNm" name="mnuNm" class="form-control" title="메뉴명" required="all1">
						</td>	
						<th>메뉴경로</th>
						<td>
							<input type="text" id="url" name="url" class="form-control gray" title="메뉴경로" required="all1" readonly="readonly">
						</td>				
					</tr>		
					<tr>
						<th>최상위메뉴경로</th>
						<td>
							<input type="text" id="topUrl" name="topUrl" class="form-control gray" title="최상위메뉴경로" readonly="readonly">
						</td>	
						<th>상위메뉴경로</th>
						<td>
							<input type="text" id="upperUrl" name="upperUrl" class="form-control gray" title="상위메뉴경로" readonly="readonly">
						</td>				
					</tr>
					<tr>
						<th>메뉴노출여부</th>
						<td class="tc">
							<label for="openY">노출 </label> <input type="radio" value="Y" id="openY" name="openYn">
							&nbsp;&nbsp;&nbsp;&nbsp;
							<label for="openN">숨기기 </label> <input type="radio" value="N" id="openN" name="openYn">
						</td>	
						<th>권한검사여부</th>
						<td class="tc">
							<label for="authY">Y </label> <input type="radio" value="Y" id="authY" name="authYn" title="검사필요">
							&nbsp;&nbsp;&nbsp;&nbsp;
							<label for="authN">N </label> <input type="radio" value="N" id="authN" name="authYn" title="검사불필요">
						</td>
					</tr>	
					<tr>
						<th>메뉴레벨</th>
						<td>
							<input type="text" id="mnuLv" name="mnuLv" class="form-control gray" title="메뉴레벨" readonly="readonly">
						</td>	
						<th>표시순서</th>
						<td>
							<input type="text" id="mnuOrder" name="mnuOrder" class="form-control gray" title="표시순서" readonly="readonly">
						</td>				
					</tr>					
					<tr>
						<th>메뉴정보</th>
						<td colspan="3">
							<input type="text" id="info" name="info" class="form-control" title="메뉴정보" required="all1">
						</td>
					</tr>	
					<tr>
						<th>비고</th>
						<td colspan="3" style="line-height: initial;">
							<textarea id="rmrk" name="rmrk" class="form-control" title="비고" style="height: 300px; resize: none;"></textarea>
						</td>				
					</tr>
					<tr>
						<td colspan="4" style="text-align: right; line-height: normal;">
							<button type="button" class="papang-create-btn papang_btn paginate_button" onclick="updateMnu()">저장</button>
						</td>				
					</tr>																			
				</table>
			</form>
		</div>		
	</div>
</div>	