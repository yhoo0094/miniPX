<template>
    <label class="checkbox-label">
      <input type="checkbox" v-model="modelValue" />
      <slot />
    </label>
  </template>
  
  <script setup>
  defineProps({
    modelValue: Boolean
  })
  defineEmits(['update:modelValue'])
  </script>
  
  <style scoped>
  .checkbox-label {
    display: flex;
    align-items: center;
    gap: 5px;
  }
  </style>  