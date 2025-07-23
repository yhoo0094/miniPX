  <template>
    <div class="container">
      <!-- 검색 조건 -->
      <div class="filters">
        <BaseDropdown
          label="분류"
          v-model="selectedCategory"
          :options="categoryOptions"
          @change="handleCategoryChange"
        />
        <BaseDropdown
          label="상세분류"
          v-model="selectedSubCategory"
          :options="subCategoryOptions"
        />
        <input type="text" class="search-input" placeholder="검색어 입력" />
        <button class="search-btn">검색</button>
      </div>

      <!-- 상품 목록 표시 -->
      <div class="item-list">
        <div class="item-card" v-for="(item, index) in itemList.slice(0, 20)" :key="item.itemSeq">
          <div class="image-box">이미지</div>
          <div class="info-box">{{ item.itemNm }}</div>
          <div class="info-box">{{ item.price.toLocaleString() }}원</div>
          <div class="info-row">
            <div class="info-box">{{ item.starRating }}</div>
            <div class="cart-icon">🛒</div>
          </div>
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
  import BaseDropdown from '@/components/common/BaseDropdown.vue';
  import api from '@/plugins/axios';

  // 현재 라우트에 대한 권한 정보
  const route = useRoute();  
  const userStore = useUserStore();
  const path = computed(() => route.path);
  const authGrade = computed(() => userStore.getAuth(path.value));

  // 드롭다운 상태
  const selectedCategory = ref('');
  const selectedSubCategory = ref('');

  // 상태 선언
  const categoryOptions = ref<{ codeDetailNm: string; codeDetail: string }[]>([]);
  const subCategoryOptions = ref<{ codeDetailNm: string; codeDetail: string }[]>([]);
  const subCategoryOptionsAll = ref<{ codeDetailNm: string; codeDetail: string }[]>([]);

  // 상품 목록
  const itemList = ref<Array<{
    itemSeq: number;
    itemNm: string;
    price: number;
    img: string;
    starRating: string; // 별점 예: "★★★★★"
  }>>([
    // 테스트용 데이터
    {
      itemSeq: 1,
      itemNm: '핫식스 더 킹 포스, 355ml, 48개',
      price: 40460,
      img: '',
      starRating: '★★★★★',
    },
    // ... 총 20개 임시 생성
  ]);  

  // ✅ 페이지 로드시 실행
  onMounted(() => {
    getCategoryOptionList();
  });

  // ✅ 서버에서 분류 목록 조회
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
    subCategoryOptions.value = subCategoryOptionsAll.value.filter(subCategoryOptionsAll => subCategoryOptionsAll.codeDetail.startsWith(option.codeDetail));
  };

  // 상품 목록 조회
  const getItemList = async (option: {ItemType}) => {
    const response = await api.post<ApiResponse<ItemType>>('/api/item/getItemList.do', {option});
  }; 

  </script>
  
  <style scoped>
  .container {
    border: 2px solid #1c2a40;
    padding: 10px;
  }

  .filters {
    display: flex;
    gap: 10px;
  }

  .dropdown,
  .search-btn {
    border: 2px solid #1c2a40;
    padding: 5px 10px;
    background: white;
    cursor: pointer;
  }

  .search-input {
    flex: 1;
    padding: 5px;
    border: 2px solid #1c2a40;
  }

  .item-list {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 16px;
    margin-top: 20px;
  }

  .item-card {
    border: 2px solid #1c2a40;
    padding: 10px;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
  }

  .image-box {
    height: 120px;
    border: 2px solid #1c2a40;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 8px;
  }

  .info-box {
    border: 2px solid #1c2a40;
    padding: 4px;
    margin-bottom: 6px;
  }

  .info-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .cart-icon {
    font-size: 20px;
    cursor: pointer;
  }  
  </style>
  