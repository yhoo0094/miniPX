<!-- 사용예시
<BaseButton variant="primary" size="md">저장</BaseButton>
<BaseButton variant="secondary" size="md">취소</BaseButton>
<BaseButton variant="danger" size="md">삭제</BaseButton>
<BaseButton variant="ghost" size="sm">닫기</BaseButton>
<BaseButton variant="secondary" noHover>Hover 없는 취소</BaseButton>
<BaseButton variant="primary" block>가로 100%</BaseButton> 
-->

<template>
  <button
    class="base-button"
    :class="[
      `base-button--${variant}`,
      `base-button--${size}`,
      { 'is-block': block, 'no-hover': noHover }
    ]"
    :type="type"
    :disabled="disabled"
    @click="onClick"
    :style="inlineStyle"
  >
    <slot />
  </button>
</template>

<script setup lang="ts">
import { computed } from "vue";

interface Props {
  type?: "button" | "submit" | "reset";
  disabled?: boolean;
  stop?: boolean;
  prevent?: boolean;

  // ✅ 팀 공통 토큰
  variant?: "primary" | "secondary" | "danger" | "ghost";
  size?: "sm" | "md" | "lg";
  block?: boolean;

  // ✅ 필요 시 예외적으로만 사용 (가능하면 size로 통일)
  width?: string | number;
  height?: string | number;

  // ✅ hover 제거 옵션 (요청하신 케이스)
  noHover?: boolean;
}

const props = defineProps<Props>();
const emit = defineEmits<{ (e: 'click', ev: MouseEvent): void }>();

const onClick = (ev: MouseEvent) => {
  if (props.prevent) ev.preventDefault();
  if (props.stop) ev.stopPropagation(); 
  emit('click', ev);
};

const variant = computed(() => props.variant ?? "primary");
const size = computed(() => props.size ?? "md");

const inlineStyle = computed(() => ({
  ...(props.width ? { width: typeof props.width === "number" ? `${props.width}px` : props.width } : {}),
  ...(props.height ? { height: typeof props.height === "number" ? `${props.height}px` : props.height } : {}),
}));
</script>

<style scoped>
/* =========================================================
  1) 공통 베이스
========================================================= */
.base-button {
  border: none;
  border-radius: 0.5rem;
  font-size: 0.95rem;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s ease, box-shadow 0.2s ease, transform 0.08s ease;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  user-select: none;
}

/* block */
.base-button.is-block {
  width: 100%;
}

/* =========================================================
  2) 사이즈 토큰
========================================================= */
.base-button--sm { height: 1.8rem; padding: 0 0.7rem; font-size: 0.85rem; }
.base-button--md { height: 2.0rem; padding: 0 0.9rem; font-size: 0.95rem; }
.base-button--lg { height: 2.4rem; padding: 0 1.1rem; font-size: 1.0rem; }

/* =========================================================
  3) variant 토큰(색/hover/active/disabled까지 세트로)
========================================================= */
/* primary */
.base-button--primary {
  background-color: #0c254d;
  color: #fff;
  box-shadow: 0 2px 6px rgba(12, 37, 77, 0.2);
}
.base-button--primary:hover {
  background-color: #123a77;
  box-shadow: 0 3px 8px rgba(12, 37, 77, 0.3);
}
.base-button--primary:active {
  background-color: #0a1e3f;
  transform: translateY(1px);
}

/* secondary */
.base-button--secondary {
  background-color: #e8edf5;
  color: #0c254d;
  box-shadow: 0 2px 6px rgba(12, 37, 77, 0.12);
}
.base-button--secondary:hover { background-color: #dbe3f1; }
.base-button--secondary:active { background-color: #cfd9eb; transform: translateY(1px); }

/* danger */
.base-button--danger {
  background-color: #d64545;
  color: #fff;
  box-shadow: 0 2px 6px rgba(214, 69, 69, 0.2);
}
.base-button--danger:hover { background-color: #e05757; }
.base-button--danger:active { background-color: #b83a3a; transform: translateY(1px); }

/* ghost (테두리만) */
.base-button--ghost {
  background-color: transparent;
  color: #0c254d;
  border: 1px solid rgba(12, 37, 77, 0.35);
  box-shadow: none;
}
.base-button--ghost:hover { background-color: rgba(12, 37, 77, 0.06); }
.base-button--ghost:active { background-color: rgba(12, 37, 77, 0.12); transform: translateY(1px); }

/* disabled 공통 */
.base-button:disabled {
  background-color: #b8c4d1 !important;
  color: #ffffff !important;
  cursor: not-allowed;
  box-shadow: none !important;
  border-color: transparent !important;
  transform: none !important;
}

/* =========================================================
  4) hover 효과 제거 옵션 (noHover)
========================================================= */
.base-button.no-hover:hover {
  background-color: inherit;
  box-shadow: inherit;
  transform: none;
}
</style>
