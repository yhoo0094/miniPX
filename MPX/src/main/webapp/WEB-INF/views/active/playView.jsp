<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<% 
/**
 * @화면명: 놀이 조회
 * @작성자: KimSangMin
 * @생성일: 2023. 12. 5. 오후 1:20:45
 * @설명: 놀이 등록/조회/수정 화면 
**/
 %>
 
 <script>
	const param = '<%= request.getAttribute("param") %>';
</script>

<div>
	<%@include file="/WEB-INF/views/com/menuInfo.jsp" %>
	<div class="papang-content-div">
		<form id="viewForm" name="viewForm" method="post" enctype="multipart/form-data">
			<input type="hidden" id="boardSeq" name="boardSeq" value="">
			<input type="hidden" id="boardCode" name="boardCode" value="05"><!-- 게시판구분코드(01:공지사항,02:자유게시판,03:질문게시판,04:지역게시판,05:놀이) -->
			<div class="viewCardWrap">
			    <div class="cardItem">
			        <div class="cardImg">
						<input type="file" id="thumbnail" name="thumbnail" accept="image/*">
					    <img id="thumbnailImg" class="thumbnailImg" title="썸네일을 선택하세요." src="<%=request.getContextPath()%>/resources/images/etc/upload.png" alt="no image"/>
					    <span id="cardImgMsg" title="썸네일을 선택하세요.">썸네일을 선택하세요.</span>						        
			        </div>
			        <div class="cardContent">
			            <div class="cardBtn">
			            	<img src="<%=request.getContextPath()%>/resources/images/etc/search.png" alt="no image"/>
			            	<img src="<%=request.getContextPath()%>/resources/images/etc/like.png" alt="no image"/>	
			            </div>
			            <div class="cardTitle">
			            	<input id="title" name="title" class="form-control" type="text" title="제목" placeholder="제목을 입력하세요." maxlength="100" required="all1"/>
			            </div>
			            <div class="cardIntro">
			            	<textarea id="intro" name="intro" class="form-control" title="소개" placeholder="놀이에 대한 소개를 입력하세요." maxlength="200" required="all1"></textarea>
			            </div>
			        </div>
			    </div>		
		    </div>
		    
		    <div class="editor-container">
				<div id="cn" title="내용" class="editor form-control"></div>
				<div id="cnByteDisplay" class="byteDisplay"></div>
			</div>
			
			<div class="modal_btn_wrapper">
				<button type="button" id="saveBtn" class="btn papang-save-btn papang_btn" onclick="saveBtnClick()" style="display: none;">저장</button>
				<button type="button" id="delBtn" class="btn papang-del-btn papang_btn" onclick="deleteBtnClick()" style="display: none;">삭제</button>
				<button type="button" id="historyBackBtn" class="btn papang-close-btn papang_btn" onclick="moveList()">돌아가기</button>
			</div>
		</form>
	</div>	
</div>	