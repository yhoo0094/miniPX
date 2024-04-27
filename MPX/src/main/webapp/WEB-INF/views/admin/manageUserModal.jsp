<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<% 
/**
 * @화면명: manageUserModal.jsp
 * @작성자: KimSangMin
 * @생성일: 2024. 1. 16. 오전 10:19:32
 * @설명: 사용자 관리 모달
**/
 %>
 <script src="<%=request.getContextPath()%>/resources/js/admin/manageUserModal.js"></script>
 
 <div id="manageUserModal" class="modal" style="width: 800px; height: 600px; padding: 15px;">
 	<form id="writeForm" action="#">
		<table class="papang-table" style="width: 700px; height: 500px;">
			<colgroup>
				<col style="width: 20%">
				<col style="width: 30%">
				<col style="width: 20%">
				<col style="width: 30%">
			</colgroup>
			<tbody>
				<tr class="h50px">
					<th>아이디</th>
					<td>
						<input id="userIdModal" name="userId" class="form-control gray" title="아이디" type="text" readonly="readonly" required="all1"/>
					</td>
					<th>이름</th>
					<td>
						<input id="userNameModal" name="userName" title="이름" class="form-control" type="text" required="all1"/>
					</td>
				</tr>
				<tr class="h50px">
					<th>권한그룹</th>
					<td>
						<select id="roleSeqModal" name="roleSeq" class="form-select w100 roleSeqTag" title="권한그룹"></select>
					</td>			
					<th>상태</th>
					<td>
						<select id="userStatusCodeModal" name="userStatusCode" class="form-select w100 makeSelectTag" title="상태"></select>
					</td>
				</tr>
				<tr>
					<th>비고</th>
					<td colspan="3">
						<textarea id="rmrkModal" name="rmrkModal" title="비고" class="form-control" style="height: 400px; resize: none;"></textarea>
					</td>
				</tr>
				<tr>
					<td colspan="4">
						<div class="modal_btn_wrapper">
							<button type="button" id="modalPwResetBtn" class="btn papang-close-btn papang_btn" onclick="pwReset()">비밀번호 초기화</button>
							<button type="button" id="modalSaveBtn" class="btn papang-save-btn papang_btn" onclick="saveUserInfo()">저장</button>
						</div>
					</td>
				</tr>
			</tbody>
		</table>
	</form>
</div>