<template>
  <div class="order-container">
    <BaseToast ref="toastRef" />

    <div class="title-main">
      <span class="title-pill">신규상품주문</span>
      <span class="title-menual">등록되지 않은 상품을 자유롭게 주문할 수 있습니다.</span>
    </div>

    <form class="order-form" @submit.prevent="requestOrder">
      <div class="form-row">
        <label class="label required">상품명</label>
        <div class="field-wrap">
          <BaseInput v-model="newOrder.itemNm" placeholder="주문할 상품명을 입력하세요" />
        </div>
      </div>
      <div class="form-row">
        <label class="label">이미지</label>
        <div class="image-box" @click="triggerFileSelect">
          <div class="image-preview">
            <img v-if="newOrder.imgPath" :src="newOrder.imgPath" alt="상품 이미지" />
            <img v-else :src="plusImage" alt="이미지 업로드" />
          </div>
        </div>
        <input ref="fileInputRef" type="file" accept="image/*" class="file-input-hidden" @change="handleImageChange" />
      </div>
      <div class="form-row">
        <label class="label required">개수</label>
        <div class="field-wrap small">
          <BaseInput v-model="newOrder.cnt" width="5rem" type="number" :min="1" :max="1000" />
        </div>
      </div>
      <div class="form-row">
        <label class="label">상품설명</label>
        <div class="field-wrap">
          <BaseTextarea v-model="newOrder.newOrderDtl" :maxlength="300" :height="'10rem'" placeholder="상품 설명을 입력하세요" />
        </div>
      </div>
      <div class="form-actions">
        <BaseButton v-if="!newOrderSeq" type="submit" width="7rem" height="2.5rem">구매요청</BaseButton>
        <BaseButton v-if="newOrderSeq && newOrder.orderStatusCode === '01'" type="submit" width="7rem" height="2.5rem">수정하기</BaseButton>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import api from '@/plugins/axios';
import BaseInput from '@/components/common/BaseInput.vue';
import BaseTextarea from '@/components/common/BaseTextarea.vue';
import BaseButton from '@/components/common/BaseButton.vue';
import BaseToast from '@/components/common/BaseToast.vue';
import Constant from '@/constants/constant';
import { useUiStore } from '@/stores/uiStore';
import plusImage from '@/assets/img/upload_icon.png';
import type { NewOrder } from '@/types/order/order.new.type';
import router from '@/router';

const uiStore = useUiStore();
const toastRef = ref();
const route = useRoute();

const newOrderSeq = route.query.orderSeq;

// 폼 데이터
const newOrder = ref<NewOrder>({});
const DEFAULT_NEW_ORDER: NewOrder = {
  newOrderSeq: 0,
  userSeq: 0,
  itemNm: '',
  img: '',
  price: 0,
  cnt: 1,
  newOrderDtl: '',
  orderStatusCode: '',
  orderDtti: '',
  orderStatusDtl: '',
  fstRegSeq: 0,
  fstRegDtti: '',
  lstUpdSeq: 0,
  lstUpdDtti: '',
  imgPath: '',
};

// 이미지
const fileInputRef = ref<HTMLInputElement | null>(null);
const imageFile = ref<File | null>(null);

// 이미지 영역 클릭 시 파일 선택
const triggerFileSelect = () => {
  fileInputRef.value?.click();
};

// 이미지 선택 시
const handleImageChange = (event: Event) => {
  const target = event.target as HTMLInputElement;
  const file = target.files?.[0];

  if (!file) {
    return;
  }

  imageFile.value = file;
  newOrder.value.imgPath = URL.createObjectURL(file);
};

// 유효성 체크
const validateForm = (): boolean => {
  if (!newOrder.value.itemNm.trim()) {
    toastRef.value?.showToast('상품명을 입력하세요.');
    return false;
  }

  const countNum = Number(newOrder.value.cnt);
  if (!countNum || countNum <= 0) {
    toastRef.value?.showToast('개수는 1개 이상이어야 합니다.');
    return false;
  }

  return true;
};

// 구매요청
const requestOrder = async () => {
  if (!validateForm()) return;

  if (!confirm('입력한 내용으로 구매 요청하시겠습니까?')) {
    return;
  }

  try {
    uiStore.showLoading('구매 요청 중입니다...');

    //값 바인딩
    const payload = {
      newOrderSeq: newOrder.value.newOrderSeq,
      itemNm: newOrder.value.itemNm,
      cnt: newOrder.value.cnt,
      price: newOrder.value.price,
      newOrderDtl: newOrder.value.newOrderDtl,
    };
    const formData = new FormData();
    for (const key in payload) {
      formData.append(key, String(payload[key as keyof typeof payload]));
    }
    
    //이미지를 변경한 경우 파라미터에 추가
    if (imageFile.value) {
      formData.append('imageFile', imageFile.value);
    }    

    const url = newOrderSeq ? '/itemNew/updateNewOrder' : '/itemNew/insertNewOrder';
    const response = await api.post(url,
                                    formData,
                                   {withCredentials: true,}); // ✅ 쿠키 쓰면 필수

    if (response.data?.[Constant.RESULT] === Constant.RESULT_SUCCESS) {
      alert('구매 요청이 등록되었습니다.');
      if(!newOrderSeq){
        window.location.href = `/market/itemNew?orderSeq=` + response.data[Constant.OUT_DATA].newOrderSeq;
      }
    } else {
      toastRef.value?.showToast(
        response.data?.OUT_RESULT_MSG || '구매 요청 등록에 실패했습니다.'
      );
    }
  } catch (error) {
    console.error(error);
    toastRef.value?.showToast('구매 요청 처리 중 오류가 발생했습니다.');
  } finally {
    uiStore.hideLoading();
  }
};

//주문 조회
const getNewOrder = async () => {
  uiStore.showLoading('주문 정보를 조회 중입니다...');

  try {
    const payload = { newOrderSeq };
    const response = await api.post('/itemNew/getNewOrder', payload);
    if (response.data?.RESULT_DETAIL === Constant.UNAUTHORIZED_ACCESS) {
      response.data.OUT_RESULT_MSG ? alert(response.data.OUT_RESULT_MSG) : alert("허가되지 않은 접근입니다.");
      router.push('/market/itemList');
    } else {
      // 실제 응답 형식에 맞게 추출
      newOrder.value = response.data?.OUT_DATA;
      newOrder.value.imgPath = `/api/itemNew/getItemNewImage?img=` + newOrder.value.img;
    }
  } catch (error) {
    console.error('주문 조회 실패:', error);
    toastRef.value?.showToast('주문 정보를 불러오지 못했습니다.');
  } finally {
    uiStore.hideLoading();
  }
};

onMounted(() => {
  //주문 조회인 경우
  if (newOrderSeq) {
    getNewOrder();
  } else {
    newOrder.value = { ...DEFAULT_NEW_ORDER };
  }
});
</script>

<style scoped>
.order-container {
  padding: 1.5rem 1.75rem;           /* 24px 28px */
  background: #f9fbff;
  border-radius: 1rem;               /* 16px */
  box-shadow: 0 0.5rem 1.125rem rgba(15, 23, 42, 0.08); /* 8px 18px */
  box-sizing: border-box;
}

.title-pill {
  display: inline-flex;
  align-items: center;
  padding: 0.375rem 0.875rem;
  font-size: 1.3rem;
  font-weight: bold;
  letter-spacing: 0.02rem;
}

.title-menual{
 font-size: 0.7rem;
 font-weight: bold;
 flex: 1;  
}

.order-form {
  background: #ffffff;
  border-radius: 0.75rem;             
  padding: 1.25rem 1.375rem 1rem;     
  border: 0.0625rem solid #e2e8f0;  
}

.form-row {
  display: flex;
  align-items: flex-start;
  margin-bottom: 1rem;                
}

.label {
  width: 4rem;                
  font-size: 0.9rem;
  color: #111827;
  padding-top: 0.375rem;      
  font-weight: bold;
  text-align: right;
  margin-right: 1rem;
}

.label.required::before {
  content: '*';
  color: #ef4444;
  margin-right: 0.25rem;      
}

.field-wrap {
  flex: 1;
}

.field-wrap.small {
  max-width: 13.75rem;                /* 220px */
}

.field-wrap :deep(textarea) {
  padding: 0.3rem;
}

.image-box {
  width: 10rem;
  height: 10rem;
  border: 0.0625rem solid #b8c4d1;    /* 1px */
  background: #f9fafb;
  border-radius: 0.25rem;             /* 4px */
  margin-top: 0.25rem;                /* 4px */
  display: flex;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
  cursor: pointer;
}

.image-preview {
  width: 10rem;
  height: 10rem;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
}

.image-preview img {
    max-width: 100%;
    max-height: 100%;

  /* height: 9.9rem;
  width: 9.9rem; */
  object-fit: contain;
  align-content: center;
}

/* file input 완전 숨김 */
.file-input-hidden {
  display: none;
}

/* 이미지 박스 클릭 가능하게 */
.image-box {
  width: 10rem;
  height: 10rem;
  border: 0.0625rem solid #b8c4d1; 
  border-radius: 0.25rem;             
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f9fafb;
  cursor: pointer;
  transition: border-color 0.2s ease, background-color 0.2s ease;
}

.image-box:hover {
  border-color: #0c7bce;
  background-color: #f0f7ff;
}

/* 버튼 영역 */
.form-actions {
  margin-top: 0.5rem;                 /* 8px */
  display: flex;
  justify-content: flex-end;
}
</style>