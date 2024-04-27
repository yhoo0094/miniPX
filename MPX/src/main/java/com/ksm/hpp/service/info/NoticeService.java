package com.ksm.hpp.service.info;

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

@Service("NoticeService")
public class NoticeService {
	
	@Autowired
	SqlSession sqlSession; //SqlSession 빈 DI	
	
	@Resource(name = "FileService")
	protected FileService fileService;

	/**
	 * @메소드명: selectNotice
	 * @작성자: 김상민
	 * @생성일: 2022. 12. 21. 오전 10:49:08
	 * @설명: 공지사항 조회
	 */
	public Map<String, Object> selectNotice(StringBuilder logStr, Map<String, Object> inData) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		
		List<Map<String, Object>> list = sqlSession.selectList("mapper.info.NoticeMapper.selectNotice", inData);
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
	 * @메소드명: insertNotice
	 * @작성자: 김상민
	 * @생성일: 2022. 11. 2. 오후 6:59:16
	 * @설명: 공지사항 등록
	 */	
	public Map<String, Object> insertNotice(StringBuilder logStr, Map<String, Object> inData, List<MultipartFile> fileList) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		
		inData = StringUtil.XssReplaceInData(inData);	//XSS스크립트 방지를 위해 텍스트 변환
		
		sqlSession.insert("mapper.info.NoticeMapper.insertNotice", inData);
		fileService.insertFile(logStr, inData, fileList);	
		
		result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
		return result;
	}
	
	/**
	 * @메소드명: updateNotice
	 * @작성자: 김상민
	 * @생성일: 2023. 1. 3. 오전 10:45:24
	 * @설명: 공지사항 수정
	 */
	public Map<String, Object> updateNotice(StringBuilder logStr, Map<String, Object> inData, List<MultipartFile> fileList) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		
		inData = StringUtil.XssReplaceInData(inData);	//XSS스크립트 방지를 위해 텍스트 변환
		
		sqlSession.update("mapper.info.NoticeMapper.updateNotice", inData);
		fileService.insertFile(logStr, inData, fileList);	
		
		result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
		return result;
	}	
	
	/**
	 * @메소드명: deleteNotice
	 * @작성자: 김상민
	 * @생성일: 2023. 1. 4. 오전 11:49:25
	 * @설명: 게시글 삭제
	 */
	public Map<String, Object> deleteNotice(StringBuilder logStr, Map<String, Object> inData) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		
		int cnt = sqlSession.delete("mapper.info.NoticeMapper.deleteNotice", inData);
		result.put("cnt", cnt);
		
		//첨부파일 삭제
		result.put("delResult",fileService.deleteFileAll(inData));
		
		result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
		return result;
	}	
	
	/**
	* @메소드명: increaseHit
	* @작성자: KimSangMin
	* @생성일: 2023. 4. 26. 오후 6:40:23
	* @설명: 공지사항 조회수 증가
	*/
	public Map<String, Object> increaseHit(StringBuilder logStr, Map<String, Object> inData) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		
		int cnt = sqlSession.update("mapper.info.NoticeMapper.increaseHit", inData);
		result.put("cnt", cnt);
		
		result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
		return result;
	}	
}
