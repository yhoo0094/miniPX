<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
/**
 * @화면명: 로그인 정보 없음 오류
 * @작성자: 김상민
 * @생성일: 2023. 1. 11. 오후 1:20:33
 * @설명: 세션에 로그인 정보가 없을 때 나타는 오류 화면
**/
%>
<% 
/**
 * @화면명: 로그인 
 * @작성자: KimSangMin
 * @생성일: 2024. 4. 25. 오전 10:50:08
 * @설명: 로
**/
 %>
<div class="banner_wrap">
	<div class="banner_con fade_in_box">
		<div class="banner_login">
			<div class="login_box">
				<img id="bannerImg"  src="<%=request.getContextPath()%>/resources/images/etc/delivery_man2.png">
			</div>
			<div class="login_box">
				<h1 class="font-bold">LOGIN</h1>
				<div class="user_box">
					<input type="text" id="" class="form-control" placeholder="아이디">
				</div>
				<div class="user_box">
					<input type="text" id="" class="form-control" placeholder="비밀번호">
				</div>
				<div class="user_box tr">
					<input type="checkbox" id="saveId" class="form-check-input">
					<label for="saveId">아이디 저장</label>
				</div>
				<div class="tr">
					<button type="button" class="btn btn-dark" onclick="login()">로그인</button>
				</div>
			</div>
		</div>
	</div>
</div>	