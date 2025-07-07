import { defineStore } from 'pinia';
import router from '@/router';

export const useMenuStore = defineStore('menu', {
  state: () => ({
    menuList: []
  }),
  actions: {
    setMenuList(list) {
      this.menuList = list;
    },
    clearMenuList() {
      this.menuList = [];
    },
    restoreRoutes() {
      this.menuList.forEach((menu) => {
        // 라우터에 동일 경로 중복 추가 방지
        const alreadyExists = router
          .getRoutes()
          .some((r) => r.path === menu.url);
        if (!alreadyExists) {
          router.addRoute('DefaultLayout', {
            path: menu.url,
            component: () =>
              import(`@/pages/${menu.compntPath}/${menu.compntNm}.vue`),
            meta: {
              requiresAuth: true,
              title: menu.mnuNm,
              mnuLv: menu.mnuLv,
              compntPath: menu.compntPath,
            },
          });
        }
      });
    },
  },
  persist: {
    enabled: true,
    strategies: [
      {
        storage: localStorage,
        paths: ['menuList']
      }
    ]
  }
});
