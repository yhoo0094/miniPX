import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia';
import { useUserStore } from '@/stores/user';
import App from './App.vue'
import router from './router'
import Cookies from 'js-cookie'

const app = createApp(App);
app.use(router);
app.use(createPinia());
app.mount('#app');
