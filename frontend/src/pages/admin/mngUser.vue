<template>
  <BaseToast ref="toastRef" />

  <div class="user-page-wrap">

    <!-- ===== 상단 헤더 ===== -->
    <div class="user-header">
      <div class="user-title-main">
        <span class="user-title-pill">
          사용자 관리({{ users.length }})
        </span>
        <BaseButton
          class="create-user-btn"
          width="7rem"
          height="2.125rem"
          variant="primary"
          @click="insertUser"
        >
          사용자 생성
        </BaseButton>        
      </div>
    </div>

    <!-- ===== 검색 영역 ===== -->
    <section class="user-search">
      <div class="search-row">
        <div class="search-group flex">
          <label class="search-label">아이디/이름</label>
          <BaseInput
            v-model="searchKeyword"
            class="search-text"
            height="2.125rem"
            placeholder="아이디/이름 입력"
            @keydown.enter.prevent="getMngUserList"
          />
          <BaseButton
            width="5rem"
            height="2.125rem"
            type="button"
            @click="getMngUserList"
          >
            검색
          </BaseButton>
        </div>
      </div>
    </section>

    <!-- ===== 사용자 카드 리스트 ===== -->
    <section class="user-list">
      <article class="user-card" v-for="user in users" :key="user.userSeq">

        <!-- 조회(view) 모드 -->
        <div v-if="user.mode === 'view'" class="user-main-info">
          <div class="field-row">
            <span class="field-label">아이디</span>
            <span class="field-cont">{{ user.userId }}</span>
          </div>
          <div class="field-row">
            <span class="field-label">이름</span>
            <span class="field-cont">{{ user.userNm }}</span>
          </div>
          <div class="field-row">
            <span class="field-label">사용여부</span>
            <span class="field-cont">{{ user.useYn }}</span>
          </div>
          <div class="field-row">
            <span class="field-label">AI Open</span>
            <span class="field-cont">{{ user.aiOpenYn }}</span>
          </div>
          <div class="field-row">
            <span class="field-label">크레딧</span>
            <span class="field-cont">{{ user.credit.toLocaleString() }}</span>
          </div>
          <div class="user-actions">
            <BaseButton
              class="action-button"
              type="button"
              @click="changeMode(user)"
            >
              수정
            </BaseButton>
          </div>
        </div>

        <!-- 수정(modify) 모드 -->
        <div v-if="user.mode === 'modify'" class="user-main-info">
          <div class="field-row">
            <span class="field-label">아이디</span>
            <BaseInput
              v-model="user.userId"
              class="field-input"
              placeholder="아이디 입력"
            />            
          </div>
          <div class="field-row">
            <span class="field-label">이름</span>
            <BaseInput
              v-model="user.userNm"
              class="field-input"
              placeholder="이름 입력"
            />              
          </div>
          <div class="field-row">
            <span class="field-label">사용여부</span>
            <BaseToggle v-model="user.useYn" onText="Y" offText="N" />
          </div>
          <div class="field-row">
            <span class="field-label">AI Open</span>
            <BaseToggle v-model="user.aiOpenYn" onText="Y" offText="N" />
          </div>
          <div class="field-row">
            <span class="field-label">크레딧</span>
            <BaseInput
              type="number"
              class="field-input"
              align="right"
              width="6rem"
              v-model="user.credit"
            />
          </div>
          <div class="user-actions">
            <BaseButton
              class="action-button"
              type="button"
              @click="updateUser(user)"
            >
              저장
            </BaseButton>
          </div>
        </div>
      </article>

      <div v-if="users.length === 0" class="user-empty">
        조회된 사용자가 없습니다.
      </div>
    </section>
  </div>
</template>


<script setup lang="ts">
import { ref, onMounted } from 'vue';
import api from '@/plugins/axios';
import BaseInput from '@/components/common/BaseInput.vue';
import BaseButton from '@/components/common/BaseButton.vue';
import BaseToast from '@/components/common/BaseToast.vue';
import BaseToggle from '@/components/common/BaseToggle.vue';
import Constant from '@/constants/constant';
import { useUiStore } from '@/stores/uiStore';
import type { ApiResponse } from '@/types/api/response';
import type { MngUser } from '@/types/user/user.mng.type.ts';

const toastRef = ref();
const uiStore = useUiStore();
const searchKeyword = ref('');

const users = ref<MngUser[]>([]);

onMounted(() => {
  getMngUserList();
});

//조회
const getMngUserList = async() => {
  try {
    uiStore.showLoading('사용자 목록을 조회 중입니다...');

    const payload = {
      searchKeyword: searchKeyword.value,
    };

    const response = await api.post<ApiResponse<any[]>>('/mngUser/getMngUserList', payload);
    if (response.data?.RESULT === Constant.RESULT_SUCCESS) {
      users.value = (response.data?.OUT_DATA || []).map((user) => {
        return {
          ...user,
          mode: 'view', 
        }
      });
    } else {
      toastRef.value?.showToast(
        response.data?.OUT_RESULT_MSG || '처리 실패했습니다.'
      );
    }      
  } catch (e) {
    console.error(e);
    toastRef.value?.showToast('사용자 목록 조회 중 오류가 발생했습니다.');
  } finally {
    uiStore.hideLoading();
  }  
};

// 모드 변경(수정/저장)
const changeMode = (user: MngUser) => {
  user.mode = (user.mode === 'view') ? 'modify' : 'view';
};

// 저장
const updateUser = async(user: MngUser) => {
  try {
    uiStore.showLoading('처리 중입니다...');

    const response = await api.post('/mngUser/updateUser', user);
    if (response.data?.RESULT === Constant.RESULT_SUCCESS) {
      toastRef.value?.showToast('처리 완료되었습니다.');
      changeMode(user);
    } else {
      toastRef.value?.showToast(
        response.data?.OUT_RESULT_MSG || '처리 실패했습니다.'
      );
    }
  } catch (e) {
    console.error(e);
    toastRef.value?.showToast('처리 중 오류가 발생했습니다.');
  } finally {
    uiStore.hideLoading();
  }
};

// 사용자 생성
const insertUser = async() => {
  try {
    uiStore.showLoading('처리 중입니다...');

    const payload = {};    
    const response = await api.post('/mngUser/insertUser', payload);
    if (response.data?.RESULT === Constant.RESULT_SUCCESS) {
      toastRef.value?.showToast('신규 사용자 생성이 완료되었습니다.');
    } else {
      toastRef.value?.showToast(
        response.data?.OUT_RESULT_MSG || '처리 실패했습니다.'
      );
    }
  } catch (e) {
    console.error(e);
    toastRef.value?.showToast('처리 중 오류가 발생했습니다.');
  } finally {
    uiStore.hideLoading();
  }
};
</script>


<style scoped>
/* =======================
   전체 페이지 래퍼
   ======================= */
.user-page-wrap {
  padding: 1rem;
  box-sizing: border-box;
  background: #f9fbff;
  border-radius: 1rem;
  box-shadow: 0 0.5rem 1rem rgba(15, 23, 42, 0.08);
}

/* =======================
   미결제 금액 표시
   ======================= */
.user-unpaid {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.375rem 0.75rem;
  border-radius: 0.8rem;
  background: #fff1f2;          /* 연한 레드 */
  border: 2px solid #fecdd3;
  /* box-shadow: 0 0.25rem 0.25rem rgba(220, 38, 38, 0.15); */
}

.unpaid-label {
  font-size: 0.85rem;
  font-weight: 600;
  color: #000000;
}

.unpaid-amount {
  font-size: 1rem;
  font-weight: 800;
  color: #e40e0e;
}

/* =======================
   검색 영역
   ======================= */
.user-search {
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

.search-label {
  font-size: 0.9rem;
  font-weight: 600;
  color: #475569;
}

.search-text {
  flex: 1;
}

.search-input {
  border-radius: 0.5rem;              /* 8px */
  border: 0.0625rem solid #cbd5e1;    /* 1px */
  padding: 0.25rem 0.5rem;            /* 4px 8px */
  font-size: 0.9rem;
  background: #f8fafc;
  outline: none;
  transition: border-color 0.15s ease, box-shadow 0.15s ease, background 0.15s ease;
}

.search-input:focus {
  border-color: #3b82f6;
  background: #ffffff;
  box-shadow: 0 0 0 0.0625rem rgba(59, 130, 246, 0.2); /* 1px */
}

.search-input.date {
  width: 8.4375rem; /* 135px */
}

.search-tilde {
  margin: 0 0.25rem; /* 4px */
  font-size: 0.9rem;
  color: #6b7280;
}

/* 검색 입력 + 버튼 그룹 */
.search-group {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex-wrap: nowrap;
  white-space: nowrap;
}

.flex{
  flex: 1;
}

/* 검색 input은 줄어들 수 있도록 */
.search-itemNm {
  flex: 1 1 auto;
  min-width: 0;
}

/* 버튼은 줄어들지 않도록 */
.search-group > button {
  flex-shrink: 0;
}

/* =======================
   주문 카드 리스트
   ======================= */
.user-list {
  margin-top: 0.25rem;
  display: flex;
  flex-direction: column;
}

/* 개별 주문 카드 */
.user-card {
  display: flex;
  gap: 1rem;
  padding: 0.75rem 0.875rem;
  border-bottom: 2px solid #e2e8f0;
  background-color: #ffffff;
  box-sizing: border-box;
  transition: transform 0.15s ease, box-shadow 0.15s ease, border-color 0.15s ease;
}

/* 이미지 영역 */
.user-image-box {
  width: 8rem;
  height: auto;
  border-radius: 0.5rem;
  border: 0.0625rem solid #e2e8f0;
  background: #f8fafc;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  box-sizing: border-box;
}

.user-image {
  width: 8rem;
  height: 8rem;
  object-fit: contain;
}

.user-no-image {
  font-size: 0.85rem;
  color: #64748b;
  padding: 0.375rem 0.625rem; /* 6px 10px */
  border-radius: 62.4375rem;  /* 999px */
  background: rgba(15, 23, 42, 0.04);
  border: 0.0625rem dashed #cbd5e1;
}

/* 상품 정보 영역 */
.user-main-info {
  flex: 1;
  display: flex;
  gap: 0.5rem;
}

.field-row {
  display: flex;
  flex: 1;
  align-items: center;
  height: 1.8rem;
  min-width: 0;
}

.field-label {
  width: 3.75rem;
  font-size: 0.9rem;
  font-weight: bold;
  color: #475569;
}

.field-cont {
  font-size: 0.9rem;
  font-weight: 600;
  color: #0f172a;
}

.field-input {
  max-width: 10rem;
  height: 2rem;
}

/* 버튼 영역 */
.user-actions {
  display: flex;
  justify-content: end;
  gap: 0.3rem;
}

.action-button{
  width: 6rem;
  height: 2.125rem;
}

/* 비어 있을 때 */
.user-empty {
  padding: 1.5rem 0; /* 24px */
  text-align: center;
  font-size: 0.9rem;
  color: #9ca3af;
}

/* =======================
   상단 주문내역 헤더
   ======================= */
.user-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 1rem 1rem;
}

.user-title-main {
  display: flex;
  flex-direction: row;
  align-items: center;
  width: 100%; 
  gap: 0.75rem;  
}

.user-title-pill {
  display: inline-flex;
  align-items: center;
  padding: 0.375rem 0.875rem; /* 6px 14px */
  font-size: 1.3rem;
  font-weight: bold;
  letter-spacing: 0.02rem;
}

.create-user-btn{
  margin-left: auto;
}

/* 반응형 */
@media (max-width: 1200px) {
  .user-main-info {
    flex-direction: column;
  }

  .user-card{
    border-radius: 0.875rem;
    border: 0.0625rem solid #e2e8f0;
    box-shadow: 0 0.25rem 0.625rem rgba(15, 23, 42, 0.06);
  }

  .user-list {
    gap: 0.75rem;
  }  

  .field-row {
    gap: 0.8rem;
  }  
}
</style>

