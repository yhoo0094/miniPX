<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<% 
/**
 * @화면명: 비밀번호 변경 모달 
 * @작성자: KimSangMin
 * @생성일: 2024. 1. 18. 오전 10:33:18
 * @설명: 비밀번호 변경 모달
**/
 %>

<script src="<%=request.getContextPath()%>/resources/js/user/chngPwModal.js"></script>

<div id="chngPwModal" class="modal" style="width: 400px; height: 200px;">
	<form id="chngPwForm" method="post" style="margin: 10px 0px;">
		<table class="papang-table">
			<colgroup>
				<col style="width: 10rem">
				<col style="width: *">
			</colgroup>
			<tbody>
				<tr>
					<td><b>기존 비밀번호</b></td>
					<td><input id="bfoUserPwModal" type="password" name="bfoUserPwModal" title="기존 비밀번호" maxlength="50" onkeypress="if(event.keyCode == 13){chngUserPw()}" tabindex="1" required="allM2"></td>
				</tr>
				<tr>
					<td><b>변경 비밀번호</b></td>
					<td><input id="chngUserPwModal" type="password" name="userPw" title="변경 비밀번호" maxlength="50" onkeypress="if(event.keyCode == 13){chngUserPw()}" tabindex="2" required="allM2"></td>
				</tr>
				<tr>
					<td><b>변경 비밀번호 확인</b></td>
					<td><input id="chngUserPwModalChk" type="password" name="chngUserPwModalChk" title="변경 비밀번호 확인" maxlength="50" onkeypress="if(event.keyCode == 13){chngUserPw()}" tabindex="3" required="allM2"></td>
				</tr>
				<tr>
					<td colspan="2" style="font-size: 0.8rem; color: red;">
						<b>조건: 숫자, 영어, 특수문자를 포함하여 8글자 이상</b>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<button type="button" class="papang-create-btn papang_btn f-right" style="margin-top: 5px;" onclick="chngUserPw()">변경하기</button>
					</td>
				</tr>
			</tbody>
		</table>
	</form>
</div>
