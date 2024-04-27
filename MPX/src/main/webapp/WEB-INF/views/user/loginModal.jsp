<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
/**
 * @화면명: 로그인 모달 
 * @작성자: 김상민
 * @생성일: 2022. 11. 8. 오전 7:53:55
 * @설명: 로그인 정보 입력 모달
**/
%>
<script src="<%=request.getContextPath()%>/resources/js/user/loginModal.js"></script>

<div id="loginModal" class="modal" style="width: 400px; height: 160px;">
	<form id="loginForm" method="post" style="margin: 10px 0px;">
		<table>
			<colgroup>
				<col style="width: 25%">
				<col style="width: *">
				<col style="width: 25%">
			</colgroup>
			<tbody>
				<tr>
					<td><b>아이디</b></td>
					<td><input id="idModal" type="text" name="userId" title="아이디" maxlength="20" onkeypress="if(event.keyCode == 13){login()}" tabindex="1" required="allM1"></td>
					<td rowspan="2"><button type="button" class="papang_btn papang-create-btn" onclick="login()" style="height: 60px;">로그인</button></td>
				</tr>
				<tr>
					<td><b>비밀번호</b></td>
					<td><input id="userPwModal" type="password" name="userPw" title="비밀번호" maxlength="50" onkeypress="if(event.keyCode == 13){login()}" tabindex="2" required="allM1"></td>
				</tr>
				<tr>
					<td colspan="3" style="text-align: right; font-size: 13px; padding: 5px 0 0 0;">				
						<p style="margin-bottom: 0;">
							<input id="rememberIdChk" type="checkbox" title="아이디 기억하기">
							<label for="rememberIdChk" style="position: relative; top: -1.5px;">아이디 저장</label>
						</p>
					</td>
				</tr>					
				<tr>
					<td colspan="3" style="text-align: center; font-size: 13px; padding: 10px 0 0 0;">
						<a href="/user/signUp" class="remove-a" style="text-decoration: underline; text-underline-position:under;">회원가입</a>
						&nbsp;&nbsp;&nbsp;
						<a href="#" class="remove-a" style="text-decoration: underline; text-underline-position:under;">아이디/비밀번호 찾기</a>
					</td>
				</tr>								
			</tbody>
		</table>
	</form>
</div>