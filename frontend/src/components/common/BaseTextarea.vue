<template>
  <div class="base-textarea">
    <label v-if="label" :for="id">{{ label }}</label>
    <textarea
      :id="id"
      :value="modelValue"
      @input="$emit('update:modelValue', ($event.target as HTMLTextAreaElement)?.value)"
      :placeholder="placeholder"
      :style="styleProp"
    ></textarea>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';

interface Props {
  id?: string;
  label?: string;
  modelValue: string;
  placeholder?: string;
  width?: string | number;
  height?: string | number;
}

const props = defineProps<Props>();

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void;
}>();

// width, height 스타일 바인딩
const styleProp = computed(() => {
  return {
    width: props.width,
    height: props.height,
  };
});
</script>

<style scoped>
.base-textarea {
  display: flex;
  flex-direction: column;
}

/* label 스타일 (BaseInput과 동일하게) */
.base-textarea label {
  font-size: 0.85rem;
  font-weight: 600;
  color: #0c254d;
  margin-bottom: 0.35rem;
}

/* textarea 스타일 */
.base-textarea textarea {
  min-height: 4rem;
  padding: 0.6rem 0.75rem;
  border: 1.5px solid #b8c4d1;
  border-radius: 8px;
  font-size: 1rem;
  outline: none;
  transition: all 0.2s ease;
  background-color: #f8fbff;
  resize: vertical; /* 세로로만 크기 조절 */
  line-height: 1.4;
}

/* placeholder */
.base-textarea textarea::placeholder {
  color: #9aa5b1;
}

/* focus 스타일 */
.base-textarea textarea:focus {
  border-color: #0c7bce;
  background-color: #ffffff;
  box-shadow: 0 0 0 3px rgba(12, 123, 206, 0.15);
}

/* hover 스타일 */
.base-textarea textarea:hover {
  border-color: #7a8ea5;
}
</style>
