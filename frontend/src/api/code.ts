import api from '@/plugins/axios';

export const getCodeList = async (codeGroup: string) => {
  const response = await api.post<{ codeDetail: string; 
                                    codeDetailNm: string;
                                    codeGroup: string;
                                    detailOrder: string;
                                    modifyYn: string;
                                }[]>('/common/getCodeList', {codeGroup: codeGroup});
  return response.data;
};