<template>
  <BaseToast ref="toastRef" />
  <div class="basket-page-wrap">
    <div class="basket-header">
      <div class="basket-title-main">
        <span class="basket-title-pill">장바구니({{ BasketTypes.length }})</span>
      </div>
    </div>

    <div class="basket-page">
      <!-- 좌측 : 상품 카드 리스트 -->
      <div class="basket-list">
        <div class="basket-item" v-for="(item, index) in BasketTypes" :key="item.itemSeq">
          <!-- 선택 체크박스 -->
          <div class="basket-item-check">
            <input type="checkbox" v-model="item.checked" />
          </div>

          <!-- 카드 본문 -->
          <div class="basket-item-body">
            <!-- 이미지 영역 -->
            <div class="basket-item-image-box" @click="clickItemCard(item.itemSeq)">
              <img v-if="item.imgFile" :src="item.imgFile" alt="상품 이미지" class="basket-item-image"/>
              <span v-else class="basket-item-no-image">이미지</span>
            </div>

            <!-- 정보/입력 영역 -->
            <div class="basket-item-info">
              <div class="field-row">
                <label class="field-label">상품명</label>
                <label class="field-cont" :title="item.itemNm">
                  {{ item.itemNm }} <span v-if="item.unit > 1">{{ item.unit }}개</span>
                </label>
              </div>
              <div class="field-row">
                <label class="field-label">가격</label>
                <label class="field-cont">{{ item.unitPrice.toLocaleString() }}</label>
              </div>
              <div class="field-row">
                <label class="field-label">개수</label>
                <div class="cnt-box">
                  <button type="button" class="cnt-btn" @click="decreaseCnt(index)">−</button>
                  <span class="cnt-value">{{ item.unitCnt }}</span>
                  <button type="button" class="cnt-btn" @click="increaseCnt(index)">+</button>
                </div>
              </div>
              <div class="btn-area">
                <button type="button" class="delete-btn" @click="clickDeleteBtn(index)">삭제</button>
              </div>              
            </div>
          </div>
        </div>

        <!-- 비어 있을 때 -->
        <div v-if="BasketTypes.length === 0" class="basket-empty">
          장바구니에 담긴 상품이 없습니다.
        </div>
      </div>

      <!-- 우측 : 요약 박스 -->
      <aside class="basket-summary">
        <div class="summary-card">
          <div class="summary-row">
            <label class="summary-label">총액</label>
            <label class="summary-value">{{ totalAmount.toLocaleString() }} 원</label>
          </div>

          <button type="button" class="summary-submit" @click="requestOrder" :disabled="selectedItems.length === 0">
            구매 요청
          </button>
        </div>

        <!-- 전체 선택 영역 -->
        <div class="basket-top-bar">
          <label class="check-all-box">
            <input type="checkbox" v-model="isAllChecked" @change="toggleAll" />
            <span>전체 선택</span>
          </label>
        </div>
      </aside>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import api from '@/plugins/axios';
import type { ApiResponse } from '@/types/api/response';
import type { BasketType } from '@/types/basketType';
import Constant from '@/constants/constant';
import BaseToast from '@/components/common/BaseToast.vue';
import { useUiStore } from '@/stores/uiStore';
import router from '@/router';

const BasketTypes = ref<BasketType[]>([]);

//Toast
const toastRef = ref();

// 전역 로딩 스토어
const uiStore = useUiStore();

// 실제로는 장바구니 API에서 가져오면 됨
onMounted(async () => {
  getBasketList();
});

//장바구니 목록 조회
const getBasketList = async () => {
  const payload = {};
  const res = await api.post<ApiResponse<BasketType[]>>('/basket/getBasketList', payload);
  BasketTypes.value = (res.data.OUT_DATA || []).map(item => ({
    itemSeq: item.itemSeq,
    itemNm: item.itemNm,
    img: item.img,
    price: item.price,
    unitPrice: item.unitPrice,
    cnt: item.cnt,
    unitCnt: item.unitCnt,
    unit: item.unit,
    checked: true,
    imgFile: `/api/item/getItemImage?img=${item.img}`,
  }));
};

// 체크된 아이템만 반환
const selectedItems = computed(() =>
  BasketTypes.value.filter(item => item.checked)
);

// 총액 계산 (체크된 것만)
const totalAmount = computed(() =>
  selectedItems.value.reduce(
    (sum, item) => sum + item.price * item.cnt,
    0
  )
);

//상품 상세보기
const clickItemCard = (itemSeq: number) => {
  router.push({
    path: '/market/itemDetail',
    query: { itemSeq }
  });
};

//삭제 버튼
const clickDeleteBtn = async (index: number) => {
  if (!confirm('삭제하시겠습니까?')) { return; }

  let item = BasketTypes.value[index];

  try {
    uiStore.showLoading('삭제 중입니다...');
    const response = await deleteBasket(item);

    if (response.data?.RESULT === Constant.RESULT_SUCCESS) {
      getBasketList();
    } else {
      toastRef.value?.showToast(response.data?.OUT_RESULT_MSG || "삭제 실패");
    }
  } catch (error) {
    toastRef.value?.showToast("장바구니 추가 중 오류 발생");
  } finally {
    uiStore.hideLoading();
  }
};

//구매 요청
const requestOrder = async () => {
  if (selectedItems.value.length === 0) {
    toastRef.value?.showToast('선택된 상품이 없습니다.');
    return;
  }

  if (!confirm('선택한 상품으로 구매 요청을 진행하시겠습니까?')) { return; }

  // 체크된 항목들의 itemSeq만 추출
  const itemSeqList = selectedItems.value.map(item => item.itemSeq);

  try {
    uiStore.showLoading('구매 요청 중입니다...');

    const response = await requestOrderApi(itemSeqList);

    if (response.data?.RESULT === Constant.RESULT_SUCCESS) {
      getBasketList();
      toastRef.value?.showToast('구매 요청이 완료되었습니다.');
      // 상태 변경 후 목록 다시 조회
    } else {
      toastRef.value?.showToast(response.data?.OUT_RESULT_MSG || '구매 요청 실패');
    }
  } catch (error) {
    console.error(error);
    toastRef.value?.showToast('구매 요청 중 오류가 발생했습니다');
  } finally {
    uiStore.hideLoading();
  }
};

// 주문 상태 일괄 변경 API (구매요청)
const requestOrderApi = async (itemSeqList: number[]) => {
  const payload = {itemSeqList};
  return await api.post('/basket/insertOrder', payload);
};


//수량 증가
const increaseCnt = async (index: number) => {
  try {
    uiStore.showLoading('수량 변경 중입니다...');
      BasketTypes.value[index].cnt = BasketTypes.value[index].cnt + BasketTypes.value[index].unit;
      BasketTypes.value[index].unitCnt++;
    const response = await updateBasketCnt(BasketTypes.value[index]);

    if (response.data?.RESULT !== Constant.RESULT_SUCCESS) {
      toastRef.value?.showToast(response.data?.OUT_RESULT_MSG || "수량 변경 실패");
    }
  } catch (error) {
    toastRef.value?.showToast("장바구니 추가 중 오류 발생");
  } finally {
    uiStore.hideLoading();
  }
};

//수량 감소
const decreaseCnt = async (index: number) => {
  if (BasketTypes.value[index].cnt > BasketTypes.value[index].unit) {
    try {
      uiStore.showLoading('수량 변경 중입니다...');
      BasketTypes.value[index].cnt = BasketTypes.value[index].cnt - BasketTypes.value[index].unit;
      BasketTypes.value[index].unitCnt--;
      const response = await updateBasketCnt(BasketTypes.value[index]);

      if (response.data?.RESULT !== Constant.RESULT_SUCCESS) {
        toastRef.value?.showToast(response.data?.OUT_RESULT_MSG || "수량 변경 실패");
      }
    } catch (error) {
      toastRef.value?.showToast("장바구니 추가 중 오류 발생");
    } finally {
      uiStore.hideLoading();
    }
  }
};

//장바구니 개수 수정
const updateBasketCnt = async (item) => {
  return await api.post('/basket/updateBasketCnt', item);
};

//장바구니 삭제
const deleteBasket = async (item) => {
  return await api.post('/basket/deleteBasket', item);
};

// 전체 선택 여부 계산
const isAllChecked = computed({
  get: () => BasketTypes.value.length > 0 && BasketTypes.value.every(item => item.checked),
  set: (val: boolean) => {
    BasketTypes.value.forEach(item => (item.checked = val));
  }
});

//전체 선택 토글 함수
const toggleAll = () => {
  const newVal = isAllChecked.value;
  BasketTypes.value.forEach(item => (item.checked = newVal));
};
</script>

<style scoped>
.basket-page-wrap {
  padding: 1.25rem 1.5rem;
  box-sizing: border-box;
  background: #f9fbff;
  border-radius: 1rem;
  box-shadow: 0 0.5rem 1.125rem rgba(15, 23, 42, 0.08);
  min-width: 31.25rem;
}

/* 전체 페이지 컨테이너 */
.basket-page {
  display: flex;
  gap: 1.5rem;
}

/* 좌측: 장바구니 리스트 */
.basket-list {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 0.875rem;
}

/* 개별 아이템 래퍼 (체크박스 + 카드) */
.basket-item {
  display: flex;
  gap: 0.625rem;
  align-items: stretch;
}

/* 체크박스 영역 */
.basket-item-check {
  display: flex;
  align-items: center;
  padding-top: 0.375rem;
  min-width: 0;
}

.basket-item-check input[type='checkbox'] {
  width: 1.125rem;
  height: 1.125rem;
  accent-color: #0c254d;
}

/* 카드 본체 */
.basket-item-body {
  width: 95%;
  flex: 1;
  display: flex;
  gap: 1rem;
  padding: 0.75rem 0.875rem;
  border-radius: 0.875rem;
  border: 0.0625rem solid #e2e8f0;
  background-color: #ffffff;
  box-shadow: 0 0.25rem 0.625rem rgba(15, 23, 42, 0.06);
  box-sizing: border-box;
  transition: transform 0.15s ease, box-shadow 0.15s ease,
    border-color 0.15s ease;
}

/* 이미지 영역 */
.basket-item-image-box {
  width: 10rem;
  height: 10rem;
  border-radius: 0.75rem;
  border: 0.0625rem solid #e2e8f0;
  background: #f8fafc;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  box-sizing: border-box;
  cursor: pointer;
}

.basket-item-image {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.basket-item-image:hover {
  transform: scale(1.1);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.basket-item-no-image {
  font-size: 0.85rem;
  color: #64748b;
  padding: 0.375rem 0.625rem;
  border-radius: 62.4375rem;
  background: rgba(15, 23, 42, 0.04);
  border: 0.0625rem dashed #cbd5e1;
}

/* 정보 영역 */
.basket-item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  min-width: 0;
}

/* 라벨 + 인풋 한 줄 */
.field-row {
  display: flex;
  align-items: center;
  min-width: 0;
}

.field-label {
  width: 3.75rem;
  font-size: 0.9rem;
  font-weight: bold;
  color: #475569;
}

.field-cont {
  flex: 1;
  min-width: 0;
  font-weight: bold;
  font-size: 0.9rem;
  white-space: nowrap;    /* 한 줄 */
  overflow: hidden;       /* 넘침 숨김 */
  text-overflow: ellipsis;/* ... 처리 */  
}

.btn-area {
  flex: 1;
  text-align: right;
  justify-content: end;
  align-items: end;
  display: flex;
}

/* ----------------수량 조절 박스---------------- */
.cnt-box {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.cnt-btn {
  width: 1.75rem;
  height: 1.75rem;
  border-radius: 0.375rem;
  border: 0.0625rem solid #cbd5e1;
  background: #f1f5f9;
  color: #0f172a;
  font-size: 1.1rem;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.15s ease;
  box-shadow: 0 0.125rem 0.25rem rgba(15, 23, 42, 0.08);
}

/* hover 효과 */
.cnt-btn:hover {
  background: #e2e8f0;
  transform: translateY(-0.0625rem);
  box-shadow: 0 0.25rem 0.625rem rgba(15, 23, 42, 0.18);
}

/* 클릭 효과 */
.cnt-btn:active {
  transform: translateY(0);
  box-shadow: 0 0.125rem 0.25rem rgba(15, 23, 42, 0.08);
}

/* 숫자 */
.cnt-value {
  min-width: 1.5rem;
  text-align: center;
  font-size: 0.9rem;
  font-weight: bold;
  color: #0f172a;
}

/* ----------------버튼 영역---------------- */
.delete-btn {
  min-width: 5rem;
  height: 2.25rem;
  border-radius: 62.4375rem;
  border: none;
  padding: 0 0.875rem;
  font-size: 0.85rem;
  font-weight: 600;
  cursor: pointer;
  background: #f97373;
  color: #ffffff;
  box-shadow: 0 0.1875rem 0.5rem rgba(248, 113, 113, 0.4);
  transition: transform 0.12s ease, box-shadow 0.12s ease, opacity 0.12s ease;
}

.delete-btn:hover {
  transform: translateY(-0.0625rem);
  box-shadow: 0 0.3125rem 0.75rem rgba(248, 113, 113, 0.55);
}

.delete-btn:active {
  transform: translateY(0);
  box-shadow: 0 0.125rem 0.375rem rgba(248, 113, 113, 0.35);
  opacity: 0.9;
}

/* 비어 있을 때 */
.basket-empty {
  padding: 1.5rem 0;
  text-align: center;
  font-size: 0.9rem;
  color: #9ca3af;
}

/* 우측 요약 박스 */
.basket-summary {
  width: 15rem;
  flex-shrink: 0;
}

.summary-card {
  border-radius: 0.875rem;
  border: 0.0625rem solid #e2e8f0;
  background-color: #ffffff;
  padding: 1rem 1rem 0.875rem;
  box-shadow: 0 0.25rem 0.625rem rgba(15, 23, 42, 0.06);
  box-sizing: border-box;
}

/* 총액 행 */
.summary-row {
  display: flex;
  align-items: center;
  margin-bottom: 1rem;
  gap: 0.5rem;
}

.summary-label {
  width: 3rem;
  font-weight: bold;
  font-size: 0.9rem;
  color: #475569;
}

.summary-value {
  font-weight: bold;
  font-size: 1.2rem;
}

/* 구매 요청 버튼 */
.summary-submit {
  width: 100%;
  height: 2.375rem;
  margin-top: 0.25rem;
  border-radius: 0.5rem;
  border: none;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  background: #0c254d;
  color: #ffffff;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 0.25rem 0.625rem rgba(15, 23, 42, 0.3);
  transition: transform 0.12s ease, box-shadow 0.12s ease, opacity 0.12s ease;
}

.summary-submit:hover {
  transform: translateY(-0.0625rem);
  box-shadow: 0 0.375rem 0.875rem rgba(15, 23, 42, 0.45);
}

.summary-submit:active {
  transform: translateY(0);
  box-shadow: 0 0.1875rem 0.5rem rgba(15, 23, 42, 0.25);
  opacity: 0.95;
}

.summary-submit:disabled {
  opacity: 0.45;
  cursor: not-allowed;
  box-shadow: none;
}

/* 반응형 */
@media (max-width: 56.25rem) {
  .basket-page {
    flex-direction: column-reverse;
  }

  .basket-summary {
    width: 100%;
  }
}

/* =======================
   전체 선택 Top Bar
   ======================= */
.basket-top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem 0.875rem;
  margin-bottom: 0.75rem;
}

.check-all-box {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.95rem;
  font-weight: 600;
  color: #1e293b;
}

.check-all-box input[type="checkbox"] {
  width: 1.125rem;
  height: 1.125rem;
  accent-color: #0c254d;
}

/* =======================
   상단 장바구니 헤더
   ======================= */
.basket-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 1rem 1rem;
}

.basket-title-main {
  display: flex;
  flex-direction: column;
}

.basket-title-pill {
  display: inline-flex;
  align-items: center;
  padding: 0.375rem 0.875rem;
  font-size: 1.3rem;
  font-weight: bold;
  letter-spacing: 0.02rem;
}
</style>

