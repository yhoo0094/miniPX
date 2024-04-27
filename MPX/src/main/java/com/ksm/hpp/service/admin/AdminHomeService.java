package com.ksm.hpp.service.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ksm.hpp.framework.util.Constant;

@Service("AdminHomeService")
public class AdminHomeService {
	
	@Autowired
	SqlSession sqlSession; //SqlSession 빈 DI	
	
	/**
	* @메소드명: drawLoginLogChart
	* @작성자: KimSangMin
	* @생성일: 2024. 1. 19. 오후 2:16:56
	* @설명: 접속기록 그래프 그리기
	 */	
	public Map<String, Object> drawLoginLogChart(StringBuilder logStr, Map<String, Object> inData) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		
		List<Map<String, Object>> list = sqlSession.selectList("mapper.admin.AdminHomeMapper.drawLoginLogChart", inData);
		result.put(Constant.OUT_DATA, list);
		result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
		return result;
	}	
	
	/**
	* @메소드명: drawRequestLogChart
	* @작성자: KimSangMin
	* @생성일: 2024. 1. 22. 오후 1:26:04
	* @설명: 요청기록 그래프 그리기
	 */	
	public Map<String, Object> drawRequestLogChart(StringBuilder logStr, Map<String, Object> inData) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		
		List<Map<String, Object>> list = sqlSession.selectList("mapper.admin.AdminHomeMapper.drawRequestLogChart", inData);
		result.put(Constant.OUT_DATA, list);
		result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
		return result;
	}		
}
