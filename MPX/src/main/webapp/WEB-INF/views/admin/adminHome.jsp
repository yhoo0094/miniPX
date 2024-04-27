<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
/**
 * @화면명: 관리자홈
 * @작성자: 김상민
 * @생성일: 2023. 1. 12. 오후 2:46:04
 * @설명: 관리자가 사이트 환황을 조회할 수 있는 메뉴입니다.
**/
%>

<div>
	<%@include file="/WEB-INF/views/com/menuInfo.jsp" %>
	
	<div>
		<div class="titleDiv" style="margin: 10px 5% 0 5%; font-size: 1.3rem">
			<img src="<%=request.getContextPath()%>/resources/images/title-logo/arrow.png" style="width: 30px; margin-bottom: 4px; margin-right: -10px;">
			접속기록
			<p class="f-right">
				<select id="loginCode" name="loginCode" class="form-select w100 makeSelectTag" onchange="loginLogChart()"></select>
			</p>
		</div>		
		<div id="loginLogChart"></div>
	</div>
	
	<div>
		<div class="titleDiv" style="margin: 10px 5% 0 5%; font-size: 1.3rem">
			<img src="<%=request.getContextPath()%>/resources/images/title-logo/arrow.png" style="width: 30px; margin-bottom: 4px; margin-right: -10px;">
			요청기록
			<p class="f-right">
				<select id="reqTypeCode" name="reqTypeCode" class="form-select w100 makeSelectTag" onchange="requestLogChart()">
					<option value="">전체</option>
				</select>
			</p>
		</div>		
		<div id="requestLogChart"></div>
	</div>	
</div>	