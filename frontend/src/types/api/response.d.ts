/**
 * 공통 API 응답 구조
 */
export interface ApiResponse<T = any> {
  success: boolean;
  message?: string;
  data: T;
}