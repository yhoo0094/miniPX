package com.ksm.hpp.service.mart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ksm.hpp.framework.util.Constant;
import com.ksm.hpp.service.com.FileService;

@Service("MartService")
public class MartService {
	
	@Autowired
	SqlSession sqlSession; //SqlSession 빈 DI	
	
	@Resource(name = "FileService")
	protected FileService fileService;

	/**
	* @메소드명: selectItemList
	* @작성자: KimSangMin
	* @생성일: 2024. 4. 30. 오후 3:45:45
	* @설명: 상품 리스트 조회
	 */
	public Map<String, Object> selectItemList(StringBuilder logStr, Map<String, Object> inData) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		
		List<Map<String, Object>> list = sqlSession.selectList("mapper.mart.MartMapper.selectItemList", inData);
		result.put("data", list);
		result.put(Constant.OUT_DATA, list);
		if(!list.isEmpty()) {
			result.put("recordsFiltered", list.get(0).get("rowCnt"));	//필터링 후의 총 레코드 수
		} else {
			result.put("recordsFiltered", "0");	//필터링 후의 총 레코드 수
		}		
		result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
		return result;
	}		
	
	/**
	* @메소드명: selectTypeList
	* @작성자: KimSangMin
	* @생성일: 2024. 5. 2. 오후 4:26:16
	* @설명: 분류 목록 조회
	 */	
	public Map<String, Object> selectTypeList(StringBuilder logStr, Map<String, Object> inData) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		
		List<Map<String, Object>> list = sqlSession.selectList("mapper.mart.MartMapper.selectTypeList", inData);
		result.put("data", list);
		result.put(Constant.OUT_DATA, list);
		result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
		return result;
	}
	
	/**
	* @메소드명: selectDtlTypeList
	* @작성자: KimSangMin
	* @생성일: 2024. 5. 2. 오후 5:53:51
	* @설명: 상세 분류 목록 조회
	 */	
	public Map<String, Object> selectDtlTypeList(StringBuilder logStr, Map<String, Object> inData) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		
		List<Map<String, Object>> list = sqlSession.selectList("mapper.mart.MartMapper.selectDtlTypeList", inData);
		result.put("data", list);
		result.put(Constant.OUT_DATA, list);
		result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
		return result;
	}	
}
