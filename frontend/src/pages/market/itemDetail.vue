<template>
  <div class="order-container">
    <BaseToast ref="toastRef" />

    <h2 class="title">상품상세정보</h2>

    <form class="order-form" @submit.prevent="upsertItem">
      <div class="form-row">
        <label class="label">상품명</label>
        <div class="field-wrap">
          <label v-if="mode === 'view' && itemDetail" class="label">
            {{ itemDetail.itemNm }} <span v-if="itemDetail.unit > 1">{{ itemDetail.unit }}개</span>
          </label>
          <BaseInput v-else v-model="itemDetail.itemNm" />
        </div>
      </div>
      <div class="form-row">
        <label class="label">이미지</label>
        <div class="image-box" :class="{ 'is-modify': mode === 'modify' }" @click="triggerFileSelect">
          <div class="image-preview">
            <img v-if="itemDetail.imgPath" :src="itemDetail.imgPath" alt="상품 이미지 미리보기" />
            <img v-else :src="plusImage" alt="이미지 업로드" />
          </div>
        </div>
        <input ref="fileInputRef" type="file" accept="image/*" class="file-input-hidden" @change="handleImageChange" />
      </div>
      <div v-if="mode === 'view'" class="form-row">
        <label class="label">개수</label>
        <div class="field-wrap small cnt-box">
          <button type="button" class="cnt-btn" @click="decreaseCnt">−</button>
          <span class="cnt-value">{{ itemDetail.cnt }}</span>
          <button type="button" class="cnt-btn" @click="increaseCnt">+</button>
        </div>
      </div>
      <div v-if="mode === 'modify'" class="form-row">
        <label class="label">판매단위</label>
        <div class="field-wrap small cnt-box">
          <button type="button" class="cnt-btn" @click="decreaseUnit">−</button>
          <span class="cnt-value">{{ itemDetail.unit }}</span>
          <button type="button" class="cnt-btn" @click="increaseUnit">+</button>
        </div>
      </div>      
      <div class="form-row">
        <label class="label">가격</label>
        <div class="field-wrap">
          <label v-if="mode === 'view' && itemDetail" class="label">
            {{ sumPrice.toLocaleString() }}
            <span v-if="itemDetail.unit > 1">(개당 {{ itemDetail.price.toLocaleString() }}원)</span>
          </label>
          <BaseInput v-else v-model="itemDetail.price" />
        </div>
      </div>
      <div v-if="mode === 'modify'" class="form-row">
        <label class="label">상품분류</label>
        <div class="field-wrap">
          <BaseDropdown label="분류" v-model="itemDetail.itemTypeCode" :options="categoryOptions" :showPlaceholder="true" class="category-dropdown"
            placeholderLabel="전체" @change="handleCategoryChange" 
            />
          <BaseDropdown label="상세분류" v-model="itemDetail.itemDtlTypeCode" :options="subCategoryOptions" :showPlaceholder="true"
            placeholderLabel="전체" :disabled="!itemDetail.itemTypeCode" />
        </div>
      </div>    
      <div v-if="mode === 'modify'" class="form-row">
        <label class="label">품절여부</label>
        <div class="field-wrap">
          <BaseToggle v-model="itemDetail.soldOutYn" onText="Y" offText="N" />
        </div>
      </div> 
      <div v-if="mode === 'modify'" class="form-row">
        <label class="label">사용여부</label>
        <div class="field-wrap">
          <BaseToggle v-model="itemDetail.useYn" onText="Y" offText="N" />
        </div>
      </div>                   

      <div class="form-row">
        <label class="label">상품설명</label>
        <div class="field-wrap">
          <BaseTextarea v-model="itemDetail.rmrk" :height="'10rem'" :readonly="mode === 'view'" :maxlength="1000" />
        </div>
      </div>

      <!-- 버튼 -->
      <div class="form-actions">
        <BaseButton v-if="authLv > 1 && mode === 'view'" type="button" @click="changeToModify" class="action-btn">
          수정
        </BaseButton>        
        <BaseButton v-if="authLv > 1 && mode === 'modify'" type="submit" class="action-btn">
          저장
        </BaseButton>
        <BaseButton variant="danger" v-if="mode === 'modify'" type="button" @click="changeToView" class="action-btn">
          취소
        </BaseButton>           
        <BaseButton v-if="mode === 'view'" type="button" @click="upsertBasket" class="action-btn">
          장바구니
        </BaseButton>
        <BaseButton v-if="mode === 'view'" type="button" @click="requestOrder" class="action-btn">
          구매요청
        </BaseButton>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, watch  } from 'vue';
import { useRoute } from 'vue-router';
import { useUserStore } from '@/stores/userStore';
import api from '@/plugins/axios';
import BaseInput from '@/components/common/BaseInput.vue';
import BaseTextarea from '@/components/common/BaseTextarea.vue';
import BaseButton from '@/components/common/BaseButton.vue';
import BaseToast from '@/components/common/BaseToast.vue';
import BaseToggle from '@/components/common/BaseToggle.vue';
import BaseDropdown from '@/components/common/BaseDropdown.vue';
import Constant from '@/constants/constant';
import { useUiStore } from '@/stores/uiStore';
import plusImage from '@/assets/img/upload_icon.png';
import type { ItemDetail } from '@/types/item/item.detail.type';
import { getCodeList } from '@/api/code';

const route = useRoute();
const userStore = useUserStore();
const uiStore = useUiStore();
const toastRef = ref();

//권한
const authLv = computed(() => userStore.currentAuthLv);
const itemSeq = route.query.itemSeq;

// 화면 모드
const mode = ref<'view' | 'modify'>('view');

// 폼 데이터
const itemDetail = ref<ItemDetail>({});
const DEFAULT_ITEM_DETAIL: ItemDetail = {
  itemSeq: '',
  itemNm: '',
  unit: 1,
  cnt: 1,
  price: 0,
  rmrk: '',
  itemTypeCode: '',
  itemDtlTypeCode: '',
  soldOutYn: 'N',
  useYn: 'Y',
  img: '',
  imgPath: '',
};
const sumPrice = computed(() => {
  if (!itemDetail.value) return 0;
  return itemDetail.value.unitPrice * itemDetail.value.cnt;
});

// 이미지
const fileInputRef = ref<HTMLInputElement | null>(null);
const imageFile = ref<File | null>(null);

// 상태 선언
const categoryOptions = ref<{ codeDetailNm: string; codeDetail: string }[]>([]);
const subCategoryOptions = ref<{ codeDetailNm: string; codeDetail: string }[]>([]);
const subCategoryOptionsAll = ref<{ codeDetailNm: string; codeDetail: string }[]>([]);

// 서버에서 분류 목록 조회
const getCategoryOptionList = async () => {
  try {
    categoryOptions.value = await getCodeList('ITEM_TYPE_CODE');
    subCategoryOptionsAll.value = await getCodeList('ITEM_DTL_TYPE_CODE');
  } catch (error) {
    console.error('분류 조회 실패:', error);
  } finally {
    subCategoryOptions.value = subCategoryOptionsAll.value.filter(subCategoryOptionsAll =>
      subCategoryOptionsAll.codeDetail.startsWith(itemDetail.value.itemTypeCode)
    );    
  }
};

// 분류 선택
const handleCategoryChange = (option: { codeDetail: string }) => {
  //분류에서 '선택' 눌렀을 때
  if (!option.codeDetail) {
    subCategoryOptions.value = [];
    itemDetail.value.itemDtlTypeCode = '';
    return;
  }

  subCategoryOptions.value = subCategoryOptionsAll.value.filter(subCategoryOptionsAll =>
    subCategoryOptionsAll.codeDetail.startsWith(option.codeDetail)
  );

  // 상위 분류 바뀌면 하위 선택도 초기화
  itemDetail.value.itemDtlTypeCode = '';
};

// 상품상세 조회
const getItemDetail = async () => {
  uiStore.showLoading('상품 정보를 조회 중입니다...');

  try {
    const payload = { itemSeq };
    const response = await api.post('/itemDetail/getItemDetail', payload);
    const data = response.data?.OUT_DATA;
    if (!data) return;

    // 실제 응답 형식에 맞게 추출
    itemDetail.value = response.data?.OUT_DATA;
    itemDetail.value.imgPath = '/api/item/getItemImage?img=' + itemDetail.value.img;
    itemDetail.value.cnt = 1;

    // 이미지 URL이 내려오는 경우
    if (data.img) {
      itemDetail.value.imgPath = `/api/item/getItemImage?img=` + data.img;
    }
  } catch (error) {
    console.error('상품 조회 실패:', error);
    toastRef.value?.showToast('상품 정보를 불러오지 못했습니다.');
  } finally {
    uiStore.hideLoading();
  }
};

// 이미지 영역 클릭 시 파일 선택
const triggerFileSelect = () => {
  if(mode.value === 'view') {return;}
  fileInputRef.value?.click();
};

// 이미지 선택 시
const handleImageChange = (event: Event) => {
  const target = event.target as HTMLInputElement;
  const file = target.files?.[0];

  if (!file) {
    imageFile.value = null;
    itemDetail.value.imgPath = '';
    return;
  }

  imageFile.value = file;
  itemDetail.value.imgPath = URL.createObjectURL(file);
};

// 유효성 체크
const validateForm = (): boolean => {
  if (!itemDetail.value.itemNm.trim()) {
    toastRef.value?.showToast('상품명을 입력하세요.');
    return false;
  }

  const countNum = Number(itemDetail.value.cnt);
  if (!countNum || countNum <= 0) {
    toastRef.value?.showToast('개수는 1개 이상이어야 합니다.');
    return false;
  }

  return true;
};

// 장바구니
const upsertBasket = async () => {
  try {
    uiStore.showLoading('장바구니에 담는 중입니다...');

    const response = await api.post('/itemDetail/upsertBasket', itemDetail.value);

    if (response.data?.RESULT === Constant.RESULT_SUCCESS) {
      toastRef.value?.showToast("장바구니에 담았습니다 🛒");
    } else {
      toastRef.value?.showToast(response.data?.OUT_RESULT_MSG || '처리 실패');
    }
  } catch (error) {
    console.error(error);
    toastRef.value?.showToast('처리 중 오류가 발생했습니다');
  } finally {
    uiStore.hideLoading();
  }
};

// 구매요청
const requestOrder = async () => {
  try {
    uiStore.showLoading('구매 요청 중입니다...');

    const response = await api.post('/itemDetail/requestOrder', itemDetail.value);

    if (response.data?.RESULT === Constant.RESULT_SUCCESS) {
      toastRef.value?.showToast('구매 요청이 완료되었습니다.');
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

// 저장
const upsertItem = async () => {
  if (!validateForm()) return;

  if (!confirm('저장하시겠습니까?')) {
    return;
  }

  try {
    uiStore.showLoading('저장 중입니다...');

    //값 바인딩
    const payload = {
      itemSeq: itemDetail.value.itemSeq,
      itemNm: itemDetail.value.itemNm,
      unit: itemDetail.value.unit,
      price: itemDetail.value.price,
      rmrk: itemDetail.value.rmrk,
      itemTypeCode: itemDetail.value.itemTypeCode,
      itemDtlTypeCode: itemDetail.value.itemDtlTypeCode,
      soldOutYn: itemDetail.value.soldOutYn,
      useYn: itemDetail.value.useYn,
    };
    const formData = new FormData();
    for (const key in payload) {
      formData.append(key, String(payload[key as keyof typeof payload]));
    }

    //이미지를 변경한 경우 파라미터에 추가
    if (imageFile.value) {
      formData.append('imageFile', imageFile.value);
    }

    const response = await api.post('/itemDetail/upsertItem',
                                    formData,
                                   {withCredentials: true,}); // ✅ 쿠키 쓰면 필수

    if (response.data?.[Constant.RESULT] === Constant.RESULT_SUCCESS) {
      toastRef.value?.showToast('저장되었습니다.');
      changeToView();
    } else {
      toastRef.value?.showToast(
        response.data?.OUT_RESULT_MSG || '저장에 실패했습니다.'
      );
    }
  } catch (error) {
    console.error(error);
    toastRef.value?.showToast('저장 중 오류가 발생했습니다.');
  } finally {
    uiStore.hideLoading();
  }
};

// 수정 버튼 클릭
const changeToModify = () => {
  mode.value = 'modify';
};

// 취소 버튼 클릭
const changeToView = () => {
  mode.value = 'view';
};

//개수 증가
const increaseCnt = async () => {
  itemDetail.value.cnt++;
}

//개수 감소
const decreaseCnt = async () => {
  if(itemDetail.value.cnt > 1) {itemDetail.value.cnt--;}
}

//판매단위 증가
const increaseUnit = async () => {
  itemDetail.value.unit++;
}

//판매단위 감소
const decreaseUnit = async () => {
  if(itemDetail.value.unit > 1) {itemDetail.value.unit--;}
}

onMounted(() => {
  getCategoryOptionList();
  if (itemSeq) { 
    getItemDetail(); 
  } else {
    itemDetail.value = { ...DEFAULT_ITEM_DETAIL };
    changeToModify();
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
  font-weight: bold;
  text-align: right;
  margin-right: 1rem;
}

.field-wrap {
  flex: 1;
  font-size: 0.9rem;
}

.field-wrap.small {
  max-width: 220px;
}

.image-preview {
  text-align: center;
}

.image-preview img {
  width: 99%;
  height: 99%;
  object-fit: contain;
}

/* file input 완전 숨김 */
.file-input-hidden {
  display: none;
}

/* 이미지 박스 */
.image-box {
  width: 10rem;
  height: 10rem;
  border: 1px solid #b8c4d1;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f9fafb;
  overflow: hidden;
}

.image-box.is-modify {
  cursor: pointer;
}

/* 개수 */
.cnt-box {
  display: flex;
  align-items: center;
  gap: 8px;
}

.cnt-btn {
  width: 28px;
  height: 28px;
  border-radius: 6px;
  border: 1px solid #cbd5e1;
  background: #f1f5f9;
  color: #0f172a;
  font-size: 1.1rem;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.15s ease;
  box-shadow: 0 2px 4px rgba(15, 23, 42, 0.08);
}

.cnt-value {
  font-size: 0.9rem;
  font-weight: bold;
  width: 1rem;
  text-align: center;
}

.category-dropdown{
  margin-right: 0.5rem;
}

/* 버튼 영역 */
.form-actions {
  margin-top: 8px;
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
}

.action-btn{
  width: 7rem;
  height: 2.5rem;
}
</style>
