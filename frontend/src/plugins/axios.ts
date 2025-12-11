import axios  from 'axios'; 
import type { AxiosInstance, AxiosError, InternalAxiosRequestConfig } from 'axios';
import { useUserStore } from '@/stores/userStore';

// ✅ 커스텀 config에 _retry 속성 추가를 위해 타입 확장
interface CustomAxiosRequestConfig extends InternalAxiosRequestConfig {
  _retry?: boolean;
}

// ✅ axios 인스턴스 생성
const api: AxiosInstance = axios.create({
  baseURL: '/api',          // ✅ 프록시 경유
  withCredentials: true,
});

// ✅ 응답 인터셉터 (401 → 토큰 재발급)
api.interceptors.response.use(
  response => response,
  async (error: AxiosError) => {
    const originalRequest = error.config as CustomAxiosRequestConfig;

    if (
      error.response &&
      error.response.status === 401 &&
      !originalRequest._retry &&
      originalRequest.url !== '/auth/reissue'
    ) {
      originalRequest._retry = true;

      try {
        console.log('토큰 재발급');
        const res = await api.post('/auth/reissue', {});
        if (res.status === 200) {
          return api(originalRequest);
        }
      } catch (e) {
        const userStore = useUserStore();
        userStore.logout();
        alert('로그인 정보가 만료되었습니다. 다시 로그인해주세요.');
        window.location.href = '/login';
        return Promise.reject(e);
      }
    }

    return Promise.reject(error);
  }
);

export default api;
