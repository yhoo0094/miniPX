<template>
  <div class="logout-container">
    <BaseButton @click="logout" class="button" type="button">로그아웃</BaseButton>
    <BaseButton @click="checkIsLoggedIn" class="button" type="button">isLoggedIn</BaseButton>
    <BaseButton @click="adminHome" class="button" type="button">AdminHome</BaseButton>
    <BaseButton @click="market" class="button" type="button">market</BaseButton>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/userStore';
import BaseButton from '@/components/common/BaseButton.vue';
import api from '@/plugins/axios';
import type { UserType } from '@/types/userType';

// ✅ 사용자 목록 타입 선언
const users = ref<UserType[]>([]);

const router = useRouter();
const userStore = useUserStore();

// 로그아웃
const logout = () => {
  userStore.logout();
};

// 로그인 여부 확인
const checkIsLoggedIn = () => {
  if (userStore.isLoggedIn) {
    console.log('로그인 여부:', userStore.isLoggedIn);
    console.log('로그인 사용자:', userStore.userNm);
  } else {
    console.log('로그인되어 있지 않음');
  }
};

// 관리자 홈 이동
const adminHome = () => {
  router.push('/admin/admin-home');
};

// 마켓 이동
const market = () => {
  router.push('/market/item');
};

// 사용자 리스트 가져오기
const getUserList = async () => {
  try {
    const response = await api.get<UserType[]>('/api/user/getUserList');
    users.value = response.data;
  } catch (error) {
    console.error('사용자 목록 조회 실패:', error);
  }
};
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
  margin-bottom: 10px;
}
</style>
