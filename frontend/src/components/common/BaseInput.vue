<template>
  <div class="base-input">
    <label v-if="label" :for="id">{{ label }}</label>
    <input
      :id="id"
      :type="type"
      :value="modelValue"
      @input="$emit('update:modelValue', ($event.target as HTMLInputElement)?.value)"
      :placeholder="placeholder"
      :style="styleProp"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';

// ✅ Props 타입 정의
interface Props {
  id?: string;
  label?: string;
  type?: string;
  modelValue: string;
  placeholder?: string;
  width?: string | number;
  height?: string | number;    
}

// ✅ Emits 타입 정의
const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void;
}>();

  // 버튼의 width와 height 값 계산 (매개변수 값이 없으면 기본값 사용)
  const styleProp = computed(() => {
    return {
      width: props.width,  
      height: props.height, 
    };
  });

const props = defineProps<Props>();
</script>

<style scoped>
.base-input {
  display: flex;
  flex-direction: column; /* label 위, input 아래 자연스럽게 배치 */
  /* flex: 1; */
}

/* label 스타일 */
.base-input label {
  font-size: 0.85rem;
  font-weight: 600;
  color: #0c254d;
  margin-bottom: 0.35rem;
}

/* input 스타일 */
.base-input input {
  height: 2rem;
  padding: 0 0.75rem;
  border: 1.5px solid #b8c4d1;
  border-radius: 8px;
  font-size: 1rem;
  outline: none;
  transition: all 0.2s ease;
  background-color: #f8fbff;
}

/* placeholder */
.base-input input::placeholder {
  color: #9aa5b1;
}

/* focus 스타일 */
.base-input input:focus {
  border-color: #0c7bce;
  background-color: #ffffff;
  box-shadow: 0 0 0 3px rgba(12, 123, 206, 0.15);
}

/* hover 스타일 */
.base-input input:hover {
  border-color: #7a8ea5;
}
</style>
