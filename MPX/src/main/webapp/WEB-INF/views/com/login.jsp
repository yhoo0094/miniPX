<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<% 
/**
 * @화면명: 로그인 
 * @작성자: KimSangMin
 * @생성일: 2024. 4. 25. 오전 10:50:08
 * @설명: 로그인 화면
**/
 %>
<div class="banner_wrap">
	<form id="loginForm" method="post">
		<div class="banner_con fade_in_box">
			<div class="banner_login">
				<div class="login_box">
					<img id="bannerImg"  src="<%=request.getContextPath()%>/resources/images/etc/delivery_man.webp">
				</div>
				<div class="login_box">
					<h1 class="font-bold">LOGIN</h1>
					<div class="user_box">
						<input type="text" id="userId" name="userId" class="form-control" placeholder="아이디" title="아이디" required="all1">
					</div>
					<div class="user_box">
						<input type="password" id="userPw" name="userPw" class="form-control" placeholder="비밀번호" title="비밀번호" required="all1">
					</div>
					<div class="user_box tr">
						<input type="checkbox" id="rememberIdChk" class="form-check-input">
						<label for="saveId">아이디 저장</label>
					</div>
					<div class="tr">
						<button type="button" class="btn btn-dark" onclick="login()">로그인</button>
					</div>
				</div>
			</div>
		</div>
	</form>
</div>	