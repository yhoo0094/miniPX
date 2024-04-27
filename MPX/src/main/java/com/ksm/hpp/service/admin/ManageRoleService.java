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

@Service("ManageRoleService")
public class ManageRoleService {
	
	@Autowired
	SqlSession sqlSession; //SqlSession 빈 DI	
	
	/**
	* @메소드명: selectRoleList
	* @작성자: KimSangMin
	* @생성일: 2023. 5. 8. 오후 6:36:23
	* @설명: 권한그룹 목록 조회
	*/
	public Map<String, Object> selectRoleList(StringBuilder logStr, Map<String, Object> inData) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		
		List<Map<String, Object>> list = sqlSession.selectList("mapper.admin.ManageRoleMapper.selectRoleList", inData);
		result.put("data", list);
		result.put(Constant.OUT_DATA, list);
		result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
		return result;
	}
	
	/**
	* @메소드명: selectGroupUser
	* @작성자: KimSangMin
	* @생성일: 2023. 5. 9. 오후 7:58:04
	* @설명: 권한그룹에 속한 사용자 목록 조회
	 */
	public Map<String, Object> selectGroupUser(StringBuilder logStr, Map<String, Object> inData) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		
		List<Map<String, Object>> list = sqlSession.selectList("mapper.admin.ManageRoleMapper.selectGroupUser", inData);
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
	* @메소드명: deleteGroupUser
	* @작성자: KimSangMin
	* @생성일: 2023. 5. 11. 오후 7:45:03
	* @설명: 권한그룹 사용자 제거
	*/
	public Map<String, Object> deleteGroupUser(StringBuilder logStr, Map<String, Object> inData) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		
		int cnt = sqlSession.delete("mapper.admin.ManageRoleMapper.deleteGroupUser", inData);
		result.put("cnt", cnt);
		
		result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
		return result;
	}		
}
