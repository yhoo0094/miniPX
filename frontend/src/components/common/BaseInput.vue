<template>
  <div class="base-input">
    <label v-if="label" :for="id">{{ label }}</label>
    <input
      :id="id"
      :type="type ?? 'text'"
      :value="modelValue"
      :placeholder="placeholder"
      :disabled="disabled"
      :readonly="readonly"
      :min="type === 'number' ? min : undefined"
      :max="type === 'number' ? max : undefined"
      :step="type === 'number' ? step : undefined"
      :style="{
        ...styleProp,
        textAlign: inputAlign
      }"
      @input="onInput"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';

// ✅ Props 타입 정의
interface Props {
  id?: string;
  label?: string;
  type?: 'text' | 'number' | 'password';
  modelValue: string | number;
  placeholder?: string;
  disabled?: boolean;
  readonly?: boolean;
  width?: string | number;
  height?: string | number;
  padding?: string | number;
  align?: 'left' | 'center' | 'right';

  /* 숫자 전용 옵션 */
  min?: number;
  max?: number;
  step?: number;
  numberOnly?: boolean; 
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
      padding: props.padding, 
    };
  });

  //입력 제어 로직
  const onInput = (e: Event) => {
    const target = e.target as HTMLInputElement;
    let value: string | number = target.value;

    if (props.type === 'number' || props.numberOnly) {
      //숫자 타입일 때
      value = value.replace(/[^0-9]/g, '');
      emit('update:modelValue', value === '' ? '' : Number(value));
    } else {
      emit('update:modelValue', value);
    }
  };  

  //정렬 로직
  const inputAlign = computed(() => {
    if (props.align) return props.align;
    if (props.type === 'number' || props.numberOnly) return 'right';
    return 'left';
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
