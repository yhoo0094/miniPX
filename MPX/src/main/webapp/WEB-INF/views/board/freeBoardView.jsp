<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<% 
/**
 * @화면명: 자유게시판 조회
 * @작성자: KimSangMin
 * @생성일: 2023. 11. 23. 오후 7:01:06
 * @설명: 자유게시판 등록/조회/수정 화면 
**/
 %>
 <script>
	const param = '<%= request.getAttribute("param") %>';
</script>
 
<div>
	<%@include file="/WEB-INF/views/com/menuInfo.jsp" %>
	<div class="papang-content-div">
		<form id="freeBoardForm" name="freeBoardForm" method="post" enctype="multipart/form-data">
			<input type="hidden" id="boardSeq" name="boardSeq" value="">
			<input type="hidden" id="boardCode" name="boardCode" value="02"><!-- 게시판구분코드(01:공지사항,02:자유게시판,03:질문게시판,04:지역게시판,05:놀이) -->
			
			<table class="papang-table">
				<colgroup>
					<col style="width: 10%">
					<col style="width: 40%">
					<col style="width: 10%">
					<col style="width: 40%">
				</colgroup>
				<tbody>
					<tr>
						<th>제목</th>
						<td colspan="3"><input id="title" name="title" class="form-control" type="text" title="제목" maxlength="100" required="all1"/></td>
					</tr>
					<tr>
						<th>분류</th>
						<td>
							<!-- 01:잡담,02:정보,03:질문 -->
							<select id="boardFreeCode" name="boardFreeCode" class="form-select w30 makeSelectTag" title="분류" required="all1">
								<option value="">선택하세요
							</select>
						</td>
						<th></th>
						<td></td>
					</tr>
					<tr>
						<th>내용</th>
						<td colspan="3">
							<div class="editor-container">
								<div id="cn" title="내용" class="editor form-control"></div>
								<div id="cnByteDisplay" class="byteDisplay"></div>
							</div>
						</td>
					</tr>
					<tr>
						<th>첨부파일</th>
						<td colspan="3">
							<%@include file="/WEB-INF/views/com/fileAttach.jsp" %><!-- 첨부파일 div -->
						</td>
					</tr>
				</tbody>
			</table>
			
			<div class="modal_btn_wrapper">
				<button type="button" id="saveBtn" class="btn papang-save-btn papang_btn" onclick="saveFreeBoard()" style="display: none;">저장</button>
				<button type="button" id="modifyBtn" class="btn papang-save-btn papang_btn" onclick="saveFreeBoard()" style="display: none;">수정</button>
				<button type="button" id="delBtn" class="btn papang-del-btn papang_btn" onclick="deleteFreeBoard()" style="display: none;">삭제</button>
				<button type="button" id="historyBackBtn" class="btn papang-close-btn papang_btn" onclick="moveList()">돌아가기</button>
			</div>
		</form>
	</div>	
</div>	