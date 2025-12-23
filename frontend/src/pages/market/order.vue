<template>
  <BaseToast ref="toastRef" />

  <div class="order-page-wrap">

    <div class="order-header">
      <div class="order-title-main">
        <span class="order-title-pill">
          주문내역({{ orders.length }})
        </span>
      </div>

      <!-- 미결제 금액 -->
      <div class="order-unpaid">
        <span class="unpaid-label">미결제 금액</span>
        <span class="unpaid-amount">
          {{ unpaidAmount.toLocaleString() }}원
        </span>
      </div>      
    </div>

    <!-- 검색 조건 영역 -->
    <section class="order-search">
      <div class="search-row">
        <label class="search-label">주문일</label>
        <input type="date" v-model="searchForm.startDate" class="search-input date" />
        <span class="search-tilde">~</span>
        <input type="date" v-model="searchForm.endDate" class="search-input date" />
        <div>
          <BaseDropdown label="주문상태" v-model="searchForm.orderStatusCode" :options="orderStatusCodes" @change="searchOrders"
            :showPlaceholder="true" placeholderLabel="전체" />
        </div>
        <BaseInput height="2.125rem" v-model="searchForm.itemNm" class="search-text" placeholder="상품명 입력" @keydown.enter.prevent="searchOrders" />
        <BaseButton width="5rem" height="2.125rem" @click="searchOrders" type="button">검색</BaseButton>
      </div>
    </section>

    <!-- 주문 내역 리스트 -->
    <section class="order-list">
      <article class="order-card" v-for="order in orders" :key="order.orderSeq">
        <!-- 이미지 영역 -->
        <div class="order-image-box" @click="clickItemCard(order.orderSeq, order.orderDvcd)">
          <img v-if="order.imgFile" :src="order.imgFile" alt="상품 이미지" class="order-image" />
          <span v-else class="order-no-image">이미지</span>
        </div>

        <!-- 상품 정보 -->
        <div class="order-main-info">
          <div class="field-row">
            <span class="field-label">상품명</span>
            <span class="field-cont">{{ order.itemNm }}</span>
          </div>
          <div class="field-row">
            <span class="field-label">개수</span>
            <span class="field-cont">{{ order.cnt }}</span>
          </div>
          <div class="field-row">
            <span class="field-label">가격</span>
            <span class="field-cont">
              {{ (order.price * order.cnt).toLocaleString() }}원<span v-if="order.cnt > 1">(개당 {{ order.price.toLocaleString() }}원)</span>
            </span>
          </div>
          <div class="field-row">
            <span class="field-label">주문일시</span>
            <span class="field-cont">{{ order.orderDtti }}</span>
          </div>
          <div class="field-row">
            <span class="field-label">주문상태</span>
            <span class="field-cont" :class="{ 'red-text': order.orderStatusCode === '03' }">
              {{ order.orderStatusCodeNm }}
            </span>
          </div>
        </div>

        <!-- 버튼 영역(01:구매요청, 02:배송중, 03:미결제, 04:송금완료, 11:구매완료, 12:품절, 91:취소) -->
        <div class="order-actions">
          <BaseButton v-if="order.orderStatusCode == '01'" class="action-button"
              @click="cancelOrder(order)" variant="danger" type="button">
            주문취소
          </BaseButton>     
          <BaseButton v-if="order.orderStatusCode == '03'" class="action-button"
              @click="paymentCompleted(order)" variant="primary" type="button"
              title="송금을 완료한 후 눌러주세요.">
            송금완료
          </BaseButton>                 
        </div>
      </article>

      <div v-if="orders.length === 0" class="order-empty">
        주문 내역이 없습니다.
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import api from '@/plugins/axios';
import type { ApiResponse } from '@/types/api/response';
import Constant from '@/constants/constant';
import BaseInput from '@/components/common/BaseInput.vue';
import BaseButton from '@/components/common/BaseButton.vue';
import BaseToast from '@/components/common/BaseToast.vue';
import BaseDropdown from '@/components/common/BaseDropdown.vue';
import { useUiStore } from '@/stores/uiStore';
import { getCodeList } from '@/api/code';
import router from '@/router';

const uiStore = useUiStore();
const toastRef = ref();
const unpaidAmount = ref<number>(0);

/** 주문 1건 정보 */
interface Order {
  orderDvcd: string;
  orderSeq: number;
  itemSeq: number;
  itemNm: string;
  price: number;
  cnt: number;
  orderDtti: string;        // yyyy-MM-dd 형태 문자열
  orderStatusCode: string;
  orderStatusCodeNm: string;
  orderStatusNm: string;    // 화면 표시용 상태명
  imgFile?: string;
}

/** 검색조건 폼 */
interface OrderSearchForm {
  startDate: string;
  endDate: string;
  orderStatusCode: string;
  itemNm: string;
}

const orders = ref<Order[]>([]);

const searchForm = ref<OrderSearchForm>({
  startDate: '',
  endDate: '',
  orderStatusCode: '',
  itemNm: '',
});

// 상태 선언
const orderStatusCodes = ref<{ codeDetailNm: string; codeDetail: string }[]>([]);

/** 날짜 기본값: 올해 1/1 ~ 12/31 */
const initDefaultDates = () => {
  const now = new Date();
  const year = now.getFullYear();

  const start = `${year}-01-01`;
  const end = `${year}-12-31`;

  searchForm.value.startDate = start;
  searchForm.value.endDate = end;
};

/** 주문 목록 조회 */
const searchOrders = async () => {
  try {
    uiStore.showLoading('주문 내역을 조회 중입니다...');

    const payload = {
      startDate: searchForm.value.startDate,
      endDate: searchForm.value.endDate,
      orderStatusCode: searchForm.value.orderStatusCode,
      itemNm: searchForm.value.itemNm,
    };

    // TODO: 실제 API URL로 변경
    const res = await api.post<ApiResponse<any[]>>('/order/getOrderList', payload);
    const list = res.data.OUT_DATA['list'] || [];

    orders.value = list.map((row) => {
      // 백엔드 필드명에 맞게 매핑
      const orderStatusCode = row.orderStatusCode;
      const orderStatusNm = row.orderStatusNm ?? row.orderStatusName ?? '';

      return {
        orderDvcd: row.orderDvcd,
        orderSeq: row.orderSeq,
        itemSeq: row.itemSeq,
        itemNm: row.itemNm,
        img: row.img,
        price: row.price,
        cnt: row.cnt ?? 1,
        orderDtti: row.orderDtti, // 'YYYY-MM-DD' 로 내려온다고 가정
        orderStatusCode,
        orderStatusCodeNm: row.orderStatusCodeNm,
        orderStatusNm,
        imgFile:
          row.orderDvcd == '01'? `/api/item/getItemImage?img=${row.img}` : `/api/itemNew/getItemNewImage?img=${row.img}`,
      } as Order;
    });

    //미결제 금액 합계
    unpaidAmount.value = res.data.OUT_DATA['unpaidAmount'] || 0;
  } catch (e) {
    console.error(e);
    toastRef.value?.showToast('주문 내역 조회 중 오류가 발생했습니다.');
  } finally {
    uiStore.hideLoading();
  }
};

/** 주문취소 */
const cancelOrder = async (order: Order) => {
  if (!confirm('해당 주문을 취소하시겠습니까?')) {
    return;
  }

  try {
    uiStore.showLoading('주문 취소 중입니다...');
    const payload = {orderSeq: order.orderSeq};
    const response = await api.post('/order/cancelOrder', payload);

    if (response.data?.RESULT === Constant.RESULT_SUCCESS) {
      toastRef.value?.showToast('주문이 취소되었습니다.');
      await searchOrders();
    } else {
      toastRef.value?.showToast(
        response.data?.OUT_RESULT_MSG || '주문 취소에 실패했습니다.'
      );
    }
  } catch (e) {
    console.error(e);
    toastRef.value?.showToast('주문 취소 중 오류가 발생했습니다.');
  } finally {
    uiStore.hideLoading();
  }
};

/** 송금완료 */
const paymentCompleted = async (order: Order) => {
  if (!confirm('송금 완료 처리하시겠습니까?')) {
    return;
  }

  try {
    uiStore.showLoading('처리 중입니다...');
    const payload = {orderSeq: order.orderSeq};
    const response = await api.post('/order/paymentCompleted', payload);

    if (response.data?.RESULT === Constant.RESULT_SUCCESS) {
      toastRef.value?.showToast('송금 완료 처리되었습니다.');
      await searchOrders();
    } else {
      toastRef.value?.showToast(
        response.data?.OUT_RESULT_MSG || '처리에 실패했습니다.'
      );
    }
  } catch (e) {
    console.error(e);
    toastRef.value?.showToast('처리 중 오류가 발생했습니다.');
  } finally {
    uiStore.hideLoading();
  }
};

//상품 상세보기
const clickItemCard = (orderSeq: number, orderDvcd: string) => {
  let path = (orderDvcd === '01') ? '/market/itemDetail' : '/market/itemNew';
  router.push({
    path: path,
    query: { orderSeq }
  });
};

onMounted(async () => {
  //공통코드 조회
  orderStatusCodes.value = await getCodeList('ORDER_STATUS_CODE');

  initDefaultDates();
  await searchOrders();
});
</script>

<style scoped>
/* =======================
   전체 페이지 래퍼
   ======================= */
.order-page-wrap {
  padding: 20px 24px;
  box-sizing: border-box;
  background: #f9fbff;
  border-radius: 16px;
  box-shadow: 0 8px 18px rgba(15, 23, 42, 0.08);
}

/* =======================
   미결제 금액 표시
   ======================= */
.order-unpaid {
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
.order-search {
  margin-bottom: 18px;
}

.search-row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  border-radius: 14px;
  border: 1px solid #e2e8f0;
  background-color: #ffffff;
  box-shadow: 0 4px 10px rgba(15, 23, 42, 0.06);
}

.search-label {
  font-size: 0.9rem;
  font-weight: 600;
  color: #475569;
}

.search-text{
  flex: 1;
}

.search-input {
  border-radius: 8px;
  border: 1px solid #cbd5e1;
  padding: 4px 8px;
  font-size: 0.9rem;
  background: #f8fafc;
  outline: none;
  transition: border-color 0.15s ease, box-shadow 0.15s ease, background 0.15s ease;
}

.search-input:focus {
  border-color: #3b82f6;
  background: #ffffff;
  box-shadow: 0 0 0 1px rgba(59, 130, 246, 0.2);
}

.search-input.date {
  width: 135px;
}

.search-tilde {
  margin: 0 4px;
  font-size: 0.9rem;
  color: #6b7280;
}

/* =======================
   주문 카드 리스트
   ======================= */
.order-list {
  margin-top: 4px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

/* 개별 주문 카드 */
.order-card {
  display: flex;
  gap: 16px;
  padding: 12px 14px;
  border-radius: 14px;
  border: 1px solid #e2e8f0;
  background-color: #ffffff;
  box-shadow: 0 4px 10px rgba(15, 23, 42, 0.06);
  box-sizing: border-box;
  transition: transform 0.15s ease, box-shadow 0.15s ease, border-color 0.15s ease;
}

/* 이미지 영역 */
.order-image-box {
  width: 160px;
  min-width: 160px;
  height: 160px;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  background: #f8fafc;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  box-sizing: border-box;
  cursor: pointer;
}

.order-image {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.order-image:hover {
  transform: scale(1.1);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.order-no-image {
  font-size: 0.85rem;
  color: #64748b;
  padding: 6px 10px;
  border-radius: 999px;
  background: rgba(15, 23, 42, 0.04);
  border: 1px dashed #cbd5e1;
}

/* 상품 정보 영역 */
.order-main-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.field-row {
  display: flex;
  align-items: center;
  gap: 0.8rem;
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

.red-text {
  color: red;
}

/* 주문 취소 버튼 영역 */
.order-actions {
  display: flex;
  align-items: flex-end;
}

.action-button{
  width: 6rem;
  height: 2.125rem;
}

/* 비어 있을 때 */
.order-empty {
  padding: 24px 0;
  text-align: center;
  font-size: 0.9rem;
  color: #9ca3af;
}

/* =======================
   상단 주문내역 헤더
   ======================= */
.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 1rem 1rem;
}

.order-title-main {
  display: flex;
  flex-direction: column;
}

.order-title-pill {
  display: inline-flex;
  align-items: center;
  padding: 6px 14px;
  font-size: 1.3rem;
  font-weight: bold;
  letter-spacing: 0.02rem;
}

/* =======================
   반응형
   ======================= */
@media (max-width: 900px) {
  .order-page-wrap {
    padding: 14px 12px;
  }

  .search-row {
    align-items: flex-start;
  }

  .order-card {
    flex-direction: column;
  }

  .order-image-box {
    width: 100%;
    max-width: 260px;
    height: 200px;
  }

  .order-meta {
    flex-direction: row;
    justify-content: flex-start;
    gap: 16px;
  }

  .order-actions {
    justify-content: flex-end;
  }
}
</style>
