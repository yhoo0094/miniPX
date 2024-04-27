<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<% 
/**
 * @화면명: 예외 페이지
 * @작성자: KimSangMin
 * @생성일: 2023. 11. 29. 오후 6:47:33
 * @설명: 예외가 발생했을 때 호출되는 페이지
**/
 %>
<div class="error-box">
	<h3 class="redText error-msg">[${errClass}]</h3>
	<h3 class="error-msg">${errorMessage}</h3>
</div>