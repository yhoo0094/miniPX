package com.mpx.minipx.controller.common;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mpx.minipx.service.common.QdrantService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/qdrant")
public class QdrantController {
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
        Map<String, Object> result = qdrantService.upsertAllItem(inData);
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
