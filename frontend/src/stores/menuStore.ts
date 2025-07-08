import { defineStore } from 'pinia';
import router from '@/router';
import type { MenuType } from '@/types/menuType';
import type { PersistenceOptions } from 'pinia-plugin-persistedstate';

export const useMenuStore = defineStore('menu', {
  state: () => ({
    menuList: [] as MenuType[]
  }),
  actions: {
    setMenuList(list: MenuType[]) {
      this.menuList = list;
    },
    clearMenuList() {
      this.menuList = [];
    },
    restoreRoutes() {
      this.menuList.forEach((menu) => {
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
    strategies: [
      {
        storage: localStorage,
        paths: ['menuList']
      }
    ]
  } as PersistenceOptions,
});
