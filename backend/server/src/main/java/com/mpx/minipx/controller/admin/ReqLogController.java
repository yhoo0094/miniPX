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
import com.mpx.minipx.service.admin.ReqLogService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/reqLog")
public class ReqLogController {
	
    @Value("${jwt.secret}")
    private String jwtSecret;  // JWT 비밀 키 (application.properties에서 가져오기)	

    @Autowired
    private ReqLogService reqLogService;

    /**
     * @메소드명: getReqLogList
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 30.
     * @설명: 요청 로그 목록 조회
     */
    @PostMapping("/getReqLogList")
    public ResponseEntity<?> getReqLogList(@RequestBody Map<String, Object> inData,
                                           HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        Map<String, Object> result = new HashMap<>();
        
		// 사용자 정보 추출
		String accessToken = JwtUtil.extractTokenFromCookies(request, "accessToken");
		Claims claims;
		try {
			claims = JwtUtil.validateToken(accessToken, jwtSecret);
		} catch (Exception e) {
			return ResponseEntity.status(401).body("Invalid token");
		}        
		
		//관리자 권한 검증
		Integer roleSeq = claims.get("roleSeq", Integer.class);
		if (!Integer.valueOf(3).equals(roleSeq)) {
            result.put(Constant.RESULT, Constant.RESULT_FAILURE);
            result.put(Constant.OUT_RESULT_MSG, "적합한 권한이 아닙니다.");	
            return ResponseEntity.ok(result);
		}	        
        
        result = reqLogService.getReqLogList(inData);
        return ResponseEntity.ok(result);
    }
}
