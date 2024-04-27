<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<% 
/**
 * @화면명: 공지사항 팝업 
 * @작성자: KimSangMin
 * @생성일: 2023. 11. 20. 오후 8:17:36
 * @설명: 공지사항 조회 팝업
**/
 %>
<div class="noticePop">
	<table class="papang-table">
		<colgroup>
			<col style="width: 100px">
			<col style="width: *">
		</colgroup>
		<tbody>
			<tr>
				<th>제목</th>
				<td><input id="title" name="title" class="form-control" type="text" title="제목" maxlength="100" readonly="readonly"/></td>
			</tr>
			<tr>
				<th>게시기간</th>
				<td>
					<input id="strDt" name="strDt" class="form-control" type="date" title="게시시작일" readonly="readonly"/> 
				  ~ <input id="endDt" name="endDt" class="form-control" type="date" title="게시종료일" readonly="readonly"/>
				</td>
			</tr>				
			<tr>
				<th>내용</th>
				<td>
					<div id="cn" title="내용" class="editor form-control"></div>
				</td>
			</tr>
			<tr>
				<th>첨부파일</th>
				<td>
					<%@include file="/WEB-INF/views/com/fileAttach.jsp" %><!-- 첨부파일 div -->
				</td>
			</tr>
		</tbody>
	</table>
	<div class="tr">
		<button type="button" class="papang_btn papang-transparent-btn" onclick="noticePopX()">하루 동안 표시하지 않음</button>
	</div>
</div>
