/**
 * @화면명: 권한 관리
 * @작성자: KimSangMin
 * @생성일: 2023. 5. 23. 오후 7:18:19
 * @설명: 관리자가 권한 정보를 조회 및 관리할 수 있는 메뉴입니다.
**/

$(document).ready(function () {
	selectMnuListLv1();
	selectRoleListAuth();
});	

//메뉴 목록 조회(메뉴 select)
function selectMnuListLv1(){
    $.ajax({
        url: '/admin/selectMnuListLv1.do',
        type: 'POST',
        data: '',
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8', 
        dataType: 'json',
        success: function (result) {
	        if (result.RESULT == Constant.RESULT_SUCCESS){
	            var mnuList = result.OUT_DATA;
	            for(var i in mnuList){
					var $option = $('<option value = "' + mnuList[i].url +'">' + mnuList[i].mnuNm +'</option>');
					$('#url').append($option);
				}
	        } else {
				alert(result[Constant.OUT_RESULT_MSG])
			}			
        }
    });	
}

//권한그룹 목록 조회(권한 select)
function selectRoleListAuth(){
    $.ajax({
        url: '/admin/selectRoleListAuth.do',
        type: 'POST',
        data: '',
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8', 
        dataType: 'json',
        success: function (result) {
	        if (result.RESULT == Constant.RESULT_SUCCESS){
	            var roleList = result.OUT_DATA;
	            for(var i in roleList){
					var $option = $('<option value = "' + roleList[i].roleSeq +'">' + roleList[i].roleNm +'</option>');
					$('#roleSeq').append($option);
				}
				makeDataTableServerSide();	//DataTable 만들기(페이지네이션 서버 처리)
	        } else {
				alert(result[Constant.OUT_RESULT_MSG])
			}			
        }
    });	
}

//검색
function doSearch(){
	mainTable.ajax.reload();
};

var columInfo = [
        { title: "메뉴명"		
        , data: "mnuNm"			
        , width: "*"	
        , className: "mnuNm text_align_left"
        , render: function ( data, type, row, meta ) {
			var result;
			if(row.mnuLv == 1){
				result = '<img src="/resources/images/tree-icon/open_folder.png"> <b>' + data + '</b>';
			} else if(row.mnuLv == 2){
				result = '<img src="/resources/images/tree-icon/folder_tree_arrow2.png"> ' + data;
			}
			return result;
		  }
        }
      , { title: "권한등급"	
		, data: "authGrade"		
		, width: "100px"	
		, className: "authGrade text_align_center"	
		, defaultContent: ""
        , render: function ( data, type, row, meta ) {
			 var result = '<a href="#" class="remove-a" onclick="authChng(this)" data-val="-1"><b>&lt;</b> </a>' 
			 			+ '<span name="authGrade" data-rowidx="' + meta.row + '">' + data + '</span>'
			 			+ '<a href="#" class="remove-a" onclick="authChng(this)" data-val="+1"> <b>&gt;</b></a>';
			 return result;
		  }		
		}
      , { title: "권한명"		
		, data: "authNm"			
		, width: "300px"	
		, className: "authNm text_align_center"	
		, defaultContent: ""
        , render: function ( data, type, row, meta ) {
			 var isReadonly = (row.authGrade > 2)?'':'readonly';
			 var isInvisibleInput = (row.authGrade > 2)?'':'invisibleInput';
			 var result = '<input type="text" id="authNm' + meta.row +'" name="authNm" value="' + data +'" ' + isReadonly + ' data-rowidx="' + meta.row + '" '
			 			+ 'class="non-form-control tc form-control ' + isInvisibleInput + '" '
			 			+ 'onchange="setMainTableData(this)">';
			 return result;
		  }	
		}
      , { title: "수정 여부"		
		, data: "isChng"	
		, className: "isChng"			
		, defaultContent: false
		, visible: false
		}		
]

//권한명 변경
function setMainTableData(obj){
	var $tr = mainTable.row(obj.closest('tr'));
	var rowData = $tr.data();
	rowData.authNm = $(obj).val();
	rowData.isChng = true;
}

//권한등급 변동
function authChng(obj){
	var $tr = mainTable.row(obj.closest('tr'));
	var rowData = $tr.data();
	var authGrade = rowData.authGrade + parseInt(obj.dataset.val); 
	if(authGrade >= 0){
		rowData.authGrade = authGrade;
		rowData.isChng = true;
		switch(authGrade){
			case(0):
				rowData.authNm = '권한없음';
				break;
			case(1):
				rowData.authNm = '읽기';
				break;
			case(2):
				rowData.authNm = '쓰기';
				break;
		}
		$tr.data(rowData).draw();
	}	
}

//DataTable 만들기(페이지네이션 서버 처리)
var mainTable;
function makeDataTableServerSide() {
	var param = {};    
    mainTable = $('#mainTable').DataTable({
		ajax: {
			url: '/admin/selectMnuListTable.do',
        	type: 'POST',
			data: function(){
				//검색 조건 object에 담기
			    $.each($('#searchForm').serializeArray(), function() {
			        param[this.name] = this.value;
			    });
				return param;
			},
		},
        columns: columInfo,
	  	createdRow: function( row, data, dataIndex ) {	//행 옵션
			$(row).css('line-height','10px');			//행에 pointer css 적용
	  	},        
        paging: false,
        ordering: false,
        info: false,
        searching: false,
        lengthChange: false,
        autoWidth: false,						//자동 열 너비 조정
  		scrollY: 550,							//테이블 높이
  		scrollCollapse: true,   				//테이블 최대 높이 고정 여부     
        preDrawCallback : function(settings){	//테이블 그리기 전에 동작
			$com.loadingStart();				//로딩패널 보이기
		},
        drawCallback : function(settings){		//테이블 그리기 후에 동작
			$com.loadingEnd();					//로딩패널 숨기기
//        	mainTableData = this.api().rows({page:'current'}).data();	//테이블 데이터 변수에 담기
		},
		language : {
			zeroRecords	: '조회된 결과가 없습니다.',
		},
    });	
}

//권한 수정
function updateAuth(){
	var param = {};
	$.each($('#searchForm').serializeArray(), function() {
        param[this.name] = this.value;
    });
	
	var mainTableData = [];
	var data = mainTable.data();
	for(var i = 0; i < data.length; i++){
		mainTableData.push(data[i])
	}
	param[Constant.IN_DATA_JOSN] = JSON.stringify({mainTableData : mainTableData});
	
	$com.loadingStart();
    $.ajax({
        url: '/admin/updateAuth.do',
        type: 'POST',
        data: param,
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8', 
        dataType: 'json',
        success: function (res, textStatus, jqXHR) {
			$com.loadingEnd();
			if (res.RESULT == Constant.RESULT_SUCCESS){
				mainTable.ajax.reload();
            	alert('저장 완료하였습니다.');
			} else {
				alert(res[Constant.OUT_RESULT_MSG]);
			}	
        },
        error: function(textStatus, jqXHR, thrownError){
			$com.loadingEnd();
			alert(res[Constant.OUT_RESULT_MSG]);
		}
    });		
}

//되돌리기
function resetMainTable(){
	mainTable.ajax.reload();
}
