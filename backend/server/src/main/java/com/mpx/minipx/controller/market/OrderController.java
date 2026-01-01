package com.mpx.minipx.controller.market;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mpx.minipx.framework.util.JwtUtil;
import com.mpx.minipx.service.market.OrderService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/order")
public class OrderController {
	
    @Value("${jwt.secret}")
    private String jwtSecret;  // JWT 비밀 키 (application.properties에서 가져오기)	

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
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
        String accessToken = JwtUtil.extractTokenFromCookies(request, "accessToken");
        Claims claims;
        try {
            claims = JwtUtil.validateToken(accessToken, jwtSecret);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid token");
        }        
        inData.put("userId", (String) claims.get("userId"));
        inData.put("userSeq", claims.get("userSeq"));        
        
        result = orderService.getOrderList(inData);
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
        inData.put("userSeq", claims.get("userSeq"));
        
        inData.put("orderStatusCode", "91");
        result = orderService.updateOrderStatus(inData);
        return ResponseEntity.ok(result);
    }    
    
    /**
     * @메소드명: paymentCompleted
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 19.
     * @설명: 송금완료
     */
    @PostMapping("/paymentCompleted")
    public ResponseEntity<?> paymentCompleted(@RequestBody Map<String, Object> inData,
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
        inData.put("userSeq", claims.get("userSeq"));
        
        inData.put("orderStatusCode", "04");
        result = orderService.updateOrderStatus(inData);
        return ResponseEntity.ok(result);
    }        
}
