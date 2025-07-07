import { defineStore } from 'pinia';
import api from '@/plugins/axios'
import router from '@/router'

export const useUserStore = defineStore('user', {
  state: () => ({
    user: null,
  }),

  actions: {
    //사용자 정보 저장
    setUserInfo(userInfo) {
      try {
        this.user = userInfo;
      } catch (err) {
        console.error('Invalid token', err);
      }
    },

    //로그아웃
    async logout() {
        if(!confirm('로그아웃 하시겠습니까?')){return};
      this.user = null;

      try {
        const res = await api.post('/api/user/logout.do', {});
        if(res.status === 200){router.push('/login')};
      } catch (e) {
        console.error('서버 로그아웃 실패', e);
      }
    }
  },

  getters: {
    isLoggedIn: (state) => !!state.user,
    userNm: (state) => state.user?.userNm || '',
    roleSeq: (state) => state.roleSeq?.roleSeq || '',
  }
});
