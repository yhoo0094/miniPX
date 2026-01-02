package com.mpx.minipx.controller.admin;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mpx.minipx.framework.util.Constant;
import com.mpx.minipx.framework.util.JwtUtil;
import com.mpx.minipx.service.admin.AiLogService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/aiLog")
public class AiLogController {
	
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Autowired
    private AiLogService aiLogService;

    /**
     * @메소드명: getAiSessionList
     * @작성자: KimSangMin
     * @생성일: 2026. 1. 2.
     * @설명: AI 로그 조회
     */
    @PostMapping("/getAiSessionList")
    public ResponseEntity<?> getAiSessionList(@RequestBody Map<String, Object> inData,
                                           HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        Map<String, Object> result = new HashMap<>();
        
		// 사용자 정보 추출
		String accessToken = JwtUtil.extractTokenFromCookies(request, "accessToken");
		Claims claims;
		try {
			claims = JwtUtil.validateToken(accessToken, jwtSecret);
		} catch (Exception e) {
			return ResponseEntity.status(401).body("Invalid refresh token");
		}        
		
		//관리자 권한 검증
		Integer roleSeq = claims.get("roleSeq", Integer.class);
		if (!Integer.valueOf(3).equals(roleSeq)) {
            result.put(Constant.RESULT, Constant.RESULT_FAILURE);
            result.put(Constant.OUT_RESULT_MSG, "적합한 권한이 아닙니다.");	
            return ResponseEntity.ok(result);
		}	        
        
        result = aiLogService.getAiSessionList(inData);
        return ResponseEntity.ok(result);
    }
    
    /**
     * @메소드명: getAiSessionDetail
     * @작성자: KimSangMin
     * @생성일: 2026. 1. 2.
     * @설명: 세션 상세 조회
     */
    @PostMapping("/getAiSessionDetail")
    public ResponseEntity<?> getAiSessionDetail(@RequestBody Map<String, Object> inData,
                                           HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        Map<String, Object> result = new HashMap<>();
        
		// 사용자 정보 추출
		String accessToken = JwtUtil.extractTokenFromCookies(request, "accessToken");
		Claims claims;
		try {
			claims = JwtUtil.validateToken(accessToken, jwtSecret);
		} catch (Exception e) {
			return ResponseEntity.status(401).body("Invalid refresh token");
		}        
		
		//관리자 권한 검증
		Integer roleSeq = claims.get("roleSeq", Integer.class);
		if (!Integer.valueOf(3).equals(roleSeq)) {
            result.put(Constant.RESULT, Constant.RESULT_FAILURE);
            result.put(Constant.OUT_RESULT_MSG, "적합한 권한이 아닙니다.");	
            return ResponseEntity.ok(result);
		}	        
        
        result = aiLogService.getAiSessionDetail(inData);
        return ResponseEntity.ok(result);
    }    
}
