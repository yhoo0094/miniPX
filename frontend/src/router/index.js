import { createRouter, createWebHistory, useRouter } from 'vue-router'
import { checkAuth } from '@/api/auth'

const routes = [
  { path: '/', redirect: '/main' },
  { path: '/login', name: 'Login', component: () => import('@/pages/common/login.vue') },
  {
    path: '/',
    name: 'DefaultLayout',
    component: () => import('@/layouts/DefaultLayout.vue'),
    children: [
      {
        path: 'main',
        component: () => import('@/pages/common/main.vue'),
        meta: { requiresAuth: true }
      },
    ]
  },
  { path: '/notFound', name: 'NotFound', component: () => import('@/pages/error/not-found.vue') },
  { path: '/:pathMatch(.*)*', redirect: '/notFound'},
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

//전역 가드
router.beforeEach(async (to, from, next) => {
  if (to.meta.requiresAuth) {
    const isAuthenticated = await checkAuth()
    if (!isAuthenticated) {
      alert('로그인 정보가 없습니다. 다시 로그인해주세요.');
      return next('/login');
    }
  }
  next()
})

export default router
