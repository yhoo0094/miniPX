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
<div>
	<%@include file="/WEB-INF/views/com/menuInfo.jsp" %>
	<div class="papang-content-div">
		<div id="marketItem">
		    <img id="marketThumbnail" src="#" alt="Market Thumbnail"/>
		    <div id="marketContent">
			    <div><span id="proName"></span></div>
			    <div id="starBox"><span id="stars">★★★★☆</span><span id="starCnt">(77)</span></div>
			    <div class="tr"><span id="price"></span>원</div>
			    <hr>
			    <div>
			    	<span class="productInfo">판매자</span>: 
			    	<span id="fstRegNm">홍길동</span>
			    </div>
			    <div>
			    	<span class="productInfo">수량</span>: 
			    	<input id="cnt" name="cnt" type="number" value="1" class="form-control w30" max="100" min="1">
			    </div>
			    <div id="btnBox">
			    	<div>
				    	<button type="button" id="" class="btn papang-search-btn papang_btn" onclick="$util.notYet()">장바구니</button>
				    	<button type="button" id="" class="btn papang-search-btn papang_btn" onclick="$util.notYet()">구매하기</button>
			    	</div>
			    </div>
			</div>
		</div>
		
		<div class="editor-container">
			<div id="cn" title="내용" class="editor form-control"></div>
			<div id="cnByteDisplay" class="byteDisplay"></div>
		</div>
			
		<div class="modal_btn_wrapper">
			<button type="button" id="modifyBtn" class="btn papang-save-btn papang_btn" onclick="modifyBtnClick()" style="display: none;">수정</button>
			<button type="button" id="delBtn" class="btn papang-del-btn papang_btn" onclick="deleteBtnClick()" style="display: none;">삭제</button>
			<button type="button" id="historyBackBtn" class="btn papang-close-btn papang_btn" onclick="moveList()">돌아가기</button>
		</div>
	</div>	
</div>	
