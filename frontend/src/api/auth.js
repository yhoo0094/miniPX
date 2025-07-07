import api from '@/plugins/axios'

export const checkAuth = async () => {
  try {
    const response = await api.get('/api/auth/check');
    return response.data.authenticated === true
  } catch (error) {
    return false
  }
}