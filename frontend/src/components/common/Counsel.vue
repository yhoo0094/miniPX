<template>
  <div v-if="userStore.aiOpenYn === 'Y'" class="counsel-box">
    <transition name="chat-fade">  
      <div class="counsel-text-box" v-show="isOpenCounsel">
        <div class="bubble">
          <button class="close-button" @click="isOpenCounsel = false">&times;</button>
          <div class="chat-content" ref="chatContentRef">
            <template v-for="msg in messages" :key="msg.id">
              <div class="chat-row" :class="msg.role">
                <div class="chat-bubble">
                  <template v-if="msg.role === 'bot' && msg.isPending">
                    <TypingDots />
                  </template>
                  <template v-else>
                    <div v-if="msg.role === 'bot'" class="chat-bubble-content" v-html="msg.html || msg.text"></div>
                    <div v-else>{{ msg.text }}</div>
                  </template>
                </div>
              </div>
            </template>
          </div>

          <!-- 문의사항 입력창 -->
          <div class="chat-input-box">
            <BaseTextarea v-if="isDesktop" v-model="question" :height="'2rem'" class="chat-input" placeholder="문의사항을 입력하세요"
              :maxlength="300" @keydown.enter.exact.prevent="clickCounselBtn"/>   
            <BaseTextarea v-else v-model="question" :height="'2rem'" class="chat-input" placeholder="문의사항을 입력하세요"
              :maxlength="300"/>
            <BaseButton
              type="button"
              class="chat-button"
              width="6rem"
              height="auto"
              @click="clickCounselBtn"
              :disabled="isLoading"
            >
              {{ isLoading ? '처리 중...' : '문의하기' }}
            </BaseButton>
          </div>
        </div>
      </div>
    </transition>  

    <img class="counsel-img" :src="counsel" alt="img" @click="clickCounselImg" />
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue';
import counsel from '@/assets/img/counsel.png';
import api from '@/plugins/axios';
import BaseInput from '@/components/common/BaseInput.vue';
import BaseTextarea from '@/components/common/BaseTextarea.vue';
import BaseButton from '@/components/common/BaseButton.vue';
import TypingDots from '@/components/common/TypingDots.vue';
import { renderMarkdownToHtml } from '@/utils/useMarkdown';
import { useUserStore } from '@/stores/userStore';

const sessionId = ref<number>();
const question = ref<string>('');
const isOpenCounsel = ref(false);
const isLoading = ref(false);
const chatContentRef = ref<HTMLElement | null>(null);
const userStore = useUserStore();

const isDesktop = !/Mobi|Android|iPhone|iPad/i.test(navigator.userAgent);

interface ChatMessage {
  id: number;
  role: 'user' | 'bot';
  text: string;         // 원본 텍스트 (Markdown)
  html?: string;        // 변환된 HTML
  isPending?: boolean; // 로딩 중인 답변 여부
}

const messages = ref<ChatMessage[]>([
  {
    id: Date.now(),
    role: 'bot',
    text: '반갑습니다. 무엇을 도와드릴까요?',
  }
]);

//문의 봇 이미지 클릭
const clickCounselImg = () => {
  isOpenCounsel.value = !isOpenCounsel.value;
};

//문의하기 버튼 클릭
const clickCounselBtn = async () => {
  if (!question.value.trim()) {
    alert('질문을 입력해주세요.');
    return;
  }

  const currentQuestion = question.value.trim();
  question.value = '';

  messages.value.push({
    id: Date.now(),
    role: 'user',
    text: currentQuestion,
  });
  scrollToBottom();

  isLoading.value = true;

  const pendingId = Date.now() + 1;
  messages.value.push({
    id: pendingId,
    role: 'bot',
    text: '',
    isPending: true,
  });
  scrollToBottom();

  try {
    const response = await api.post('/openai/getAiAnswer', {
      question: currentQuestion,
      sessionId: sessionId.value
    });
    const botText = response.data['answer'] as string;
    sessionId.value = response.data['sessionId'];

    // 로딩 메시지 찾아서 실제 답변으로 교체
    const index = messages.value.findIndex((m) => m.id === pendingId);
    if (index !== -1) {
      messages.value[index] = {
        ...messages.value[index],
        text: botText,
        html: renderMarkdownToHtml(botText),
        isPending: false,
      };
    }
  } catch (error) {
    // 실패 시: 로딩 메시지에 에러 안내 표시
    const index = messages.value.findIndex((m) => m.id === pendingId);
    if (index !== -1) {
      messages.value[index] = {
        ...messages.value[index],
        text: '답변 처리 중 오류가 발생했습니다.',
        isPending: false,
      };
    }
    alert('질문에 실패했습니다.\n' + error);
  } finally {
    isLoading.value = false;
  }   
};

//스크롤 아래로 이동
const scrollToBottom = async () => {
  await nextTick(); // DOM 업데이트 후 실행
  if (chatContentRef.value) {
    chatContentRef.value.scrollTo({
      top: chatContentRef.value.scrollHeight,
      behavior: "smooth"
    });
  }
};
</script>

<style scoped>
.counsel-box {
  position: relative;
}

/* 상담원 캐릭터 */
.counsel-img {
  position: fixed;
  right: 1rem;
  bottom: 1rem;
  height: 5rem;
  width: 5rem;
  cursor: pointer;
}

/* 말풍선 위치 */
.counsel-text-box {
  position: fixed;
  right: 1rem;
  bottom: 7rem;
  padding: 0.5rem;
  display: flex;
  justify-content: flex-end;
  align-items: flex-end;
}

/* 말풍선 박스 */
.bubble {
  display: flex;
  flex-direction: column;  
  height: 30rem;
  width: clamp(200px, calc(100vw - 2rem), 50rem);
  min-height: 10rem;
  background: #e9f6ff;
  padding: 0.5rem;
  border-radius: 0.5rem;
  color: #333;
  font-size: 0.9rem;
  line-height: 1.4;
  position: relative; /* 닫기 버튼을 위한 상대 위치 */
  box-shadow: 0px 3px 10px rgba(0,0,0,0.15);
  word-wrap: break-word;
  resize: both;
  overflow: auto;  
  box-sizing: border-box;
}

/* 닫기 버튼 */
.close-button {
  position: absolute;
  top: 0.5rem;
  right: 0.5rem;
  background: none;
  border: none;
  font-size: 1.5rem;
  font-weight: bold;
  color: #333;
  cursor: pointer;
  transition: color 0.2s;
}

.close-button:hover {
  color: #f00;
}

/* 공통 행 스타일 */
.chat-row {
  display: flex;
  align-items: flex-start;
  gap: 0.4rem;
}

.chat-content {
  overflow-y: auto;      /* 세로 스크롤 추가 */
  padding-right: 4px;    /* 스크롤바와 텍스트 겹침 방지 */
}

/* 사용자 메시지 스타일 */
.chat-row.user .chat-label {
  background-color: #d5f0ff;
  color: #006199;
}

/* 상담원 메시지 스타일 */
.chat-row.bot .chat-label {
  background-color: #fff0d9;
  color: #b06b00;
}

/*문의사항 입력*/
.chat-input-box {
  display: flex;
  margin-top: auto;
}

.chat-input {
  flex-grow: 1;
  margin-right: 0.5rem;
}

.chat-input :deep(textarea) {
  align-content: center;
}

/* --------채팅창 나타나기/사라지기 애니메이션-------- */
/* 나타나기 전 상태 */
.chat-fade-enter-from,
.chat-fade-leave-to {
  opacity: 0;
  transform: translateY(10px);
}

/* 나타나는 중, 사라지는 중 공통 */
.chat-fade-enter-active,
.chat-fade-leave-active {
  transition: opacity 0.25s ease, transform 0.25s ease;
}

/* 나타난 후 상태 */
.chat-fade-enter-to,
.chat-fade-leave-from {
  opacity: 1;
  transform: translateY(0);
}
/* --------채팅창 나타나기/사라지기 애니메이션-------- */

/* 상담원(상대) = 왼쪽 정렬 */
.chat-row.bot {
  justify-content: flex-start;
}

/* 나(사용자) = 오른쪽 정렬 */
.chat-row.user {
  justify-content: flex-end;
}

/* 라벨 (나 / 상담원) */
.chat-label {
  font-size: 0.7rem;
  color: #888;
  margin: 0 0.4rem;
  flex-shrink: 0;
}

/* flex 방향/순서 조정:  
   - bot: [라벨][말풍선]
   - user: [말풍선][라벨]
*/
.chat-row.bot .chat-label {
  order: 1;
}
.chat-row.bot .chat-bubble {
  order: 2;
}
.chat-row.user .chat-label {
  order: 2;
}
.chat-row.user .chat-bubble {
  order: 1;
  white-space: pre-wrap;  /* 줄바꿈 허용 */
}

/* 말풍선 공통 */
.chat-bubble {
  max-width: 70%;
  padding: 0.5rem 0.75rem;
  border-radius: 12px;
  font-size: 0.9rem;
  line-height: 1.4;
  position: relative;
  word-wrap: break-word;
  margin-bottom: 1rem;
}

/* v-html로 들어온 내용에 스타일 적용 */
.chat-bubble-content :deep(p),
.chat-bubble-content :deep(ul),
.chat-bubble-content :deep(ol),
.chat-bubble-content :deep(li) {
  margin: 0;
  word-break: break-word;
}

/* 리스트가 말풍선 안쪽에서 시작되도록 */
.chat-bubble-content :deep(ul),
.chat-bubble-content :deep(ol) {
  margin: 0.25rem 0;
  padding-left: 1.2rem;          /* 말풍선 안쪽으로 들여쓰기 */
  /* list-style-position: inside;   숫자/불릿을 안쪽으로 */
}

/* 상담원 말풍선 */
.chat-row.bot .chat-bubble {
  background: #ffffff;
  border: 1px solid rgba(0, 0, 0, 0.06);
  /* border-bottom-left-radius: 4px; */
  margin-left: 0.4rem;
}

/* 왼쪽 꼬리 (삼각형) */
.chat-row.bot .chat-bubble::after {
  content: "";
  position: absolute;
  left: -8px;
  top: 15px;
  width: 0;
  height: 0;
  border-right: 8px solid #ffffff;
  border-top: 6px solid transparent;
  border-bottom: 6px solid transparent;
  filter: drop-shadow(-1px 0px 0px rgba(0,0,0,0.05));
}

/* 내 말풍선 */
.chat-row.user .chat-bubble {
  background: #fffa32;
  color: #000000;
  /* border-bottom-right-radius: 4px; */
  margin-right: 0.4rem;
}

/* 오른쪽 꼬리 (삼각형) */
.chat-row.user .chat-bubble::after {
  content: "";
  position: absolute;
  right: -8px;
  top: 15px;
  width: 0;
  height: 0;
  border-left: 8px solid #fffa32;
  border-top: 6px solid transparent;
  border-bottom: 6px solid transparent;
}
</style>
