<template>
  <div class="dropdown-container" v-click-outside="close">
    <button class="dropdown-button" 
            :class="{ disabled: disabled }"
            @click="toggle" 
            type="button"
            :disabled="disabled">
      <span class="dropdown-label">
        {{ selectedLabel || label }}
      </span>
      <span
        class="dropdown-caret"
        :class="[{ open: isOpen }, { 'is-sort': props.caretText === '⇅' }]"
      >
        {{ props.caretText }}
      </span>
    </button>

    <ul v-if="isOpen" class="dropdown-menu">
      <li
        v-for="option in displayOptions"
        :key="option.codeDetail"
        class="dropdown-item"
        :class="{ selected: option.codeDetail === props.modelValue }"
        @click="select(option)"
      >
        {{ option.codeDetailNm }}
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch} from 'vue';

interface Option {
  codeDetailNm: string;
  codeDetail: string | number;
}

interface Props {
  label: string;
  modelValue: string | number;
  options: Option[];
  showPlaceholder?: boolean;      // 선택 옵션 표시 여부
  placeholderLabel?: string;      // 선택 옵션 텍스트 (기본: '선택')  
  disabled?: boolean;
  caretText?: string; // 기본 '▼', 정렬용은 '⇅'
}

const props = withDefaults(defineProps<Props>(), {
  showPlaceholder: false,
  placeholderLabel: '선택',
  disabled: false,
  caretText: '▼',
});
const emit = defineEmits(['update:modelValue', 'change']);

const isOpen = ref(false);
const selectedLabel = ref('');

// 옵션 목록 + '선택' 옵션을 합친 목록
const displayOptions = computed<Option[]>(() => {
  if (!props.showPlaceholder) {
    return props.options;
  }

  const placeholderOption: Option = {
    codeDetailNm: props.placeholderLabel,
    codeDetail: '' as string, // placeholder 선택 시 modelValue를 ''로 사용
  };

  return [placeholderOption, ...props.options];
});

// 외부에서 modelValue가 바뀌었을 때 label 동기화
watch(
  () => props.modelValue,
  (newVal) => {
    // 🔹 아무것도 선택 안 된 상태는 selectedLabel을 비워둔다
    if (newVal === '' || newVal === null || newVal === undefined) {
      selectedLabel.value = '';
      return;
    }

    const found = displayOptions.value.find(
      (opt) => opt.codeDetail === newVal
    );

    selectedLabel.value = found?.codeDetailNm || '';
  },
  { immediate: true }
);

const toggle = () => {
  if (props.disabled) return;
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
  display: inline-block;
  font-size: 0.9rem;
}

/* 버튼 */
.dropdown-button {
  display: inline-flex;
  align-items: center;
  justify-content: space-between;
  gap: 6px;
  min-width: 7rem;
  padding: 6px 10px;
  border-radius: 8px;
  border: 1px solid #cbd5e1;
  background: #ffffff;
  cursor: pointer;
  text-align: left;
  outline: none;
  transition: border-color 0.18s ease, box-shadow 0.18s ease, background-color 0.18s ease,
    transform 0.08s ease;
}

.dropdown-button:hover {
  border-color: #3b82f6;
  box-shadow: 0 0 0 1px rgba(59, 130, 246, 0.15);
  background-color: #f8fafc;
}

.dropdown-button:active {
  transform: translateY(1px);
  box-shadow: 0 0 0 1px rgba(59, 130, 246, 0.08);
}

/* 비활성화 상태 스타일 */
.dropdown-button.disabled,
.dropdown-button:disabled {
  background-color: #f3f4f6;
  border-color: #e5e7eb;
  color: #9ca3af;
  cursor: not-allowed;
  box-shadow: none;
}

/* 버튼 안 텍스트 */
.dropdown-label {
  flex: 1;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: #0f172a;
}

/* 버튼의 화살표 아이콘 */
.dropdown-caret {
  font-size: 0.7rem;
  color: #64748b;
  transition: transform 0.15s ease;
}

.dropdown-caret.open {
  transform: rotate(180deg);
}

/* 정렬 아이콘(⇅)은 아이콘처럼 + 회전 금지 */
.dropdown-caret.is-sort {
  width: 1.5rem;
  height: 1.5rem;
  border-radius: 999px;
  display: inline-flex;
  align-items: center;
  justify-content: center;

  /* 아이콘처럼 보이게 */
  font-size: 0.85rem;
  font-weight: 800;
  line-height: 1;
  letter-spacing: -0.05em;

  color: #334155;

  /* 회전 애니메이션 자체를 꺼버림 */
  transition: none;
}

/* 혹시 open 클래스가 같이 붙어도 회전 안 되게 강제 */
.dropdown-caret.is-sort.open {
  transform: none;
}

/* 메뉴 */
.dropdown-menu {
  position: absolute;
  top: calc(100% + 4px);
  left: 0;
  z-index: 1000;
  margin: 0;
  padding: 4px 0;
  list-style: none;
  background: #ffffff;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 10px 25px rgba(15, 23, 42, 0.18);
  min-width: 100%;
  max-height: 260px;
  overflow-y: auto;
  box-sizing: border-box;
}

/* 메뉴 항목 */
.dropdown-item {
  padding: 8px 10px;
  cursor: pointer;
  white-space: nowrap;
  font-size: 0.9rem;
  color: #0f172a;
  transition: background-color 0.12s ease, color 0.12s ease;
}

.dropdown-item:hover {
  background-color: #eff6ff;
}

/* 선택된 항목 표시 */
.dropdown-item.selected {
  background-color: #dbeafe;
  color: #1d4ed8;
  font-weight: 600;
}

/* 스크롤바 약간 정리 (웹킷 기준) */
.dropdown-menu::-webkit-scrollbar {
  width: 6px;
}

.dropdown-menu::-webkit-scrollbar-track {
  background: transparent;
}

.dropdown-menu::-webkit-scrollbar-thumb {
  background: rgba(148, 163, 184, 0.7);
  border-radius: 999px;
}
</style>
