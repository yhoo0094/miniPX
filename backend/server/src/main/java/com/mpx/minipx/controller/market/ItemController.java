package com.mpx.minipx.controller.market;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mpx.minipx.dto.market.ItemImage;
import com.mpx.minipx.service.market.ItemService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/item")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @Value("${jwt.secret}")
    private String jwtSecret;  // JWT 비밀 키 (application.properties에서 가져오기)

    /**
     * @메소드명: getItemList
     * @작성자: KimSangMin
     * @생성일: 2025. 7. 17.
     * @설명: 상품 목록 조회
     */
    @PostMapping("/getItemList")
    public Map<String, Object> getItemList(@RequestBody Map<String, Object> inData,
                                           HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        Map<String, Object> result = new HashMap<>();
        result = itemService.getItemList(inData);
        return result;
    }

    /**
     * 
     * @메소드명: getItemImage
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 6.
     * @설명: 상품 이미지 조회
     */
    @GetMapping("/getItemImage")
    public ResponseEntity<Resource> getItemImage(@RequestParam("img") String img) {
        try {
            ItemImage itemImage = itemService.getItemImage(img);
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
