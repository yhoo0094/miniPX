package com.ksm.hpp.service.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ksm.hpp.framework.exception.ConfigurationException;
import com.ksm.hpp.framework.util.Constant;
import com.ksm.hpp.framework.util.StringUtil;
import com.ksm.hpp.service.com.FileService;

@Service("ManageAuthService")
public class ManageAuthService {
	
	@Autowired
	SqlSession sqlSession; //SqlSession 빈 DI	
	
	/**
	* @메소드명: selectMnuListLv1
	* @작성자: KimSangMin
	* @생성일: 2023. 5. 23. 오후 7:45:55
	* @설명: 메뉴 목록 조회
	*/
	public Map<String, Object> selectMnuListLv1(StringBuilder logStr, Map<String, Object> inData) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		
		List<Map<String, Object>> list = sqlSession.selectList("mapper.admin.ManageAuthMapper.selectMnuListLv1", inData);
		result.put("data", list);
		result.put(Constant.OUT_DATA, list);
		result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
		return result;
	}
	
	/**
	* @메소드명: selectRoleListAuth
	* @작성자: KimSangMin
	* @생성일: 2023. 5. 23. 오후 8:16:08
	* @설명: 권한그룹 목록 조회
	*/	
	public Map<String, Object> selectRoleListAuth(StringBuilder logStr, Map<String, Object> inData) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		
		List<Map<String, Object>> list = sqlSession.selectList("mapper.admin.ManageAuthMapper.selectRoleListAuth", inData);
		result.put("data", list);
		result.put(Constant.OUT_DATA, list);
		result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
		return result;
	}	
	
	
	/**
	* @메소드명: selectMnuListTable
	* @작성자: KimSangMin
	* @생성일: 2023. 5. 23. 오후 8:56:50
	* @설명: 메뉴 목록 조회
	*/
	public Map<String, Object> selectMnuListTable(StringBuilder logStr, Map<String, Object> inData) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		
		List<Map<String, Object>> list = sqlSession.selectList("mapper.admin.ManageAuthMapper.selectMnuListTable", inData);
		result.put("data", list);
		result.put(Constant.OUT_DATA, list);
		result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
		return result;
	}	
	
	/**
	* @메소드명: updateAuth
	* @작성자: KimSangMin
	* @생성일: 2023. 5. 27. 오후 7:58:30
	* @설명: 권한 수정
	*/
	public Map<String, Object> updateAuth(StringBuilder logStr, Map<String, Object> inData) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		int cnt = sqlSession.update("mapper.admin.ManageAuthMapper.updateAuth", inData);
		result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
		result.put(Constant.OUT_DATA, cnt);
		return result;
	}		
}
