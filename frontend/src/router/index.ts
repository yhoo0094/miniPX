import { createRouter, createWebHistory } from 'vue-router';
import type { RouteRecordRaw } from 'vue-router';
import { checkAuth } from '@/api/auth';
import { useUserStore } from '@/stores/userStore';

// 라우트 타입 명시
const routes: RouteRecordRaw[] = [
  { path: '/', 
    // redirect: '/main', 
    // component: () => import('@/pages/common/main.vue'),
    component: () => import('@/pages/market/itemList.vue'),
  },
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
        path: '/main',
        // component: () => import('@/pages/common/main.vue'),
        component: () => import('@/pages/market/itemList.vue'),
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
router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore();

  //루트 경로 -> 로그인한 경우 메인으로, 로그인 안한 경우 로그인으로
  if(to.path === '/'){
    const isAuthenticated = await checkAuth('/main', userStore);
    if (!isAuthenticated) {
      return next('/login');
    } else {
      return next('/main');
    }
  }

  //로그인 및 권한 검증
  if (to.meta.requiresAuth) {
    const isAuthenticated = await checkAuth(to.path, userStore);
    if (!isAuthenticated) {
      if(to.path !== '/'){
        alert('로그인 정보가 없습니다. 다시 로그인해주세요.');
      }
      return next('/login');
    }
  }
  next();
});

export default router;
