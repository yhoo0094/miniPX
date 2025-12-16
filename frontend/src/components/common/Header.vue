<template>
  <header class="header">
    <div class="logo-box">
      <img class="logo-img" :src="home" alt="logo" @click="main" />
    </div>

    <nav class="nav">
      <div class="nav-item" v-for="menu in topLevelMenus" :key="menu.path" 
      @click="activeMenu = (activeMenu == null) ? menu.path : null" 
        >
          <div class="nav-link">
          {{ menu.title }}
          </div>

        <!-- 2depth 드롭다운 -->
        <div class="dropdown" v-if="getChildren(menu).length > 0 && activeMenu === menu.path">
          <router-link v-for="sub in getChildren(menu)" :key="sub.path" :to="sub.path" class="dropdown-link">
            {{ sub.meta.title }}
          </router-link>
        </div>
      </div>
    </nav>

    <div class="header-button-box">
      <BaseButton width="5rem" height="2rem" @click="logout" class="button" type="button">로그아웃</BaseButton>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useUserStore } from '@/stores/userStore';
import home from '@/assets/img/home.png';
import router from '@/router';
import BaseButton from '@/components/common/BaseButton.vue'
const userStore = useUserStore();

//메인으로 이동
const main = () => {
  router.push('/main');
};

//로그아웃
const logout = () => {
  userStore.logout();
}

const activeMenu = ref(null);

// 모든 실제 등록된 라우트를 가져옴 (동적 포함)
const allRoutes = router.getRoutes();

// 상위 메뉴 가져오기
const topLevelMenus = ref<any[]>([]);
allRoutes.forEach((route) => {
  const exists = topLevelMenus.value.some(menu => menu.mnuSeq === route.meta.upperMnuSeq);
  if (!exists && route.meta.upperMnuSeq) {
    topLevelMenus.value.push({
      mnuSeq: route.meta.upperMnuSeq,
      title: route.meta.upperMnuNm,
      path: route.path
    });
  }
});

// 하위 메뉴(2depth) 가져오기
const getChildren = (parent) => {
  return allRoutes.filter(
    (r) => r.meta?.upperMnuSeq === parent.mnuSeq
  );
};
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 30px;
  background-color: #000000;
  height: 3.75rem;
  position: relative;
  font-weight: bold;
}

/* 왼쪽 로고 */
.logo-box {
  height: 100%;
  display: flex;
  align-items: center;
}

.logo-img {
  height: 32px;
  cursor: pointer;
}

/* 상단 1depth 메뉴 */
.nav {
  display: flex;
  gap: 24px;
  position: relative;
  align-items: center;
}

.nav-item {
  position: relative;
  cursor: pointer;
}

.nav-link {
  color: #e5e7eb;
  text-decoration: none;
  padding: 8px 10px;
  font-size: 0.95rem;
  letter-spacing: 0.03em;
}

/* 우측 버튼 */
.header-button-box {
  display: flex;
  align-items: center;
}

/* 2depth dropdown 컨테이너 */
.dropdown {
  position: absolute;
  top: calc(100% + 8px);
  left: 50%;
  transform: translateX(-50%);
  background: #0c254d;
  border-radius: 4px;
  padding: 4px 0;
  z-index: 20;
  min-width: 130px;
  box-shadow: 0 10px 24px rgba(15, 23, 42, 0.45);
  display: flex;
  flex-direction: column;
  font-weight: bold;
}

/* 위쪽에 작은 화살표 느낌 */
.dropdown::before {
  content: "";
  position: absolute;
  top: -6px;
  left: 50%;
  transform: translateX(-50%) rotate(45deg);
  width: 10px;
  height: 10px;
  background: #0c254d;
}

/* 2depth 링크 */
.dropdown-link {
  display: block;
  padding: 9px 18px;
  color: #e5e7eb;
  text-decoration: none;
  font-size: 0.9rem;
  white-space: nowrap;
  transition: background-color 0.12s ease, color 0.12s ease, transform 0.08s ease;
}

/* hover 시 */
.dropdown-link:hover {
  background-color: #1f3f6d;
  color: #ffffff;
  transform: translateY(-1px);
}

/* 모바일 대응 간단 정리 */
@media (max-width: 768px) {
  .header {
    padding: 0 16px;
  }

  .nav {
    gap: 14px;
  }

  .nav-link {
    padding: 6px 4px;
    font-size: 0.9rem;
  }
}
</style>
