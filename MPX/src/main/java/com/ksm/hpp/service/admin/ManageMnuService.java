package com.ksm.hpp.service.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ksm.hpp.framework.util.Constant;
import com.ksm.hpp.service.com.FileService;

@Service("ManageMnuService")
public class ManageMnuService {
	
	@Autowired
	SqlSession sqlSession; //SqlSession 빈 DI	
	
	/**
	* @메소드명: selectMnuList
	* @작성자: KimSangMin
	* @생성일: 2023. 5. 8. 오후 6:36:23
	* @설명: 권한그룹 목록 조회
	*/
	public Map<String, Object> selectMnuList(StringBuilder logStr, Map<String, Object> inData) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		
		List<Map<String, Object>> list = sqlSession.selectList("mapper.admin.ManageMnuMapper.selectMnuList", inData);
		result.put("data", list);
		result.put(Constant.OUT_DATA, list);
		result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
		return result;
	}
	
	/**
	* @메소드명: updateAuth
	* @작성자: KimSangMin
	* @생성일: 2024. 1. 15. 오전 9:49:04
	* @설명: 메뉴 수정
	*/	
	public Map<String, Object> updateMnu(StringBuilder logStr, Map<String, Object> inData) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		int cnt = sqlSession.update("mapper.admin.ManageMnuMapper.updateMnu", inData);
		
		if(cnt != 1) {
			result.put(Constant.RESULT, Constant.RESULT_FAILURE);
			result.put(Constant.OUT_RESULT_MSG, "저장 과정에서 오류가 발생하였습니다.");
			return result;			
		}
		
		result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
		result.put(Constant.OUT_DATA, cnt);
		return result;
	}			
}
