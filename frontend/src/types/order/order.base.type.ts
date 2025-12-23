export interface OrderBase {
  orderSeq: number;         // ORDER_SEQ (INT) 주문일련번호
  userSeq: number;          // USER_SEQ (INT) 사용자일련번호
  itemSeq: number;          // ITEM_SEQ (INT) 상품일련번호
  price: number;            // PRICE (INT) 가격
  cnt: number;              // CNT (INT) 개수
  orderStatusCode: string;  // ORDER_STATUS_CODE (VARCHAR(2)) 주문상태코드
  orderDtti: string;        // ORDER_DTTI (TIMESTAMP) 주문일시
  orderStatusDtl: string;   // ORDER_STATUS_DTL (VARCHAR(3000)) 주문상태상세
  fstRegSeq: number;        // FST_REG_SEQ (INT) 최초등록자일련번호
  fstRegDtti: string;       // FST_REG_DTTI (TIMESTAMP) 최초등록일시
  lstUpdSeq: number;        // LST_UPD_SEQ (INT) 최종수정자일련번호
  lstUpdDtti: string;       // LST_UPD_DTTI (TIMESTAMP) 최종수정일시
}