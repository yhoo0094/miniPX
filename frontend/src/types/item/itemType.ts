export interface ItemType {
  itemSeq: number;
  itemNm: string;
  price: number;
  unit: number;
  rmrk: string;
  img: string;
  itemTypeCode: string;
  itemDtlTypeCode: string;
  soldOutYn: string;
  useYn: string;
  fstRegSeq: number;
  fstRegDtti: string;
  lstUpdDtti: number;
  lstUpdSeq: string;
  [key: string]: any;
}
