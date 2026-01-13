package com.mpx.minipx.service.info;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mpx.minipx.framework.config.QuillSanitizer;
import com.mpx.minipx.framework.util.Constant;
import com.mpx.minipx.service.common.QdrantService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ManualService {
	
    @Autowired
    private SqlSessionTemplate sqlSession; 
    
    @Autowired
    private QdrantService qdrantService;    

    /**
     * @메소드명: getManualList
     * @작성자: KimSangMin
     * @생성일: 2026. 1. 2.
     * @설명: 매뉴얼 목록 조회
     */
    public Map<String, Object> getManualList(Map<String, Object> inData) {
    	Map<String, Object> result = new HashMap<String, Object>();
    	Map<String, Object> data = new HashMap<>();
    	
    	int page = Integer.parseInt(String.valueOf(inData.getOrDefault("page", "1")));
    	int pageSize = Integer.parseInt(String.valueOf(inData.getOrDefault("pageSize", "20")));
    	int offset = (page - 1) * pageSize;
    	inData.put("pageSize", pageSize);
    	inData.put("offset", offset);    	
    	
    	List<Map<String, Object>> list = sqlSession.selectList("com.mpx.minipx.mapper.ManualMapper.getManualList", inData);
    	int totalCount = sqlSession.selectOne("com.mpx.minipx.mapper.ManualMapper.getManualListCount", inData);
    	
    	data.put("list", list);
    	data.put("totalCount", totalCount);
    	result.put(Constant.OUT_DATA, data);
    	result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
    	return result;    	
    }    
    
    /**
     * @메소드명: getManual
     * @작성자: KimSangMin
     * @생성일: 2026. 1. 14.
     * @설명: 매뉴얼 조회
     */
    public Map<String, Object> getManual(Map<String, Object> inData) {
    	Map<String, Object> result = new HashMap<String, Object>(); 
    	Map<String, Object> map = sqlSession.selectOne("com.mpx.minipx.mapper.ManualMapper.getManual", inData);
    	result.put(Constant.OUT_DATA, map);
    	result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
    	return result;    	
    }
    
    /**
     * @메소드명: upsertManual
     * @작성자: KimSangMin
     * @생성일: 2026. 1. 4.
     * @설명: 매뉴얼 등록/수정
     */    
    public Map<String, Object> upsertManual(Map<String, Object> inData) {
        Map<String, Object> result = new HashMap<>();

        int affected = 0;
        inData.put("manualContent", QuillSanitizer.sanitize((String) inData.get("manualContent")));
        if(inData.get("manualSeq") != null && !inData.get("manualSeq").equals("")) {
        	affected = sqlSession.update("com.mpx.minipx.mapper.ManualMapper.updateManual", inData);
        } else {
        	affected = sqlSession.insert("com.mpx.minipx.mapper.ManualMapper.insertManual", inData);
        }

        if (affected > 0) {
        	String useYn = String.valueOf(inData.get("useYn"));
        	
            if ("N".equalsIgnoreCase(useYn)) {
                qdrantService.qdrantDeletePoint(inData.get("manualSeq"), "manuals");
            } else {
                qdrantService.qdrantUpsertManual(inData);
            } 
        	
            result.put(Constant.OUT_DATA, affected);
            result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
        } else {
            result.put(Constant.OUT_DATA, affected);
            result.put(Constant.RESULT, Constant.RESULT_FAILURE);
            result.put(Constant.OUT_RESULT_MSG, "매뉴얼 등록에 실패했습니다.");
        }

        return result;
    }        
}
