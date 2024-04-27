<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
/**
 * @화면명: 공지사항 모달 
 * @작성자: 김상민
 * @생성일: 2022. 11. 15. 오전 8:11:13
 * @설명: 공지사항 조회 및 관리 모달
**/
%>

<script src="<%=request.getContextPath()%>/resources/js/info/noticeModal.js"></script>

<style>
	.ck-editor__editable[role="textbox"] {
	    min-height: 500px;
	}
</style>

<div id="noticeModal" class="modal" style="width: 1000px; height: 800px;">
	<form id="noticeForm" name="noticeForm" method="post" enctype="multipart/form-data">
		<input type="hidden" id="boardSeq" name="boardSeq" value="">
		<input type="hidden" id="boardCode" name="boardCode" value="01">
		
		<table class="papang-table">
			<colgroup>
				<col style="width: 100px">
				<col style="width: 400px">
				<col style="width: 100px">
				<col style="width: *">
			</colgroup>
			<tbody>
				<tr>
					<th>제목</th>
					<td colspan="3"><input id="title" name="title" class="form-control" type="text" title="제목" maxlength="100" required="all1"/></td>
				</tr>
				<tr>
					<th>게시기간</th>
					<td>
						<input id="strDt" name="strDt" class="form-control" type="date" title="게시시작일" required="all1"/> 
					  ~ <input id="endDt" name="endDt" class="form-control" type="date" title="게시종료일" required="all1"/>
					</td>
					<th class="popYn">팝업여부</th>
					<td class="popYn">
						<div id="popYnRead" style="font-weight: bold;"></div>
						<div id="popYnWrite" style="display: none;">
							<label for="popY">Y </label> <input id="popY" name="popYn" type="radio" value="Y">
							<label for="popN">N </label> <input id="popN" name="popYn" type="radio" value="N" checked="checked">
						</div>
					</td>
				</tr>				
				<tr>
					<th>내용</th>
					<td colspan="3">
						<div class="editor-container fixed-container">
							<div id="cn" title="내용" class="editor form-control"></div>
							<div id="cnByteDisplay" class="byteDisplay" style="text-align: right;"></div>
						</div>
					</td>
				</tr>
				<tr>
					<th>첨부파일</th>
					<td colspan="3">
						<!-- 첨부파일 div -->
						<%@include file="/WEB-INF/views/com/fileAttach.jsp" %>
					</td>
				</tr>
			</tbody>
		</table>
		
		<div class="modal_btn_wrapper">
			<button type="button" id="modalSaveBtn" class="btn papang-save-btn papang_btn" onclick="saveNotice()" style="display: none;">저장</button>
			<button type="button" id="modalModifyBtn" class="btn papang-save-btn papang_btn" onclick="setModifyMode()" style="display: none;">수정</button>
			<button type="button" id="modalDelBtn" class="btn papang-del-btn papang_btn" onclick="deleteBoard()" style="display: none;">삭제</button>
			<button type="button" id="modalCloseBtn" class="btn papang-close-btn papang_btn" onclick="closeModal()">닫기</button>
		</div>
	</form>
</div>
