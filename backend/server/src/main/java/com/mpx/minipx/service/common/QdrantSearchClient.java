package com.mpx.minipx.service.common;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class QdrantSearchClient {
    private final WebClient webClient;
    private final String collection;

    public QdrantSearchClient(
            WebClient.Builder builder,
            @Value("${qdrant.base-url}") String baseUrl,
            @Value("${qdrant.collection}") String collection
    ) {
        this.webClient = builder.baseUrl(baseUrl).build();
        this.collection = collection;
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> search(float[] vector, int topK, String categoryFilterOrNull) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("vector", vector);
        body.put("limit", topK);
        body.put("with_payload", true);

        if (categoryFilterOrNull != null && !categoryFilterOrNull.isBlank()) {
            Map<String, Object> filter = Map.of(
                "must", List.of(
                    Map.of(
                        "key", "category",
                        "match", Map.of("value", categoryFilterOrNull)
                    )
                )
            );
            body.put("filter", filter);
        }

        Map<String, Object> resp = webClient.post()
            .uri("/collections/{c}/points/search", collection)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
            .block();

        if (resp == null) return List.of();
        Object result = resp.get("result");
        return (result instanceof List) ? (List<Map<String, Object>>) result : List.of();
    }
    
    @SuppressWarnings("unchecked")
    public float[] retrieveVector(long pointId) {
        Map<String, Object> body = Map.of(
            "ids", List.of(pointId),
            "with_vectors", true,
            "with_payload", false
        );

        Map<String, Object> resp = webClient.post()
            .uri("/collections/{c}/points", collection)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
            .block();

        if (resp == null) throw new IllegalStateException("Qdrant response is null");

        Object resultObj = resp.get("result");
        if (!(resultObj instanceof List<?> resultList) || resultList.isEmpty()) {
            throw new IllegalArgumentException("Point not found: " + pointId);
        }

        Map<String, Object> first = (Map<String, Object>) resultList.get(0);

        // vector는 보통 float 배열(List<Number>)로 옴
        Object vectorObj = first.get("vector");
        if (!(vectorObj instanceof List<?> vList) || vList.isEmpty()) {
            throw new IllegalStateException("Vector is empty for point: " + pointId);
        }

        float[] vector = new float[vList.size()];
        for (int i = 0; i < vList.size(); i++) {
            vector[i] = ((Number) vList.get(i)).floatValue();
        }
        return vector;
    }
    
}
