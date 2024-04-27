<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"  %>

<% 
/**
 * @화면명: 팝업 레이아웃
 * @작성자: KimSangMin
 * @생성일: 2023. 11. 20. 오후 8:58:51
 * @설명: 팝업에 타일즈 적용을 위한 레이아웃 페이지
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
  	<div>
		<div class='popContent'>  	
  			<tiles:insertAttribute name="body"/>
  		</div>
  	</div>
  	
  	<!-- 로딩 패널 -->
  	<div id="loadingPanel" class="loadingPanel">
  		<div class="loadingImg"></div>
  		<div class="loadingText">L o a d i n g . . .</div>
  	</div>
  </body>
  
</html>