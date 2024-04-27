/**
* @파일명: UploadAdapter.js
* @작성자: KimSangMin
* @생성일: 2023. 1. 26. 오후 5:49:55
* @설명: CK에디터 이미지 업로드 어뎁터
*/

class UploadAdapter {
    // 파일로더가 업로드 어댑터를 사용해서 이미지를 서버에 전송
    constructor(loader) {
        this.loader = loader; //파일로더
    }

	// 업로드를 시작
    upload() {
        return this.loader.file.then(file => new Promise(((resolve, reject) => {
            this._initRequest();
            this._initListeners(resolve, reject, file);
            this._sendRequest(file);
        })))
    }

    _initRequest() {
        const xhr = this.xhr = new XMLHttpRequest();
        xhr.open('POST', '/common/ckUploadImage.do', true);
        xhr.responseType = 'json';
    }

	//XHR 리스너 초기화 하기
    _initListeners(resolve, reject, file) {
        const xhr = this.xhr;
        const loader = this.loader;
        const genericErrorText = '파일을 업로드 할 수 없습니다.'

        xhr.addEventListener('error', () => {reject(genericErrorText)})
        xhr.addEventListener('abort', () => reject())
        xhr.addEventListener('load', () => {
            const response = xhr.response
            if(!response || response.error) {
                return reject( response && response.error ? response.error.message : genericErrorText );
            }

            resolve({
                default: response.url //업로드된 파일 주소
            })
        })
    }

    _sendRequest(file) {
        const data = new FormData()
        data.append('upload',file)
        this.xhr.send(data)
    }
}	