export interface BasketType {
  itemSeq: number;
  itemNm: string;
  price: number;
  unitPrice: number;
  cnt: number;
  unit: number;
  checked: boolean;
  imgFile?: string;
  [key: string]: any;   //정의되지 않은 필드 허용
}
