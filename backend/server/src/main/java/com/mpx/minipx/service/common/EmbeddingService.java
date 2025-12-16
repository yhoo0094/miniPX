package com.mpx.minipx.service.common;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class EmbeddingService {
    private final WebClient webClient;
    private final String model;

    public EmbeddingService(
            WebClient.Builder builder,
            @Value("${openai.api-key}") String apiKey,
            @Value("${openai.embedding-model}") String model
    ) {
        this.webClient = builder
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.model = model;
    }

    @SuppressWarnings("unchecked")
    public float[] embed(String text) {
        Map<String, Object> body = Map.of(
                "model", model,
                "input", text
        );

        Map<String, Object> resp = webClient.post()
                .uri("/embeddings")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

        if (resp == null) throw new IllegalStateException("OpenAI embeddings response is null");

        // resp.data[0].embedding => List<Number>
        List<Map<String, Object>> data = (List<Map<String, Object>>) resp.get("data");
        if (data == null || data.isEmpty()) throw new IllegalStateException("Embeddings data empty");

        List<Number> emb = (List<Number>) data.get(0).get("embedding");
        if (emb == null || emb.isEmpty()) throw new IllegalStateException("Embedding empty");

        float[] vec = new float[emb.size()];
        for (int i = 0; i < emb.size(); i++) {
            vec[i] = emb.get(i).floatValue();
        }
        return vec;
    }
}
