<template>
  <transition name="fade">
    <div v-if="visible" class="loading-overlay">
      <div class="loading-content">
        <div class="spinner"></div>
        <div class="loading-text">{{ message }}</div>
      </div>
    </div>
  </transition>
</template>

<script setup lang="ts">
interface Props {
  visible: boolean;
  message?: string;
}

const props = defineProps<Props>();
</script>

<style scoped>
.loading-overlay {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.35);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

/* 중앙 카드 */
.loading-content {
  min-width: 180px;
  padding: 18px 22px;
  border-radius: 14px;
  background: #ffffff;
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.28);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

/* 동그라미 스피너 */
.spinner {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: 4px solid #e5e7eb;
  border-top-color: #0c254d;
  animation: spin 0.8s linear infinite;
}

.loading-text {
  font-size: 0.9rem;
  font-weight: 500;
  color: #0f172a;
}

/* 페이드 애니메이션 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.18s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>
