import { defineStore } from 'pinia';

interface UiState {
  loading: boolean;
  loadingMessage: string;
}

export const useUiStore = defineStore('ui', {
  state: (): UiState => ({
    loading: false,
    loadingMessage: '',
  }),
  actions: {
    showLoading(message?: string) {
      this.loading = true;
      this.loadingMessage = message || '처리 중입니다...';
    },
    hideLoading() {
      this.loading = false;
      this.loadingMessage = '';
    },
  },
});
