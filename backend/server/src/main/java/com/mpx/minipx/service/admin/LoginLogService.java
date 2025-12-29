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
public class LoginLogService {
	
	protected static final Log log = LogFactory.getLog(BaseController.class);
    
    @Autowired
    private SqlSessionTemplate sqlSession;       

    /**
     * @메소드명: getLoginLogList
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 29.
     * @설명: 로그인 로그 조회
     */
    public Map<String, Object> getLoginLogList(Map<String, Object> inData) {
    	Map<String, Object> result = new HashMap<String, Object>();
    	Map<String, Object> data = new HashMap<>();
    	
    	int page = Integer.parseInt(String.valueOf(inData.getOrDefault("page", "1")));
    	int pageSize = Integer.parseInt(String.valueOf(inData.getOrDefault("pageSize", "20")));
    	int offset = (page - 1) * pageSize;
    	inData.put("pageSize", pageSize);
    	inData.put("offset", offset);    	
    	
    	List<Map<String, Object>> list = sqlSession.selectList("com.mpx.minipx.mapper.LoginLogMapper.getLoginLogList", inData);
    	int totalCount = sqlSession.selectOne("com.mpx.minipx.mapper.LoginLogMapper.getLoginLogListCount", inData);
    	
    	data.put("list", list);
    	data.put("totalCount", totalCount);
    	result.put(Constant.OUT_DATA, data);
    	result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
    	return result;
	}
}
