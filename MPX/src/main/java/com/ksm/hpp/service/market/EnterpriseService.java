package com.ksm.hpp.service.market;

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

@Service("EnterpriseService")
public class EnterpriseService {
	
	@Autowired
	SqlSession sqlSession; //SqlSession 빈 DI	
	
	@Resource(name = "FileService")
	protected FileService fileService;

	/**
	* @메소드명: selectEnterprise
	* @작성자: KimSangMin
	* @생성일: 2023. 12. 14. 오후 3:12:47
	* @설명: 기업장터 조회
	 */	
	public Map<String, Object> selectEnterprise(StringBuilder logStr, Map<String, Object> inData) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		
		List<Map<String, Object>> list = sqlSession.selectList("mapper.market.EnterpriseMapper.selectEnterprise", inData);
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
	* @메소드명: selectEnterprise
	* @작성자: KimSangMin
	* @생성일: 2023. 12. 18. 오후 4:31:29
	* @설명:기업장터 조회(홈 화면)
	 */	
	public Map<String, Object> selectEnterpriseHome(StringBuilder logStr, Map<String, Object> inData) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		
		List<Map<String, Object>> list = sqlSession.selectList("mapper.market.EnterpriseMapper.selectEnterpriseHome", inData);
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
	* @메소드명: insertEnterprise
	* @작성자: KimSangMin
	* @생성일: 2023. 12. 13. 오후 3:17:19
	* @설명: 기업장터 등록
	*/
	public Map<String, Object> insertEnterprise(StringBuilder logStr, Map<String, Object> inData, MultipartFile image) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		
		inData = StringUtil.XssReplaceInData(inData);	//XSS스크립트 방지를 위해 텍스트 변환
		
		//이미지 저장
		String dirName = "enterprise";
		Map<String, Object> saveImage = fileService.saveImage(logStr, inData, image, dirName);
		if(saveImage.get(Constant.RESULT).equals(Constant.RESULT_FAILURE)) {
			result.put(Constant.RESULT, Constant.RESULT_FAILURE);
			result.put(Constant.OUT_RESULT_MSG, saveImage.get(Constant.OUT_RESULT_MSG));
			return result;
		}
		inData.put("thumbnail", saveImage.get("saveAtcFileNm"));
		
		int cnt = sqlSession.insert("mapper.market.EnterpriseMapper.insertEnterprise", inData);
		if(cnt != 1) {
			result.put(Constant.RESULT, Constant.RESULT_FAILURE);
			result.put(Constant.OUT_RESULT_MSG, "저장 과정에서 오류가 발생하였습니다.");
			return result;
		}
		
		result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
		result.put(Constant.OUT_DATA, inData);
		return result;
	}
	
	/**
	* @메소드명: updateEnterprise
	* @작성자: KimSangMin
	* @생성일: 2023. 12. 19. 오전 10:56:32
	* @설명: 기업장터 수정
	*/	
	public Map<String, Object> updateEnterprise(StringBuilder logStr, Map<String, Object> inData, MultipartFile image) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		
		inData = StringUtil.XssReplaceInData(inData);	//XSS스크립트 방지를 위해 텍스트 변환
		
		if(image != null) {
			//이미지 저장
			String dirName = "enterprise";
			Map<String, Object> saveImage = fileService.saveImage(logStr, inData, image, dirName);
			if(saveImage.get(Constant.RESULT).equals(Constant.RESULT_FAILURE)) {
				result.put(Constant.RESULT, Constant.RESULT_FAILURE);
				result.put(Constant.OUT_RESULT_MSG, saveImage.get(Constant.OUT_RESULT_MSG));
				return result;
			}
			inData.put("thumbnail", saveImage.get("saveAtcFileNm"));			
		}
		
		int cnt = sqlSession.update("mapper.market.EnterpriseMapper.updateEnterprise", inData);
		if(cnt != 1) {
			result.put(Constant.RESULT, Constant.RESULT_FAILURE);
			result.put(Constant.OUT_RESULT_MSG, "저장 과정에서 오류가 발생하였습니다.");
			return result;
		}
		
		result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
		result.put(Constant.OUT_DATA, inData);
		return result;
	}	
	
	/**
	* @메소드명: deleteEnterprise
	* @작성자: KimSangMin
	* @생성일: 2023. 12. 18. 오후 5:42:33
	* @설명: 기업장터 삭제
	*/	
	public Map<String, Object> deleteEnterprise(StringBuilder logStr, Map<String, Object> inData) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		
		int cnt = sqlSession.update("mapper.market.EnterpriseMapper.deleteEnterprise", inData);
		String consResult = (cnt == 1)? Constant.RESULT_SUCCESS : Constant.RESULT_FAILURE;
		result.put(Constant.RESULT, consResult);
		result.put(Constant.OUT_DATA, inData);
		return result;
	}	
}
