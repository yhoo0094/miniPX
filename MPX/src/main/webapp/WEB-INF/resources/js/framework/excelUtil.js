/**
 * @작성자: 김상민
 * @생성일: 2023. 1. 13. 오전 9:30:05
 * @설명: excel 유틸
 */
var $excelUtil = {};
 
/**
 * 엑셀 파일 다운로드(데이터 기반)
 * @param defaultFileName 	기본 파일명
 * @param sheetName 		시트명 
 * @param cols 				엑셀 컬럼 정보 - 배열에 오브젝트 담은 형식 ex.[{key: 'index', label: '순번'}, {key: 'name', label: '이름'}]
 * @param data 				엑셀 입력 데이터 - 배열에 오브젝트 담은 형식 ex.[{index: '1', name: '홍길동'}, {index: '2', name: '김길동'}]
 */
$excelUtil.downloadData = function(defaultFileName, sheetName, cols, data){
	var fileName = prompt('저장할 파일명을 입력해주세요.',defaultFileName);
	if(fileName == null){
		return false;	
	}

	//유효성 검증
	if($util.isEmpty(fileName)){alert("파일명을 입력해주세요"); $excelUtil.downloadData(defaultFileName, sheetName, cols, data); return false;}
	var fileNameRule = /^[가-힣a-zA-Z0-9\s]+$/;;	//파일명 명명 규칙(한글, 영문 대소문자, 숫자만 입력 가능)
	if(!fileNameRule.test(fileName)){alert("파일명은 한글, 영문 대소문자, 숫자만 입력 가능합니다."); return false;}
	if($util.isEmpty(sheetName)){alert("시트명을 입력해주세요"); return false;}
	if($util.isEmpty(cols)){alert("출력할 데이터가 존재하지 않습니다."); return false;}
	if($util.isEmpty(data)){alert("출력할 데이터가 존재하지 않습니다."); return false;}
	
	var formTage = document.createElement("form");
	formTage.setAttribute("action", "/excel/downloadData.do");
	formTage.setAttribute("method", "post");
	
	//파라미터 설정
	var inDataParam = {};
	inDataParam[Constant.EXCEL_FILENM]	= fileName;		//파일명
	inDataParam[Constant.EXCEL_SHEETNM]	= sheetName;	//시트명
	inDataParam[Constant.EXCEL_COLUMN]	= cols;			//컬럼 정보
	inDataParam[Constant.EXCEL_DATA] 	= data;			//입력 데이터 
	
	//폼에 파라미터 입력(추후 구현: ajax로 데이터 보내보자)
	var inputTag = document.createElement("input");
	inputTag.setAttribute("type", "hidden");
	inputTag.setAttribute("name", Constant.IN_DATA_JOSN);
	inputTag.value = JSON.stringify(inDataParam);
	formTage.append(inputTag);
	
	//전송
	$('body').append(formTage);
	$(formTage).submit();
	$(formTage).remove();
};

/**
 * 엑셀 파일 다운로드(URL 기반)
 * @param defaultFileName 	기본 파일명
 * @param sheetName 		시트명 
 * @param cols 				엑셀 컬럼 정보 - 배열에 오브젝트 담은 형식 ex.[{key: 'index', label: '순번'}, {key: 'name', label: '이름'}]
 * @param url 				데이터 조회를 위한 url - String
 * @param param 			데이터 조회할 때 조건 - object
 */
$excelUtil.downloadURL = function(defaultFileName, sheetName, cols, url, param){
	var fileName = prompt('저장할 파일명을 입력해주세요.',defaultFileName);
	if(fileName == null){
		return false;	
	}

	//유효성 검증
	if($util.isEmpty(fileName)){alert("파일명을 입력해주세요"); $excelUtil.downloadURL(defaultFileName, sheetName, cols, url, param); return false;}
	var fileNameRule = /^[가-힣a-zA-Z0-9\s]+$/;;	//파일명 명명 규칙(한글, 영문 대소문자, 숫자만 입력 가능)
	if(!fileNameRule.test(fileName)){alert("파일명은 한글, 영문 대소문자, 숫자만 입력 가능합니다."); return false;}
	if($util.isEmpty(sheetName)){alert("시트명을 입력해주세요"); return false;}
	if($util.isEmpty(cols)){alert("출력할 데이터가 존재하지 않습니다."); return false;}
	
	var formTage = document.createElement("form");
	formTage.setAttribute("action", url);
	formTage.setAttribute("method", "post");
	
	//파라미터 설정
	var inDataParam = {};
	inDataParam[Constant.EXCEL_FILENM]	= fileName;		//파일명
	inDataParam[Constant.EXCEL_SHEETNM]	= sheetName;	//시트명
	inDataParam[Constant.EXCEL_COLUMN]	= cols;			//컬럼 정보
	for(var i in param){
		inDataParam[i] = param[i];
	}
	delete inDataParam.strIdx;	//페이징 처리 제거

	//폼에 파라미터 입력(추후 구현: ajax로 데이터 보내보자)
	var inputTag = document.createElement("input");
	inputTag.setAttribute("type", "hidden");
	inputTag.setAttribute("name", Constant.IN_DATA_JOSN);
	inputTag.value = JSON.stringify(inDataParam);
	formTage.append(inputTag);
	
	//전송
	$('body').append(formTage);
	$(formTage).submit();
	$(formTage).remove();
};

/**
 * 엑셀 파일 업로드
 * @param uploadOption 		업로드 관련 설정 정보
 * @param callBackfunc 		콜백함수
 */
$excelUtil.upload = function(excelUploadOptions, callBackfunc){
	var fileInputTag = document.createElement("input");
	$(fileInputTag).attr('type','file');
	$(fileInputTag).attr('name','tempFileObj');
	$(fileInputTag).attr('id','tempFileObj');
	$(fileInputTag).bind('change', function(){
		var file = this.files[0];
		
		var fileName = file.name;
		var extension = fileName.substr(fileName.lastIndexOf(".") + 1);
		if(!$util.isSameText(extension,'xls') && !$util.isSameText(extension,'xlsx')){
			alert(extension + "은 지원하지 않는 확장자입니다.\n xlsx 혹은 xls 확장자를 사용해 주십시오.");
			return false;
		};
		
		var fileSize = file.size;
		if(fileSize > 100 * 1024 * 1024){
			alert("파일 용량은 100MB를 초과할 수 없습니다.");
			return false;
		}
	
		var formData = new FormData();
		formData.append("file", file);
		formData.append(Constant.IN_DATA_JOSN, JSON.stringify({EXCEL_UPLOAD_OPTION:excelUploadOptions}));
		
	    $.ajax({
			type: 'POST',
			enctype: 'multipart/form-data',
	        url: '/excel/upload.do',
	        data: formData,
	        processData: false,
	        contentType: false,
	        cache: false, 	        
	        success: function (result) {
	            callBackfunc(result);
	        }
	    });			
	})
	
	$(fileInputTag).click();
}