export interface NewOrder {
  newOrderSeq: number;          // NEW_ORDER_SEQ (INT) 신규상품주문일련번호
  userSeq: number;              // USER_SEQ (INT) 사용자일련번호
  itemNm: string;               // ITEM_NM (VARCHAR(300)) 상품명
  img: string;                  // IMG (VARCHAR(100)) 이미지(파일명/경로 등)
  price: number;                // PRICE (INT) 가격
  cnt: number;                  // CNT (INT) 개수
  newOrderDtl: string;          // NEW_ORDER_DTL (VARCHAR(300)) 신규상품주문상세
  orderStatusCode: string;      // ORDER_STATUS_CODE (VARCHAR(2)) 주문상태코드
  orderDtti: string;            // ORDER_DTTI (TIMESTAMP) 주문일시
  orderStatusDtl: string;       // ORDER_STATUS_DTL (VARCHAR(3000)) 주문상태상세
  fstRegSeq: number;            // FST_REG_SEQ (INT) 최초등록자일련번호
  fstRegDtti: string;           // FST_REG_DTTI (TIMESTAMP) 최초등록일시
  lstUpdSeq: number;            // LST_UPD_SEQ (INT) 최종수정자일련번호
  lstUpdDtti: string;           // LST_UPD_DTTI (TIMESTAMP) 최종수정일시

  imgPath: string; 
}