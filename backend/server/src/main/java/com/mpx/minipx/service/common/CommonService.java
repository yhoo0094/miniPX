package com.mpx.minipx.service.common;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mpx.minipx.controller.common.BaseController;
import com.mpx.minipx.entity.TbCodeDetail;
import com.mpx.minipx.repository.TbCodeDetailRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommonService {
	
	protected static final Log log = LogFactory.getLog(BaseController.class);
//	protected static final Logger logger = LogManager.getLogger(ControllerAdvice.class);
	
    @Autowired
    private TbCodeDetailRepository tbCodeDetailRepository;
    
    @Value("${jwt.secret}")
    private String jwtSecret;  // JWT 비밀 키 (application.properties에서 가져오기)    

    /**
     * @메소드명: getUserList
     * @작성자: KimSangMin
     * @생성일: 2025. 7. 16.
     * @설명: 공통 코드 목록 조회
     */
    public List<TbCodeDetail> getCodeList(Map<String, Object> inData) {
//    	List<TbCodeDetail> result = tbCodeDetailRepository.findByCodeGroup((String) inData.get("codeGroup")); 
    	List<TbCodeDetail> result = tbCodeDetailRepository.findByIdCodeGroup((String) inData.get("codeGroup"));
    	return result;
    }
    
}
