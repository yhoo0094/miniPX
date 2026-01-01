import { defineStore } from 'pinia';
import api from '@/plugins/axios';
import router from '@/router';
import type { UserType } from '@/types/user/user.base.type';
import Constant from '@/constants/constant';

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

    //서버에서 사용자 정보 재조회
    async fetchMyInfo() {
      try {
        const response = await api.post('/user/getLoginInfo', {});
        if (response.data?.RESULT === Constant.RESULT_SUCCESS) {
          this.user = response.data?.OUT_DATA;
          return true;
        }
        return false;
      } catch (e) {
        this.user = null;
        return false;
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
    userId: (state): string => state.user?.userId || '',
    userNm: (state): string => state.user?.userNm || '',
    aiOpenYn: (state): string => state.user?.aiOpenYn || '',
    credit: (state): number | '' => state.user?.credit ?? '',
    roleSeq: (state): number | '' => state.user?.roleSeq ?? '',
  },
});
