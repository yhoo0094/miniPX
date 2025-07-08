  <template>
    <div class="logout-container">
      <div>admin-home</div>
      <BaseButton 
        @click="logout" 
        class="button"
        type="button">로그아웃</BaseButton>
        <BaseButton 
        @click="isLoggedIn" 
        class="button"
        type="button">isLoggedIn</BaseButton>        
    </div>
  </template>
  
  <script setup lang="ts">
  import { useRouter } from 'vue-router'  
  import { useUserStore } from '@/stores/userStore';
  import BaseButton from '@/components/common/BaseButton.vue'
  import Cookies from 'js-cookie'

  const router = useRouter();  
  const userStore = useUserStore();
  
  //로그아웃
  const logout = () => {
    Cookies.remove('accessToken');
    Cookies.remove('refreshToken');
    router.push('/login')
  }

  //로그인
  const isLoggedIn = () => {
    if (userStore.isLoggedIn) {
      console.log('로그인 여부:', userStore.isLoggedIn);
      console.log('로그인 사용자:', userStore.userNm);
    }
  }

  
  </script>
  
  <style scoped>
  .logout-container {
    max-width: 400px;
    margin: 100px auto;
    padding: 30px;
    border: 1px solid #ccc;
    border-radius: 8px;
    background: #fff;
  }
  
  .button {
    width: 100%;
  }
  </style>
  