<template>
  <transition name="toast-slide-down">
    <div v-if="visible" class="toast">{{ message }}</div>
  </transition>
</template>

<script setup lang="ts">
import { ref } from 'vue';

const visible = ref(false);
const message = ref('');

const showToast = (msg: string, duration = 1500) => {
  message.value = msg;
  visible.value = true;

  setTimeout(() => {
    visible.value = false;
  }, duration);
};

// 부모가 사용할 수 있도록 expose
defineExpose({ showToast });
</script>

<style scoped>
.toast {
  position: fixed;
  top: 5rem;        /* 시작 기준 위치 */
  left: 50%;
  transform: translateX(-50%);
  background: #0f172a;
  color: white;
  padding: 10px 16px;
  border-radius: 8px;
  box-shadow: 0 6px 14px rgba(0,0,0,0.2);
  font-size: 0.9rem;
  z-index: 9999;
}

/* 진입/퇴장 공통: 애니메이션 시간 & 이징 */
.toast-slide-down-enter-active,
.toast-slide-down-leave-active {
  transition: all 0.3s ease;
}

/* 처음 등장할 때: 약간 위 + 투명 */
.toast-slide-down-enter-from {
  opacity: 0;
  transform: translate(-50%, -20px); /* X: -50%로 가운데, Y: 위로 살짝 */
}

/* 완전히 자리 잡았을 때 */
.toast-slide-down-enter-to {
  opacity: 1;
  transform: translate(-50%, 0);
}

/* 사라질 때: 다시 위로 올라가면서 투명해짐 */
.toast-slide-down-leave-from {
  opacity: 1;
  transform: translate(-50%, 0);
}

.toast-slide-down-leave-to {
  opacity: 0;
  transform: translate(-50%, -20px);
}
</style>
