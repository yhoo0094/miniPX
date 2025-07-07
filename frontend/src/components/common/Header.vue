<template>
  <header class="header">
    <div class="logo-box">
      <img class="logo-img" :src="home" alt="logo" @click="main" />
    </div>

    <nav class="nav">
      <div
        class="nav-item"
        v-for="menu in topLevelMenus"
        :key="menu.path"
        @mouseenter="activeMenu = menu.path"
        @mouseleave="activeMenu = null"
      >
        <router-link :to="menu.path" class="nav-link">
          {{ menu.meta.title }}
        </router-link>

        <!-- 2depth 드롭다운 -->
        <div
          class="dropdown"
          v-if="getChildren(menu).length > 0 && activeMenu === menu.path"
        >
          <router-link
            v-for="sub in getChildren(menu)"
            :key="sub.path"
            :to="sub.path"
            class="dropdown-link"
          >
            {{ sub.meta.title }}
          </router-link>
        </div>
      </div>
    </nav>
  </header>
</template>

<script setup>
import { ref, computed } from 'vue';
import home from '@/assets/img/home.png';
import router from '@/router';

const main = () => {
  router.push('/main');
};

const activeMenu = ref(null);

// 모든 실제 등록된 라우트를 가져옴 (동적 포함)
const allRoutes = router.getRoutes();

// DefaultLayout 하위에만 메뉴 있는 경우 필터링
const topLevelMenus = computed(() =>
  allRoutes.filter(
    (r) =>
      r.meta?.title &&
      r.meta.mnuLv === 1 // 1depth 메뉴
  )
);

// 하위 메뉴(2depth) 가져오기
const getChildren = (parent) => {
  return allRoutes.filter(
    (r) => r.meta?.mnuLv === 2 && parent.meta.compntPath === r.meta.compntPath
  );
};
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 30px;
  background-color: #2d71b5;
  color: white;
  height: 60px;
  position: relative;
}

.logo-box {
  height: 100%;
}

.logo-img {
  height: 100%;
  cursor: pointer;
}

.nav {
  display: flex;
  gap: 20px;
  position: relative;
}

.nav-item {
  position: relative;
}

.nav-link {
  color: white;
  text-decoration: none;
  font-size: 14px;
  padding: 10px;
}

.nav-link:hover {
  text-decoration: underline;
}

/* 2depth dropdown */
.dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  background: #183c61;
  border-radius: 4px;
  padding: 5px 0;
  z-index: 10;
  min-width: 120px;
}

.dropdown-link {
  display: block;
  padding: 8px 15px;
  color: white;
  text-decoration: none;
  font-size: 13px;
}

.dropdown-link:hover {
  background-color: #3b5998;
}
</style>
