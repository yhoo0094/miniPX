package com.mpx.minipx.controller.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mpx.minipx.framework.util.Constant;
import com.mpx.minipx.framework.util.JwtUtil;
import com.mpx.minipx.service.common.QdrantService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/qdrant")
public class QdrantController {
    @Value("${jwt.secret}")
    private String jwtSecret;	
	
    private final QdrantService qdrantService;

    
    public QdrantController(QdrantService qdrantService) {
    	this.qdrantService = qdrantService;
    }

    public record SearchReq(float[] vector, int topK, String category) {}

    /**
     * @메소드명: getCandidates
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 17.
     * @설명: Qdrant에서 후보군 추출하기
     */
    public record RecommendReq(String query, Integer topK, Integer finalLimit) {}
    @PostMapping("/candidates")
    public List<Map<String, Object>> candidates(@RequestBody RecommendReq req) {
        return qdrantService.getCandidates(
            req.query(),
            req.topK() == null ? 30 : req.topK(),
            req.finalLimit() == null ? 15 : req.finalLimit()
        );
    }   
    
    /**
     * @메소드명: upsertAllItem
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 17.
     * @설명: 모든 상품 정보를 Qdrant에 저장
     */
    @PostMapping(value = "/upsertAllItem")
	public ResponseEntity<?> upsertAllItem(@RequestBody Map<String, Object> inData, HttpServletRequest request, HttpServletResponse response) throws Exception {
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
    	
        result = qdrantService.upsertAllItem(inData);
        return ResponseEntity.ok(result);    	
    }    

    /**
     * @메소드명: incremental
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 17.
     * @설명: 개별 상품 정보를 Qdrant에 저장
     */
    @PostMapping("/incremental")
    public String incremental() {
        //qdrantService.runIncremental();
        return "OK";
    }      
}
