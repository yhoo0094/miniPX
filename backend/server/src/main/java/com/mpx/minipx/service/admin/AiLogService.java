package com.mpx.minipx.service.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mpx.minipx.framework.util.Constant;
import com.mpx.minipx.repository.TbLogReqRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiLogService {
	
    @Autowired
    private SqlSessionTemplate sqlSession;  	

    /**
     * @메소드명: getReqLogList
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 30.
     * @설명: 요청 로그 목록 조회
     */    
    public Map<String, Object> getAiSessionList(Map<String, Object> inData) {
    	Map<String, Object> result = new HashMap<String, Object>();
    	Map<String, Object> data = new HashMap<>();
    	
    	int page = Integer.parseInt(String.valueOf(inData.getOrDefault("page", "1")));
    	int pageSize = Integer.parseInt(String.valueOf(inData.getOrDefault("pageSize", "20")));
    	int offset = (page - 1) * pageSize;
    	inData.put("pageSize", pageSize);
    	inData.put("offset", offset);    	
    	
    	List<Map<String, Object>> list = sqlSession.selectList("com.mpx.minipx.mapper.AiLogMapper.getAiSessionList", inData);
    	int totalCount = sqlSession.selectOne("com.mpx.minipx.mapper.AiLogMapper.getAiSessionListCount", inData);
    	
    	data.put("list", list);
    	data.put("totalCount", totalCount);
    	result.put(Constant.OUT_DATA, data);
    	result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
    	return result;    	
    }    
    
    /**
     * @메소드명: getAiSessionDetail
     * @작성자: KimSangMin
     * @생성일: 2026. 1. 2.
     * @설명: 세션 상세 조회
     */    
    public Map<String, Object> getAiSessionDetail(Map<String, Object> inData) {
    	Map<String, Object> result = new HashMap<String, Object>();
    	Map<String, Object> data = new HashMap<>();
    	
    	List<Map<String, Object>> list = sqlSession.selectList("com.mpx.minipx.mapper.AiLogMapper.getAiSessionDetail", inData);
    	
    	data.put("list", list);
    	result.put(Constant.OUT_DATA, data);
    	result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
    	return result;    	
    }      
}
