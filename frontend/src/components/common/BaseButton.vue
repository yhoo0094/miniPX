<template>
  <button
    class="base-button"
    :type="type"
    :disabled="disabled"
    @click="$emit('click')"
  >
    <slot />
  </button>
</template>

<script setup lang="ts">
// ✅ Props 타입 선언
interface Props {
  type?: 'button' | 'submit' | 'reset';
  disabled?: boolean;
}

// ✅ Emits 타입 선언
const emit = defineEmits<{
  (e: 'click'): void;
}>();

const props = defineProps<Props>();
</script>

<style scoped>
.base-button {
  width: 100px;
  height: 60px;
  background-color: white;
  border: 2px solid #0c254d;
  cursor: pointer;
}
.base-button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}
</style>
