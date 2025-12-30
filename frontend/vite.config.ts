import { fileURLToPath, URL } from 'node:url';

import { defineConfig, loadEnv } from 'vite';
import vue from '@vitejs/plugin-vue';
import vueDevTools from 'vite-plugin-vue-devtools';

export default defineConfig(({ mode }) => {
  // .env, .env.development, .env.ec2 등에서 값 읽기
  const env = loadEnv(mode, process.cwd(), '');

  // 환경변수에서 프록시 대상 설정 (없으면 기본값은 localhost:8080)
  const proxyTarget = env.VITE_API_PROXY_TARGET || 'http://localhost:8080';

  return {
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
      allowedHosts: [
        '.ngrok-free.dev',
        '.ngrok-free.app',
      ],
      proxy: {
        '/api': {
          target: proxyTarget,
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
    build: {
      sourcemap: false,      // ✅ JS/CSS sourcemap 생성 안 함
    },    
  };
});
