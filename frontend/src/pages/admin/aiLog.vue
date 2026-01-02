<template>
  <BaseToast ref="toastRef" />

  <div class="ailog-page-wrap">
    <!-- ===== 상단 헤더 ===== -->
    <div class="ailog-header">
      <div class="ailog-title-main">
        <span class="ailog-title-pill">AI 로그</span>
      </div>
    </div>

    <!-- ===== 검색 영역 ===== -->
    <section class="ailog-search">
      <div class="search-row">
        <div class="search-group">
          <label class="search-label">일시</label>
          <BaseInput
            v-model="startDate"
            class="search-text date"
            type="date"
            height="2.125rem"
            @keydown.enter.prevent="getSessionList(true)"
          />
          <span class="search-tilde">~</span>
          <BaseInput
            v-model="endDate"
            class="search-text date"
            type="date"
            height="2.125rem"
            @keydown.enter.prevent="getSessionList(true)"
          />
        </div>

        <div class="search-group flex">
          <BaseInput
            v-model="searchKeyword"
            class="search-text"
            height="2.125rem"
            placeholder="요청자/제목을 입력하세요."
            @keydown.enter.prevent="getSessionList(true)"
          />

          <BaseButton
            width="5rem"
            height="2.125rem"
            type="button"
            @click="getSessionList(true)"
          >
            검색
          </BaseButton>
        </div>
      </div>
    </section>

    <!-- ===== 본문(좌: 목록 / 우: 대화) ===== -->
    <section class="ailog-body">
      <!-- ===== 세션 목록 테이블 ===== -->
      <div class="left">
        <div class="table-wrap">
          <table class="ailog-table">
            <thead>
              <tr>
                <th style="width: 28%;">일시</th>
                <th style="width: 32%;">요청자</th>
                <th style="width: 40%;">제목</th>
              </tr>
            </thead>

            <tbody>
              <tr
                v-for="row in sessions"
                :key="row.sessionId"
                class="row-click"
                :class="{ selected: row.sessionId === selectedSessionId }"
                @click="selectSession(row)"
                title="클릭하면 대화 내용을 조회합니다."
              >
                <td>{{ row.lstUpdDtti || row.fstRegDtti || '-' }}</td>
                <td>{{ formatRequester(row) }}</td>
                <td class="td-left">
                  <span class="ellipsis" :title="row.title || ''">
                    {{ row.title || '-' }}
                  </span>
                </td>
              </tr>

              <tr v-if="sessions.length === 0">
                <td class="empty" colspan="3">조회된 세션이 없습니다.</td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- pagination -->
        <div class="pagination">
          <div class="pagination-left">
            총 <b>{{ totalCount.toLocaleString() }}</b>건
          </div>

          <div class="pagination-right">
            <BaseButton class="pagination-btn" :disabled="page <= 1" @click="goPage(1)">&lt;&lt;</BaseButton>
            <BaseButton class="pagination-btn" :disabled="page <= 1" @click="goPage(page - 1)">&lt;</BaseButton>
            <span class="page-info">{{ page }} / {{ totalPages }}</span>
            <BaseButton class="pagination-btn" :disabled="page >= totalPages" @click="goPage(page + 1)">&gt;</BaseButton>
            <BaseButton class="pagination-btn" :disabled="page >= totalPages" @click="goPage(totalPages)">&gt;&gt;</BaseButton>
          </div>
        </div>
      </div>

      <!-- ===== 대화 내용(버블) ===== -->
      <div class="right">
        <div class="chat-panel">
          <div class="chat-panel-header">
            <div class="chat-title">
              <div class="chat-title-main">
                {{ selectedSessionTitle }}
              </div>
              <div class="chat-title-sub">
                <span v-if="selectedMeta">
                  모델: <b>{{ selectedMeta.llmModel || '-' }}</b>
                  · 프롬프트: <b>{{ selectedMeta.promptVersion || '-' }}</b>
                </span>
              </div>
            </div>

            <BaseButton
              width="6rem"
              height="2.125rem"
              variant="secondary"
              type="button"
              @click="reloadDetail"
              :disabled="!selectedSessionId"
            >
              새로고침
            </BaseButton>
          </div>

          <div class="chat-content" ref="chatContentRef">
            <template v-if="!selectedSessionId">
              <div class="chat-empty">
                왼쪽 목록에서 세션을 선택하면 대화 내용이 표시됩니다.
              </div>
            </template>

            <template v-else>
              <template v-for="(msg, idx) in messages" :key="idx">
                <div class="chat-row" :class="msg.role">
                  <div class="chat-bubble">
                    <template v-if="msg.role === 'bot'">
                      <div
                        class="chat-bubble-content"
                        :class="{ clamp: msg.isCollapsible && !msg.expanded }"
                        v-html="msg.html || msg.text"
                        @click="toggleExpand(msg)"
                        :title="msg.isCollapsible ? '클릭하면 전체 내용을 표시합니다.' : ''"
                        :ref="(el) => setMsgEl(el, idx)"
                      ></div>

                      <div
                        v-if="msg.isCollapsible"
                        class="expand-hint"
                        @click.stop="toggleExpand(msg)"
                      >
                        {{ msg.expanded ? '접기' : '전체보기' }}
                      </div>

                      <div class="msg-meta" v-if="msg.metaText">{{ msg.metaText }}</div>
                    </template>

                    <template v-else>
                      <div>{{ msg.text }}</div>
                      <div class="msg-meta" v-if="msg.metaText">{{ msg.metaText }}</div>
                    </template>
                  </div>
                </div>
              </template>

              <div v-if="messages.length === 0" class="chat-empty">
                대화 이벤트가 없습니다.
              </div>
            </template>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, nextTick } from 'vue';
import api from '@/plugins/axios';
import BaseInput from '@/components/common/BaseInput.vue';
import BaseButton from '@/components/common/BaseButton.vue';
import BaseToast from '@/components/common/BaseToast.vue';
import Constant from '@/constants/constant';
import { useUiStore } from '@/stores/uiStore';
import type { ApiResponse } from '@/types/api/response';
import { renderMarkdownToHtml } from '@/utils/useMarkdown';

type AiSessionRow = {
  sessionId: number;
  userSeq: number | null;
  userId?: string | null;
  userNm?: string | null;
  title: string | null;
  llmModel?: string | null;
  promptVersion?: string | null;
  fstRegDtti?: string | null;
  lstUpdDtti?: string | null;
};

type AiEventRow = {
  eventId?: number;
  turnId?: number;
  seqNo?: number;
  eventType: 'USER_MSG' | 'ASSIST_MSG' | 'TOOL_CALL' | 'TOOL_OUTPUT';
  role?: string | null;
  toolNm?: string | null;
  callId?: string | null;
  contentText?: string | null;
  contentJson?: string | null;
  fstRegDtti?: string | null;
};

interface ChatMessage {
  role: 'user' | 'bot';
  text: string;
  html?: string;
  metaText?: string;

  // ✅ 추가: 접기/펼치기
  isCollapsible?: boolean;
  expanded?: boolean;
}

const toastRef = ref<any>();
const uiStore = useUiStore();

const startDate = ref('');
const endDate = ref('');
const searchKeyword = ref('');

const sessions = ref<AiSessionRow[]>([]);
const totalCount = ref(0);
const page = ref(1);
const pageSize = ref(10);

const selectedSessionId = ref<number | null>(null);
const selectedMeta = ref<AiSessionRow | null>(null);

const messages = ref<ChatMessage[]>([]);
const chatContentRef = ref<HTMLElement | null>(null);

// ✅ v-html 영역 DOM refs
const msgEls = ref<(HTMLElement | null)[]>([]);
const setMsgEl = (el: any, idx: number) => {
  msgEls.value[idx] = (el as HTMLElement) ?? null;
};

onMounted(async () => {
  // 기본: 최근 7일
  const today = new Date();
  const from = new Date(today);
  from.setDate(today.getDate() - 6);

  startDate.value = formatYmd(from);
  endDate.value = formatYmd(today);

  await getSessionList(true);
});

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

const formatRequester = (row: AiSessionRow) => {
  if (row.userId) return `${row.userNm ?? ''}(${row.userId})`;
  if (row.userNm) return row.userNm;
  return row.userSeq != null ? `USER#${row.userSeq}` : '-';
};

const selectedSessionTitle = computed(() => {
  if (!selectedMeta.value) return '대화 내용';
  return selectedMeta.value.title || `세션 ${selectedMeta.value.sessionId}`;
});

const totalPages = computed(() => {
  if (totalCount.value === 0) return 1;
  return Math.ceil(totalCount.value / pageSize.value);
});

const goPage = async (p: number) => {
  const safe = Math.min(Math.max(p, 1), totalPages.value);
  page.value = safe;
  await getSessionList(false);
};

// 세션 목록 조회
const getSessionList = async (resetPage = false) => {
  if (resetPage) page.value = 1;
  if (!validateRange()) return;

  try {
    uiStore.showLoading('AI 세션 목록을 조회 중입니다...');

    const payload = {
      startDate: startDate.value,
      endDate: endDate.value,
      searchKeyword: searchKeyword.value,
      page: page.value,
      pageSize: pageSize.value,
    };

    const res = await api.post<ApiResponse<AiSessionRow[]>>(
      '/aiLog/getAiSessionList',
      payload
    );

    if (res.data?.RESULT === Constant.RESULT_SUCCESS) {
      sessions.value = res.data?.OUT_DATA['list'] || [];
      totalCount.value = res.data?.OUT_DATA['totalCount'] || 0;

      if (selectedSessionId.value) {
        const found = sessions.value.find(s => s.sessionId === selectedSessionId.value);
        if (!found) {
          selectedSessionId.value = null;
          selectedMeta.value = null;
          messages.value = [];
        }
      }
    } else {
      toastRef.value?.showToast(res.data?.OUT_RESULT_MSG || '처리 실패했습니다.');
      sessions.value = [];
      totalCount.value = 0;
    }
  } catch (e) {
    console.error(e);
    toastRef.value?.showToast('AI 세션 목록 조회 중 오류가 발생했습니다.');
    sessions.value = [];
    totalCount.value = 0;
  } finally {
    uiStore.hideLoading();
  }
};

// 세션 클릭 → 상세 조회
const selectSession = async (row: AiSessionRow) => {
  selectedSessionId.value = row.sessionId;
  selectedMeta.value = row;
  await getSessionDetail(row.sessionId);
};

const reloadDetail = async () => {
  if (!selectedSessionId.value) return;
  await getSessionDetail(selectedSessionId.value);
};

const getSessionDetail = async (sessionId: number) => {
  try {
    uiStore.showLoading('대화 내용을 조회 중입니다...');

    const payload = {
      sessionId,
      limit: 200,
    };

    const res = await api.post<ApiResponse<AiEventRow[]>>(
      '/aiLog/getAiSessionDetail',
      payload
    );

    if (res.data?.RESULT !== Constant.RESULT_SUCCESS) {
      toastRef.value?.showToast(res.data?.OUT_RESULT_MSG || '처리 실패했습니다.');
      messages.value = [];
      return;
    }

    const events: AiEventRow[] = res.data?.OUT_DATA['list'] || [];

    // 이벤트 → 화면용 메시지 변환
    messages.value = eventsToChatMessages(events);

    // ✅ DOM refs 배열 길이 맞추기
    msgEls.value = new Array(messages.value.length).fill(null);

    // ✅ 길면 접기 적용
    await applyCollapsible();

    await scrollToBottom();
  } catch (e) {
    console.error(e);
    toastRef.value?.showToast('대화 내용 조회 중 오류가 발생했습니다.');
    messages.value = [];
  } finally {
    uiStore.hideLoading();
  }
};

/* --------------------------------
   contentJson pretty print (옵션)
--------------------------------- */
const prettyJson = (s?: string | null) => {
  if (!s) return '';
  try {
    return JSON.stringify(JSON.parse(s), null, 2);
  } catch {
    return s;
  }
};

const eventsToChatMessages = (events: AiEventRow[]): ChatMessage[] => {
  const out: ChatMessage[] = [];

  for (const ev of events) {
    const dt = ev.fstRegDtti ? ` · ${ev.fstRegDtti}` : '';
    const toolInfo = ev.toolNm ? ` (${ev.toolNm})` : '';
    const meta = `${ev.eventType}${toolInfo}${dt}`;

    if (ev.eventType === 'USER_MSG') {
      out.push({
        role: 'user',
        text: ev.contentText || '',
        metaText: meta,
      });
      continue;
    }

    if (ev.eventType === 'ASSIST_MSG') {
      const t = ev.contentText || '';
      out.push({
        role: 'bot',
        text: t,
        html: renderMarkdownToHtml(t),
        metaText: meta,
      });
      continue;
    }

    if (ev.eventType === 'TOOL_CALL') {
      const json = ev.contentJson ? prettyJson(ev.contentJson) : '';
      const md = `🛠️ **툴 호출**: \`${ev.toolNm || '-'}\`${json ? `\n\n\`\`\`json\n${json}\n\`\`\`` : ''}`;
      out.push({
        role: 'bot',
        text: `🛠️ 툴 호출: ${ev.toolNm || '-'}${json ? `\n\n${json}` : ''}`,
        html: renderMarkdownToHtml(md),
        metaText: meta,
      });
      continue;
    }

    if (ev.eventType === 'TOOL_OUTPUT') {
      const json = ev.contentJson ? prettyJson(ev.contentJson) : '';
      const md = `📦 **툴 결과**: \`${ev.toolNm || '-'}\`${json ? `\n\n\`\`\`json\n${json}\n\`\`\`` : ''}`;
      out.push({
        role: 'bot',
        text: `📦 툴 결과: ${ev.toolNm || '-'}${json ? `\n\n${json}` : ''}`,
        html: renderMarkdownToHtml(md),
        metaText: meta,
      });
      continue;
    }
  }

  return out;
};

/* --------------------------------
   접기/펼치기 로직
--------------------------------- */
const CLAMP_LINES = 6;

const toggleExpand = (msg: ChatMessage) => {
  if (!msg.isCollapsible) return;
  msg.expanded = !msg.expanded;
};

const applyCollapsible = async () => {
  await nextTick();

  messages.value.forEach((msg, idx) => {
    if (msg.role !== 'bot') return;

    const el = msgEls.value[idx];
    if (!el) return;

    // clamp 제거 후 전체 높이 측정
    el.classList.remove('clamp');

    const style = window.getComputedStyle(el);
    const lineHeight = parseFloat(style.lineHeight || '0');

    if (!lineHeight) {
      msg.isCollapsible = false;
      msg.expanded = true;
      return;
    }

    const fullHeight = el.scrollHeight;
    const limitHeight = lineHeight * CLAMP_LINES;

    const isLong = fullHeight > (limitHeight + 2);

    msg.isCollapsible = isLong;
    msg.expanded = !isLong;

    if (msg.isCollapsible && !msg.expanded) {
      el.classList.add('clamp');
    }
  });
};

const scrollToBottom = async () => {
  await nextTick();
  if (chatContentRef.value) {
    chatContentRef.value.scrollTo({
      top: chatContentRef.value.scrollHeight,
      behavior: 'smooth',
    });
  }
};
</script>

<style scoped>
/* ===== 페이지 래퍼 ===== */
.ailog-page-wrap {
  padding: 1rem;
  box-sizing: border-box;
  background: #f9fbff;
  border-radius: 1rem;
  box-shadow: 0 0.5rem 1rem rgba(15, 23, 42, 0.08);
}

/* ===== 헤더 ===== */
.ailog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 1rem 1rem;
}

.ailog-title-main {
  display: flex;
  flex-direction: row;
  align-items: center;
  width: 100%;
  gap: 0.75rem;
}

.ailog-title-pill {
  display: inline-flex;
  align-items: center;
  padding: 0.375rem 0.875rem;
  font-size: 1.3rem;
  font-weight: bold;
  letter-spacing: 0.02rem;
}

/* ===== 검색 ===== */
.ailog-search {
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
.flex { flex: 1; }

.search-label {
  font-size: 0.9rem;
  font-weight: 600;
  color: #475569;
}

.search-text { flex: 1; min-width: 0; }
.search-text.date { flex: 0 0 auto; width: 9rem; }

.search-tilde { margin: 0 0.25rem; font-size: 0.9rem; color: #6b7280; }

/* ===== 본문 레이아웃 ===== */
.ailog-body {
  display: grid;
  grid-template-columns: 1fr 1.35fr;
  gap: 1rem;
  align-items: start;
}

.left, .right { min-width: 0; }

/* ===== 테이블 ===== */
.table-wrap {
  overflow-x: auto;
  border-radius: 0.75rem;
  border: 0.0625rem solid #e2e8f0;
  background: #ffffff;
  box-shadow: 0 0.5rem 0.5rem rgba(15, 23, 42, 0.06);
}

.ailog-table {
  width: 100%;
  border-collapse: collapse;
  table-layout: fixed;
}

.ailog-table thead th {
  padding: 0.75rem 0.5rem;
  color: #ffffff;
  font-weight: 800;
  text-align: center;
  background: #0c254d;
  border-right: 1px solid rgba(255, 255, 255, 0.35);
}
.ailog-table thead th:last-child { border-right: none; }

.ailog-table tbody td {
  padding: 0.7rem 0.5rem;
  text-align: center;
  color: #0f172a;
  border-top: 1px solid #e2e8f0;
}

.ailog-table tbody tr:nth-child(odd) td { background: #eef3ff; }
.ailog-table tbody tr:nth-child(even) td { background: #f7f9ff; }

.row-click { cursor: pointer; }
.row-click:hover td { filter: brightness(0.98); }

.row-click.selected td {
  outline: 2px solid rgba(12, 37, 77, 0.35);
  outline-offset: -2px;
}

.ailog-table tbody td.empty {
  padding: 1.25rem 0.5rem;
  color: #9ca3af;
  font-size: 0.9rem;
}

.td-left { text-align: left !important; }

.ellipsis {
  display: inline-block;
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  vertical-align: middle;
}

/* ===== pagination ===== */
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

/* ===== 우측 채팅 패널 ===== */
.chat-panel {
  border-radius: 0.75rem;
  border: 0.0625rem solid #e2e8f0;
  background: #e9f6ff;
  box-shadow: 0 0.5rem 0.5rem rgba(15, 23, 42, 0.06);
  overflow: hidden;
}

.chat-panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.75rem;
  padding: 0.75rem 0.875rem;
  background: #ffffff;
  border-bottom: 1px solid #e2e8f0;
}

.chat-title { min-width: 0; }
.chat-title-main {
  font-weight: 800;
  color: #0f172a;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.chat-title-sub {
  margin-top: 0.25rem;
  font-size: 0.8rem;
  color: #64748b;
}

.chat-content {
  height: 34rem;
  overflow-y: auto;
  padding: 0.75rem;
}

.chat-empty {
  color: #64748b;
  background: rgba(255,255,255,0.65);
  border: 1px dashed rgba(100,116,139,0.5);
  border-radius: 0.75rem;
  padding: 1rem;
}

/* ===== 버블 ===== */
.chat-row {
  display: flex;
  margin-bottom: 0.9rem;
}

.chat-row.bot { justify-content: flex-start; }
.chat-row.user { justify-content: flex-end; }

.chat-bubble {
  max-width: 78%;
  padding: 0.55rem 0.8rem;
  border-radius: 12px;
  font-size: 0.9rem;
  position: relative;
  word-wrap: break-word;
  white-space: pre-wrap;
  line-height: 1.4;
}

.chat-row.bot .chat-bubble {
  background: #ffffff;
  border: 1px solid rgba(0, 0, 0, 0.06);
  margin-left: 0.25rem;
}
.chat-row.user .chat-bubble {
  background: #fffa32;
  color: #000000;
  margin-right: 0.25rem;
}

/* 꼬리 */
.chat-row.bot .chat-bubble::after {
  content: "";
  position: absolute;
  left: -8px;
  top: 15px;
  width: 0; height: 0;
  border-right: 8px solid #ffffff;
  border-top: 6px solid transparent;
  border-bottom: 6px solid transparent;
  filter: drop-shadow(-1px 0px 0px rgba(0,0,0,0.05));
}
.chat-row.user .chat-bubble::after {
  content: "";
  position: absolute;
  right: -8px;
  top: 15px;
  width: 0; height: 0;
  border-left: 8px solid #fffa32;
  border-top: 6px solid transparent;
  border-bottom: 6px solid transparent;
}

/* v-html 내용 스타일 */
.chat-bubble-content :deep(p),
.chat-bubble-content :deep(ul),
.chat-bubble-content :deep(ol),
.chat-bubble-content :deep(li) {
  margin: 0;
  word-break: break-word;
}
.chat-bubble-content :deep(ul),
.chat-bubble-content :deep(ol) {
  margin: 0.25rem 0;
  padding-left: 1.2rem;
}

/* ✅ 접기(라인 클램프) */
.chat-bubble-content {
  cursor: default;
}
.chat-bubble-content.clamp {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 6; /* CLAMP_LINES와 맞추기 */
  overflow: hidden;
  text-overflow: ellipsis;
  cursor: pointer;
}

/* ✅ 전체보기/접기 */
.expand-hint {
  margin-top: 0.3rem;
  font-size: 0.78rem;
  color: #2563eb;
  cursor: pointer;
  user-select: none;
  font-weight: 700;
}
.expand-hint:hover {
  text-decoration: underline;
}

.msg-meta {
  margin-top: 0.35rem;
  font-size: 0.75rem;
  color: #6b7280;
  opacity: 0.85;
}

/* 반응형 */
@media (max-width: 1100px) {
  .ailog-body {
    grid-template-columns: 1fr;
  }
  .chat-content {
    height: 28rem;
  }
}
</style>
