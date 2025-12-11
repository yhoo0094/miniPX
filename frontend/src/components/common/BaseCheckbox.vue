<template>
  <label class="checkbox-wrapper" :class="{ disabled }">
    <input
      type="checkbox"
      :checked="modelValue"
      :disabled="disabled"
      @change="emitChange"
    />
    <span class="checkbox-ui"></span>
    <span class="checkbox-label">{{ label }}</span>
  </label>
</template>

<script setup lang="ts">
const props = defineProps({
  modelValue: { type: Boolean, required: true },
  label: { type: String, default: "" },
  disabled: { type: Boolean, default: false }
});

const emit = defineEmits(["update:modelValue"]);

const emitChange = (event: Event) => {
  const value = (event.target as HTMLInputElement).checked;
  emit("update:modelValue", value);
};
</script>

<style scoped>
/* 전체 wrapper */
.checkbox-wrapper {
  display: flex;
  align-items: center;
  gap: 0.45rem;
  cursor: pointer;
  user-select: none;
  font-size: 0.9rem;
  font-weight: 600;
  color: #0c254d;
  margin-bottom: 0.35rem;
}

/* 비활성화 상태 */
.checkbox-wrapper.disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 기본 checkbox 숨김 */
.checkbox-wrapper input[type="checkbox"] {
  appearance: none;
  -webkit-appearance: none;
  position: absolute;
  width: 0;
  height: 0;
}

/* 커스텀 UI 박스 */
.checkbox-ui {
  width: 1rem;
  height: 1rem;
  border: 2px solid #008cff;
  border-radius: 4px;
  display: inline-block;
  position: relative;
  transition: all 0.2s ease;
}

/* 체크되었을 때 배경 */
.checkbox-wrapper input[type="checkbox"]:checked + .checkbox-ui {
  background-color: #008cff;
  border-color: #008cff;
}

/* 체크 표시 (✔) */
.checkbox-ui::after {
  content: "";
  position: absolute;
  left: 4px;
  top: 1px;
  width: 4px;
  height: 8px;
  border-right: 2px solid white;
  border-bottom: 2px solid white;
  transform: rotate(45deg);
  opacity: 0;
  transition: opacity 0.2s ease;
}

/* 체크되었을 때 표시 */
.checkbox-wrapper input[type="checkbox"]:checked + .checkbox-ui::after {
  opacity: 1;
}

/* 라벨 텍스트 */
.checkbox-label {
  color: #0c254d;
}
</style>
