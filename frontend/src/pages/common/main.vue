  <template>
    <div class="logout-container">
      <BaseButton 
        @click="logout" 
        class="button"
        type="button">로그아웃</BaseButton>
        <BaseButton 
        @click="isLoggedIn" 
        class="button"
        type="button">isLoggedIn</BaseButton>  
        <BaseButton 
        @click="adminHome" 
        class="button"
        type="button">AdminHome</BaseButton>         
        <BaseButton 
        @click="getUserList" 
        class="button"
        type="button">getUserList</BaseButton>            
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue'
  import { useRouter } from 'vue-router'  
  import { useUserStore } from '@/stores/user';
  import BaseButton from '@/components/common/BaseButton.vue'
  import api from '@/plugins/axios'

  const router = useRouter();  
  const userStore = useUserStore();
  const users = ref([]);
  
  //로그아웃
  const logout = () => {
    userStore.logout();
  }

  //로그인 여부 확인
  const isLoggedIn = () => {
    if (userStore.isLoggedIn) {
      console.log('로그인 여부:', userStore.isLoggedIn);
      console.log('로그인 사용자:', userStore.userNm);
    }
  }

  //관리자 홈 이동
  const adminHome = () => {
    router.push('/admin/admin-home');
  }

  //getUserList
  const getUserList = async () => {
    const response = await api.get('/api/user/getUserList');
    users.value = response.data
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
  