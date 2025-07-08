<template>
  <label class="checkbox-label">
    <input
      type="checkbox"
      class="checkbox-input"
      :checked="modelValue"
      @change="$emit('update:modelValue', ($event.target as HTMLInputElement).checked)"
    />
    <slot />
  </label>
</template>

<script setup lang="ts">
// ✅ Props 타입 선언
interface Props {
  modelValue: boolean;
}

// ✅ Emits 타입 선언
const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void;
}>();

const props = defineProps<Props>();
</script>

<style scoped>
.checkbox-label {
  display: flex;
  align-items: center;
  gap: 5px;
}

.checkbox-input {
  width: 1rem;
  height: 1rem;
}
</style>
