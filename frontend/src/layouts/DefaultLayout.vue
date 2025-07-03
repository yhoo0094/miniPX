<template>
    <div>레이아웃</div>
    <router-view /> <!-- 실제 페이지 렌더링 -->
</template>

<!-- <template>
    <div>
      <Header />
      <div class="main-layout">
        <Sidebar />
        <main class="content-area">
          <router-view /> 
        </main>
      </div>
      <Footer />
    </div>
  </template> 
  -->
  
  <script setup>
//   import Header from '@/components/common/Header.vue'
//   import Sidebar from '@/components/common/Sidebar.vue'
//   import Footer from '@/components/common/Footer.vue'
  </script>
  
  <style scoped>
  .main-layout {
    display: flex;
  }
  .content-area {
    flex: 1;
    padding: 20px;
  }
  </style>
  