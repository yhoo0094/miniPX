<template>
  <BaseToast ref="toastRef" />

  <div class="order-page-wrap">

  <div class="order-header">
    <div class="order-title-main">
      <span class="order-title-pill">
        주문관리({{ orders.length }})
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
          <BaseDropdown label="주문상태" v-model="searchForm.orderStatusCode" :options="orderStatusCodesSch" @change="searchOrders"
            :showPlaceholder="true" placeholderLabel="전체" />
        </div>
        <BaseInput height="2.125rem" v-model="searchForm.itemNm" class="search-text" placeholder="주문자/상품명 입력" @keydown.enter.prevent="searchOrders" />
        <BaseButton width="5rem" height="2.125rem" @click="searchOrders" type="button">검색</BaseButton>
      </div>
    </section>

    <!-- 주문 내역 리스트 -->
    <section class="order-list">
      <article class="order-card" v-for="order in orders" :key="order.orderSeq">
        <!-- 이미지 영역 -->
        <div class="order-image-box">
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
            <span class="field-label">가격</span>
            <span class="field-cont">
              {{ order.price.toLocaleString() }} 원
            </span>
          </div>
          <div class="field-row">
            <span class="field-label">개수</span>
            <BaseInput v-if="order.orderStatusCode == '01'" type="number" align="right" width="6rem" height="2.125rem" v-model="order.cnt" class="search-text" />
            <span v-if="order.orderStatusCode != '01'" class="field-cont">{{ order.cnt }}</span>
          </div>
          <div class="field-row">
            <span class="field-label">주문일시</span>
            <span class="field-cont">{{ order.orderDtti }}</span>
          </div>
          <div class="field-row">
            <span class="field-label">주문상태</span>
            <span class="field-cont">{{ order.orderStatusCodeNm }}</span>
          </div>
          <div class="field-row">
            <span class="field-label">주문자</span>
            <span class="field-cont">{{ order.userNm }}</span>
          </div>          
        </div>

        <!-- 버튼 영역(01:구매요청, 02:배송중, 03:미결제, 04:송금완료, 11:구매완료, 12:품절, 91:취소) -->
        <div class="order-actions">
          <BaseButton v-if="order.orderStatusCode == '01'" class="action-button"
            @click="updateOrderStatus(order, '02')" variant="primary" type="button">배송중
          </BaseButton>
          <BaseButton v-if="order.orderStatusCode == '01'" class="action-button"
            @click="updateOrderStatus(order, '12')" variant="danger" type="button">품절
          </BaseButton>   
          <BaseButton v-if="order.orderStatusCode == '02'" class="action-button"
            @click="updateOrderStatus(order, '03')" type="button">미결제
          </BaseButton>           
          <BaseButton v-if="order.orderStatusCode == '02'" class="action-button"
            @click="updateOrderStatus(order, '01')" variant="danger" type="button">구매요청
          </BaseButton>     
          <BaseDropdown v-if="!['01', '02'].includes(order.orderStatusCode)" :showPlaceholder="false"
            v-model="order.orderStatusCode" :options="orderStatusCodes"
            @change="updateOrderStatus(order, order.orderStatusCode)"
            />
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

const uiStore = useUiStore();
const toastRef = ref();

/** 주문 1건 정보 */
interface Order {
  orderSeq: number;
  userSeq: string;
  userNm: string;
  itemSeq: number;
  itemNm: string;
  price: number;
  cnt: number;
  orderDtti: string;        // yyyy-MM-dd 형태 문자열
  orderStatusCode: string;
  orderStatusCodeNm: string;
  orderStatusNm: string;    // 화면 표시용 상태명
  imgFile?: string;
  orderDvcd: string;        // 주문구분코드(01:주문, 02:신규상품주문)
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
const orderStatusCodesSch = ref<{ codeDetailNm: string; codeDetail: string }[]>([]);
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
    const res = await api.post<ApiResponse<any[]>>('/mngOrder/getOrderList', payload);
    const list = res.data.OUT_DATA || [];

    orders.value = list.map((row) => {
      // 백엔드 필드명에 맞게 매핑
      const orderStatusCode = row.orderStatusCode;
      const orderStatusNm = row.orderStatusNm ?? row.orderStatusName ?? '';

      return {
        orderSeq: row.orderSeq,
        userSeq: row.userSeq,
        userNm: row.userNm,
        itemSeq: row.itemSeq,
        itemNm: row.itemNm,
        price: row.price,
        cnt: row.cnt ?? 1,
        orderDtti: row.orderDtti, // 'YYYY-MM-DD' 로 내려온다고 가정
        orderStatusCode,
        orderStatusCodeNm: row.orderStatusCodeNm,
        orderStatusNm,
        imgFile:
          row.orderDvcd == '01'? `/api/item/getItemImage?itemSeq=${row.itemSeq}` : `/api/itemNew/getItemNewImage?orderSeq=${row.orderSeq}`
          ,
      } as Order;
    });
  } catch (e) {
    console.error(e);
    toastRef.value?.showToast('주문 내역 조회 중 오류가 발생했습니다.');
  } finally {
    uiStore.hideLoading();
  }
};

// 주문 상태 변경
const updateOrderStatus = async (order: Order, orderStatusCode: string) => {
  //(01:구매요청, 02:배송중, 03:미결제, 04:송금완료, 11:구매완료, 12:품절, 91:취소)
  let msg = '';
  let loadingMsg = '';

  switch (orderStatusCode){
    case '12':
      msg = '품절 처리하시겠습니까?';
      loadingMsg = '품절 처리 중입니다...';
      break;
  };

  if (msg != '' && !confirm(msg)) {
    return;
  }

  try {
    uiStore.showLoading(loadingMsg);

    const payload = {
      orderSeq: order.orderSeq,
      orderStatusCode: orderStatusCode,
      cnt: order.cnt,
    };

    const response = await api.post('/mngOrder/updateOrderStatus', payload);

    if (response.data?.RESULT === Constant.RESULT_SUCCESS) {
      toastRef.value?.showToast('처리 완료되었습니다.');
      await searchOrders();
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

onMounted(async () => {
  //공통코드 조회
  orderStatusCodes.value = await getCodeList('ORDER_STATUS_CODE');
  const list = [...orderStatusCodes.value]; 

  const extra = [
    { codeDetailNm: '구매요청/배송중', codeDetail: '001' },
    { codeDetailNm: '미결제/송금완료', codeDetail: '002' },
  ];
  list.unshift(...extra);
  orderStatusCodesSch.value = list;

  initDefaultDates();
  await searchOrders();
});

/** 미결제 금액 합계 */
const unpaidAmount = computed(() => {
  return orders.value
    .filter(o => o.orderStatusCode === '03' || o.orderStatusCode === '04') // 미결제/송금완료
    .reduce((sum, o) => sum + o.price * o.cnt, 0);
});
</script>

<style scoped>
/* =======================
   전체 페이지 래퍼
   ======================= */
.order-page-wrap {
  padding: 1rem;
  box-sizing: border-box;
  background: #f9fbff;
  border-radius: 1rem;
  box-shadow: 0 0.5rem 1rem rgba(15, 23, 42, 0.08);
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

/* =======================
   주문 카드 리스트
   ======================= */
.order-list {
  margin-top: 0.25rem; /* 4px */
  display: flex;
  flex-direction: column;
  gap: 0.75rem; /* 12px */
}

/* 개별 주문 카드 */
.order-card {
  display: flex;
  gap: 1rem; /* 16px */
  padding: 0.75rem 0.875rem; /* 12px 14px */
  border-radius: 0.875rem;   /* 14px */
  border: 0.0625rem solid #e2e8f0;
  background-color: #ffffff;
  box-shadow: 0 0.25rem 0.625rem rgba(15, 23, 42, 0.06); /* 4px 10px */
  box-sizing: border-box;
  transition: transform 0.15s ease, box-shadow 0.15s ease, border-color 0.15s ease;
}

/* 이미지 영역 */
.order-image-box {
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

.order-image {
  width: 8rem;
  height: 8rem;
  object-fit: contain;
}

.order-no-image {
  font-size: 0.85rem;
  color: #64748b;
  padding: 0.375rem 0.625rem; /* 6px 10px */
  border-radius: 62.4375rem;  /* 999px */
  background: rgba(15, 23, 42, 0.04);
  border: 0.0625rem dashed #cbd5e1;
}

/* 상품 정보 영역 */
.order-main-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 0.5rem; /* 8px */
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

/* 버튼 영역 */
.order-actions {
  display: flex;
  align-items: flex-end;
  gap: 0.3rem;
}

.action-button{
  width: 6rem;
  height: 2.125rem;
}

/* 비어 있을 때 */
.order-empty {
  padding: 1.5rem 0; /* 24px */
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
  padding: 0.375rem 0.875rem; /* 6px 14px */
  font-size: 1.3rem;
  font-weight: bold;
  letter-spacing: 0.02rem;
}
</style>

