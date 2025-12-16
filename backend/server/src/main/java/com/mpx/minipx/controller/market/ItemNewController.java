package com.mpx.minipx.controller.market;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mpx.minipx.dto.market.ItemImage;
import com.mpx.minipx.framework.util.JwtUtil;
import com.mpx.minipx.service.market.ItemNewService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/itemNew")
public class ItemNewController {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private final ItemNewService itemNewService;

    public ItemNewController(ItemNewService itemNewService) {
        this.itemNewService = itemNewService;
    }

    /**
     * @메소드명: insertNewOrder
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 10.
     * @설명: 신규상품주문 등록
     */
    @PostMapping(
    	    value = "/insertNewOrder",
    	    consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    	)
    	public ResponseEntity<?> insertNewOrder(
    	        @RequestParam("itemNm") String itemNm,
    	        @RequestParam("cnt") Integer cnt,
    	        @RequestParam("newOrderDtl") String newOrderDtl,
    	        @RequestPart(value = "imageFile", required = false) MultipartFile imageFile,
    	        HttpServletRequest request
    	) throws Exception {

        // ---------- 사용자 정보 추출 ----------
        String refreshToken = JwtUtil.extractTokenFromCookies(request, "refreshToken");
        Claims claims;
        try {
            claims = JwtUtil.validateToken(refreshToken, jwtSecret);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid refresh token");
        }

        // ---------- 서비스에 넘길 데이터 구성 ----------
        Map<String, Object> inData = new HashMap<>();
        inData.put("itemNm", itemNm);
        inData.put("cnt", cnt);
        inData.put("newOrderDtl", newOrderDtl);

        inData.put("userId", (String) claims.get("userId"));
        inData.put("userSeq", (String) claims.get("userSeq"));

        Map<String, Object> result = itemNewService.insertNewOrder(inData, imageFile);

        return ResponseEntity.ok(result);
    }
    
    /**
     * @메소드명: getItemNewImage
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 15.
     * @설명: 신규상품주문 이미지 조회
     */
    @GetMapping("/getItemNewImage")
    public ResponseEntity<Resource> getItemNewImage(@RequestParam("orderSeq") Long orderSeq) {
        try {
            ItemImage itemImage = itemNewService.getItemNewImage(orderSeq);
            return ResponseEntity.ok()
                    .contentType(itemImage.getMediaType())
                    .body(itemImage.getResource());
        } catch (IllegalStateException e) {
            // 경로 없음, 파일 없음 등
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            // 알 수 없는 오류
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }    
    
}
