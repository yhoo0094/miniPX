<template>
  <div class="base-input">
    <label :for="id">{{ label }}</label>
    <input
      :id="id"
      :type="type"
      :value="modelValue"
      @input="$emit('update:modelValue', ($event.target as HTMLInputElement)?.value)"
      :placeholder="placeholder"
    />
  </div>
</template>

<script setup lang="ts">
// ✅ Props 타입 정의
interface Props {
  id?: string;
  label: string;
  type?: string;
  modelValue: string;
  placeholder?: string;
}

// ✅ Emits 타입 정의
const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void;
}>();

const props = defineProps<Props>();
</script>

<style scoped>
.base-input {
  display: flex;
  align-items: center;
}
.base-input label {
  width: 80px;
  margin-right: 10px;
}
.base-input input {
  flex-grow: 1;
  height: 2rem;
  padding: 5px;
  border: 2px solid #0c254d;
}
</style>
