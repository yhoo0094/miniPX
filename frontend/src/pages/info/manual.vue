<template>
  <BaseToast ref="toastRef" />

  <div class="manual-page-wrap">
    <!-- ===== 상단 헤더 ===== -->
    <div class="manual-header">
      <div class="manual-title-main">
        <span class="manual-title-pill">
          사용 매뉴얼
        </span>
      </div>
    </div>

    <!-- ===== 검색 영역 ===== -->
    <section class="manual-search">
      <div class="search-row">
        <div class="search-group">
          <BaseDropdown
            label="구분"
            v-model="manualDvcd"
            :options="manualDvcdCodes"
            :showPlaceholder="true"
            placeholderLabel="전체"
            @change="getManualList(true)"
          />

          <div class="search-group flex">
            <BaseInput
              v-model="searchKeyword"
              class="search-text"
              height="2.125rem"
              placeholder="제목/내용을 입력하세요."
              @keydown.enter.prevent="getManualList(true)"
            />

            <BaseButton
              width="5rem"
              height="2.125rem"
              type="button"
              @click="getManualList(true)"
            >
              검색
            </BaseButton>
          </div>
        </div>
      </div>
    </section>

    <!-- ===== 목록(아코디언) ===== -->
    <section class="manual-list">
      <div class="accordion-wrap">
        <div
          v-for="row in manuals"
          :key="row.manualSeq"
          class="accordion-item"
        >
          <button
            type="button"
            class="accordion-head"
            @click="toggleAccordion(row.manualSeq)"
          >
            <div class="accordion-title">
              <span class="badge" :class="badgeClass(row.manualDvcd)">
                [{{ dvcdLabel(row.manualDvcd) }}]
              </span>
              <span class="title-text" :title="row.manualTitle">
                {{ row.manualTitle }}
              </span>
            </div>

            <span class="caret" :class="{ open: expandedSeq === row.manualSeq }">▼</span>
          </button>

          <div v-if="expandedSeq === row.manualSeq" class="accordion-body">
            <pre class="manual-content">{{ row.manualContent }}</pre>
          </div>
        </div>

        <div v-if="manuals.length === 0" class="empty-box">
          조회된 매뉴얼이 없습니다.
        </div>
      </div>

      <!-- ✅ pagination (요청 로그 화면과 동일 UI 패턴) -->
      <div class="pagination">
        <div class="pagination-left">
          총 <b>{{ totalCount.toLocaleString() }}</b>건
        </div>

        <div class="pagination-right">
          <BaseButton
            class="pagination-btn"
            :disabled="page <= 1"
            @click="goPage(1)"
          >
            <<
          </BaseButton>

          <BaseButton
            class="pagination-btn"
            :disabled="page <= 1"
            @click="goPage(page - 1)"
          >
            <
          </BaseButton>

          <span class="page-info">
            {{ page }} / {{ totalPages }}
          </span>

          <BaseButton
            class="pagination-btn"
            :disabled="page >= totalPages"
            @click="goPage(page + 1)"
          >
            >
          </BaseButton>

          <BaseButton
            class="pagination-btn"
            :disabled="page >= totalPages"
            @click="goPage(totalPages)"
          >
            >>
          </BaseButton>
        </div>
      </div>
    </section>
    <!-- 버튼 -->
    <div v-if="authLv > 1" class="btn-box">
      <BaseButton type="button" height="2.125rem" @click="moveToMngManual" class="action-btn">
        등록
      </BaseButton>
    </div>  
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import api from '@/plugins/axios';
import BaseInput from '@/components/common/BaseInput.vue';
import BaseButton from '@/components/common/BaseButton.vue';
import BaseDropdown from '@/components/common/BaseDropdown.vue';
import BaseToast from '@/components/common/BaseToast.vue';
import Constant from '@/constants/constant';
import { useUiStore } from '@/stores/uiStore';
import type { ApiResponse } from '@/types/api/response';
import { getCodeList } from '@/api/code';
import { useUserStore } from '@/stores/userStore';
import router from '@/router';

type ManualRow = {
  manualSeq: number;
  manualTitle: string;
  manualContent: string;
  manualDvcd: string;     // 01/02/03/99
  useYn: 'Y' | 'N';
  fstRegSeq?: number;
  fstRegDtti?: string;
  lstUpdSeq?: number;
  lstUpdDtti?: string;
};

const toastRef = ref<any>();
const uiStore = useUiStore();
const userStore = useUserStore();
const authLv = computed(() => userStore.currentAuthLv);

// 검색 조건
const manualDvcd = ref('');      // 전체는 빈값
const searchKeyword = ref('');

// 목록
const manuals = ref<ManualRow[]>([]);

// 구분 코드
const manualDvcdCodes = ref<{ codeDetailNm: string; codeDetail: string }[]>([]);

// 아코디언 펼침
const expandedSeq = ref<number | null>(null);

// ✅ pagination
const page = ref(1);
const pageSize = ref(10);
const totalCount = ref(0);

onMounted(async () => {
  // 공통코드 조회 (없으면 fallback)
  try {
    manualDvcdCodes.value = await getCodeList('MANUAL_DVCD');
    if (!manualDvcdCodes.value || manualDvcdCodes.value.length === 0) {
      manualDvcdCodes.value = fallbackDvcdCodes();
    }
  } catch (e) {
    manualDvcdCodes.value = fallbackDvcdCodes();
  }

  getManualList(true);
});

const fallbackDvcdCodes = () => ([
  { codeDetailNm: '마켓', codeDetail: '01' },
  { codeDetailNm: '정보', codeDetail: '02' },
  { codeDetailNm: 'AI',   codeDetail: '03' },
  { codeDetailNm: '기타', codeDetail: '99' },
]);

const dvcdLabel = (dvcd: string) => {
  const hit = manualDvcdCodes.value.find(v => v.codeDetail === dvcd);
  return hit?.codeDetailNm ?? dvcd ?? '';
};

const badgeClass = (dvcd: string) => {
  if (dvcd === '01') return 'b-market';
  if (dvcd === '02') return 'b-info';
  if (dvcd === '03') return 'b-ai';
  return 'b-etc';
};

const toggleAccordion = (seq: number) => {
  expandedSeq.value = expandedSeq.value === seq ? null : seq;
};

// ✅ totalPages
const totalPages = computed(() => {
  if (totalCount.value === 0) return 1;
  return Math.ceil(totalCount.value / pageSize.value);
});

const goPage = (p: number) => {
  const safe = Math.min(Math.max(p, 1), totalPages.value);
  page.value = safe;
  getManualList(false);
};

// 조회
const getManualList = async (resetPage = false) => {
  if (resetPage) page.value = 1;
  expandedSeq.value = null;

  try {
    uiStore.showLoading('사용 매뉴얼을 조회 중입니다...');

    const payload = {
      manualDvcd: manualDvcd.value,        // ''이면 전체
      searchKeyword: searchKeyword.value,
      useYn: 'Y',                          // 기본: 사용중만
      page: page.value,                    // 1-based
      pageSize: pageSize.value,
      offset: (page.value - 1) * pageSize.value,
    };

    // ✅ 서버 엔드포인트는 프로젝트에 맞게 변경
    const response = await api.post<ApiResponse<any>>(
      '/manual/getManualList',
      payload
    );

    if (response.data?.RESULT === Constant.RESULT_SUCCESS) {
      // 서버 응답: OUT_DATA.list / OUT_DATA.totalCount 형태를 권장
      manuals.value = response.data?.OUT_DATA?.['list'] || [];
      totalCount.value = response.data?.OUT_DATA?.['totalCount'] || 0;

      // 방어: 혹시 서버가 totalCount 없이 list만 내려주면
      if (!totalCount.value && Array.isArray(response.data?.OUT_DATA)) {
        manuals.value = response.data.OUT_DATA;
        totalCount.value = manuals.value.length;
      }
    } else {
      toastRef.value?.showToast(response.data?.OUT_RESULT_MSG || '처리 실패했습니다.');
      manuals.value = [];
      totalCount.value = 0;
    }
  } catch (e) {
    console.error(e);
    toastRef.value?.showToast('사용 매뉴얼 조회 중 오류가 발생했습니다.');
    manuals.value = [];
    totalCount.value = 0;
  } finally {
    uiStore.hideLoading();
  }
};

//매뉴얼 등록 화면 이동
const moveToMngManual = () => {
  router.push({
    path: '/info/mngManual',
  });
};
</script>

<style scoped>
/* =======================
   전체 페이지 래퍼
   ======================= */
.manual-page-wrap {
  padding: 1rem;
  box-sizing: border-box;
  background: #f9fbff;
  border-radius: 1rem;
  box-shadow: 0 0.5rem 1rem rgba(15, 23, 42, 0.08);
}

/* =======================
   상단 헤더
   ======================= */
.manual-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 1rem 1rem;
}

.manual-title-main {
  display: flex;
  flex-direction: row;
  align-items: center;
  width: 100%;
  gap: 0.75rem;
}

.manual-title-pill {
  display: inline-flex;
  align-items: center;
  padding: 0.375rem 0.875rem;
  font-size: 1.3rem;
  font-weight: bold;
  letter-spacing: 0.02rem;
}

/* =======================
   검색 영역
   ======================= */
.manual-search {
  margin-bottom: 1rem;
}

.search-row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 0.5rem;
  padding: 1rem;
  border-radius: 1rem;
  border: 0.0625rem solid #e2e8f0;
  background-color: #ffffff;
  box-shadow: 0 0.5rem 0.5rem rgba(15, 23, 42, 0.06);
}

.search-group {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex-wrap: nowrap;
  white-space: nowrap;
  width: 100%;
}

.flex {
  flex: 1;
}

.search-text {
  flex: 1;
  min-width: 0;
}

/* =======================
   아코디언 영역
   ======================= */
.accordion-wrap {
  border-radius: 0.75rem;
  border: 0.0625rem solid #e2e8f0;
  background: #ffffff;
  box-shadow: 0 0.5rem 0.5rem rgba(15, 23, 42, 0.06);
  overflow: hidden;
}

.accordion-item + .accordion-item {
  border-top: 1px solid #e2e8f0;
}

.accordion-head {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.75rem;
  padding: 0.9rem 1rem;
  background: #eef3ff;
  border: none;
  cursor: pointer;
  text-align: left;
}

.accordion-title {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  min-width: 0;
}

.badge {
  display: inline-flex;
  align-items: center;
  padding: 0.15rem 0.45rem;
  border-radius: 0.5rem;
  font-weight: 800;
  font-size: 0.9rem;
  white-space: nowrap;
}

.b-market { background: rgba(12, 37, 77, 0.12); color: #0c254d; }
.b-info   { background: rgba(2, 132, 199, 0.12); color: #0369a1; }
.b-ai     { background: rgba(22, 163, 74, 0.12); color: #15803d; }
.b-etc    { background: rgba(100, 116, 139, 0.12); color: #475569; }

.title-text {
  font-weight: 800;
  color: #0f172a;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.caret {
  flex: 0 0 auto;
  font-weight: 900;
  color: #0c254d;
  transition: transform 0.15s ease-in-out;
}

.caret.open {
  transform: rotate(180deg);
}

.accordion-body {
  padding: 0.9rem 1rem;
  background: #f7f9ff;
}

.manual-content {
  margin: 0;
  padding: 0.75rem;
  border-radius: 0.75rem;
  border: 0.0625rem solid #e2e8f0;
  background: #ffffff;
  color: #0f172a;
  font-size: 0.95rem;
  line-height: 1.5rem;
  white-space: pre-wrap;
  word-break: break-word;
}

/* empty */
.empty-box {
  padding: 1.25rem 0.5rem;
  text-align: center;
  color: #9ca3af;
  font-size: 0.9rem;
}

/* =======================
   페이지네이션 (요청 로그 화면과 동일)
   ======================= */
.pagination {
  display: flex;
  align-items: center;
  margin-top: 0.75rem;
  padding: 0 0.25rem;
}

.pagination-left {
  font-size: 0.9rem;
  color: #475569;
  position: absolute;
}

.pagination-right {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex: 1;
  justify-content: center;
}

.page-info {
  min-width: 5.5rem;
  text-align: center;
  font-weight: 700;
  color: #0f172a;
}

.pagination-btn {
  width: 3rem;
  height: 2.125rem;
}

.btn-box{
  text-align: right;
  height: 3rem;
  vertical-align: middle;
  line-height: 3rem;  
}

/* 반응형 */
@media (max-width: 900px) {
  .search-group {
    flex-wrap: wrap;
    white-space: normal;
  }
  .accordion-head {
    padding: 0.8rem 0.85rem;
  }
}
</style>
