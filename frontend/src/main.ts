import { createApp } from 'vue'; // Vue 앱 인스턴스 생성
import App from './App.vue';     // 루트 컴포넌트

import * as QuillNamespace from 'quill';
const Quill = (QuillNamespace as any).default ?? (QuillNamespace as any)
;(window as any).Quill = Quill

import { createPinia } from 'pinia'; // 상태관리
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'; // 상태 저장 플러그인

import { useMenuStore } from '@/stores/menuStore'
import router from './router';   // Vue Router
import clickOutside from '@/directives/v-click-outside';    //clickOutside 커스텀 디렉티브
import './assets/styles/index.scss';    //css

import { QuillEditor } from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css'

const app = createApp(App);      // 앱 인스턴스 생성
app.directive('click-outside', clickOutside);

const pinia = createPinia();     // Pinia 인스턴스 생성
pinia.use(piniaPluginPersistedstate); // 플러그인 적용

app.use(pinia);    // Pinia 연결

// ✅ 라우터 등록 후 menu 복원
const menuStore = useMenuStore();
menuStore.restoreRoutes();

app.use(router);   // 라우터 연결
app.mount('#app'); // 앱 마운트
