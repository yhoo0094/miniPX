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
import com.ksm.hpp.service.com.FileService;

@Service("ManageUserService")
public class ManageUserService {
	
	@Autowired
	SqlSession sqlSession; //SqlSession 빈 DI	
	
	/**
	* @메소드명: selectRoleList
	* @작성자: KimSangMin
	* @생성일: 2024. 1. 15. 오후 4:28:25
	* @설명: 권한그룹 select태그 만들기
	 */	
	public Map<String, Object> makeRoleSelectTag(StringBuilder logStr, Map<String, Object> inData) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		
		List<Map<String, Object>> list = sqlSession.selectList("mapper.admin.ManageUserMapper.makeRoleSelectTag", inData);
		
		if(list.isEmpty()) {
			result.put(Constant.RESULT, Constant.RESULT_FAILURE);
			result.put(Constant.OUT_RESULT_MSG, "권한그룹 조회 과정에서 오류가 발생하였습니다.");
			return result;			
		}
		
		result.put(Constant.OUT_DATA, list);
		result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
		return result;
	}	
	
	/**
	* @메소드명: selectUser
	* @작성자: KimSangMin
	* @생성일: 2024. 1. 15. 오후 5:58:07
	* @설명: 사용자 조회
	 */ 	
	public Map<String, Object> selectUser(StringBuilder logStr, Map<String, Object> inData) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		
		List<Map<String, Object>> list = sqlSession.selectList("mapper.admin.ManageUserMapper.selectUser", inData);
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
	* @메소드명: insertUserInfo
	* @작성자: KimSangMin
	* @생성일: 2024. 1. 16. 오후 3:30:02
	* @설명: 사용자 정보 등록
	 */	
	public Map<String, Object> insertUserInfo(StringBuilder logStr, Map<String, Object> inData) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		
		List<Map<String, Object>> list = sqlSession.selectList("mapper.admin.ManageUserMapper.selectUser", inData);
		if(!list.isEmpty()) {
			throw new ConfigurationException("이미 존재하는 아이디입니다.");
		}
		
		int cnt = sqlSession.insert("mapper.admin.ManageUserMapper.insertUserInfo", inData);
		if(cnt != 1) {
			throw new ConfigurationException("사용자 정보 저장 과정에서 오류가 발생하였습니다.");
		}
		
		cnt = sqlSession.insert("mapper.admin.ManageUserMapper.insertUserRoleMap", inData);
		if(cnt != 1) {
			throw new ConfigurationException("권한그룹 정보 저장 과정에서 오류가 발생하였습니다.");
		}		
		
		result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
		result.put(Constant.OUT_DATA, cnt);
		return result;
	}	
	
	/**
	* @메소드명: updateUserInfo
	* @작성자: KimSangMin
	* @생성일: 2024. 1. 16. 오후 1:32:28
	* @설명: 사용자 정보 수정
	 */	
	public Map<String, Object> updateUserInfo(StringBuilder logStr, Map<String, Object> inData) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		int cnt = sqlSession.update("mapper.admin.ManageUserMapper.updateUserInfo", inData);
		if(cnt != 1) {
			result.put(Constant.RESULT, Constant.RESULT_FAILURE);
			result.put(Constant.OUT_RESULT_MSG, "사용자 정보 저장 과정에서 오류가 발생하였습니다.");
			return result;			
		}
		
		cnt = sqlSession.update("mapper.admin.ManageUserMapper.updateUserRoleMap", inData);
		if(cnt != 1) {
			result.put(Constant.RESULT, Constant.RESULT_FAILURE);
			result.put(Constant.OUT_RESULT_MSG, "권한그룹 정보 저장 과정에서 오류가 발생하였습니다.");
			return result;			
		}		
		
		result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
		result.put(Constant.OUT_DATA, cnt);
		return result;
	}	
	
	/**
	* @메소드명: pwReset
	* @작성자: KimSangMin
	* @생성일: 2024. 1. 16. 오후 4:50:20
	* @설명: 비밀번호 초기화
	 */	
	public Map<String, Object> pwReset(StringBuilder logStr, Map<String, Object> inData) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		int cnt = sqlSession.update("mapper.admin.ManageUserMapper.pwReset", inData);
		if(cnt != 1) {
			result.put(Constant.RESULT, Constant.RESULT_FAILURE);
			result.put(Constant.OUT_RESULT_MSG, "비밀번호 초기화 과정에서 오류가 발생하였습니다.");
			return result;			
		}
		
		result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
		result.put(Constant.OUT_DATA, cnt);
		return result;
	}	
	
}
