<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
/**
 * @화면명: 파일첨부 Div
 * @작성자: 김상민
 * @생성일: 2022. 11. 17. 오후 7:28:00
 * @설명: 파일첨부 Div
**/
%>

<script src="<%=request.getContextPath()%>/resources/js/com/fileAttach.js"></script>

<div class="fileAttachDiv">
	<div id="fileListDiv" class="fileListDiv form-control" style="line-height: 1; overflow: auto;">
		<label id="fileAttachBtn" class="fileAttachBtn" for="inputFile">
			<span style="line-height: 1.5;">파일찾기</span>
		</label>	
	</div>
</div>
<div>
	<input type="file" id="inputFile" style="display: none;" multiple="multiple" onchange="$fileUtil.addFile(this);">
</div>