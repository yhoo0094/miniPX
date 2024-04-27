/**
 * @화면명: (공통) 파일첨부
 * @작성자: 김상민
 * @생성일: 2022. 11. 17. 오후 7:28:00
 * @설명: (공통) 파일첨부
**/
var $fileUtil = {};

var fileNo = 0;
var filesArr = new Array();
var delFiles = new Array();

//첨부파일 추가
$fileUtil.addFile = function addFile(obj){
    var maxFileCnt = 5;   // 첨부파일 최대 개수
    var attFileCnt = document.querySelectorAll('.filebox').length;    // 기존 추가된 첨부파일 개수
    var curFileCnt = obj.files.length;  // 현재 선택된 첨부파일 개수
 
    // 첨부파일 개수 확인
    if (curFileCnt > maxFileCnt - attFileCnt) {
        alert("첨부파일은 " + maxFileCnt + "개 까지 첨부 가능합니다.");
        return;
    }    
    
    for (const file of obj.files) {
	    // 첨부파일 검증
	    if ($fileUtil.validation(file)) {
	        // 파일 배열에 담기
	        var reader = new FileReader();
	        reader.onload = function () {
	            filesArr.push(file);
	        };
	        reader.readAsDataURL(file);
	
	        // 목록 추가
	        let htmlData = '';
	        htmlData += '<div id="file' + fileNo + '" class="filebox">';
	        htmlData += '   <p class="attachedFile">' + file.name + ' (' + $fileUtil.formatBytes(file.size) + ')</p>';
	        htmlData += '   <a class="deleteFileBtn" onclick="$fileUtil.deleteFile(' + fileNo + ');"><i class="far fa-minus-square"></i></a>';
	        htmlData += '</div>';
	        $('.fileListDiv').append(htmlData);
	        fileNo++;
	    } else {
	        continue;
	    }
    }
    //초기화
    document.querySelector("input[type=file]").value = "";   
}

//첨부파일 삭제
$fileUtil.deleteFile = function deleteFile(num) {
    document.querySelector("#file" + num).remove();
    filesArr[num].is_delete = true;
}

//첨부파일 초기화
$fileUtil.resetFile = function resetFile() {
	fileNo = 0;
	filesArr = new Array();	
	$('.filebox').remove();
}

//첨부파일 조회
$fileUtil.selectFile = function selectFile(data) {
	$fileUtil.resetFile();	//첨부파일 초기화
	
    $.ajax({
        url: '/file/selectFile',
        type: 'POST',
        data: {'boardSeq': data.boardSeq},
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8', 
        dataType: 'json',
        success: function (result) {
            //권한에 따라 수정 가능 여부 추후 구현
            
            //파일 리스트 그리기
            for(var i in result.list){
				var fileData = result.list[i];
				
				fileData.is_delete = false;	//파일삭제 여부
				filesArr.push(fileData);
				
		        // 목록 추가
		        let htmlData = '';
		        htmlData += '<div id="file' + fileNo + '" class="filebox">';
		        htmlData += '   <p class="attachedFile" onclick="$fileUtil.downloadFile(' + fileData.boardSeq + ',' + fileData.atcfileNum + ');">' + fileData.atcFileNm + ' (' + $fileUtil.formatBytes(fileData.atcFileCapaVal) + ')</p>';
		        htmlData += '   <a class="deleteFileBtn" onclick="$fileUtil.deleteFile(' + fileNo + ');"><i class="far fa-minus-square"></i></a>';
		        htmlData += '</div>';
		        $('.fileListDiv').append(htmlData);
		        fileNo++;					
			}
			
			if(data.readonly == 'Y'){
				$('.deleteFileBtn').css('display','none');			//파일 제거 버튼
			} else {
				$('#fileAttachBtn').css('display','inline-block');			//파일찾기 버튼
			}
        }
    });		
}

//첨부파일을 포함한 페이지 저장
$fileUtil.saveFile = function saveFile(url, formData){
	return new Promise(function(resolve, reject){
	    for(var i in filesArr){
			formData.append("files", filesArr[i]);
			
			//삭제된 파일 처리(undefinded면 false로 처리됨)
			if(filesArr[i].is_delete){
				delFiles.push(filesArr[i].atcfileNum);
			};
		}
		formData.append("delFiles", delFiles);
	    
	    $.ajax({
			type: 'POST',
			enctype: 'multipart/form-data',
	        url: url,
	        data: formData,
	        processData: false,
	        contentType: false,
	        cache: false, 
	        success: function (result) {
				resolve(result);
	        }
	    });   
    })
}

//첨부파일 다운로드
$fileUtil.downloadFile = function downloadFile(boardSeq, atcfileNum){
    $("[id='downloadForm']").remove();	//이미 생성된 form이 있으면 제거
    
    //form 생성
    var form = document.createElement("form");
    form.id = 'downloadForm';
    form.action = '/file/downloadFile';
    form.method = 'post';
    document.body.appendChild(form);
    
    //input 생성
    var boardSeqInput = document.createElement("input");
    boardSeqInput.type = "text";
    boardSeqInput.name = "boardSeq";
    boardSeqInput.value = boardSeq;
    form.appendChild(boardSeqInput);
 
    var atcfileNumInput = document.createElement("input");
    atcfileNumInput.type = "text";
    atcfileNumInput.name = "atcfileNum";
    atcfileNumInput.value = atcfileNum;
    form.appendChild(atcfileNumInput);
    form.submit();
    
    $("[id='downloadForm']").remove();	
}

//첨부가능한 파일 타입
const fileTypes = [
	'application/pdf'
  , 'image/gif'
  , 'image/jpeg'
  , 'image/png'
  , 'image/bmp'
  , 'image/tif'
  , 'application/haansofthwp'
  , 'application/x-hwp'
  , 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
  , 'text/plain'
  , 'text/xml'
];

/* 첨부파일 검증 */
$fileUtil.validation = function validation(obj){
    if (obj.name.length > 100) {
        alert(obj.name + " 파일은 파일명이 100자를 초과하여 제외되었습니다.");
        return false;
    } else if (obj.size > (100 * 1024 * 1024)) {
        alert(obj.name + " 파일은 100MB를 초과하여 제외되었습니다.");
        return false;
    } else if (obj.name.lastIndexOf('.') == -1) {
        alert(obj.name + " 파일은 확장자가 없어 제외되었습니다.");
        return false;
    } else if (!fileTypes.includes(obj.type)) {
        alert(obj.type + " 확장자는 불가능하여 제외되었습니다.");
        return false;
    } else {
        return true;
    }
}

//첨부파일 사이즈 단위 변경
$fileUtil.formatBytes = function formatBytes(bytes){
	var sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
	if(bytes == 0) return '0 Bytes';
	var i = parseInt(Math.floor(Math.log(bytes) / Math.log(1024)));
	return Math.round(bytes / Math.pow(1024, i), 2) + ' ' + sizes[i];
}