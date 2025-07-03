<template>
    <div>
      <h2>사용자 목록</h2>
      <ul>
        <!-- <li v-for="user in users" :key="user.USER_ID">
          {{ user.USER_NM }} ({{ user.USER_ID }})
        </li> -->

        <li v-for="user in users" :key="user.userSeq">
            {{ user.userNm }} ({{ user.userId }})
        </li>        
      </ul>
    </div>
  </template>
  
  <script setup>
  import { onMounted, ref } from 'vue'
  import axios from 'axios'
  
  const users = ref([])
  
  onMounted(async () => {
    const response = await axios.get('http://localhost:8080/api/user/getUserList')
    users.value = response.data
  })
  </script>