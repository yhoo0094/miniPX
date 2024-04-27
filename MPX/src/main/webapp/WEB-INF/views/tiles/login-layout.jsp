<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"  %>
<% 
/**
 * @화면명: 로그인 레이아웃
 * @작성자: KimSangMin
 * @생성일: 2024. 4. 25. 오전 11:20:13
 * @설명: 로그인 타일즈 적용을 위한 레이아웃 페이지
**/
 %>

<!DOCTYPE html>
<html>
  <head>
  	<tiles:insertAttribute name="commonLib"/>
    <link rel="stylesheet" href="<%=request.getContextPath()%><tiles:getAsString name="css"/>"> <!-- 페이지 개별 css -->   
    <script src="<%=request.getContextPath()%><tiles:getAsString name="js"/>"></script> <!-- 페이지 개별 -->
  </head>

  <body>
  	<div class='mainsection'>
		<tiles:insertAttribute name="body"/>
  	</div>
  	
  	<!-- 로딩 패널 -->
  	<div id="loadingPanel" class="loadingPanel">
  		<div class="loadingImg"></div>
  		<div class="loadingText">L o a d i n g . . .</div>
  	</div>
  </body>
  
</html>