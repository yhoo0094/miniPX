  <template>
    <div class="">
      <BaseButton 
        @click="upsertAllItem" 
        class="button"
        type="button"
        width="10rem"
        >Qdrant 재설정</BaseButton>
    </div> 
  </template>
  
  <script setup lang="ts">
  import { ref } from 'vue';
  import { useRouter } from 'vue-router';
  import { useUserStore } from '@/stores/userStore';
  import BaseInput from '@/components/common/BaseInput.vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import api from '@/plugins/axios';
  import Cookies from 'js-cookie';

  const router = useRouter();  
  const userStore = useUserStore();

  // ✅ ref 타입 명시
  const question = ref<string>('');
  const answer = ref<string>('');

  //Qdrant 재설정
  const upsertAllItem = async() => {
    const response = await api.post('/qdrant/upsertAllItem', {});
  }
  </script>
  
  <style scoped>
  </style>
  