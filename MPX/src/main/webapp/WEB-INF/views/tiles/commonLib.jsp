<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"  %>
<% 
/**
 * @화면명: 공통 라이브러리
 * @작성자: KimSangMin
 * @생성일: 2024. 4. 25. 오전 11:20:13
 * @설명: 전체 템플릿에서 공통으로 사용하는 라이브러리 모음
**/
 %>

<!DOCTYPE html>
	<meta property="og:type" content="website">
	<meta property="og:url" content="/">							<!-- 공유시 이동 url -->
	<meta property="og:title" content="와싸다!(개발)"> 					<!-- 타이틀 -->
	<meta property="og:image" content="./images/kakao_icon2.png"> 	<!-- 미리보기 될 이미지(410x200px 을 권장) -->
	<meta property="og:description" content="와싸다! 초저가 구매대행"> 	<!-- URL 요약 --> 
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>와싸다!(개발)</title>

  	<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
	<link rel="stylesheet" href="<%=request.getContextPath()%>/resources/css/font.css"> <!-- 폰트적용 -->
  	
	<link rel="preconnect" href="https://fonts.googleapis.com"> <!-- google 폰트적용 -->
	<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin> <!-- google 폰트적용 -->
	<link href="https://fonts.googleapis.com/css2?family=Open+Sans:ital,wght@0,300;0,400;0,500;0,600;0,700;0,800;1,300;1,400;1,500;1,600;1,700;1,800&display=swap" rel="stylesheet"> <!-- google 폰트적용 -->
    <link rel="shortcut icon" type="image/x-icon" href="<%=request.getContextPath()%>/resources/images/etc/title_icon.png"> <!-- title 아이콘 변경 -->	
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/jquery-modal/0.9.1/jquery.modal.min.css" />	<!-- jQuery Modal -->
    <link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/v/dt/dt-1.13.1/rr-1.3.3/datatables.min.css"/><!-- datatable -->
    <link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/1.10.16/css/jquery.dataTables.min.css"><!-- datatable -->
    <link rel="stylesheet" type="text/css" href="/resources/lib/datetimepicker/jquery.datetimepicker.css"><!-- datetimepicker -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/jstree/3.2.1/themes/default/style.min.css" /><!-- jstree(트리구조) -->
	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.css" /><!-- 이미지 슬라이드 -->

	<link rel="stylesheet" href="<%=request.getContextPath()%>/resources/css/template.css"> <!-- 공통 css(개별 클래스) -->
 	<link rel="stylesheet" href="<%=request.getContextPath()%>/resources/css/common.css"> <!-- 공통 css(단순 값) -->
	
	<script src="https://code.jquery.com/jquery-3.6.1.min.js"></script>
	
	<!-- datatable 관련 시작 -->
	<script type="text/javascript" src="https://cdn.datatables.net/v/dt/dt-1.13.1/datatables.min.js"></script>
    <script type="text/javascript" src="https://code.jquery.com/jquery-1.12.4.js"></script>
    <script type="text/javascript" src="https://cdn.datatables.net/1.13.1/js/jquery.dataTables.min.js"></script>
    <script type="text/javascript" src="https://cdn.datatables.net/buttons/2.3.2/js/dataTables.buttons.min.js"></script>
    <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jszip/3.1.3/jszip.min.js"></script>
    <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.32/pdfmake.min.js"></script>
    <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.32/vfs_fonts.js"></script>
    <script type="text/javascript" src="https://cdn.datatables.net/buttons/2.3.2/js/buttons.html5.min.js"></script>
    <script type="text/javascript" src="https://cdn.datatables.net/buttons/2.3.2/js/buttons.print.min.js"></script>	 
    <script type="text/javascript" src="https://cdn.datatables.net/select/1.6.2/js/dataTables.select.min.js"></script>	
    <script src="<%=request.getContextPath()%>/resources/lib/dataTables/dataTables.rowReorder.js"></script> <!-- RowReorder 관련 -->
    <script src="<%=request.getContextPath()%>/resources/lib/dataTables/jquery.dataTables.js"></script> <!-- RowReorder 관련 -->
    <script>$.fn.DataTable.ext.pager.numbers_length = 11;	//페이지 버튼 표시할 개수</script>
	<!-- datatable 관련 끝 -->

	<script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script><!-- Google 차트 -->
	<script src="https://kit.fontawesome.com/e2689e2fa2.js"></script> <!-- 아이콘 -->
	<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-modal/0.9.1/jquery.modal.min.js"></script><!-- jQuery Modal -->
	<script src="https://cdnjs.cloudflare.com/ajax/libs/jstree/3.2.1/jstree.min.js"></script><!-- jstree(트리구조) -->
	<script src="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.js"></script><!-- 이미지 슬라이드 -->
    <script src="<%=request.getContextPath()%>/resources/lib/datetimepicker/jquery.datetimepicker.full.min.js"></script><!-- datetimepicker -->
	
	<script src="<%=request.getContextPath()%>/resources/js/com/common.js"></script> <!-- 페이지 공통 -->
	<script src="<%=request.getContextPath()%>/resources/js/com/tiles-layout.js"></script> <!-- 페이지 공통 -->
	<script src="<%=request.getContextPath()%>/resources/js/framework/util.js"></script> <!-- 유틸 -->
	<script src="<%=request.getContextPath()%>/resources/js/framework/dateUtil.js"></script> <!-- 날짜 유틸 -->
	<script src="<%=request.getContextPath()%>/resources/js/framework/excelUtil.js"></script> <!-- 엑셀 유틸 -->
	<script src="<%=request.getContextPath()%>/resources/js/framework/editorUtil.js"></script> <!-- 에디터 -->
	<script src="<%=request.getContextPath()%>/resources/js/framework/constant.js"></script> <!-- js 공통 변수 -->

    <script src="<%=request.getContextPath()%>/resources/js/framework/ckeditor5_inline/build/ckeditor.js"></script> <!-- ck에디터 -->
    <script src="<%=request.getContextPath()%>/resources/js/framework/ckeditor5_inline/build/editor.js"></script><!-- ck에디터 -->
    <script src="<%=request.getContextPath()%>/resources/js/framework/ckeditor5_inline/build/UploadAdapter.js"></script><!-- ck에디터 -->
	
	<script>
		const authGrade = '<%= request.getAttribute("authGrade") %>';
		const contextPath = "<%=request.getContextPath()%>";
		
		//bootstrap cdn 오류 대응
        window.onload = function() {
//         	// CDN에서 CSS 로드 시도
//             var script = document.createElement('script');
//             script.src = 'https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css';
//             // 로드 실패 시 로컬 CSS를 로드하는 함수
//             script.onerror = function() {
//             	var link = document.createElement('link');
//                 link.rel = 'stylesheet';
//                 link.type = 'text/css';
//                 link.href = '/resources/lib/bootstrap-5.1.3-dist/css/bootstrap.min.css';
//                 document.getElementsByTagName('head')[0].appendChild(link);
//             };
//          	// 현재 스크립트에 추가
//             document.getElementsByTagName('head')[0].appendChild(script);
        };		
		
	</script>