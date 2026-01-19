<template>
  <BaseToast ref="toastRef" />

  <div class="loginlog-page-wrap">
    <!-- ===== 상단 헤더 ===== -->
    <div class="loginlog-header">
      <div class="loginlog-title-main">
        <span class="loginlog-title-pill">
          요청 로그
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
            @keydown.enter.prevent="getReqLogList(true)"
          />
          <span class="search-tilde">~</span>
          <BaseInput
            v-model="endDate"
            class="search-text date"
            type="date"
            height="2.125rem"
            @keydown.enter.prevent="getReqLogList(true)"
          />

          <BaseDropdown
            label="요청타입"
            v-model="reqTypeCode"
            :options="reqTypeCodes"
            @change="getReqLogList(true)"
            :showPlaceholder="true"
            placeholderLabel="전체"
          />
        </div>

        <div class="search-group flex">
          <BaseInput
            v-model="searchKeyword"
            class="search-text"
            height="2.125rem"
            placeholder="요청자 / IP / URI"
            @keydown.enter.prevent="getReqLogList(true)"
          />

          <BaseButton
            width="5rem"
            height="2.125rem"
            type="button"
            @click="getReqLogList(true)"
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
              <th style="width: 16%;">일시</th>
              <th style="width: 18%;">요청자</th>
              <th style="width: 14%;">IP</th>
              <th style="width: 26%;">URI</th>
              <th style="width: 26%;">파라미터</th>
            </tr>
          </thead>

          <tbody>
            <template v-for="row in logs" :key="row.reqSeq">
              <tr>
                <td>{{ row.reqDtti }}</td>
                <td>{{ row.userId ? `${row.userNm ?? ''}(${row.userId})` : '-' }}</td>
                <td>{{ row.ip }}</td>
                <td class="td-left">
                  <span class="ellipsis" :title="row.uri">{{ row.uri }}</span>
                </td>
                <td class="td-left">
                  <div class="param-cell">
                    <span class="ellipsis" :title="row.param || ''">
                      {{ toPreview(row.param) }}
                    </span>

                    <BaseButton
                      class="mini-btn"
                      height="1.875rem"
                      width="4rem"
                      type="button"
                      variant="secondary"
                      @click="toggleDetail(row.reqSeq)"
                      :disabled="!row.param"
                    >
                      {{ expandedSeq === row.reqSeq ? '접기' : '상세' }}
                    </BaseButton>
                  </div>
                </td>
              </tr>

              <!-- 상세 PARAM 펼침 -->
              <tr v-if="expandedSeq === row.reqSeq">
                <td colspan="6" class="param-detail">
                  <pre class="param-pre">{{ row.param }}</pre>
                </td>
              </tr>
            </template>

            <tr v-if="logs.length === 0">
              <td class="empty" colspan="6">조회된 로그가 없습니다.</td>
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

type ReqLogRow = {
  reqSeq: number;
  reqDtti: string;            // "YYYY-MM-DD HH:mm:ss" 같은 문자열로 내려온다고 가정
  userSeq: number | null;
  userId: string;
  userNm: string;
  ip: string;
  uri: string;
  param: string | null;
  reqTypeCode: string;
};

const toastRef = ref<any>();
const uiStore = useUiStore();

const startDate = ref('');       // YYYY-MM-DD
const endDate = ref('');         // YYYY-MM-DD
const searchKeyword = ref('');
const reqTypeCode = ref('');     // 전체는 빈값

const logs = ref<ReqLogRow[]>([]);
const reqTypeCodes = ref<{ codeDetailNm: string; codeDetail: string }[]>([]);

// 페이지네이션
const page = ref(1);
const pageSize = ref(10);
const totalCount = ref(0);

// 상세 펼침
const expandedSeq = ref<number | null>(null);

onMounted(async() => {
  //공통코드 조회
  reqTypeCodes.value = await getCodeList('REQ_TYPE_CODE');

  // 검색 기간 기본 값 설정(오늘 포함 최근 7일)
  const today = new Date();
  const from = new Date(today);
  from.setDate(today.getDate() - 6);

  startDate.value = formatYmd(from);
  endDate.value = formatYmd(today);

  getReqLogList();
});

// 날짜 포맷
const formatYmd = (d: Date) => {
  const yyyy = d.getFullYear();
  const mm = String(d.getMonth() + 1).padStart(2, '0');
  const dd = String(d.getDate()).padStart(2, '0');
  return `${yyyy}-${mm}-${dd}`;
};

const validateRange = () => {
  if (!startDate.value || !endDate.value) return true;
  if (startDate.value > endDate.value) {
    toastRef.value?.showToast('조회 기간이 올바르지 않습니다. (시작일 ≤ 종료일)');
    return false;
  }
  return true;
};

const toPreview = (v: string | null) => {
  if (!v) return '-';
  const s = String(v);
  return s.length > 80 ? s.substring(0, 80) + '…' : s;
};

const toggleDetail = (reqSeq: number) => {
  expandedSeq.value = expandedSeq.value === reqSeq ? null : reqSeq;
};

// 조회
const getReqLogList = async (resetPage = false) => {
  if (resetPage) page.value = 1;
  expandedSeq.value = null;

  if (!validateRange()) return;

  try {
    uiStore.showLoading('요청 로그를 조회 중입니다...');

    const payload = {
      startDate: startDate.value,
      endDate: endDate.value,
      searchKeyword: searchKeyword.value,
      reqTypeCode: reqTypeCode.value,
      page: page.value,          // 1-based
      pageSize: pageSize.value,  // size
    };

    const response = await api.post<ApiResponse<ReqLogRow[]>>(
      '/reqLog/getReqLogList',
      payload
    );

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
    toastRef.value?.showToast('요청 로그 조회 중 오류가 발생했습니다.');
    logs.value = [];
    totalCount.value = 0;
  } finally {
    uiStore.hideLoading();
  }
};

// 페이지네이션
const totalPages = computed(() => {
  if (totalCount.value === 0) return 1;
  return Math.ceil(totalCount.value / pageSize.value);
});

const goPage = (p: number) => {
  const safe = Math.min(Math.max(p, 1), totalPages.value);
  page.value = safe;
  getReqLogList();
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
   검색 영역
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
  background: #0c254d;
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
  background: #eef3ff;
}

.loginlog-table tbody tr:nth-child(even) td {
  background: #f7f9ff;
}

.loginlog-table tbody td.empty {
  padding: 1.25rem 0.5rem;
  color: #9ca3af;
  font-size: 0.9rem;
}

.td-left {
  text-align: left !important;
}

.ellipsis {
  display: inline-block;
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  vertical-align: middle;
}

.param-cell {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.mini-btn {
  flex: 0 0 auto;
  padding: 0 0.5rem;
  font-size: 0.85rem;
}

/* 상세 영역 */
.param-detail {
  padding: 0.75rem !important;
  background: #ffffff !important;
  text-align: left !important;
}

.param-pre {
  margin: 0;
  padding: 0.75rem;
  border-radius: 0.75rem;
  border: 0.0625rem solid #e2e8f0;
  background: #0b1220;
  color: #e5e7eb;
  font-size: 0.85rem;
  line-height: 1.35rem;
  white-space: pre-wrap;
  word-break: break-word;
}

/* 페이지네이션 */
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

/* 반응형 */
@media (max-width: 900px) {
  .search-group {
    flex-wrap: wrap;
    white-space: normal;
  }
}
</style>
