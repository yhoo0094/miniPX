import { ItemBase } from './item.base.type';

export interface ItemDetail extends ItemBase {
    imgPath: string;
    cnt: number;        //구매 개수
}