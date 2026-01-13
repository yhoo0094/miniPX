package com.mpx.minipx.controller.market;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mpx.minipx.framework.util.JwtUtil;
import com.mpx.minipx.service.market.ItemDetailService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/itemDetail")
public class ItemDetailController {

    private final ItemDetailService itemDetailService;

    public ItemDetailController(ItemDetailService itemDetailService) {
        this.itemDetailService = itemDetailService;
    }

    @Value("${jwt.secret}")
    private String jwtSecret;  // JWT 비밀 키 (application.properties에서 가져오기)
    
    /**
     * @메소드명: getItemDetail
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 19.
     * @설명: 상품상세 조회
     */
    @PostMapping("/getItemDetail")
    public Map<String, Object> getItemDetail(@RequestBody Map<String, Object> inData,
                                           HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        Map<String, Object> result = new HashMap<>();
        result = itemDetailService.getItemDetail(inData);
        return result;
    }    
    
    /**
     * @메소드명: requestOrder
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 20.
     * @설명: 주문 등록
     */
    @PostMapping("/requestOrder")
    public ResponseEntity<?> requestOrder(@RequestBody Map<String, Object> inData,
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
        
        result = itemDetailService.requestOrder(inData);
        return ResponseEntity.ok(result);
    }
    
    
    /**
     * @메소드명: upsertBasket
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 20.
     * @설명: 장바구니 등록
     */
    @PostMapping("/upsertBasket")
    public ResponseEntity<?> upsertBasket(@RequestBody Map<String, Object> inData,
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
        
        result = itemDetailService.upsertBasket(inData);
        return ResponseEntity.ok(result);
    }    
    
    /**
     * @메소드명: upsertItem
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 20.
     * @설명: 아이템 정보 저장
     */
    @PostMapping(
    	    value = "/upsertItem",
    	    consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    	)
    	public ResponseEntity<?> upsertItem(
    			@RequestParam Map<String, Object> inData, 
    			@RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
    	        HttpServletRequest request
    	) throws Exception {

        // ---------- 사용자 정보 추출 ----------
        String accessToken = JwtUtil.extractTokenFromCookies(request, "accessToken");
        Claims claims;
        try {
            claims = JwtUtil.validateToken(accessToken, jwtSecret);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid token");
        }
        inData.put("userId", (String) claims.get("userId"));
        inData.put("userSeq", claims.get("userSeq"));

        Map<String, Object> result = itemDetailService.upsertItem(inData, imageFile);

        return ResponseEntity.ok(result);
    }    
}
