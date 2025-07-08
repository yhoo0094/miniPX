import api from '@/plugins/axios'

export const checkAuth = async (): Promise<boolean> => {
  try {
    const response = await api.get<{ authenticated: boolean }>('/api/auth/check');
    return response.data.authenticated === true;
  } catch (error) {
    return false;
  }
}
