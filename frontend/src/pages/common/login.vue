<template>
  <div class="login-wrapper">
    <div class="login-container">
      <div class="login-info-box">
        <BaseInput height="2.2rem" v-model="userId" class="input" label="아이디" placeholder="아이디를 입력하세요"
          @keydown.enter.prevent="login"/>
        <BaseInput height="2.2rem" v-model="userPw" type="password" class="input" label="비밀번호" placeholder="비밀번호를 입력하세요"
          @keydown.enter.prevent="login"/>
        <BaseCheckbox class="chkBox" v-model="rememberId" label="아이디 저장" />
        <BaseButton height="3rem" type="button" class="button" @click="login" width="100%">로그인</BaseButton>
      </div>
      <div class="login-banner-container">
        <img :src="delivery_man" alt="no img"/>
      </div>
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
import BaseCheckbox from '@/components/common/BaseCheckbox.vue';
import router from '@/router';
import api from '@/plugins/axios';
import delivery_man from '@/assets/img/delivery_man.webp';
import type { UserType } from '@/types/user/user.base.type';
import type { MenuType } from '@/types/menuType';
import type { ApiResponse } from '@/types/api/response';
import Constant from '@/constants/constant';

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
    const response = await api.post<ApiResponse<LoginPayload>>('/user/login', {
      userId: userId.value,
      userPw: userPw.value,
    });

    if (response.data?.RESULT === Constant.RESULT_SUCCESS) {
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
      await menuStore.restoreRoutes();

      if(userInfo.pwInitYn === 'Y'){
        //최초 로그인한 경우
        alert("비밀번호가 초기화되었습니다. \n비밀번호를 변경해주세요.");
        await router.replace('/info/pwchng');
      } else {
        await router.replace('/main');
      }
    } else {
      alert(response.data[Constant.OUT_RESULT_MSG]);
    }
  } catch (error) {
    alert('로그인에 실패했습니다.\n' + error);
  }
};
</script>

<style scoped>
.login-banner-container {
  display: flex;
  height: 20.625rem;
}

.login-banner-underLine {
  display: flex;
  height: 0.6rem;
  background-color: #0c254d;
  width: 100%;
}

.login-container {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  max-width: 25rem;
  margin: auto;
  padding: 1rem;
  border: 1px solid #ccc;
  border-radius: 0.5rem;
  background: #fff;
}

.login-info-box {
  display: flex;
  flex-wrap: wrap;
}

.input {
  margin-bottom: 1rem;
  width: 100%;
}

.chkBox{
  margin-bottom: 0.5rem;
}

.button {
  width: 100%;
}

.login-wrapper {
  display: flex;
  justify-content: center; /* 가로 중앙 */
  align-items: center;     /* 세로 중앙 */
  min-height: 100vh;       /* 화면 전체 높이 */
  background: #f5f5f5;     /* 선택 사항: 배경색 */
}
</style>