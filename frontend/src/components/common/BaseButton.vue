<template>
  <button
    class="base-button"
    :type="type"
    :disabled="disabled"
    @click="$emit('click')"
    :style="buttonStyle"
  >
    <slot />
  </button>
</template>

<script setup lang="ts">
  import { ref, computed } from 'vue';

  // ✅ Props 타입 선언
  interface Props {
    type?: 'button' | 'submit' | 'reset';
    disabled?: boolean;
    width?: string | number;
    height?: string | number;  
  }

  // ✅ Emits 타입 선언
  const emit = defineEmits<{
    (e: 'click'): void;
  }>();

  // Props 정의
  const props = defineProps<Props>();

  // 버튼의 width와 height 값 계산 (매개변수 값이 없으면 기본값 사용)
  const buttonStyle = computed(() => {
    return {
      width: props.width || '100px',  // 기본값: '100px'
      height: props.height || '60px', // 기본값: '60px'
    };
  });
</script>

<style scoped>
.base-button {
  background-color: white;
  border: 2px solid #0c254d;
  cursor: pointer;
}
.base-button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}
</style>
