package com.mpx.minipx.controller.admin;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mpx.minipx.framework.util.JwtUtil;
import com.mpx.minipx.service.admin.MngOrderService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/mngOrder")
public class MngOrderController {
	
    @Value("${jwt.secret}")
    private String jwtSecret;  // JWT 비밀 키 (application.properties에서 가져오기)	

    private final MngOrderService mngOrderService;

    public MngOrderController(MngOrderService mngOrderService) {
        this.mngOrderService = mngOrderService;
    }

    /**
     * @메소드명: getOrderList
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 9.
     * @설명: 주문 목록 조회
     */
    @PostMapping("/getOrderList")
    public ResponseEntity<?> getOrderList(@RequestBody Map<String, Object> inData,
                                           HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        Map<String, Object> result = new HashMap<>();
        
    	// 사용자 정보 추출
        String refreshToken = JwtUtil.extractTokenFromCookies(request, "refreshToken");
        Claims claims;
        try {
            claims = JwtUtil.validateToken(refreshToken, jwtSecret);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid refresh token");
        }        
        inData.put("userId", (String) claims.get("userId"));
        inData.put("userSeq", (String) claims.get("userSeq"));        
        
        result = mngOrderService.getOrderList(inData);
        return ResponseEntity.ok(result);
    }
    
    /**
     * @메소드명: cancelOrder
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 9.
     * @설명: 주문취소
     */
    @PostMapping("/cancelOrder")
    public ResponseEntity<?> cancelOrder(@RequestBody Map<String, Object> inData,
                                            HttpServletRequest request,
                                            HttpServletResponse response) throws Exception {
        Map<String, Object> result = new HashMap<>();

    	// 사용자 정보 추출
        String refreshToken = JwtUtil.extractTokenFromCookies(request, "refreshToken");
        Claims claims;
        try {
            claims = JwtUtil.validateToken(refreshToken, jwtSecret);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid refresh token");
        }        
        inData.put("userId", (String) claims.get("userId"));
        inData.put("userSeq", (String) claims.get("userSeq"));
        
        result = mngOrderService.cancelOrder(inData);
        return ResponseEntity.ok(result);
    }       
}
