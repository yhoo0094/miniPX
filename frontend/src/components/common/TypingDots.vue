<template>
  <div class="typing-dots">
    <span class="dot"></span>
    <span class="dot"></span>
    <span class="dot"></span>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
.typing-dots {
  display: inline-flex;
  align-items: center;
  justify-content: flex-start;
  gap: 4px;
  height: 1rem;
}

.dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background-color: #8fa3bf;
  animation: bounce 1s infinite ease-in-out;
}

/* 각 점의 시작 타이밍 살짝씩 다르게 */
.dot:nth-child(1) {
  animation-delay: 0s;
}
.dot:nth-child(2) {
  animation-delay: 0.15s;
}
.dot:nth-child(3) {
  animation-delay: 0.3s;
}

@keyframes bounce {
  0%, 80%, 100% {
    transform: translateY(0);
    opacity: 0.4;
  }
  40% {
    transform: translateY(-4px);
    opacity: 1;
  }
}
</style>