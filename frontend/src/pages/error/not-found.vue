  <template>
    <div>페이지를 찾을 수 없습니다.</div>
  </template>
  
  <script setup lang="ts">
  import { onMounted} from 'vue'
  import { useRouter } from 'vue-router'  
  const router = useRouter();  

  onMounted(async () => {
    alert('페이지를 찾을 수 없습니다.');
    router.push('/main');
  })  
  </script>
  
  <style scoped>
  </style>
  