<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<% 
/**
 * @화면명: home
 * @작성자: KimSangMin
 * @생성일: 2023. 10. 20. 오후 1:20:18
 * @설명: home으로 리다이렉트
**/
 %>
<html>
<head>
<meta charset="UTF-8">

<meta property="og:type" content="website">
<meta property="og:url" content="<%=request.getContextPath()%>/home">	<!-- 공유시 이동 url -->
<meta property="og:title" content="Hello Papang!"> 						<!-- 타이틀 -->
<meta property="og:image" content="<%=request.getContextPath()%>/resources/images/title-logo/superhero.png"> 	<!-- 미리보기 될 이미지 -->
<meta property="og:description" content="육아하는 아빠를 위한 최고의 파트너."> 	<!-- URL 요약 --> 

<title>Hello PaPang!</title>
</head>
<body>
<script>
	window.location.href ='/home';
</script>
</body>
</html>