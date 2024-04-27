<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"  %>

<%
/**
 * @화면명: tiles layout
 * @작성자: 김상민
 * @생성일: 2022. 11. 8. 오후 6:51:35
 * @설명: 타일즈 적용을 위한 레이아웃 페이지
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
  	<div class='wrap'>
  		<tiles:insertAttribute name="header"/>
		<div class='content'>  	
  			<tiles:insertAttribute name="left"/>
	  		<div id="centerCon">
	  			<tiles:insertAttribute name="body"/>
	  		</div>
	  		<tiles:insertAttribute name="right"/>
  		</div>
  		<tiles:insertAttribute name="foot"/>
  	</div>
  	
  	<!-- 로딩 패널 -->
  	<div id="loadingPanel" class="loadingPanel">
  		<div class="loadingImg"></div>
  		<div class="loadingText">L o a d i n g . . .</div>
  	</div>
  </body>
  
</html>