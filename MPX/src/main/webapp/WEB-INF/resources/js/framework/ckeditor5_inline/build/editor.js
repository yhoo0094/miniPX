/**
* @파일명: editor.js
* @작성자: KimSangMin
* @생성일: 2023. 1. 26. 오후 5:52:16
* @설명: CK에디터 설정 파일
*/


$(()=>{
	//에디터 인스턴스 생성 및 전역 변수 할당
	const watchdog = new CKSource.EditorWatchdog();
	window.watchdog = watchdog;
	
	//에디터 생성 함수 정의
	watchdog.setCreator( ( element, config ) => {
		return CKSource.Editor
			.create( element, config )
			.then( editor => {
				return editor;
			} );
	});
	
	//에디터 제거 함수 정의
	watchdog.setDestructor( editor => {
		return editor.destroy();
	} );
	
	//에디터 관리 중에 발생하는 오류를 처리하기 위한 핸들러 함수 등록
	watchdog.on( 'error', handleSampleError );
})

//에디터 생성 함수를 전역 스코프에 정의
window.createEditor = function(target) {
    return watchdog
        .create(document.querySelector(target), {
            extraPlugins: [MyCustomUploadAdapterPlugin],
            toolbar: {
                items: [
                    'fontSize', 'fontColor', 'fontBackgroundColor', 'fontFamily', 'alignment', '|',
                    'bold', 'italic', 'underline', '|',
                    'highlight', 'outdent', 'indent', '|',
                    'specialCharacters', 'imageInsert', 'heading'
                    //, 'style'
                    // 'imageUpload', 'insertTable', 'undo', 'redo', 'findAndReplace',
//                    '-',
                ],
                shouldNotGroupWhenFull: true
            },
            allow: [{
                name: /.*/,
                attributes: true,
                classes: true,
                styles: true
            }],
            language: 'ko',
            image: {
                toolbar: [
                    'imageTextAlternative',
                    'toggleImageCaption',
                    'imageStyle:inline',
                    'imageStyle:block',
                    'imageStyle:side',
                    // 'linkImage'
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
            },
            fontSize: {
	            options: [
	                8,10,12,14,16,18,20,24,28,32,36,40
	            ],
	            supportAllValues: false
	        }
        })
        .catch(handleSampleError);
};

function handleSampleError( error ) {
	const issueUrl = 'https://github.com/ckeditor/ckeditor5/issues';

	const message = [
		'Oops, something went wrong!',
		`Please, report the following error on ${ issueUrl } with the build id "y4qgc2cwbgnp-nohdljl880ze" and the error stack trace:`
	].join( '\n' );

	console.error( message );
	console.error( error );
}

function MyCustomUploadAdapterPlugin(editor) {
    editor.plugins.get('FileRepository').createUploadAdapter = (loader) => {
        return new UploadAdapter(loader)
    }
}	