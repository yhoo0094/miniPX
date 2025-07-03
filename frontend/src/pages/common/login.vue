<template>
    <div class="login-container">
      <BaseInput
        v-model="userId"
        placeholder="아이디를 입력하세요"
        class="input"
        label="아이디"
        @keydown.enter.prevent="login"
      />
      <BaseInput
        v-model="userPw"
        type="password"
        placeholder="비밀번호를 입력하세요"
        class="input"
        label="비밀번호"
        @keydown.enter.prevent="login"
      />
      <label class="remember-checkbox">
        <input type="checkbox" v-model="rememberId" />
        아이디 저장
      </label>      
      <BaseButton 
        @click="login" 
        class="button"
        type="button">로그인</BaseButton>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue'
  import { useUserStore } from '@/stores/user';
  import Cookies from 'js-cookie'
  import BaseInput from '@/components/common/BaseInput.vue'
  import BaseButton from '@/components/common/BaseButton.vue'
  import router from '@/router'  // main에서 사용 중인 라우터 인스턴스 사용
  import api from '@/plugins/axios'

  
  const userId = ref('');
  const userPw = ref('');
  const rememberId = ref(false)

  const userStore = useUserStore();

  // ▶ 저장된 쿠키 아이디 로딩
  onMounted(() => {
    const savedId = Cookies.get('savedUserId')
    if (savedId) {
      userId.value = savedId
      rememberId.value = true
    }
  })
  
  const login = async () => {
    if (!userId.value || !userPw.value){
        alert('아이디와 비밀번호를 입력해주세요.');
        return;
    }

    try {
      const response = await api.post('/api/user/login.do', {
        userId: userId.value,
        userPw: userPw.value
      })

      if(response.data.success){
        const userInfo = response.data.userInfo;
        userStore.setUserInfo(userInfo); // Pinia에 저장

        // ▶ 아이디 저장 여부에 따른 쿠키 처리
        if (rememberId.value) {
            Cookies.set('savedUserId', userId.value, {
            expires: 30, // 아이디 저장 기간: 30일
            path: '/',
            secure: true,
            sameSite: 'Strict'
            })
        } else {
            Cookies.remove('savedUserId', { path: '/' })
        }        

        // 메뉴를 동적으로 routes에 추가
        const mnuList = response.data.mnuList;
        mnuList.forEach(menu => {
            router.addRoute('DefaultLayout', {
                path: menu.url,
                component: () => import(`@/pages/${menu.compntPath}/${menu.compntNm}.vue`),
                meta: { requiresAuth: true },
            });
        });

        router.push('/main');
      } else {
        alert(response.data.message)
      }
    } catch (error) {
      alert('로그인에 실패했습니다.');
    }
  };
  </script>
  
  <style scoped>
  .login-container {
    max-width: 400px;
    margin: 100px auto;
    padding: 30px;
    border: 1px solid #ccc;
    border-radius: 8px;
    background: #fff;
  }
  
  .input {
    margin-bottom: 15px;
    width: 100%;
  }
  
  .button {
    width: 100%;
  }

  .remember-checkbox {
    display: flex;
    align-items: center;
    font-size: 14px;
    margin-bottom: 10px;
  }
  </style>
  