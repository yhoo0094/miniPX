package com.mpx.minipx.controller.admin;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mpx.minipx.framework.util.Constant;
import com.mpx.minipx.framework.util.JwtUtil;
import com.mpx.minipx.service.admin.MngUserService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/mngUser")
public class MngUserController {
	
    @Value("${jwt.secret}")
    private String jwtSecret;  // JWT 비밀 키 (application.properties에서 가져오기)	

    private final MngUserService mngUserService;

    public MngUserController(MngUserService mngUserService) {
        this.mngUserService = mngUserService;
    }

    /**
     * @메소드명: getMngUserList
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 26.
     * @설명: 사용자 목록 조회
     */
    @PostMapping("/getMngUserList")
    public ResponseEntity<?> getMngUserList(@RequestBody Map<String, Object> inData,
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
        
        result = mngUserService.getMngUserList(inData);
        return ResponseEntity.ok(result);
    }
    
    /**
     * @메소드명: insertUser
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 26.
     * @설명: 사용자 생성
     */
    @PostMapping("/insertUser")
    public ResponseEntity<?> insertUser(@RequestBody Map<String, Object> inData,
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
		inData.put("loginUserId", (String) claims.get("userId"));
		inData.put("loginUserSeq", claims.get("userSeq"));
		
		//관리자 권한 검증
		Integer roleSeq = claims.get("roleSeq", Integer.class);
		if (!Integer.valueOf(3).equals(roleSeq)) {
            result.put(Constant.RESULT, Constant.RESULT_FAILURE);
            result.put(Constant.OUT_RESULT_MSG, "적합한 권한이 아닙니다.");	
            return ResponseEntity.ok(result);
		}		
		
		result = mngUserService.insertUser(inData);
		return ResponseEntity.ok(result);
	}
    
    /**
     * @메소드명: updateUser
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 26.
     * @설명: 사용자 수정
     */
    @PostMapping("/updateUser")
    public ResponseEntity<?> updateUser(@RequestBody Map<String, Object> inData,
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
		inData.put("loginUserId", (String) claims.get("userId"));
		inData.put("loginUserSeq", claims.get("userSeq"));
		
		//관리자 권한 검증
		Integer roleSeq = claims.get("roleSeq", Integer.class);
		if (!Integer.valueOf(3).equals(roleSeq)) {
            result.put(Constant.RESULT, Constant.RESULT_FAILURE);
            result.put(Constant.OUT_RESULT_MSG, "적합한 권한이 아닙니다.");	
            return ResponseEntity.ok(result);
		}
		
		result = mngUserService.updateUser(inData);
		return ResponseEntity.ok(result);
	}       
}
