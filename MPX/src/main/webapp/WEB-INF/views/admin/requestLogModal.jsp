<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<% 
/**
 * @화면명: 요청 로그 모달
 * @작성자: KimSangMin
 * @생성일: 2023. 4. 13. 오전 8:29:53
 * @설명: 요청 로그 상세 정보 조회 
**/
 %>
<script src="<%=request.getContextPath()%>/resources/js/admin/requestLogModal.js"></script>

<div id="requestLogModal" class="modal" style="width: 800px; height: 600px; padding: 15px;">
	<table class="papang-table" style="width: 700px; height: 500px;">
		<colgroup>
			<col style="width: 20%">
			<col style="width: 30%">
			<col style="width: 20%">
			<col style="width: 30%">
		</colgroup>
		<tbody>
			<tr class="h50px">
				<th>아이디</th>
				<td>
					<input id="userIdModal" class="form-control" type="text" readonly="readonly"/>
				</td>
				<th>아이피</th>
				<td>
					<input id="ipModal" class="form-control" type="text" readonly="readonly"/>
				</td>
			</tr>
			<tr class="h50px">
				<th>메뉴</th>
				<td>
					<input id="reqTypeNmModal" class="form-control" type="text" readonly="readonly"/>
				</td>			
				<th>발생일시</th>
				<td>
					<input id="reqDttiModal" class="form-control" type="text" readonly="readonly"/>
				</td>
			</tr>			
			<tr class="h50px">
				<th>URI</th>
				<td colspan="3">
					<input id="uriModal" class="form-control" type="text" readonly="readonly"/>
				</td>
			</tr>			
			<tr>
				<th>파라미터</th>
				<td colspan="3">
					<textarea id="paramModal" class="form-control" readonly="readonly" style="height: 400px; resize: none;"></textarea>
				</td>
			</tr>
		</tbody>
	</table>
</div>