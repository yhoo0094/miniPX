package com.mpx.minipx.service.market;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mpx.minipx.controller.common.BaseController;
import com.mpx.minipx.framework.util.Constant;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
	
	protected static final Log log = LogFactory.getLog(BaseController.class);
	
    @Autowired
    private SqlSessionTemplate sqlSession;      

    /**
     * @메소드명: getOrderList
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 9.
     * @설명: 주문 목록 조회
     */
    public Map<String, Object> getOrderList(Map<String, Object> inData) {
    	Map<String, Object> result = new HashMap<String, Object>(); 
    	Map<String, Object> data = new HashMap<String, Object>(); 
    	
    	//주문 목록 조회
    	List<Map<String, Object>> list = sqlSession.selectList("com.mpx.minipx.mapper.OrderMapper.getOrderList", inData);
    	data.put("list", list);
    	
    	//미결제 금액 조회
    	int unpaidAmount = sqlSession.selectOne("com.mpx.minipx.mapper.OrderMapper.getUnpaidAmount", inData);
    	data.put("unpaidAmount", unpaidAmount);
    	
    	result.put(Constant.OUT_DATA, data);
    	result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
    	return result;
    }
    
    /**
     * @메소드명: cancelOrder
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 9.
     * @설명: 주문취소
     */    
    public Map<String, Object> updateOrderStatus(Map<String, Object> inData) {
        Map<String, Object> result = new HashMap<>();

        int affected = sqlSession.update("com.mpx.minipx.mapper.OrderMapper.updateOrderStatus", inData);
        result.put(Constant.OUT_DATA, affected);
        result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
        return result;
    }       
}
