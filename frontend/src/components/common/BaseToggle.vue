<template>
  <div class="base-toggle" :style="styleProp">
    <label v-if="label" class="toggle-label">
      {{ label }}
    </label>

    <button
      type="button"
      class="toggle-switch"
      :class="{
        on: isOn,
        off: !isOn,
        disabled: disabled || readonly
      }"
      :disabled="disabled"
      @click="toggle"
    >

      <!-- ON 텍스트 -->
      <span v-if="onText" class="toggle-text on-text">
        {{ onText }}
      </span>
      <span class="toggle-thumb"></span>
      <!-- OFF 텍스트 -->
      <span v-if="offText" class="toggle-text off-text">
        {{ offText }}
      </span>

    </button>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';

/* =========================
 * Props
 * ========================= */
interface Props {
  modelValue: boolean | string;
  label?: string;
  disabled?: boolean;
  readonly?: boolean;
  width?: string | number;
  height?: string | number;
  onText?: string;
  offText?: string;
}

const props = defineProps<Props>();

/* =========================
 * Emits
 * ========================= */
const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean | 'Y' | 'N'): void;
}>();

/* =========================
 * 내부 상태 (항상 boolean)
 * ========================= */
const isOn = computed<boolean>(() => {
  if (typeof props.modelValue === 'boolean') {
    return props.modelValue;
  } else if(props.modelValue === 'Y'){
    return true;
  } else {
    return false;
  }
});

/* =========================
 * Style
 * ========================= */
const styleProp = computed(() => ({
  width: props.width,
  height: props.height,
}));

/* =========================
 * Toggle
 * ========================= */
const toggle = () => {
  if (props.disabled || props.readonly) return;

  // 원래 타입 유지
  if (typeof props.modelValue === 'boolean') {
    emit('update:modelValue', !props.modelValue);
  } else {
    emit('update:modelValue', props.modelValue === 'Y' ? 'N' : 'Y');
  }
};
</script>

<style scoped>
.base-toggle {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
}

.toggle-label {
  font-size: 0.85rem;
  font-weight: 600;
  color: #0c254d;
}

.toggle-switch {
  position: relative;
  min-width: 4.2rem;
  height: 1.6rem;
  border-radius: 0.5rem;
  border: 1.5px solid #b8c4d1;
  background-color: #e5e7eb;
  cursor: pointer;
  transition: all 0.25s ease;
  padding: 0 0.5rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.toggle-thumb {
  position: absolute;
  top: 50%;
  left: 0.2rem;
  width: 1.2rem;
  height: 1.2rem;
  background-color: #ffffff;
  border-radius: 50%;
  transform: translateY(-50%);
  transition: all 0.25s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.15);
  z-index: 2;
}

.toggle-text {
  font-size: 0.9rem;
  font-weight: 700;
  user-select: none;
  z-index: 1;
}

.off-text {
  color: #374151;
}

.on-text {
  color: #ffffff;
}

/* ON */
.toggle-switch.on {
  background-color: #0c7bce;
  border-color: #0c7bce;
}

.toggle-switch.on .toggle-thumb {
  left: calc(100% - 1.4rem);
}

/* OFF */
.toggle-switch.off {
  background-color: #e5e7eb;
}

/* hover */
.toggle-switch:hover:not(.disabled) {
  box-shadow: 0 0 0 3px rgba(12, 123, 206, 0.15);
}

/* disabled */
.toggle-switch.disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
