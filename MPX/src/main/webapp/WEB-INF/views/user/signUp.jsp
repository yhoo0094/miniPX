<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%
/**
 * @화면명: 회원가입
 * @작성자: 김상민
 * @생성일: 2022. 10. 26. 오전 8:37:17
 * @설명: 회원가입 정보 입력 페이지
**/
%>

<div>
	<%@include file="/WEB-INF/views/com/menuInfo.jsp" %>
	<div class="wrap">
		<form id="signUpForm" method="post">
			<table class="centerTable">
				<colgroup>
					<col style="width: 150px">
					<col style="width: *">
					<col style="width: 300px">
				</colgroup>
				<tbody>
					<tr>
						<td>아이디<span class="required">*</span></td>
						<td><input id="userId" name="userId" type="text" class="papang_input" title="아이디" maxlength="20" required="all1"></td>
						<td>
							<button id="chkUniqIdBtn" type="button" class="papang_btn papang-create-btn" onclick="chkUniqId()">중복확인</button>
							<span id="idUniChkMsg" class="idUniChkBf"></span>
						</td>
					</tr>
					<tr>
						<td>비밀번호<span class="required">*</span></td>
						<td><input id="userPw" name="userPw" type="password" class="papang_input" title="비밀번호" maxlength="50" required="all1"></td>
						<td></td>
					</tr>
					<tr>
						<td>비밀번호 확인<span class="required">*</span></td>
						<td><input id="pwChk" name="userPwChk" type="password" class="papang_input" title="비밀번호 확인" maxlength="50" required="all1"></td>
						<td></td>
					</tr>					
					<tr>
						<td colspan="3">
							<div style="text-align: center;">
								<button type="button" class="papang_btn papang-create-btn" onclick="insertUser()">가입</button>
							</div>						
						</td>
					</tr>									
				</tbody>
			</table>
			

		</form>
	</div>
</div>	

