<template>
  <BaseToast ref="toastRef" />

  <div class="loginlog-page-wrap">
    <!-- ===== 상단 헤더 ===== -->
    <div class="loginlog-header">
      <div class="loginlog-title-main">
        <span class="loginlog-title-pill">
          로그인 로그
        </span>
      </div>
    </div>

    <!-- ===== 검색 영역 ===== -->
    <section class="loginlog-search">
      <div class="search-row">
        <div class="search-group">
          <label class="search-label">일시</label>
          <BaseInput
            v-model="startDate"
            class="search-text date"
            type="date"
            height="2.125rem"
            @keydown.enter.prevent="getLoginLogList(true)"
          />
          <span class="search-tilde">~</span>
          <BaseInput
            v-model="endDate"
            class="search-text date"
            type="date"
            height="2.125rem"
            @keydown.enter.prevent="getLoginLogList(true)"
          />
          <BaseDropdown label="구분" v-model="loginCode" :options="loginCodes" @change="getLoginLogList(true)"
            :showPlaceholder="true" placeholderLabel="전체" />          
        </div>  
        <div class="search-group flex">
          <BaseInput
            v-model="searchKeyword"
            class="search-text"
            height="2.125rem"
            placeholder="아이디/아이피 입력"
            @keydown.enter.prevent="getLoginLogList(true)"
          />
          <BaseButton
            width="5rem"
            height="2.125rem"
            type="button"
            @click="getLoginLogList(true)"
          >
            검색
          </BaseButton>
        </div>
      </div>
    </section>

    <!-- ===== 테이블 ===== -->
    <section class="loginlog-list">
      <div class="table-wrap">
        <table class="loginlog-table">
          <thead>
            <tr>
              <th style="width: 25%;">일시</th>
              <th style="width: 25%;">아이디</th>
              <th style="width: 25%;">아이피</th>
              <th style="width: 25%;">구분</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in logs" :key="row.loginSeq">
              <td>{{ row.loginDtti }}</td>
              <td>{{ row.userId }}</td>
              <td>{{ row.ip }}</td>
              <td>{{ row.loginCodeNm }}</td>
            </tr>
            <tr v-if="logs.length === 0">
              <td class="empty" colspan="4">조회된 로그가 없습니다.</td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- ✅ pagination -->
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

type LoginLogRow = {
  loginSeq: number;
  loginDtti: string;
  userId: string;
  ip: string;
  loginCode: string;
  loginCodeNm: string;
};

const toastRef = ref<any>();
const uiStore = useUiStore();

const startDate = ref('');       // YYYY-MM-DD
const endDate = ref('');         // YYYY-MM-DD
const searchKeyword = ref('');
const loginCode = ref('');

const logs = ref<LoginLogRow[]>([]);
const loginCodes = ref<{ codeDetailNm: string; codeDetail: string }[]>([]);

//페이지네이션
const page = ref(1);          // 1부터
const pageSize = ref(10);     // 기본 10개
const totalCount = ref(0);

onMounted(async() => {
  //공통코드 조회
  loginCodes.value = await getCodeList('LOGIN_CODE');

  // 검색 기간 기본 값 설정(오늘 포함 최근 7일)
  const today = new Date();
  const from = new Date(today);
  from.setDate(today.getDate() - 6);
  const toYmd = formatYmd(today);
  const fromYmd = formatYmd(from);
  startDate.value = fromYmd;
  endDate.value = toYmd;

  getLoginLogList();
});

//날짜 포멧
const formatYmd = (d: Date) => {
  const yyyy = d.getFullYear();
  const mm = String(d.getMonth() + 1).padStart(2, '0');
  const dd = String(d.getDate()).padStart(2, '0');
  return `${yyyy}-${mm}-${dd}`;
};

const validateRange = () => {
  if (!startDate.value || !endDate.value) return true;
  // 문자열 비교(YYYY-MM-DD)로도 범위 체크 가능
  if (startDate.value > endDate.value) {
    toastRef.value?.showToast('조회 기간이 올바르지 않습니다. (시작일 ≤ 종료일)');
    return false;
  }
  return true;
};

// 조회
const getLoginLogList = async (resetPage = false) => {
  if (resetPage) page.value = 1;
  if (!validateRange()) return;

  try {
    uiStore.showLoading('로그인 로그를 조회 중입니다...');

    const payload = {
      startDate: startDate.value,
      endDate: endDate.value,
      searchKeyword: searchKeyword.value,
      loginCode: loginCode.value,
      page: page.value,          // 1-based
      pageSize: pageSize.value,  // size      
    };

    const response = await api.post<ApiResponse<LoginLogRow[]>>('/loginLog/getLoginLogList', payload);

    if (response.data?.RESULT === Constant.RESULT_SUCCESS) {
      logs.value = response.data?.OUT_DATA['list'] || [];
      totalCount.value = response.data?.OUT_DATA['totalCount'] || 0;
    } else {
      toastRef.value?.showToast(response.data?.OUT_RESULT_MSG || '처리 실패했습니다.');
      logs.value = [];
      totalCount.value = 0;
    }
  } catch (e) {
    console.error(e);
    toastRef.value?.showToast('로그인 로그 조회 중 오류가 발생했습니다.');
    logs.value = [];
    totalCount.value = 0;
  } finally {
    uiStore.hideLoading();
  }
};

//페이지네이션
const totalPages = computed(() => {
  if (totalCount.value === 0) return 1;
  return Math.ceil(totalCount.value / pageSize.value);
});

const goPage = (p: number) => {
  const safe = Math.min(Math.max(p, 1), totalPages.value);
  page.value = safe;
  getLoginLogList();
};

const onChangePageSize = () => {
  // pageSize 변경 시 1페이지로
  page.value = 1;
  getLoginLogList();
};
</script>

<style scoped>
/* =======================
   전체 페이지 래퍼
   ======================= */
.loginlog-page-wrap {
  padding: 1rem;
  box-sizing: border-box;
  background: #f9fbff;
  border-radius: 1rem;
  box-shadow: 0 0.5rem 1rem rgba(15, 23, 42, 0.08);
}

/* =======================
   상단 헤더
   ======================= */
.loginlog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 1rem 1rem;
}

.loginlog-title-main {
  display: flex;
  flex-direction: row;
  align-items: center;
  width: 100%;
  gap: 0.75rem;
}

.loginlog-title-pill {
  display: inline-flex;
  align-items: center;
  padding: 0.375rem 0.875rem;
  font-size: 1.3rem;
  font-weight: bold;
  letter-spacing: 0.02rem;
}

/* =======================
   검색 영역 (사용자 관리 화면 스타일 재사용)
   ======================= */
.loginlog-search {
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
}

.flex {
  flex: 1;
}

.search-label {
  font-size: 0.9rem;
  font-weight: 600;
  color: #475569;
}

.search-label.keyword {
  margin-left: 1rem;
}

.search-text {
  flex: 1;
  min-width: 0;
}

.search-text.date {
  flex: 0 0 auto;
  width: 9rem;
}

.search-tilde {
  margin: 0 0.25rem;
  font-size: 0.9rem;
  color: #6b7280;
}

/* =======================
   테이블
   ======================= */
.table-wrap {
  overflow-x: auto;
  border-radius: 0.75rem;
  border: 0.0625rem solid #e2e8f0;
  background: #ffffff;
  box-shadow: 0 0.5rem 0.5rem rgba(15, 23, 42, 0.06);
}

.loginlog-table {
  width: 100%;
  border-collapse: collapse;
  table-layout: fixed;
}

.loginlog-table thead th {
  padding: 0.75rem 0.5rem;
  color: #ffffff;
  font-weight: 800;
  text-align: center;
  background: #0c254d; /* 이미지 느낌의 블루 */
  border-right: 1px solid rgba(255, 255, 255, 0.35);
}

.loginlog-table thead th:last-child {
  border-right: none;
}

.loginlog-table tbody td {
  padding: 0.7rem 0.5rem;
  text-align: center;
  color: #0f172a;
  border-top: 1px solid #e2e8f0;
}

.loginlog-table tbody tr:nth-child(odd) td {
  background: #eef3ff; /* 연한 블루톤 줄무늬 */
}

.loginlog-table tbody tr:nth-child(even) td {
  background: #f7f9ff;
}

.loginlog-table tbody td.empty {
  padding: 1.25rem 0.5rem;
  color: #9ca3af;
  font-size: 0.9rem;
}

/*페이지네이션*/
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

.pagination-btn{
  width: 3rem;
  height: 2.125rem;
}


/* 반응형 */
@media (max-width: 900px) {
  .search-group {
    flex-wrap: wrap;
    white-space: normal;
  }

  .search-label.keyword {
    margin-left: 0;
  }
}
</style>
