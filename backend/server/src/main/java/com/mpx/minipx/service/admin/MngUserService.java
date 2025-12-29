package com.mpx.minipx.service.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.mpx.minipx.controller.common.BaseController;
import com.mpx.minipx.framework.util.Constant;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MngUserService {
	
	protected static final Log log = LogFactory.getLog(BaseController.class);
	
    @Autowired
    private SqlSessionTemplate sqlSession;      
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;      
    
    @Value("${app.reset-password}")
    private String resetPassword;  // 초기화 비밀번호      

    /**
     * @메소드명: getMngUserList
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 26.
     * @설명: 사용자 목록 조회
     */
    public Map<String, Object> getMngUserList(Map<String, Object> inData) {
    	Map<String, Object> result = new HashMap<String, Object>(); 
    	List<Map<String, Object>> list = sqlSession.selectList("com.mpx.minipx.mapper.MngUserMapper.getMngUserList", inData);
    	result.put(Constant.OUT_DATA, list);
    	result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
    	return result;
    }
    
    /**
     * @메소드명: insertUser
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 26.
     * @설명: 사용자 생성
     */    
    public Map<String, Object> insertUser(Map<String, Object> inData) {
        Map<String, Object> result = new HashMap<>();
        
        //기본 비밀번호 인코딩
        String userPw = passwordEncoder.encode(resetPassword);
        inData.put("userPw", userPw);
        int affected = sqlSession.insert("com.mpx.minipx.mapper.MngUserMapper.insertUser", inData);
		
        result.put(Constant.OUT_DATA, affected);
        result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
        return result;
    }      
    
    /**
     * @메소드명: updateUser
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 26.
     * @설명: 사용자 수정
     */    
    public Map<String, Object> updateUser(Map<String, Object> inData) {
        Map<String, Object> result = new HashMap<>();

        //중복 아이디 숫자 검증
        Map<String, Object> map = sqlSession.selectOne("com.mpx.minipx.mapper.MngUserMapper.countUserId", inData);
        Number cntNum = (Number) map.get("cnt");
        int cnt = (cntNum == null) ? 0 : cntNum.intValue();

        if (cnt > 0) {
            result.put(Constant.RESULT, Constant.RESULT_FAILURE);
            result.put(Constant.OUT_RESULT_MSG, "중복된 아이디가 존재합니다.");
            return result;
        }
        
        int affected = sqlSession.update("com.mpx.minipx.mapper.MngUserMapper.updateUser", inData);
		
        result.put(Constant.OUT_DATA, affected);
        result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
        return result;
    }       
}
