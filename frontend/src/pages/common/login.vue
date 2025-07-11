<template>
  <div class="login-container">
    <div class="login-info-box">
      <BaseInput v-model="userId" class="input" label="아이디" placeholder="아이디를 입력하세요"
        @keydown.enter.prevent="login"/>
      <BaseInput v-model="userPw" type="password" class="input" label="비밀번호" placeholder="비밀번호를 입력하세요"
        @keydown.enter.prevent="login"/>
      <label class="remember-checkbox">
        <input id="rememberId" type="checkbox" v-model="rememberId"/>
        <label>아이디 저장</label>
      </label>      
      <BaseButton type="button" class="button" @click="login" width="100%">로그인</BaseButton>
    </div>
    <div class="login-banner-container">
      <img :src="delivery_man" alt="no img"/>
    </div>    
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useUserStore } from '@/stores/userStore';
import { useMenuStore } from '@/stores/menuStore';
import Cookies from 'js-cookie';
import BaseInput from '@/components/common/BaseInput.vue';
import BaseButton from '@/components/common/BaseButton.vue';
import router from '@/router';
import api from '@/plugins/axios';
import delivery_man from '@/assets/img/delivery_man.webp';
import type { UserType } from '@/types/userType';
import type { MenuType } from '@/types/menuType';
import type { ApiResponse } from '@/types/api/response';

interface LoginPayload {
  userInfo: UserType;
  mnuList: MenuType[];
}

// ✅ ref 타입 명시
const userId = ref<string>('');
const userPw = ref<string>('');
const rememberId = ref<boolean>(false);

const userStore = useUserStore();
const menuStore = useMenuStore();

// ▶ 저장된 쿠키 아이디 로딩
onMounted(() => {
  const savedId = Cookies.get('savedUserId');
  if (savedId) {
    userId.value = savedId;
    rememberId.value = true;
  }
});

const login = async () => {
  if (!userId.value || !userPw.value) {
    alert('아이디와 비밀번호를 입력해주세요.');
    return;
  }

  try {
    const response = await api.post<ApiResponse<LoginPayload>>('/api/user/login.do', {
      userId: userId.value,
      userPw: userPw.value,
    });

    if (response.data.success) {
      const userInfo = response.data['userInfo'];
      userStore.setUserInfo(userInfo);

      // ▶ 아이디 저장 여부에 따른 쿠키 처리
      if (rememberId.value) {
        Cookies.set('savedUserId', userId.value, {
          expires: 30,
          path: '/',
          secure: true,
          sameSite: 'Strict',
        });
      } else {
        Cookies.remove('savedUserId', { path: '/' });
      }

      // 메뉴 저장 및 라우트 추가
      const mnuList = response.data['mnuList'];
      menuStore.setMenuList(mnuList);
      menuStore.restoreRoutes();

      router.push('/main');
    } else {
      alert(response.data.message);
    }
  } catch (error) {
    alert('로그인에 실패했습니다.\n' + error);
  }
};
</script>

<style scoped>
.login-banner-container {
  display: flex;
  height: 330px;
}

.login-container {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  max-width: 400px;
  margin: 100px auto;
  padding: 30px;
  border: 1px solid #ccc;
  border-radius: 8px;
  background: #fff;
}

.login-info-box {
  display: flex;
  flex-wrap: wrap;
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
  margin-bottom: 10px;
}
</style>