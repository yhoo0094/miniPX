import { defineStore } from 'pinia';
import api from '@/plugins/axios';
import router from '@/router';
import type { UserType } from '@/types/userType';

export const useUserStore = defineStore('user', {
  state: () => ({
    user: null as UserType | null,
    authMap: {} as Record<string, number>,  // 경로별 권한 맵
    currentAuthLv: 0,                       // 현재 페이지 권한 (반응형)
  }),

  actions: {
    // 사용자 정보 저장
    setUserInfo(userInfo: UserType) {
      try {
        this.user = userInfo;
      } catch (err) {
        console.error('Invalid token', err);
      }
    },

    // 로그아웃
    async logout() {
      if (!confirm('로그아웃 하시겠습니까?')) return;

      this.user = null;
      this.authMap = {}; 

      try {
        const res = await api.post('/user/logout', {});
        if (res.status === 200) {
          router.push('/login');
        }
      } catch (e) {
        console.error('서버 로그아웃 실패', e);
      }
    },

    // 경로에 대한 권한 설정
    setAuth(path: string, grade: number) {
      this.authMap[path] = grade;
    },

     // 현재 페이지 권한 세팅
    setCurrentAuth(path: string): number {
      const lv = this.authMap[path] ?? 0;
      this.currentAuthLv = lv;
      return lv;
    },    
  },

  getters: {
    isLoggedIn: (state): boolean => !!state.user,
    userNm: (state): string => state.user?.userNm || '',
    roleSeq: (state): number | '' => state.user?.roleSeq ?? '',
  },
});
