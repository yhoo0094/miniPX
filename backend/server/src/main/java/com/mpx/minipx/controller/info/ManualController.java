package com.mpx.minipx.controller.info;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mpx.minipx.framework.util.JwtUtil;
import com.mpx.minipx.service.info.ManualService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/manual")
public class ManualController {
	
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Autowired
    private ManualService manualService;

    /**
     * @메소드명: getManualList
     * @작성자: KimSangMin
     * @생성일: 2026. 1. 2.
     * @설명: 매뉴얼 목록 조회
     */
    @PostMapping("/getManualList")
    public ResponseEntity<?> getManualList(@RequestBody Map<String, Object> inData,
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
        
        result = manualService.getManualList(inData);
        return ResponseEntity.ok(result);
    }
    
    
    /**
     * @메소드명: upsertManual
     * @작성자: KimSangMin
     * @생성일: 2026. 1. 4.
     * @설명: 매뉴얼 등록/수정
     */
    @PostMapping("/upsertManual")
	public ResponseEntity<?> upsertManual(@RequestBody Map<String, Object> inData, 
							              HttpServletRequest request,
							              HttpServletResponse response) throws Exception {

    	// ---------- 사용자 정보 추출 ----------
        String accessToken = JwtUtil.extractTokenFromCookies(request, "accessToken");
        Claims claims;
        try {
            claims = JwtUtil.validateToken(accessToken, jwtSecret);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid token");
        }
        inData.put("loginUserId", (String) claims.get("userId"));
        inData.put("loginUserSeq", claims.get("userSeq"));
        Map<String, Object> result = manualService.upsertManual(inData);
        return ResponseEntity.ok(result);
    }      
}
