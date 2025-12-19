package com.mpx.minipx.service.admin;

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
public class MngOrderService {
	
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
    	List<Map<String, Object>> list = sqlSession.selectList("com.mpx.minipx.mapper.MngOrderMapper.getOrderList", inData);
    	result.put(Constant.OUT_DATA, list);
    	result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
    	return result;
    }
    
    /**
     * @메소드명: updateOrderStatus
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 9.
     * @설명: 주문 상태 변경
     */    
    public Map<String, Object> updateOrderStatus(Map<String, Object> inData) {
        Map<String, Object> result = new HashMap<>();

        int affected = sqlSession.update("com.mpx.minipx.mapper.MngOrderMapper.updateOrderStatus", inData);
        result.put(Constant.OUT_DATA, affected);
        result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
        return result;
    }       
}
