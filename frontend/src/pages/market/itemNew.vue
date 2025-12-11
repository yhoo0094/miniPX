<template>
  <div class="order-container">
    <BaseToast ref="toastRef" />

    <h2 class="title">신규상품주문</h2>

    <form class="order-form" @submit.prevent="requestOrder">
      <!-- 상품명 -->
      <div class="form-row">
        <label class="label required">상품명</label>
        <div class="field-wrap">
          <BaseInput
            v-model="itemNm"
            placeholder="주문할 상품명을 입력하세요"
          />
        </div>
      </div>

      <!-- 이미지 -->
      <div class="form-row">
        <label class="label">이미지</label>

        <!-- ✅ 클릭 영역 -->
        <div class="image-box" @click="triggerFileSelect">
          <div class="image-preview">
            <img
              v-if="imagePreviewUrl"
              :src="imagePreviewUrl"
              alt="상품 이미지 미리보기"
            />
            <img
              v-else
              :src="plusImage"
              alt="이미지 업로드"
            />
          </div>
        </div>

        <!-- ✅ 숨겨진 file input -->
        <input
          ref="fileInputRef"
          type="file"
          accept="image/*"
          class="file-input-hidden"
          @change="handleImageChange"
        />
      </div>

      <!-- 개수 -->
      <div class="form-row">
        <label class="label required">개수</label>
        <div class="field-wrap small">
          <BaseInput
            v-model="cnt"
            type="number"
            min="1"
          />
        </div>
      </div>

      <!-- 상품설명 -->
      <div class="form-row">
        <label class="label">상품설명</label>
        <div class="field-wrap">
          <BaseTextarea
            v-model="newOrderDtl"
            :height="'10rem'"
            placeholder="상품 설명을 입력하세요"
          />
        </div>
      </div>

      <!-- 버튼 -->
      <div class="form-actions">
        <BaseButton
          type="submit"
          width="7rem"
          height="2.5rem"
        >
          구매요청
        </BaseButton>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import api from '@/plugins/axios';
import BaseInput from '@/components/common/BaseInput.vue';
import BaseTextarea from '@/components/common/BaseTextarea.vue';
import BaseButton from '@/components/common/BaseButton.vue';
import BaseToast from '@/components/common/BaseToast.vue';
import Constant from '@/constants/constant';
import { useUiStore } from '@/stores/uiStore';
import plusImage from '@/assets/img/upload_icon.png';

const uiStore = useUiStore();
const toastRef = ref();

// 폼 데이터
const itemNm = ref('');
const cnt = ref('1');
const newOrderDtl = ref('');

// 이미지
const fileInputRef = ref<HTMLInputElement | null>(null);
const imageFile = ref<File | null>(null);
const imagePreviewUrl = ref<string>('');

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
    imagePreviewUrl.value = '';
    return;
  }

  imageFile.value = file;
  imagePreviewUrl.value = URL.createObjectURL(file);
};

// 유효성 체크
const validateForm = (): boolean => {
  if (!itemNm.value.trim()) {
    toastRef.value?.showToast('상품명을 입력하세요.');
    return false;
  }

  const countNum = Number(cnt.value);
  if (!countNum || countNum <= 0) {
    toastRef.value?.showToast('개수는 1개 이상이어야 합니다.');
    return false;
  }

  return true;
};

// 구매요청
const requestOrder = async () => {
  if (!validateForm()) return;

  if (!confirm('입력한 내용으로 신규 상품 구매를 요청하시겠습니까?')) {
    return;
  }

  try {
    uiStore.showLoading('구매 요청 중입니다...');

    const formData = new FormData();
    formData.append('itemNm', itemNm.value);
    formData.append('cnt', String(cnt.value));
    formData.append('newOrderDtl', String(newOrderDtl.value));

    if (imageFile.value) {
      formData.append('imageFile', imageFile.value);
    }

    const response = await api.post(
      '/api/itemNew/insertNewOrder',
      formData,
      {
        withCredentials: true, // ✅ 쿠키 쓰면 필수
      }
    );

    if (response.data?.[Constant.RESULT] === Constant.RESULT_SUCCESS) {
      toastRef.value?.showToast('구매 요청이 등록되었습니다.');

      // 폼 초기화
      itemNm.value = '';
      cnt.value = '';
      newOrderDtl.value = '';
      imageFile.value = null;
      imagePreviewUrl.value = '';
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
