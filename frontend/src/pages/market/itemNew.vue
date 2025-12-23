<template>
  <div class="order-container">
    <BaseToast ref="toastRef" />

    <h2 class="title">신규상품주문</h2>

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
            <img v-if="newOrder.imgPath" :src="newOrder.imgPath" alt="상품 이미지 미리보기" />
            <img v-else :src="plusImage" alt="이미지 업로드" />
          </div>
        </div>
        <input ref="fileInputRef" type="file" accept="image/*" class="file-input-hidden" @change="handleImageChange" />
      </div>
      <div class="form-row">
        <label class="label required">개수</label>
        <div class="field-wrap small">
          <BaseInput v-model="newOrder.cnt" width="5rem" type="number" :min="1" />
        </div>
      </div>
      <div class="form-row">
        <label class="label">상품설명</label>
        <div class="field-wrap">
          <BaseTextarea v-model="newOrder.newOrderDtl" :height="'10rem'" placeholder="상품 설명을 입력하세요" />
        </div>
      </div>
      <div class="form-actions">
        <BaseButton v-if="!newOrderSeq" type="submit" width="7rem" height="2.5rem">구매요청</BaseButton>
        <BaseButton v-else type="submit" width="7rem" height="2.5rem">수정하기</BaseButton>
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
    imageFile.value = null;
    newOrder.value.imgPath = '';
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
      toastRef.value?.showToast('구매 요청이 등록되었습니다.');
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
    const data = response.data?.OUT_DATA;
    if (!data) return;

    // 실제 응답 형식에 맞게 추출
    newOrder.value = response.data?.OUT_DATA;
    newOrder.value.imgPath = `/api/itemNew/getItemNewImage?img=` + newOrder.value.img;
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
  padding: 24px 28px;
  background: #f9fbff;
  border-radius: 16px;
  box-shadow: 0 8px 18px rgba(15, 23, 42, 0.08);
  box-sizing: border-box;
}

.title {
  margin: 0 0 18px;
  font-size: 1.3rem;
  font-weight: 700;
  color: #0f172a;
}

.order-form {
  background: #ffffff;
  border-radius: 12px;
  padding: 20px 22px 16px;
  border: 1px solid #e2e8f0;
}

.form-row {
  display: flex;
  align-items: flex-start;
  margin-bottom: 16px;
}

.label {
  width: 90px;
  font-size: 0.9rem;
  color: #111827;
  padding-top: 6px;
  font-weight: bold;
  text-align: right;
  margin-right: 1rem;
}

.label.required::before {
  content: '*';
  color: #ef4444;
  margin-right: 4px;
}

.field-wrap {
  flex: 1;
}

.field-wrap.small {
  max-width: 220px;
}

.image-box {
  width: 10rem;
  height: 10rem;
  border: 1px solid #b8c4d1;
  border-radius: 4px;
  margin-top: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f9fafb;
  cursor: pointer;
}

.image-preview {
  text-align: center;
}

.image-preview img {
  width: 80%;
  height: 80%;
  object-fit: contain;
}

/* file input 완전 숨김 */
.file-input-hidden {
  display: none;
}

/* 이미지 박스 클릭 가능하게 */
.image-box {
  width: 10rem;
  height: 10rem;
  border: 1px solid #b8c4d1;
  border-radius: 4px;
  margin-top: 4px;
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
  margin-top: 8px;
  display: flex;
  justify-content: flex-end;
}
</style>
