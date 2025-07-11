  <template>

  </template>
  
  <script setup lang="ts">
  import { ref } from 'vue'
  import { useRouter } from 'vue-router'  
  import { useUserStore } from '@/stores/userStore';
  import BaseButton from '@/components/common/BaseButton.vue'

  const router = useRouter();  
  const userStore = useUserStore();
  const users = ref([]);
  </script>
  
  <style scoped>
  </style>
  