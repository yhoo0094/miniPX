package com.mpx.minipx.service.market;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mpx.minipx.controller.common.BaseController;
import com.mpx.minipx.framework.util.Constant;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemService {
	
	protected static final Log log = LogFactory.getLog(BaseController.class);
//	protected static final Logger logger = LogManager.getLogger(ControllerAdvice.class);
	
    @Autowired
    private SqlSessionTemplate sqlSession;      
	
    @Value("${jwt.secret}")
    private String jwtSecret;  // JWT 비밀 키 (application.properties에서 가져오기)    

    /**
     * @메소드명: getItemList
     * @작성자: KimSangMin
     * @생성일: 2025. 7. 17.
     * @설명: 상품 목록 조회
     */
    public Map<String, Object> getItemList(Map<String, Object> inData) {
    	Map<String, Object> result = new HashMap<String, Object>(); 
    	List<Map<String, Object>> list = sqlSession.selectList("com.mpx.minipx.mapper.UserMapper.getUserList");
    	result.put(Constant.OUT_DATA, list);
    	result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
    	return result;
    }
    
}
