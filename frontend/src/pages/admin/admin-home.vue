  <template>
    <!-- <div class="logout-container">
      <div>admin-home</div>
      <BaseButton 
        @click="logout" 
        class="button"
        type="button">로그아웃</BaseButton>
        <BaseButton 
        @click="isLoggedIn" 
        class="button"
        type="button">isLoggedIn</BaseButton>        
    </div> -->
    <!-- <div class="openai-container">
      <BaseInput v-model="question" class="input" label="질문" placeholder="질문을 입력하세요"
        @keydown.enter.prevent="doQuestion"/>
      <BaseButton type="button" class="button" @click="doQuestion" width="100%">질문하기</BaseButton>        
      <div>{{answer}}</div>
    </div>     -->
  </template>
  
  <script setup lang="ts">
  import { ref } from 'vue';
  import { useRouter } from 'vue-router';
  import { useUserStore } from '@/stores/userStore';
  import BaseInput from '@/components/common/BaseInput.vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import api from '@/plugins/axios';
  import Cookies from 'js-cookie';

  const router = useRouter();  
  const userStore = useUserStore();

  // ✅ ref 타입 명시
  const question = ref<string>('');
  const answer = ref<string>('');
  
  //질문하기
  const doQuestion = async () => {

    if (!question.value) {
      alert('질문을 입력해주세요.');
      return;
    }

    try {
      const response = await api.post('/openai/getAiAnswer', {
        question: question.value,
      });
      answer.value = response.data;
    } catch (error) {
      alert('질문에 실패했습니다.\n' + error);
    }    
  }

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
  