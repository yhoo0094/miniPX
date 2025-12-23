package com.mpx.minipx.service.market;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mpx.minipx.controller.common.BaseController;
import com.mpx.minipx.framework.util.Constant;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BasketService {
	
	protected static final Log log = LogFactory.getLog(BaseController.class);
	
    @Autowired
    private SqlSessionTemplate sqlSession;      

    /**
     * @메소드명: getBasketList
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 8.
     * @설명: 장바구니 목록 조회
     */
    public Map<String, Object> getBasketList(Map<String, Object> inData) {
    	Map<String, Object> result = new HashMap<String, Object>(); 
    	List<Map<String, Object>> list = sqlSession.selectList("com.mpx.minipx.mapper.BasketMapper.getBasketList", inData);
    	result.put(Constant.OUT_DATA, list);
    	result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
    	return result;
    }
    
    /**
     * @메소드명: insertBasket
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 8.
     * @설명: 장바구니 등록
     */
    public Map<String, Object> upsertBasket(Map<String, Object> inData) {
        Map<String, Object> result = new HashMap<>();

        int affected = sqlSession.insert("com.mpx.minipx.mapper.BasketMapper.upsertBasket", inData);
        result.put(Constant.OUT_DATA, affected);
        result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
        return result;
    }  
    
    /**
     * @메소드명: updateBasketCnt
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 9.
     * @설명: 장바구니 개수 수정
     */
    public Map<String, Object> updateBasketCnt(Map<String, Object> inData) {
        Map<String, Object> result = new HashMap<>();

        int affected = sqlSession.update("com.mpx.minipx.mapper.BasketMapper.updateBasketCnt", inData);
        result.put(Constant.OUT_DATA, affected);
        result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
        return result;
    }     
    
    /**
     * @메소드명: deleteBasket
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 10.
     * @설명: 장바구니 삭제
     */    
    public Map<String, Object> deleteBasket(Map<String, Object> inData) {
        Map<String, Object> result = new HashMap<>();

        int affected = sqlSession.delete("com.mpx.minipx.mapper.BasketMapper.deleteBasket", inData);
        result.put(Constant.OUT_DATA, affected);
        result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
        return result;
    }      
    
    /**
     * @메소드명: insertOrder
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 9.
     * @설명: 장바구니 건들 구매 요청
     */    
    @Transactional
    public Map<String, Object> insertOrder(Map<String, Object> inData) throws Exception {
        Map<String, Object> result = new HashMap<>();

        @SuppressWarnings("unchecked")
        List<Long> itemSeqList = (List<Long>) inData.get("itemSeqList");

        if (itemSeqList == null || itemSeqList.isEmpty()) {
            result.put("RESULT", Constant.RESULT_FAILURE);
            result.put("OUT_RESULT_MSG", "변경 대상이 없습니다.");
            return result;
        }
        
        int insertCnt = sqlSession.insert("com.mpx.minipx.mapper.BasketMapper.insertOrderList", inData);
        int deleteCnt = sqlSession.delete("com.mpx.minipx.mapper.BasketMapper.deleteBasketList", inData);

        if (insertCnt > 0 && deleteCnt > 0) {
            result.put("RESULT", Constant.RESULT_SUCCESS);
        } else if(insertCnt == 0) {
            result.put("RESULT", Constant.RESULT_FAILURE);
            result.put("OUT_RESULT_MSG", "구매 요청이 등록되지 않았습니다.");
        } else {
            result.put("RESULT", Constant.RESULT_FAILURE);
            result.put("OUT_RESULT_MSG", "구매 요청 대상이 존재하지 않습니다.");
        }

        return result;
    }    
}
