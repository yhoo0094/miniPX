import axios from 'axios'
import Cookies from 'js-cookie'
import { useUserStore } from '@/stores/user'

// axios 인스턴스 생성
const api = axios.create({
  baseURL: 'http://localhost:8080',
  withCredentials: true, // 쿠키 포함
})

// 응답 인터셉터 (401 발생 시 토큰 재발급)
//401: Spring Security는 인증 실패
//403: 인증은 되었지만 권한이 없음
api.interceptors.response.use(
  response => response,
  async error => {
    const originalRequest = error.config
    if (error.response && error.response.status === 401 && !originalRequest._retry  && originalRequest.url !== '/api/auth/reissue') {
      originalRequest._retry = true

      try {
        console.log('토큰 재발급');
        const res = await api.post('/api/auth/reissue', {});
        if(res.status === 200){ 
            return api(originalRequest); 
        }
      } catch (e) {
        // RefreshToken도 만료되었거나 실패하면 로그아웃
        const userStore = useUserStore()
        userStore.logout()
        alert('로그인 정보가 없습니다. 다시 로그인해주세요.');
        window.location.href = '/login'
        return Promise.reject(e)
      }
    }

    return Promise.reject(error)
  }
)

export default api
