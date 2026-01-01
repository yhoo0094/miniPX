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
import com.mpx.minipx.service.market.BasketService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/basket")
public class BasketController {
	
    @Value("${jwt.secret}")
    private String jwtSecret;  // JWT 비밀 키 (application.properties에서 가져오기)	

    private final BasketService basketService;

    public BasketController(BasketService basketService) {
        this.basketService = basketService;
    }

    /**
     * @메소드명: getBasketList
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 8.
     * @설명: 장바구니 목록 조회
     */
    @PostMapping("/getBasketList")
    public ResponseEntity<?> getBasketList(@RequestBody Map<String, Object> inData,
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
        
        result = basketService.getBasketList(inData);
        return ResponseEntity.ok(result);
    }
    
    /**
     * @메소드명: insertBasket
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 8.
     * @설명: 장바구니 등록
     */
    @PostMapping("/upsertBasket")
    public ResponseEntity<?> upsertBasket(@RequestBody Map<String, Object> inData,
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
        
        result = basketService.upsertBasket(inData);
        return ResponseEntity.ok(result);
    }    
    
    /**
     * @메소드명: updateBasketCnt
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 9.
     * @설명: 장바구니 개수 수정
     */
    @PostMapping("/updateBasketCnt")
    public ResponseEntity<?> updateBasketCnt(@RequestBody Map<String, Object> inData,
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
        
        result = basketService.updateBasketCnt(inData);
        return ResponseEntity.ok(result);
    }    
    
    
    /**
     * @메소드명: deleteBasket
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 10.
     * @설명: 장바구니 삭제
     */
    @PostMapping("/deleteBasket")
    public ResponseEntity<?> deleteBasket(@RequestBody Map<String, Object> inData,
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
        
        result = basketService.deleteBasket(inData);
        return ResponseEntity.ok(result);
    }      
    
    /**
     * @메소드명: insertOrder
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 9.
     * @설명: 장바구니 건들 구매 요청
     */    
    @PostMapping("/insertOrder")
    public ResponseEntity<?> insertOrder(@RequestBody Map<String, Object> inData,
                                          HttpServletRequest request,
                                          HttpServletResponse response) throws Exception {
        Map<String, Object> result = new HashMap<>();

        String refreshToken = JwtUtil.extractTokenFromCookies(request, "refreshToken");
        Claims claims;
        try {
            claims = JwtUtil.validateToken(refreshToken, jwtSecret);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid refresh token");
        }
        inData.put("userId",  (String) claims.get("userId"));
        inData.put("userSeq", claims.get("userSeq"));

        result = basketService.insertOrder(inData);

        return ResponseEntity.ok(result);
    }
    
    
    
}
