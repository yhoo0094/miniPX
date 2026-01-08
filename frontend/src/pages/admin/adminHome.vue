  <template>
    <BaseToast ref="toastRef" />
    <div class="">
      <BaseButton 
        @click="upsertAllItem" 
        class="button"
        type="button"
        width="10rem"
        >upsertAllItem</BaseButton>
      <BaseButton 
        @click="upsertAllManual" 
        class="button"
        type="button"
        width="10rem"
        >upsertAllManual</BaseButton>        
    </div> 
  </template>
  
  <script setup lang="ts">
  import { ref } from 'vue';
  import { useRouter } from 'vue-router';
  import { useUserStore } from '@/stores/userStore';
  import BaseButton from '@/components/common/BaseButton.vue';
  import BaseToast from '@/components/common/BaseToast.vue';
  import api from '@/plugins/axios';
  import Constant from '@/constants/constant';
  import { useUiStore } from '@/stores/uiStore';

  const router = useRouter();  
  const userStore = useUserStore();
  const toastRef = ref();
  const uiStore = useUiStore();

  // ✅ ref 타입 명시
  const question = ref<string>('');
  const answer = ref<string>('');

  //상품 Qdrant 재설정
  const upsertAllItem = async() => {
    try {    
      uiStore.showLoading('처리 중입니다...');
      const response = await api.post('/qdrant/upsertAllItem', {});
      if (response.data?.RESULT === Constant.RESULT_SUCCESS) {
        toastRef.value?.showToast('처리 완료했습니다.');
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
  }

  //매뉴얼 Qdrant 재설정
  const upsertAllManual = async() => {
    try {    
      uiStore.showLoading('처리 중입니다...');
      const response = await api.post('/qdrant/upsertAllManual', {});
      if (response.data?.RESULT === Constant.RESULT_SUCCESS) {
        toastRef.value?.showToast('처리 완료했습니다.');
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
  }  
  </script>
  
  <style scoped>
  </style>
  