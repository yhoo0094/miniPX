import type { UserType } from '@/types/user/user.base.type';
import type { Store } from 'pinia';

export interface UserState {
  user: UserType | null;
  authMap: Record<string, number>;
}

export interface UserActions {
  setUserInfo(userInfo: UserType): void;
  logout(): Promise<void>;
  setAuth(path: string, grade: number): void;
  getAuth(path: string): number | undefined;
}

export interface UserGetters {
  isLoggedIn: boolean;
  userNm: string;
  roleSeq: number | '';
}

// 최종 UserStore 타입
export type PiniaUserStoreType = Store<'user', UserState, UserGetters, UserActions>;
