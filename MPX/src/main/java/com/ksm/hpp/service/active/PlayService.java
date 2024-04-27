package com.ksm.hpp.service.active;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ksm.hpp.framework.util.Constant;
import com.ksm.hpp.framework.util.StringUtil;
import com.ksm.hpp.service.com.FileService;

@Service("PlayService")
public class PlayService {
	
	@Autowired
	SqlSession sqlSession; //SqlSession 빈 DI	
	
	@Resource(name = "FileService")
	protected FileService fileService;

	/**
	* @메소드명: selectPlay
	* @작성자: KimSangMin
	* @생성일: 2023. 12. 6. 오후 6:38:17
	* @설명: 놀이 조회
	*/
	public Map<String, Object> selectPlay(StringBuilder logStr, Map<String, Object> inData) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		
		List<Map<String, Object>> list = sqlSession.selectList("mapper.active.PlayMapper.selectPlay", inData);
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
	* @메소드명: selectPlayHome
	* @작성자: KimSangMin
	* @생성일: 2023. 12. 12. 오후 5:53:47
	* @설명: 놀이 조회(홈 화면)
	*/
	public Map<String, Object> selectPlayHome(StringBuilder logStr, Map<String, Object> inData) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		
		List<Map<String, Object>> list = sqlSession.selectList("mapper.active.PlayMapper.selectPlayHome", inData);
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
	* @메소드명: insertPlay
	* @작성자: KimSangMin
	* @생성일: 2023. 12. 5. 오후 8:48:43
	* @설명: 놀이 등록
	 */	
	public Map<String, Object> insertPlay(StringBuilder logStr, Map<String, Object> inData, List<MultipartFile> fileList) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		
		inData = StringUtil.XssReplaceInData(inData);	//XSS스크립트 방지를 위해 텍스트 변환
		
		sqlSession.insert("mapper.active.PlayMapper.insertPlay", inData);
		fileService.insertFile(logStr, inData, fileList);
		
		result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
		result.put(Constant.OUT_DATA, inData);
		return result;
	}
	
	/**
	* @메소드명: updatePlay
	* @작성자: KimSangMin
	* @생성일: 2023. 12. 11. 오후 5:12:04
	* @설명: 놀이 수정
	*/	
	public Map<String, Object> updatePlay(StringBuilder logStr, Map<String, Object> inData, List<MultipartFile> fileList) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		
		inData = StringUtil.XssReplaceInData(inData);	//XSS스크립트 방지를 위해 텍스트 변환
		
		sqlSession.update("mapper.active.PlayMapper.updatePlay", inData);
		
		if(fileList != null && fileList.size() != 0) {
			result.put("delResult",fileService.deleteFileAll(inData));	//기존 첨부파일 제거
			fileService.insertFile(logStr, inData, fileList);			//신규 첨부파일 저장
		}
		
		result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
		result.put(Constant.OUT_DATA, inData);
		return result;
	}	
	
	/**
	* @메소드명: deletePlay
	* @작성자: KimSangMin
	* @생성일: 2023. 12. 12. 오전 11:28:06
	* @설명: 놀이 삭제
	*/	
	public Map<String, Object> deletePlay(StringBuilder logStr, Map<String, Object> inData) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		
		int cnt = sqlSession.update("mapper.active.PlayMapper.deletePlay", inData);
		String consResult = (cnt == 1)? Constant.RESULT_SUCCESS : Constant.RESULT_FAILURE;
		result.put(Constant.RESULT, consResult);
		result.put(Constant.OUT_DATA, inData);
		return result;
	}		
}
