<template>
  <button
    class="base-button"
    :type="type"
    :disabled="disabled"
    @click="$emit('click')"
    :style="styleProp"
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
  const styleProp = computed(() => {
    return {
      width: props.width || '5rem',  
      height: props.height || '2rem', 
    };
  });
</script>

<style scoped>
.base-button {
  background-color: #0c254d;
  color: white;
  border: none;
  border-radius: 8px;

  font-size: 0.95rem;
  font-weight: 600;

  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 2px 6px rgba(12, 37, 77, 0.2);

  display: flex;
  align-items: center;
  justify-content: center;

  /* padding: 6px 18px;
  border-radius: 8px;
  border: none;
  background: #0c254d;
  color: #ffffff;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  white-space: nowrap;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 10px rgba(37, 99, 235, 0.35);
  transition: transform 0.12s ease, box-shadow 0.12s ease, opacity 0.12s ease;    */
}

.base-button:hover {
  background-color: #123a77;
  box-shadow: 0 3px 8px rgba(12, 37, 77, 0.3);
}

.base-button:active {
  background-color: #0a1e3f;
  transform: translateY(1px);
}

.base-button:disabled {
  background-color: #b8c4d1;
  color: #ffffff;
  cursor: not-allowed;
  box-shadow: none;
}

</style>
