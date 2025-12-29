<template>
  <div class="content-box">
    <BaseToast ref="toastRef" />

    <h2 class="title">비밀번호변경</h2> 

    <div class="form-area">

      <div class="form-row">
        <div class="form-label">기존 비밀번호</div>
        <div class="form-field">
          <BaseInput
            type="password"
            v-model="form.currentPassword"
            placeholder="기존 비밀번호 입력"
            :maxlength="30"
          />
        </div>
      </div>

      <div class="form-row">
        <div class="form-label">신규 비밀번호</div>
        <div class="form-field">
          <BaseInput
            type="password"
            v-model="form.newPassword"
            placeholder="신규 비밀번호 입력"
            :maxlength="30"
          />
        </div>
      </div>

      <div class="form-row">
        <div class="form-label">신규 비밀번호 확인</div>
        <div class="form-field">
          <BaseInput
            type="password"
            v-model="form.newPasswordConfirm"
            placeholder="신규 비밀번호 재입력"
            :maxlength="30"
          />
        </div>
      </div>

      <div class="button-area">
        <BaseButton variant="primary" @click="changePassword">
          변경하기
        </BaseButton>
      </div>

    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue';
import BaseInput from '@/components/common/BaseInput.vue';
import BaseButton from '@/components/common/BaseButton.vue';
import api from '@/plugins/axios';
import { useUiStore } from '@/stores/uiStore';

const uiStore = useUiStore();

const form = reactive({
  currentPassword: '',
  newPassword: '',
  newPasswordConfirm: '',
});

const validate = () => {
  if (!form.currentPassword) {
    alert('기존 비밀번호를 입력해주세요.');
    return false;
  }
  if (!form.newPassword) {
    alert('신규 비밀번호를 입력해주세요.');
    return false;
  }
  if (form.newPassword !== form.newPasswordConfirm) {
    alert('신규 비밀번호가 일치하지 않습니다.');
    return false;
  }
  return true;
};

const changePassword = async () => {
  if (!validate()) return;
  if (!confirm('비밀번호를 변경하시겠습니까?')) return;

  try {
    uiStore.showLoading('비밀번호 변경 중입니다...');

    const payload = {
      currentPassword: form.currentPassword,
      newPassword: form.newPassword,
    };

    const response = await api.post(
      '/user/changePassword',
      payload,
      { withCredentials: true }
    );

    if (response.data?.RESULT) {
      alert('비밀번호가 변경되었습니다.');

      form.currentPassword = '';
      form.newPassword = '';
      form.newPasswordConfirm = '';
    } else {
      alert(response.data?.RESULT_MSG || '비밀번호 변경에 실패했습니다.');
    }
  } catch (e) {
    console.error(e);
    alert('비밀번호 변경 중 오류가 발생했습니다.');
  } finally {
    uiStore.hideLoading();
  }
};
</script>

<style scoped>
.content-box {
  padding: 1.5rem;
  background: #f9fbff;
  border-radius: 1rem;
  box-shadow: 0 0.5rem 1rem rgba(15, 23, 42, 0.08);
  box-sizing: border-box;
}

.title {
  margin: 0 0 1.5rem;
  font-size: 1.3rem;
  font-weight: 700;
  color: #0f172a;
}

.form-area {
  width: 100%;
}

.form-row {
  display: flex;
  align-items: center;
  margin-bottom: 1rem;
}

.form-label {
  width: 10rem;
  font-weight: bold;
  color: #000;
}

.form-field {
  flex: 1;
}

.button-area {
  display: flex;
  justify-content: flex-end;
  margin-top: 1.5rem;
}
</style>

