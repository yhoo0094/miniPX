export interface UserType {
  userSeq: number;
  userId: string;
  userNm: string;
  aiOpenYn?: string;
  credit?: number;
  useYn?: string;
  roleSeq?: number;
  pwInitYn?: string;
  [key: string]: any; // 유연성 확보 (필요 시 제거 가능)
}

