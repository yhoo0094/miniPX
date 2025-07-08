// src/types/user.ts
export interface UserType {
  userId: string;
  userNm: string;
  roleSeq: number;
  [key: string]: any; // 유연성 확보 (필요 시 제거 가능)
}
