<template>
  <div class="container">
    <BaseToast ref="toastRef" />
    <!-- 검색 조건 -->
    <div class="filters">
      <BaseDropdown label="분류" v-model="selectedCategory" :options="categoryOptions" :showPlaceholder="true"
        placeholderLabel="전체" @change="handleCategoryChange" 
        />
      <BaseDropdown label="상세분류" v-model="selectedSubCategory" :options="subCategoryOptions" :showPlaceholder="true"
        placeholderLabel="전체" @change="getItemList" :disabled="!selectedCategory" />
      <BaseDropdown label="정렬기준" v-model="selectedSort" :options="sortOptions" :showPlaceholder="false"
        @change="getItemList" caretText="⇅"
        />
      <div class="search-group">
        <BaseInput
          height="2.125rem"
          v-model="searchItemNm"
          class="search-itemNm"
          placeholder="상품명 입력"
          @keydown.enter.prevent="getItemList"
        />
        <BaseButton
          width="5rem"
          height="2.125rem"
          @click="getItemList"
          type="button"
        >
          검색
        </BaseButton>
      </div>
    </div>

    <!-- 상품 목록 표시 -->
    <div class="item-list">
      <div class="item-card" v-for="(item, index) in itemList" :key="item.itemSeq" >
        <div class="image-box" @click="clickItemCard(item.itemSeq)">
          <img v-if="item.imgFile" :src="item.imgFile" alt="상품 이미지" class="item-image" />
          <span v-else class="no-image">이미지 없음</span>
        </div>

        <div class="info-main">
          <div class="item-name" :title="item.itemNm" @click="clickItemCard(item.itemSeq)">
              <span v-if="item.unit > 1">{{ item.itemNm }} {{ item.unit }}개</span>
              <span v-else>{{ item.itemNm }}</span>
          </div>
          <div class="item-price">
            {{ item.unitPrice.toLocaleString()}}원<span v-if="item.unit > 1">(개당 {{ item.price.toLocaleString() }}원)</span>
          </div>
        </div>

        <div class="info-row">
          <div class="rating">
            {{ item.starRating }}
          </div>
          <BaseButton stop @click="clickBasketBtn(item)" class="basket-button" type="button" size="sm">🛒 장바구니</BaseButton>
        </div>
      </div>

      <!-- 데이터 없을 때 -->
      <div v-if="itemList.length === 0" class="empty-state">
        조회된 상품이 없습니다.
      </div>
    </div>
    <!-- 버튼 -->
    <div v-if="authLv > 1" class="btn-box">
      <BaseButton type="button" @click="moveToItemDetail" class="action-btn">
        상품등록
      </BaseButton>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/userStore';
import { getCodeList } from '@/api/code';
import type { ApiResponse } from '@/types/api/response';
import type { ItemType } from '@/types/item/itemType';
import BaseInput from '@/components/common/BaseInput.vue';
import BaseButton from '@/components/common/BaseButton.vue';
import BaseDropdown from '@/components/common/BaseDropdown.vue';
import BaseToast from '@/components/common/BaseToast.vue';
import api from '@/plugins/axios';
import Constant from '@/constants/constant';
import { useUiStore } from '@/stores/uiStore';
import router from '@/router';

// 현재 라우트에 대한 권한 정보
const route = useRoute();
const userStore = useUserStore();
const path = computed(() => route.path);
const uiStore = useUiStore();
const authLv = computed(() => userStore.currentAuthLv);

// 검색 조건
const selectedCategory = ref('');
const selectedSubCategory = ref('');
const selectedSort = ref('01');
const searchItemNm = ref('');

// 상태 선언
const categoryOptions = ref<{ codeDetailNm: string; codeDetail: string }[]>([]);
const subCategoryOptions = ref<{ codeDetailNm: string; codeDetail: string }[]>([]);
const subCategoryOptionsAll = ref<{ codeDetailNm: string; codeDetail: string }[]>([]);
const sortOptions = ref<{ codeDetailNm: string; codeDetail: string }[]>([]);

// 상품 목록
const itemList = ref<Array<ItemType>>([]);

//Toast
const toastRef = ref();

// ✅ 페이지 로드시 실행
onMounted(() => {
  getCategoryOptionList();
  getItemList();
});

// 서버에서 분류 목록 조회
const getCategoryOptionList = async () => {
  try {
    categoryOptions.value = await getCodeList('ITEM_TYPE_CODE');
    subCategoryOptionsAll.value = await getCodeList('ITEM_DTL_TYPE_CODE');
    sortOptions.value = await getCodeList('ITEM_SORT_CODE');
  } catch (error) {
    console.error('분류 조회 실패:', error);
  }
};

// 분류 선택
const handleCategoryChange = (option: { codeDetailNm: string; codeDetail: string }) => {

  //분류에서 '선택' 눌렀을 때
  if (!option.codeDetail) {
    subCategoryOptions.value = [];
    selectedSubCategory.value = '';
    return;
  }

  subCategoryOptions.value = subCategoryOptionsAll.value.filter(subCategoryOptionsAll =>
    subCategoryOptionsAll.codeDetail.startsWith(option.codeDetail)
  );

  // 상위 분류 바뀌면 하위 선택도 초기화
  selectedSubCategory.value = '';

  getItemList();
};

// 상품 목록 조회
const getItemList = async () => {
    uiStore.showLoading('상품 목록을 조회 중입니다...');

  try {
    const payload = {
      itemTypeCode: selectedCategory.value,
      itemDtlTypeCode: selectedSubCategory.value,
      itemSortCode: selectedSort.value,
      itemNm: searchItemNm.value,
    };

    const response = await api.post<ApiResponse<ItemType[]>>('/item/getItemList', payload);

    // 실제 응답 형식에 맞게 추출
    itemList.value = (response.data?.OUT_DATA || []).map(item => ({
      ...item,
      imgFile: getItemImageUrl(item.img),   // 이미지 경로 자동 매핑
      starRating: '★★★★★',                    // 서버에서 별점이 없으면 기본값 설정
    }));
  } catch (error) {
    console.error('상품 목록 조회 실패:', error);
    itemList.value = []; // 실패 시 목록 초기화
  } finally {
    uiStore.hideLoading();
  }
};

// 상품 이미지 조회
const getItemImageUrl = (img: string) => {
  return `/api/item/getItemImage?img=${img}`;
};

//상품 상세보기
const clickItemCard = (itemSeq: number) => {
  router.push({
    path: '/market/itemDetail',
    query: { itemSeq }
  });
};

//장바구니
const clickBasketBtn = async (item: ItemType) => {
  try {
    uiStore.showLoading('장바구니에 담는 중입니다...');
    item.cnt = 1;
    const response = await upsertBasket(item);

    if (response.data?.RESULT === Constant.RESULT_SUCCESS) {
      toastRef.value?.showToast("장바구니에 담았습니다 🛒");
    } else {
      toastRef.value?.showToast(response.data?.OUT_RESULT_MSG || "장바구니 추가 실패");
    }
  } catch (error) {
    toastRef.value?.showToast("장바구니 추가 중 오류 발생");
  } finally {
    uiStore.hideLoading();
  }
};

//장바구니 담기
const upsertBasket = async (payload: { itemSeq: number; }) => {
  return await api.post('/basket/upsertBasket', payload);
};

//신규상품 등록 화면 이동
const moveToItemDetail = () => {
  router.push({
    path: '/market/itemDetail',
  });
};
</script>

<style scoped>
/* 전체 컨테이너 */
.container {
  padding: 1.25rem 1.5rem;
  background: #f9fbff;
  border-radius: 1rem; 
  box-shadow: 0 0.5rem 1.125rem rgba(15, 23, 42, 0.08);
  box-sizing: border-box;
}

/* 필터 영역 */
.filters {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  align-items: flex-end;
  padding: 0.75rem 1rem; /* 12px 16px */
  margin-bottom: 1.125rem; /* 18px */
  background-color: #ffffff;
  border-radius: 0.75rem; /* 12px */
  border: 0.0625rem solid #e2e8f0; /* 1px */
  box-shadow: 0 0.125rem 0.375rem rgba(15, 23, 42, 0.05); /* 2px 6px */
}

.search-group {
  display: flex;
  align-items: flex-end;
  gap: 0.5rem;
  flex-wrap: nowrap;
  white-space: nowrap;
  flex: 1;
}

.search-itemNm {
  flex: 1 1 auto;
  min-width: 0;
}

.search-group > button {
  flex-shrink: 0;
}


/* 상품 리스트 그리드 */
.item-list {
  margin-top: 0.5rem; /* 8px */
  display: grid;
  gap: 0.5rem;
  /* grid-template-columns: repeat(auto-fill, minmax(5, 1fr)); */
  grid-template-columns: repeat(5, 1fr);
}

/* 개별 카드 */
.item-card {
  background: #ffffff;
  border-radius: 0.875rem; /* 14px */
  border: 0.0625rem solid #e2e8f0; /* 1px */
  padding: 0.75rem 0.75rem 0.625rem; /* 12px 12px 10px */
  display: flex;
  flex-direction: column;
  gap: 0.5rem; /* 8px */
  box-shadow: 0 0.25rem 0.625rem rgba(15, 23, 42, 0.06); /* 4px 10px */
  transition: transform 0.15s ease, box-shadow 0.15s ease, border-color 0.15s ease;
}

/* 이미지 영역 */
.image-box {
  height: 11rem;
  border-radius: 0.75rem;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 0.5rem;
  overflow: hidden;
  position: relative;
  cursor: pointer;
}

.item-image {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
  /* 잘리기보다는 전체가 보이게 */
  border-radius: 0.625rem; /* 10px */
}

.item-image:hover {
  transform: scale(1.1);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.no-image {
  font-size: 0.8rem;
  color: #64748b;
  padding: 0.375rem 0.625rem; /* 6px 10px */
  border-radius: 62.4375rem; /* 999px */
  background: rgba(15, 23, 42, 0.04);
  border: 0.0625rem dashed #cbd5e1; /* 1px */
}

/* 상품명 + 가격 영역 */
.info-main {
  display: flex;
  flex-direction: column;
  gap: 0.25rem; /* 4px */
}

.item-name {
  font-size: 0.9rem;
  font-weight: 600;
  color: #0f172a;
  line-height: 1.3;
  height: 2.6em;
  display: flex;
  align-items: flex-end;
  overflow: hidden;
  cursor: pointer;
}

.item-price {
  font-size: 0.9rem;
  font-weight: 700;
  color: #dc2626;
}

/* 하단 정보 행 */
.info-row {
  margin-top: 0.375rem; /* 6px */
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.rating {
  font-size: 0.85rem;
  color: #f97316;
}

/* 장바구니 버튼 */
.basket-button {
  border-radius: 10rem;
}

/* 데이터 없을 때 */
.empty-state {
  grid-column: 1 / -1;
  padding: 1.5rem 0; /* 24px */
  text-align: center;
  font-size: 0.9rem;
  color: #9ca3af;
}

/* 버튼 영역 */
.btn-box {
  text-align: right;
  height: 3rem;
  vertical-align: middle;
  line-height: 3rem;
}

/* ...px 이하일 때 */
@media (max-width: 1400px) {
  .item-list {
    grid-template-columns: repeat(4, 1fr);
  }
}

@media (max-width: 1100px) {
  .item-list {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 700px) {
  .item-list {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>