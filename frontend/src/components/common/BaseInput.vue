<template>
    <div class="base-input">
      <label :for="id">{{ label }}</label>
      <input
        :id="id"
        :type="type"
        :value="modelValue"
        @input="$emit('update:modelValue', $event.target.value)"
        :placeholder="placeholder"
      />
    </div>
  </template>
  
  <script setup>
  defineProps({
    id: String,
    label: String,
    type: {
      type: String,
      default: 'text'
    },
    modelValue: String,
    placeholder: String,
  })
  defineEmits(['update:modelValue'])
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
    width: 250px;
    height: 30px;
    padding: 5px;
    border: 2px solid #0c254d;
  }
  </style>
  