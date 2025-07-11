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
          {{ menu.title }}
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

    <div class="header-button-box">
      <BaseButton 
        width="5rem" 
        height="2rem"
        @click="logout" 
        class="button"
        type="button">로그아웃</BaseButton>
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
    if (!exists) {
      topLevelMenus.value.push({mnuSeq: route.meta.upperMnuSeq,
                                title: route.meta.upperMnuNm, 
                                path: route.path});
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
}

.dropdown-link:hover {
  background-color: #3b5998;
}
</style>
