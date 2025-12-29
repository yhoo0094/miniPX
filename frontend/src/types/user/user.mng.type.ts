import { UserType } from './user.base.type';

export interface MngUser extends UserType {
  mode: 'view' | 'modify';
}
