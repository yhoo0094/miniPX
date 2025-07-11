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
      //기존 등록된 메뉴 제거
      const existingRoutes = router.getRoutes();
      existingRoutes.forEach((route) => {
        if (route.meta.isAdd) {
          router.removeRoute(route.name!); // 이름 지정이 중요
        }
      });  

      //메뉴 추가
      this.menuList.forEach((menu) => {
        router.addRoute('DefaultLayout', {
          name: menu.mnuSeq,
          path: menu.url,
          component: () =>
            import(`@/pages/${menu.compntPath}/${menu.compntNm}.vue`),
          meta: {
            mnuSeq: menu.mnuSeq,
            upperMnuSeq: menu.upperMnuSeq,
            upperMnuNm: menu.upperMnuNm,
            title: menu.mnuNm,
            mnuLv: menu.mnuLv,
            compntPath: menu.compntPath,
            requiresAuth: true, //권한 체크 여부
            isAdd: true,        //동적으로 추가되었는지 여부
          },
        });
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
