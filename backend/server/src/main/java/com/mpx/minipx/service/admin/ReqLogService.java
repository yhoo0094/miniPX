package com.mpx.minipx.service.admin;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mpx.minipx.dto.common.ReqLog;
import com.mpx.minipx.entity.TbLogReq;
import com.mpx.minipx.framework.util.Constant;
import com.mpx.minipx.repository.TbLogReqRepository;

@Service
@RequiredArgsConstructor
public class ReqLogService {
	
    @Autowired
    private SqlSessionTemplate sqlSession;  	

    private final TbLogReqRepository tbLogReqRepository;

    /**
     * @메소드명: getReqLogList
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 30.
     * @설명: 요청 로그 목록 조회
     */    
    public Map<String, Object> getReqLogList(Map<String, Object> inData) {
    	Map<String, Object> result = new HashMap<String, Object>();
    	Map<String, Object> data = new HashMap<>();
    	
    	int page = Integer.parseInt(String.valueOf(inData.getOrDefault("page", "1")));
    	int pageSize = Integer.parseInt(String.valueOf(inData.getOrDefault("pageSize", "20")));
    	int offset = (page - 1) * pageSize;
    	inData.put("pageSize", pageSize);
    	inData.put("offset", offset);    	
    	
    	List<Map<String, Object>> list = sqlSession.selectList("com.mpx.minipx.mapper.ReqLogMapper.getReqLogList", inData);
    	int totalCount = sqlSession.selectOne("com.mpx.minipx.mapper.ReqLogMapper.getReqLogListCount", inData);
    	
    	data.put("list", list);
    	data.put("totalCount", totalCount);
    	result.put(Constant.OUT_DATA, data);
    	result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
    	return result;    	
    }    
    
    /**
     * @메소드명: save
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 30.
     * @설명: 요청 로그 저장
     */
    public void saveReqLog(ReqLog log) {
        try {
            TbLogReq e = new TbLogReq();
            e.setUserSeq(log.getUserSeq());
            e.setIp(log.getIp());
            e.setUri(log.getUri());
            e.setParam(log.getParam());
            e.setReqTypeCode(log.getReqTypeCode());

            tbLogReqRepository.save(e);
        } catch (Exception ex) {
            // 로그 저장 실패가 본 요청을 깨지 않도록
            // logger.warn("request log insert failed", ex);
        }
    }
}
