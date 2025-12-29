<template>
    <div class="wrap-layout">
      <BaseLoadingOverlay :visible="loading" :message="loadingMessage" /> <!-- 전역 로딩 오버레이 -->
      <Header />
      <div class="main-layout">
        <!-- <Sidebar /> -->
        <main class="content-area">
          <router-view /> <!-- 실제 페이지 렌더링 -->
        </main>
      </div>
      <!-- <Footer /> -->
      <Counsel />
    </div>
  </template> 
 
  
  <script setup lang="ts">
  import Header from '@/components/common/Header.vue'
  import Counsel from '@/components/common/Counsel.vue'
//   import Sidebar from '@/components/common/Sidebar.vue'
//   import Footer from '@/components/common/Footer.vue'

  import { storeToRefs } from 'pinia';
  import { useUiStore } from '@/stores/uiStore';
  import BaseLoadingOverlay from '@/components/common/BaseLoadingOverlay.vue';

  const uiStore = useUiStore();
  const { loading, loadingMessage } = storeToRefs(uiStore);
  </script>
  
  <style scoped>
  .wrap-layout {
    width: 100%;
  }
  
  .main-layout {
    display: flex;
  }
  .content-area {
    flex: 1;
    padding: 2rem 2rem 8rem 2rem;
    width: 100%;
  }
  </style>
  