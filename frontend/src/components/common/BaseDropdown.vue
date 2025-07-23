<template>
  <div class="dropdown-container" v-click-outside="close">
    <button class="dropdown-button" @click="toggle">
      ▼ {{ selectedLabel || label }}
    </button>
    <ul v-if="isOpen" class="dropdown-menu">
      <li v-for="option in options" :key="option.codeDetail" @click="select(option)" >
        {{ option.codeDetailNm }}
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

interface Option {
  codeDetailNm: string;
  codeDetail: string | number;
}

interface Props {
  label: string;
  modelValue: string | number;
  options: Option[];
}

const props = defineProps<Props>();
const emit = defineEmits([ 'update:modelValue'
                         , 'change'  
                        ]);

const isOpen = ref(false);
const selectedLabel = ref('');

const toggle = () => {
  isOpen.value = !isOpen.value;
};

const close = () => {
  isOpen.value = false;
};

const select = (option: Option) => {
  selectedLabel.value = option.codeDetailNm;
  emit('update:modelValue', option.codeDetail);
  emit('change', option);
  isOpen.value = false;
};
</script>

<style scoped>
.dropdown-container {
  position: relative;
}
.dropdown-button {
  border: 2px solid #1c2a40;
  padding: 5px 10px;
  background: white;
  cursor: pointer;
  width: 7rem;
  text-align: left;
  overflow: hidden;
  white-space: nowrap;
}
.dropdown-menu {
  position: absolute;
  top: 100%;
  left: 0;
  border: 1px solid #ccc;
  background: white;
  z-index: 1000;
  width: auto;
  min-width: 100%;
}
.dropdown-menu li {
  padding: 8px;
  cursor: pointer;
  list-style: none;
  width: max-content;
  min-width: 100%;
}
.dropdown-menu li:hover {
  background-color: #f0f0f0;
}
</style>
