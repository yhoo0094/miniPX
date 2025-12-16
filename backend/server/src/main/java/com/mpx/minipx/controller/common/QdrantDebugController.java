package com.mpx.minipx.controller.common;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.*;

import com.mpx.minipx.service.common.EmbeddingService;
import com.mpx.minipx.service.common.QdrantSearchClient;

@RestController
@RequestMapping("/debug/qdrant")
public class QdrantDebugController {
    private final QdrantSearchClient client;

    private final EmbeddingService embeddingService;
    
    public QdrantDebugController(QdrantSearchClient client, EmbeddingService embeddingService) {
    	this.client = client;
    	this.embeddingService = embeddingService;
    }

    public record SearchReq(float[] vector, int topK, String category) {}

    /**
     * @메소드명: searchFromExistingPoint
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 16.
     * @설명:
     */
    // 예: GET /debug/qdrant/search-from/1002?topK=5&category=hair
    @GetMapping("/search-from/{id}")
    public List<Map<String, Object>> searchFromExistingPoint(
            @PathVariable("id") long id,
            @RequestParam(defaultValue = "5") int topK,
            @RequestParam(required = false) String category
    ) {
        float[] v = client.retrieveVector(id);
        return client.search(v, topK, category);
    }    
    
    /**
     * @메소드명: searchByText
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 16.
     * @설명:
     */
    @PostMapping("/search-by-text")
    public List<Map<String, Object>> searchByText(@RequestBody Map<String, Object> req) {
        String query = (String) req.get("query");
        Integer topK = (Integer) req.getOrDefault("topK", 10);
        String category = (String) req.get("category");

        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("query is required");
        }

        float[] qv = embeddingService.embed(query);
        return client.search(qv, topK == null ? 10 : topK, category);
    }
    
}
