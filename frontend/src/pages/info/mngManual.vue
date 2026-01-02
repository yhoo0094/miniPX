<template>
  <div class="mng-manual-container">
    <BaseToast ref="toastRef" />

    <h2 class="title">{{ isModify ? '매뉴얼 수정' : '매뉴얼 등록' }}</h2>

    <!-- 관리자 전용 (항상 입력 모드) -->
    <form class="manual-form" @submit.prevent="upsertManual">
      <div class="form-row">
        <label class="label">제목</label>
        <div class="field-wrap">
          <BaseInput v-model="manual.manualTitle" placeholder="매뉴얼 제목을 입력하세요." />
        </div>
      </div>

      <div class="form-row">
        <label class="label">구분</label>
        <div class="field-wrap small">
          <BaseDropdown
            label="구분"
            v-model="manual.manualDvcd"
            :options="manualDvcdCodes"
            :showPlaceholder="true"
            placeholderLabel="선택"
          />
        </div>
      </div>

      <div class="form-row">
        <label class="label">사용여부</label>
        <div class="field-wrap">
          <BaseToggle v-model="manual.useYn" onText="Y" offText="N" />
        </div>
      </div>

      <div class="form-row">
        <label class="label">내용</label>
        <div class="field-wrap">
          <div class="quill-wrap">
            <QuillEditor
              v-model:content="manual.manualContent"
              contentType="html"
              theme="snow"
              :options="editorOptions"
              class="quill-editor"
            />
          </div>
        </div>
      </div>

      <!-- 버튼 -->
      <div class="form-actions">
        <BaseButton type="submit" class="action-btn">
          저장
        </BaseButton>

        <BaseButton
          v-if="isModify"
          variant="danger"
          type="button"
          class="action-btn"
          @click="deleteManual"
        >
          삭제
        </BaseButton>

        <BaseButton variant="secondary" type="button" class="action-btn" @click="goBack">
          목록
        </BaseButton>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useUserStore } from '@/stores/userStore';
import api from '@/plugins/axios';
import BaseInput from '@/components/common/BaseInput.vue';
import BaseButton from '@/components/common/BaseButton.vue';
import BaseToast from '@/components/common/BaseToast.vue';
import BaseToggle from '@/components/common/BaseToggle.vue';
import BaseDropdown from '@/components/common/BaseDropdown.vue';
import Constant from '@/constants/constant';
import { useUiStore } from '@/stores/uiStore';
import { getCodeList } from '@/api/code';

// ✅ VueQuill
import { QuillEditor } from '@vueup/vue-quill';
import '@vueup/vue-quill/dist/vue-quill.snow.css';

type ManualDetail = {
  manualSeq: number | null;
  manualTitle: string;
  manualContent: string; // HTML
  manualDvcd: string;    // 01/02/03/99
  useYn: 'Y' | 'N';
};

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();
const uiStore = useUiStore();
const toastRef = ref<any>();

// 권한 (프로젝트 기준: authLv > 1이 관리자)
const authLv = computed(() => userStore.currentAuthLv);

// url 파라미터 (manualSeq 있으면 수정, 없으면 등록)
const manualSeq = computed(() => {
  const v = route.query.manualSeq;
  if (!v) return null;
  const n = Number(v);
  return Number.isFinite(n) ? n : null;
});
const isModify = computed(() => manualSeq.value !== null);

// 폼 기본값
const DEFAULT_MANUAL: ManualDetail = {
  manualSeq: null,
  manualTitle: '',
  manualContent: '<p></p>', // ✅ Quill 안정 초기값
  manualDvcd: '',
  useYn: 'Y',
};

const manual = ref<ManualDetail>({ ...DEFAULT_MANUAL });

// 구분 코드
const manualDvcdCodes = ref<{ codeDetailNm: string; codeDetail: string }[]>([]);

// Quill 설정
const editorOptions = ref({
  modules: {
    toolbar: [
      [{ header: [1, 2, 3, false] }],
      ['bold', 'italic', 'underline', 'strike'],
      [{ color: [] }, { background: [] }],
      [{ list: 'ordered' }, { list: 'bullet' }],
      [{ align: [] }],
      ['link'],
      ['clean'],
    ],
  },
  placeholder: '내용을 입력하세요.',
  readOnly: false,
  theme: 'snow',
});

// 관리자 체크
const guardAdmin = () => {
  if (authLv.value <= 1) {
    toastRef.value?.showToast('관리자만 접근할 수 있습니다.');
    router.replace('/');
    return false;
  }
  return true;
};

// 코드 조회
const loadCodes = async () => {
  try {
    manualDvcdCodes.value = await getCodeList('MANUAL_DVCD');
    if (!manualDvcdCodes.value || manualDvcdCodes.value.length === 0) {
      manualDvcdCodes.value = fallbackDvcdCodes();
    }
  } catch (e) {
    manualDvcdCodes.value = fallbackDvcdCodes();
  }
};

const fallbackDvcdCodes = () => ([
  { codeDetailNm: '마켓', codeDetail: '01' },
  { codeDetailNm: '정보', codeDetail: '02' },
  { codeDetailNm: 'AI',   codeDetail: '03' },
  { codeDetailNm: '기타', codeDetail: '99' },
]);

// 상세 조회
const getManualDetail = async () => {
  if (!manualSeq.value) return;

  try {
    uiStore.showLoading('매뉴얼 정보를 조회 중입니다...');

    const payload = { manualSeq: manualSeq.value };
    const response = await api.post('/manual/getManualDetail', payload);

    if (response.data?.RESULT === Constant.RESULT_SUCCESS) {
      const data = response.data?.OUT_DATA || null;
      if (!data) {
        toastRef.value?.showToast('매뉴얼 정보를 찾을 수 없습니다.');
        return;
      }

      manual.value = {
        manualSeq: data.manualSeq ?? manualSeq.value,
        manualTitle: data.manualTitle ?? '',
        manualContent: data.manualContent ?? '<p></p>',
        manualDvcd: data.manualDvcd ?? '',
        useYn: (data.useYn ?? 'Y') as 'Y' | 'N',
      };
    } else {
      toastRef.value?.showToast(response.data?.OUT_RESULT_MSG || '처리 실패했습니다.');
    }
  } catch (e) {
    console.error(e);
    toastRef.value?.showToast('매뉴얼 조회 중 오류가 발생했습니다.');
  } finally {
    uiStore.hideLoading();
  }
};

// 유효성 검사
const validateForm = () => {
  if (!manual.value.manualTitle.trim()) {
    toastRef.value?.showToast('제목을 입력하세요.');
    return false;
  }
  if (!manual.value.manualDvcd) {
    toastRef.value?.showToast('구분을 선택하세요.');
    return false;
  }
  const plain = (manual.value.manualContent || '').replace(/<[^>]*>/g, '').trim();
  if (!plain) {
    toastRef.value?.showToast('내용을 입력하세요.');
    return false;
  }
  return true;
};

// 저장
const upsertManual = async () => {
  if (!validateForm()) return;
  if (!confirm('저장하시겠습니까?')) return;

  try {
    uiStore.showLoading('저장 중입니다...');

    const payload = {
      manualSeq: manualSeq.value,
      manualTitle: manual.value.manualTitle,
      manualContent: manual.value.manualContent,
      manualDvcd: manual.value.manualDvcd,
      useYn: manual.value.useYn,
    };

    const response = await api.post('/manual/upsertManual', payload);

    if (response.data?.RESULT === Constant.RESULT_SUCCESS) {
      toastRef.value?.showToast('저장되었습니다.');

      const savedSeq =
        response.data?.OUT_DATA?.manualSeq ??
        response.data?.OUT_DATA?.['manualSeq'] ??
        manualSeq.value;

      if (!manualSeq.value && savedSeq) {
        router.replace({ query: { ...route.query, manualSeq: String(savedSeq) } });
      }

      if (savedSeq) await getManualDetail();
    } else {
      toastRef.value?.showToast(response.data?.OUT_RESULT_MSG || '저장에 실패했습니다.');
    }
  } catch (e) {
    console.error(e);
    toastRef.value?.showToast('저장 중 오류가 발생했습니다.');
  } finally {
    uiStore.hideLoading();
  }
};

// 삭제
const deleteManual = async () => {
  if (!manualSeq.value) return;
  if (!confirm('삭제하시겠습니까?')) return;

  try {
    uiStore.showLoading('삭제 중입니다...');

    const payload = { manualSeq: manualSeq.value };
    const response = await api.post('/manual/deleteManual', payload);

    if (response.data?.RESULT === Constant.RESULT_SUCCESS) {
      toastRef.value?.showToast('삭제되었습니다.');
      goBack();
    } else {
      toastRef.value?.showToast(response.data?.OUT_RESULT_MSG || '삭제에 실패했습니다.');
    }
  } catch (e) {
    console.error(e);
    toastRef.value?.showToast('삭제 중 오류가 발생했습니다.');
  } finally {
    uiStore.hideLoading();
  }
};

const goBack = () => {
  router.push('/manual');
};

onMounted(async () => {
  if (!guardAdmin()) return;

  await loadCodes();

  if (isModify.value) {
    await getManualDetail();
  } else {
    manual.value = { ...DEFAULT_MANUAL };
  }
});
</script>

<style scoped>
.mng-manual-container {
  padding: 24px 28px;
  background: #f9fbff;
  border-radius: 16px;
  box-shadow: 0 8px 18px rgba(15, 23, 42, 0.08);
  box-sizing: border-box;
}

.title {
  margin: 0 0 18px;
  font-size: 1.3rem;
  font-weight: 700;
  color: #0f172a;
}

.manual-form {
  background: #ffffff;
  border-radius: 12px;
  padding: 20px 22px 16px;
  border: 1px solid #e2e8f0;
}

.form-row {
  display: flex;
  align-items: flex-start;
  margin-bottom: 16px;
}

.label {
  width: 90px;
  font-size: 0.9rem;
  font-weight: bold;
  text-align: right;
  margin-right: 1rem;
}

.field-wrap {
  flex: 1;
  font-size: 0.9rem;
}

.field-wrap.small {
  max-width: 220px;
}

/* Quill */
.quill-wrap {
  height: 25rem;
  border: 1px solid #cbd5e1;
  border-radius: 10px;
  overflow: hidden;
  background: #fff;
}

.quill-editor {
  min-height: 16rem;
}

/* 버튼 */
.form-actions {
  margin-top: 8px;
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
}

.action-btn {
  width: 7rem;
  height: 2.5rem;
}
</style>
