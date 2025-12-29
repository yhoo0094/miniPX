import api from '@/plugins/axios'
import type { PiniaUserStoreType } from '@/types/user/userStoreType';

export const checkAuth = async (path: string, userStore: PiniaUserStoreType): Promise<boolean> => {
  try {
    const response = await api.post<{ authenticated: boolean, authGrade: number }>('/auth/check', {path: path});
    
    //해당 경로에 대한 권한 정보 저장
    if(response.data.authenticated) {
      userStore.setAuth(path, response.data.authGrade);
    }

    return response.data.authenticated;
  } catch (error) {
    return false;
  }
}
