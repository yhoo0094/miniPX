import { createRouter, createWebHistory } from 'vue-router';
import type { RouteRecordRaw } from 'vue-router';
import { checkAuth } from '@/api/auth';
import { useUserStore } from '@/stores/userStore';

// 라우트 타입 명시
const routes: RouteRecordRaw[] = [
  { path: '/', redirect: '/main' },
  {
    path: '/login',
    component: () => import('@/pages/common/login.vue'),
  },
  {
    path: '/',
    name: 'DefaultLayout',
    component: () => import('@/layouts/DefaultLayout.vue'),
    children: [
      {
        path: 'main',
        component: () => import('@/pages/market/itemList.vue'),
        meta: { requiresAuth: true },
      },
      {
        path: '/market/itemNew',
        component: () => import('@/pages/market/itemNew.vue'),
        meta: { requiresAuth: true },
      },  
      {
        path: '/market/itemDetail',
        component: () => import('@/pages/market/itemDetail.vue'),
        meta: { requiresAuth: true },
      },           
    ],
  },
  {
    path: '/notFound',
    name: 'NotFound',
    component: () => import('@/pages/error/not-found.vue'),
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/notFound',
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// 전역 가드 (타입 자동 추론됨)
let myInfoFetched = false; 
router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore();

  // 사용자 정보 조회
  if (to.meta.requiresAuth && !myInfoFetched && !userStore.user) {
    myInfoFetched = true;
    await userStore.fetchMyInfo();
  }

  //로그인 및 권한 검증
  if (to.meta.requiresAuth) {
    const isAuthenticated = await checkAuth(to.path, userStore);
    if (!isAuthenticated) {
      return next('/login');
    }
  }  

  // 현재 경로 권한 세팅
  userStore.setCurrentAuth(to.path);
  next();
});

export default router;
