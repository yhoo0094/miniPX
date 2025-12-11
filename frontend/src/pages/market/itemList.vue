<template>
  <div class="container">
    <BaseToast ref="toastRef" />
    <!-- 검색 조건 -->
    <div class="filters">
      <div>
        <BaseDropdown label="분류" v-model="selectedCategory" :options="categoryOptions" :showPlaceholder="true"
          placeholderLabel="선택" @change="handleCategoryChange" />
      </div>
      <div>
        <BaseDropdown label="상세분류" v-model="selectedSubCategory" :options="subCategoryOptions" :showPlaceholder="true"
          placeholderLabel="선택" :disabled="!selectedCategory" />
      </div>
      <div class="filter-search">
        <BaseInput height="2.125rem" v-model="searchItemNm" class="input" placeholder="상품명 입력" @keydown.enter.prevent="getItemList" />
      </div>
      <BaseButton width="5rem" height="2.125rem" @click="getItemList" type="button">검색</BaseButton>
    </div>

    <!-- 상품 목록 표시 -->
    <div class="item-list">
      <div class="item-card" v-for="(item, index) in itemList" :key="item.itemSeq" @click="clickItemCard">
        <div class="image-box">
          <img v-if="item.imgFile" :src="item.imgFile" alt="상품 이미지" class="item-image" />
          <span v-else class="no-image">이미지 없음</span>
        </div>

        <div class="info-main">
          <div class="item-name" :title="item.itemNm">{{ item.itemNm }}</div>
          <div class="item-price">{{ item.price.toLocaleString() }}원</div>
        </div>

        <div class="info-row">
          <div class="rating">
            {{ item.starRating }}
          </div>
          <button @click.stop="clickBasketBtn(item)" class="basket-btn" type="button">🛒 장바구니</button>
        </div>
      </div>

      <!-- 데이터 없을 때 -->
      <div v-if="itemList.length === 0" class="empty-state">
        조회된 상품이 없습니다.
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/userStore';
import { getCodeList } from '@/api/code';
import type { ApiResponse } from '@/types/api/response';
import type { ItemType } from '@/types/itemType';
import BaseInput from '@/components/common/BaseInput.vue';
import BaseButton from '@/components/common/BaseButton.vue';
import BaseDropdown from '@/components/common/BaseDropdown.vue';
import BaseToast from '@/components/common/BaseToast.vue';
import api from '@/plugins/axios';
import Constant from '@/constants/constant';
import { useUiStore } from '@/stores/uiStore';


// 현재 라우트에 대한 권한 정보
const route = useRoute();
const userStore = useUserStore();
const path = computed(() => route.path);
const authGrade = computed(() => userStore.getAuth(path.value));
const uiStore = useUiStore();

// 검색 조건
const selectedCategory = ref('');
const selectedSubCategory = ref('');
const searchItemNm = ref('');

// 상태 선언
const categoryOptions = ref<{ codeDetailNm: string; codeDetail: string }[]>([]);
const subCategoryOptions = ref<{ codeDetailNm: string; codeDetail: string }[]>([]);
const subCategoryOptionsAll = ref<{ codeDetailNm: string; codeDetail: string }[]>([]);

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
};

// 상품 목록 조회
interface GetItemListPayload {
  itemTypeCode: string;
  itemDtlTypeCode: string;
  itemNm: string;
}

const getItemList = async () => {
    uiStore.showLoading('상품 목록을 조회 중입니다...');

  try {
    const payload = {
      itemTypeCode: selectedCategory.value,
      itemDtlTypeCode: selectedSubCategory.value,
      itemNm: searchItemNm.value,
    };

    const response = await api.post<ApiResponse<ItemType[]>>('/item/getItemList', payload);

    // 실제 응답 형식에 맞게 추출
    itemList.value = (response.data?.OUT_DATA || []).map(item => ({
      ...item,
      imgFile: getItemImageUrl(item.itemSeq),   // 이미지 경로 자동 매핑
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
const getItemImageUrl = (itemSeq: number) => {
  return `/api/item/getItemImage?itemSeq=${itemSeq}`;
};

//상품 상세보기
const clickItemCard = () => {
  alert('상품 상세보기 기능은 구현 예정입니다.');
};

//장바구니
const clickBasketBtn = async (item: ItemType) => {
  try {
    item.cnt = 1;
    const response = await insertBasket(item);

    if (response.data?.RESULT === Constant.RESULT_SUCCESS) {
      toastRef.value?.showToast("장바구니에 담았습니다 🛒");
    } else {
      toastRef.value?.showToast(response.data?.OUT_RESULT_MSG || "장바구니 추가 실패");
    }
  } catch (error) {
    toastRef.value?.showToast("장바구니 추가 중 오류 발생");
  }
};

const insertBasket = async (payload: { itemSeq: number; }) => {
  return await api.post('/basket/insertBasket', payload);
};
</script>

<style scoped>
/* 전체 컨테이너 */
.container {
  /* max-width: 1200px; */
  padding: 20px 24px;
  background: #f9fbff;
  border-radius: 16px;
  box-shadow: 0 8px 18px rgba(15, 23, 42, 0.08);
  box-sizing: border-box;
}

/* 필터 영역 */
.filters {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  align-items: flex-end;
  padding: 12px 16px;
  margin-bottom: 18px;
  background-color: #ffffff;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 2px 6px rgba(15, 23, 42, 0.05);
}

.filter-search {
  flex: 1;
}

/* 상품 리스트 그리드 */
.item-list {
  margin-top: 8px;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 18px;
}

/* 개별 카드 */
.item-card {
  background: #ffffff;
  border-radius: 14px;
  border: 1px solid #e2e8f0;
  padding: 12px 12px 10px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  box-shadow: 0 4px 10px rgba(15, 23, 42, 0.06);
  transition: transform 0.15s ease, box-shadow 0.15s ease, border-color 0.15s ease;
  cursor: pointer;
}

.item-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 18px rgba(15, 23, 42, 0.12);
  border-color: #bfdbfe;
}

/* 이미지 영역 */
.image-box {
  height: 180px;
  /* 살짝 더 키워서 카드 비율 예쁘게 */
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 8px;
  overflow: hidden;
  position: relative;
}

.item-image {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
  /* 잘리기보다는 전체가 보이게 */
  border-radius: 10px;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.no-image {
  font-size: 0.8rem;
  color: #64748b;
  padding: 6px 10px;
  border-radius: 999px;
  background: rgba(15, 23, 42, 0.04);
  border: 1px dashed #cbd5e1;
}

/* 상품명 + 가격 영역 */
.info-main {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.item-name {
  font-size: 0.95rem;
  font-weight: 600;
  color: #0f172a;
  line-height: 1.3;
  max-height: 2.6em;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  /* -webkit-line-clamp: 2; */
  /* 2줄까지만 표시 */
  -webkit-box-orient: vertical;
}

.item-price {
  font-size: 1rem;
  font-weight: 700;
  color: #dc2626;
}

/* 하단 정보 행 */
.info-row {
  margin-top: 6px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.rating {
  font-size: 0.85rem;
  color: #f97316;
}

/* 장바구니 버튼 */
.basket-btn {
  border: none;
  background: #0f172a;
  color: #ffffff;
  font-size: 0.8rem;
  border-radius: 999px;
  padding: 4px 10px;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  transition: background 0.15s ease, transform 0.12s ease;
}

.basket-btn:hover {
  background: #111827;
  transform: translateY(-1px);
}

.basket-btn:active {
  transform: translateY(0);
}

/* 데이터 없을 때 */
.empty-state {
  grid-column: 1 / -1;
  padding: 24px 0;
  text-align: center;
  font-size: 0.9rem;
  color: #9ca3af;
}

/* 반응형(모바일) */
@media (max-width: 768px) {
  .container {
    margin: 12px;
    padding: 16px;
  }

  .filters {
    flex-direction: column;
    align-items: stretch;
  }

  .search-btn {
    width: 100%;
    justify-content: center;
  }
}
</style>