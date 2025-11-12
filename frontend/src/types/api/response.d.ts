import Constant from '@/constants/constant';
export type ResultKey = typeof Constant.RESULT; // "RESULT"


/**
 * 공통 API 응답 구조
 */
export interface ApiResponse<T = any> {
  [Constant.RESULT]: boolean;
  [Constant.OUT_RESULT_MSG]?: string;
  [Constant.OUT_DATA]: T;
}