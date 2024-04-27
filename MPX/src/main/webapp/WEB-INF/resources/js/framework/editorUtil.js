/**
* @작성자: KimSangMin
* @생성일: 2023. 1. 26. 오후 5:47:15
* @설명: CK에디터 유틸
 */
var $editorUtil = {};

/**
 * 테스트 데이터 byte 계산(MySQL의 utf8mb4 문자 집합 기준)
 * @param edit var editor 인스턴스를 저장할 변수
 * @param id String editor 태그의 id 속성
 */
var editBfo = '';	//에디터 수정 전 데이터
var edit;
$editorUtil.createEditor = function(id) {
	createEditor('#' + id).then( newEditor => {
		edit = watchdog.editor
		
		$('#' + id + 'ByteDisplay').text('0byte / 1MB');
		
		edit.model.document.on('change:data', () => {
			calcByteDisplay(id);
        });
	});
}

function calcByteDisplay(id){
	const curBytes = $util.calculateBytes(edit.getData());
	let result; 
	
	switch(true){
		case(curBytes < 1 * (1024 ** 1)):	//1KB 이하일 때 
			result = $util.numberWithCommas(curBytes) + 'byte';		
			editBfo = edit.getData();			
			break;
		case(curBytes < 1 * (1024 ** 2)):	//1MB 이하일 때 
			result = $util.numberWithCommas((curBytes / (1024 ** 1)).toFixed(2)) + 'KB';	
			editBfo = edit.getData();				
			break;
		default:							//1MB 이상일 때 
			alert('최대 입력 제한을 초과하였습니다.');
			edit.setData(editBfo);
			break;
	}
    $('#' + id + 'ByteDisplay').text(result + ' / 1MB');
}
 
/*
$editorUtil.createEditor = function(){
	
	ClassicEditor.defaultConfig = {
		toolbar: {
			items: [
				'fontSize',
				'fontColor',
				'fontBackgroundColor',
				'fontFamily',
				'alignment',
				'|',
				'bold',
				'italic',
				'underline',
				'|',				
				'highlight',
				'outdent',
				'indent',
				'|',
				'imageUpload',
				'insertTable',
				'undo',
				'redo',
				'findAndReplace',
				
				//'-',
				//'specialCharacters',
				//'imageInsert',
				//'heading',
				//'style'
			],
			shouldNotGroupWhenFull: true
		},
		language: 'ko',
		image: {
			toolbar: [
				'imageTextAlternative',
				'toggleImageCaption',
				'imageStyle:inline',
				'imageStyle:block',
				'imageStyle:side',
				'linkImage'
			]
		},
		table: {
			contentToolbar: [
				'tableColumn',
				'tableRow',
				'mergeTableCells',
				'tableCellProperties',
				'tableProperties'
			]
		}
	};	
	
	ClassicEditor.create( document.querySelector('.editor'),{
					//plugins: [ SimpleUploadAdapter]
			        simpleUpload: {
			            // The URL that the images are uploaded to.
			            uploadUrl: '/common/ckUploadImage.do',
			
			            // Enable the XMLHttpRequest.withCredentials property.
			            withCredentials: true,
			
			            // Headers sent along with the XMLHttpRequest to the upload server.
			            headers: {
			                'X-CSRF-TOKEN': 'CSRF-Token',
			                Authorization: 'Bearer <JSON Web Token>'
			            }
			        }					
				})
				.then( editor => {
					window.editor = editor;
					
					const toolbarElement = editor.ui.view.toolbar.element;
			        editor.on( 'change:isReadOnly', ( evt, propertyName, isReadOnly ) => {
			            if ( isReadOnly ) {
			                toolbarElement.style.display = 'none';
			            } else {
			                toolbarElement.style.display = 'flex';
			            }
			        });					
				} )
				.catch( error => {
					console.error( error );
				} );	
}
*/
