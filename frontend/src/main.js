import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia';
import App from './App.vue'
import router from './router'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate';
import { useMenuStore } from '@/stores/menuStore';

const app = createApp(App);
const pinia = createPinia();
pinia.use(piniaPluginPersistedstate);

app.use(router);
app.use(pinia);

// ✅ Pinia store 사용은 app.use(pinia) 이후에!
const menuStore = useMenuStore();
menuStore.restoreRoutes();

app.mount('#app');
