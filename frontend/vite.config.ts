import { fileURLToPath, URL } from 'node:url';

import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import vueDevTools from 'vite-plugin-vue-devtools';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    host: '0.0.0.0',   // 👇 모든 IPv4 인터페이스에서 리슨
    port: 5173,
    // allowedHosts: [
    //   'archetypal-uneluded-felisa.ngrok-free.dev'  // ← 여기 추가
    // ],    
  allowedHosts: [
    '.ngrok-free.dev',
    '.ngrok-free.app',
  ],

    proxy: {
      '/api': {
        target: 'http://192.168.241.136:8080',
        changeOrigin: true,
        secure: false,
      },
    },
  },
  css: {
    preprocessorOptions: {
      scss: {
        additionalData: `@import "@/assets/styles/variables.scss";`
      }
    }
  },  
});
