<template>
  <header class="header">
    <div class="logo-box">
      <img class="logo-img" :src="home" alt="logo" @click="main" />
    </div>

    <nav class="nav" ref="navRef">
      <div
        class="nav-item"
        v-for="menu in topLevelMenus"
        :key="menu.path"
        @click.stop="toggleMenu(menu.path)"
      >
        <div class="nav-link">{{ menu.title }}</div>

        <div class="dropdown" v-if="getChildren(menu).length > 0 && activeMenu === menu.path">
          <router-link
            v-for="sub in getChildren(menu)"
            :key="sub.path"
            :to="sub.path"
            class="dropdown-link"
            @click.stop="activeMenu = null"
          >
            {{ sub.meta.title }}
          </router-link>
        </div>
      </div>
    </nav>

    <div class="header-button-box">
      <span class="user-id-info">{{ userStore.userId }}님</span>
      <BaseButton width="6rem" height="2rem" @click="logout" class="button" type="button">로그아웃</BaseButton>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue';
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
  if (confirm('로그아웃 하시겠습니까?')) {userStore.logout();}
}

const activeMenu = ref<string | null>(null);
const navRef = ref<HTMLElement | null>(null);

const toggleMenu = (path: string) => {
  activeMenu.value = activeMenu.value === path ? null : path;
};

const onDocumentClick = (e: MouseEvent) => {
  if (!activeMenu.value) return;

  const target = e.target as Node | null;
  const navEl = navRef.value;

  // nav 영역 밖을 클릭했으면 닫기
  if (!navEl || !target) return;
  if (!navEl.contains(target)) {
    activeMenu.value = null;
  }
};

onMounted(() => {
  // capture 단계로 받으면 더 안정적(컴포넌트 내부 stop과 충돌 최소화)
  document.addEventListener('click', onDocumentClick, true);
});

onBeforeUnmount(() => {
  document.removeEventListener('click', onDocumentClick, true);
});

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
  padding: 0 1.875rem;
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
  height: 2rem;
  cursor: pointer;
}

/* 상단 1depth 메뉴 */
.nav {
  display: flex;
  gap: 1.5rem;
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
  padding: 0.5rem 0.625rem;
  font-size: 0.95rem;
  letter-spacing: 0.03em;
}

/* 우측 버튼 */
.header-button-box {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.user-id-info{
  color: white;
  font-weight: bold;
}

/* 2depth dropdown 컨테이너 */
.dropdown {
  position: absolute;
  top: calc(100% + 0.5rem);
  left: 50%;
  transform: translateX(-50%);
  background: #0c254d;
  border-radius: 0.25rem;
  padding: 0.25rem 0;
  z-index: 20;
  min-width: 8.125rem;
  box-shadow: 0 0.625rem 1.5rem rgba(15, 23, 42, 0.45);
  display: flex;
  flex-direction: column;
  font-weight: bold;
}

/* 위쪽에 작은 화살표 느낌 */
.dropdown::before {
  content: "";
  position: absolute;
  top: -0.375rem;
  left: 50%;
  transform: translateX(-50%) rotate(45deg);
  width: 0.625rem;
  height: 0.625rem;
  background: #0c254d;
}

/* 2depth 링크 */
.dropdown-link {
  display: block;
  padding: 0.5625rem 1.125rem;
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
  transform: translateY(-0.0625rem);
}

/* 모바일 대응 간단 정리 */
@media (max-width: 48rem) {
  .header {
    padding: 0 1rem;
  }

  .nav {
    gap: 0.875rem;
  }

  .nav-link {
    padding: 0.375rem 0.25rem;
    font-size: 0.9rem;
  }
}
</style>

