<template>
  <img
    ref="imgEl"
    :src="loadedSrc"
    :alt="alt"
    loading="lazy"
    decoding="async"
    v-bind="$attrs"
  />
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, watch } from "vue";

const props = defineProps<{
  src: string;
  alt?: string;
}>();

const imgEl = ref<HTMLImageElement | null>(null);
const loadedSrc = ref<string>("");

let observer: IntersectionObserver | null = null;

const startObserve = () => {
  // src가 없으면 아무것도 안 함
  if (!props.src) return;

  // IO 미지원 브라우저면 즉시 로드
  if (!("IntersectionObserver" in window)) {
    loadedSrc.value = props.src;
    return;
  }

  // 이전 observer 정리
  observer?.disconnect();
  observer = new IntersectionObserver(
    ([entry]) => {
      if (entry?.isIntersecting) {
        loadedSrc.value = props.src; // 이 시점에 네트워크 요청 발생
        observer?.disconnect();
        observer = null;
      }
    },
    {
      root: null,
      rootMargin: "200px 0px",
      threshold: 0.01,
    }
  );

  if (imgEl.value) observer.observe(imgEl.value);
};

onMounted(() => {
  startObserve();
});

onBeforeUnmount(() => {
  observer?.disconnect();
  observer = null;
});

// 리스트 재조회/필터 변경 등으로 src가 바뀌는 경우 재관찰
watch(
  () => props.src,
  () => {
    loadedSrc.value = ""; // 다시 빈값으로 만들어야 재로딩이 "지연"됨
    startObserve();
  }
);
</script>
