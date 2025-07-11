import api from '@/plugins/axios'

export const checkAuth = async (path: string): Promise<boolean> => {
  try {
    const response = await api.post<{ authenticated: boolean }>('/api/auth/check', {path: path});
    return response.data.authenticated === true;
  } catch (error) {
    return false;
  }
}
