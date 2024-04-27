<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<% 
/**
 * @화면명: 마이페이지 
 * @작성자: KimSangMin
 * @생성일: 2024. 1. 18. 오후 5:06:31
 * @설명: 자신의 정보를 조회 및 관리할 수 있는 메뉴입니다.
**/
 %>

<div>
	<%@include file="/WEB-INF/views/com/menuInfo.jsp" %>
	<div>
		<form id="writeForm" method="post">
			<table class="papang-table" style="margin: 10px 0; width: 100%">
				<colgroup>
					<col width="20%">
					<col width="30%">
					<col width="20%">
					<col width="30%">
				</colgroup>
				<tbody>
					<tr class="h50px">
						<th>아이디</th>
						<td>
							<input id="userId" name="userId" class="form-control gray" title="아이디" type="text" readonly="readonly"/>
						</td>
						<th>이름</th>
						<td>
							<input id="userName" name="userName" title="이름" class="form-control" type="text" required="all1"/>
						</td>
					</tr>
					<tr class="h50px">
						<th>생년월일</th>
						<td>
							<input id="birthDt" name="birthDt" class="form-control" title="생년월일" type="text" required="all1"/>
						</td>
						<th>휴대전화</th>
						<td>
							<input id="phone" name="phone" title="휴대전화" class="form-control" type="text"/>
						</td>
					</tr>
					<tr class="h50px">
						<th>가입일</th>
						<td>
							<input id="signupDt" name="signupDt" class="form-control gray" title="가입일" type="text" readonly="readonly"/>
						</td>
						<th>권한그룹</th>
						<td>
							<input id="roleNm" name="roleNm" class="form-control gray" title="권한그룹" type="text" readonly="readonly"/>
						</td>	
					</tr>
					<tr class="h50px">			
						<td colspan="4">
							<button type="button" class="papang-save-btn papang_btn lh30px f-right" onclick="updateUserInfo()">저장</button>
							<button type="button" class="papang-close-btn papang_btn lh30px f-right" onclick="chngPwModalOpen()">비밀번호 변경</button>
						</td>
					</tr>			
				</tbody>
			</table>
		</form>
	</div>
</div>	

