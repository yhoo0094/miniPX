package com.mpx.minipx.service.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpx.minipx.controller.common.BaseController;
import com.mpx.minipx.framework.util.Constant;

@Service
public class QdrantService {
	protected static final Log log = LogFactory.getLog(BaseController.class);	
	
    private final WebClient webClient;
    private final WebClient embedWebClient;
    private final ObjectMapper objectMapper;    
    private final String model;
    
    @Autowired
    private SqlSessionTemplate sqlSession;    

    public QdrantService(
            WebClient.Builder builder,
            @Value("${qdrant.base-url}") String baseUrl,
            @Value("${openai.api-key}") String apiKey,
            ObjectMapper objectMapper,
            @Value("${openai.embedding-model}") String model
    ) {
        this.webClient = builder.baseUrl(baseUrl).build();
        this.embedWebClient = builder
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();        
        this.objectMapper = objectMapper;
        this.model = model;
    }

    /**
     * @메소드명: searchItems
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 17.
     * @설명: Qdrant 상품 검색
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> searchItems(float[] vector, int topK, String itemTypeCodeNm) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("vector", vector);
        body.put("limit", topK);
        body.put("with_payload", true);

        if (itemTypeCodeNm != null && !itemTypeCodeNm.isBlank()) {
            Map<String, Object> filter = Map.of(
                "must", List.of(
                    Map.of(
                        "key", "itemTypeCodeNm",
                        "match", Map.of("value", itemTypeCodeNm)
                    )
                )
            );
            body.put("filter", filter);
        }

        Map<String, Object> resp = webClient.post()
            .uri("/collections/items/points/search")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
            .block();

        if (resp == null) return List.of();
        Object result = resp.get("result");
        return (result instanceof List) ? (List<Map<String, Object>>) result : List.of();
    }
    
    /**
     * @메소드명: searchManuals
     * @작성자: KimSangMin
     * @생성일: 2026. 1. 9.
     * @설명: Qdrant 매뉴얼 검색
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> searchManuals(float[] vector, int topK, String manualDvcdNm) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("vector", vector);
        body.put("limit", topK);
        body.put("with_payload", true);

        if (manualDvcdNm != null && !manualDvcdNm.isBlank()) {
            Map<String, Object> filter = Map.of(
                "must", List.of(
                    Map.of(
                        "key", "manualDvcdNm",
                        "match", Map.of("value", manualDvcdNm)
                    )
                )
            );
            body.put("filter", filter);
        }

        Map<String, Object> resp = webClient.post()
            .uri("/collections/manuals/points/search")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
            .block();

        if (resp == null) return List.of();
        Object result = resp.get("result");
        return (result instanceof List) ? (List<Map<String, Object>>) result : List.of();
    }    
    
    /**
     * @메소드명: qdrantUpsertItem
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 20.
     * @설명: Qdrant 상품정보 저장(임베딩)
     */
    public Map<String, Object> qdrantUpsertItem(Map<String, Object> inData) {
        Map<String, Object> result = new HashMap<>();
        //코드명 조회 떄문에 select 필요
        Map<String, Object> item = sqlSession.selectOne("com.mpx.minipx.mapper.QdrantMapper.qdrantSelectItem", inData);

        String text = buildEmbeddingTextItem(item);
        float[] vector = embed(text);

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("itemSeq", item.get("itemSeq"));
        payload.put("itemTypeCodeNm", item.get("itemTypeCodeNm"));
        payload.put("itemDtlTypeCodeNm", item.get("itemDtlTypeCodeNm"));
        payload.put("text", text);

        Map<String, Object> point = new LinkedHashMap<>();
        point.put("id", item.get("itemSeq"));
        point.put("vector", vector);
        point.put("payload", payload);

        upsertPoints(List.of(point), "items");

        result.put("success", true);
        return result;
    }    
    
    /**
     * @메소드명: qdrantUpsertManual
     * @작성자: KimSangMin
     * @생성일: 2026. 1. 8.
     * @설명: Qdrant 매뉴얼 정보 저장(임베딩)
     */
    public Map<String, Object> qdrantUpsertManual(Map<String, Object> inData) {
        Map<String, Object> result = new HashMap<>();
        
        //코드명 조회 떄문에 select 필요
        Map<String, Object> manual = sqlSession.selectOne("com.mpx.minipx.mapper.QdrantMapper.qdrantSelectManual", inData);

        String text = buildEmbeddingTextManual(manual);
        float[] vector = embed(text);

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("manualSeq", manual.get("manualSeq"));
        payload.put("manualDvcdNm", manual.get("manualDvcdNm"));
        payload.put("text", text);

        Map<String, Object> point = new LinkedHashMap<>();
        point.put("id", manual.get("manualSeq"));
        point.put("vector", vector);
        point.put("payload", payload);

        upsertPoints(List.of(point), "manuals");

        result.put("success", true);
        return result;
    }
    
    /**
     * @메소드명: upsertAllItem
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 17.
     * @설명: 모든 상품 정보를 Qdrant에 저장
     */    
    public Map<String, Object> upsertAllItem(Map<String, Object> inData) {
    	Map<String, Object> result = new HashMap<>();
    	
    	//기존 컬렉션 제거
    	try {
		  webClient.delete()
		    .uri("/collections/items")
		    .retrieve()
		    .toBodilessEntity()
		    .block();
		} catch (Exception ignored) {
		  // 컬렉션이 없으면 무시
		}
    	
    	//컬렉션 생성
    	Map<String, Object> body = Map.of(
		  "vectors", Map.of("size", 1536, "distance", "Cosine")
		);

		webClient.put()
		  .uri("/collections/items")
		  .contentType(MediaType.APPLICATION_JSON)
		  .bodyValue(body)
		  .retrieve()
		  .toBodilessEntity()
		  .block();  

		//인덱스 생성
		createKeywordIndex("itemTypeCodeNm");
		createKeywordIndex("itemDtlTypeCodeNm");
    	
    	//상품 조회
        List<Map<String, Object>> rows = sqlSession.selectList("com.mpx.minipx.mapper.QdrantMapper.qdrantSelectItem");

        //Qdrant에 데이터 적재
        final int batchSize = 10;
        List<Map<String, Object>> buffer = new ArrayList<>(batchSize);
        for (int i = 0; i < rows.size(); i++) {
        	Map<String, Object> p = rows.get(i);

            String text = buildEmbeddingTextItem(p);
            float[] vec = embed(text);

            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("itemSeq", p.get("itemSeq"));
            payload.put("itemTypeCodeNm", p.get("itemTypeCodeNm"));
            payload.put("itemDtlTypeCodeNm", p.get("itemDtlTypeCodeNm"));
            payload.put("text", text);

            Map<String, Object> point = new LinkedHashMap<>();
            point.put("id", p.get("itemSeq"));
            point.put("vector", vec);
            point.put("payload", payload);

            buffer.add(point);
            if (buffer.size() >= batchSize || i == rows.size() - 1) {
            	upsertPoints(buffer, "items");
                buffer.clear();
                log.info("저장 중(" + (i + 1) + "/" + rows.size() + ")...");
            }
        }
        log.info("저장 완료(" + rows.size() + "/" + rows.size() + ")!!!");
        
        result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
        result.put("total", rows.size());
        result.put("msg", rows.size() + "건 저장하였습니다.");
    	return result;        
    }
    
    /**
     * @메소드명: upsertAllManual
     * @작성자: KimSangMin
     * @생성일: 2026. 1. 9.
     * @설명: 모든 매뉴얼 정보를 Qdrant에 저장
     */
    public Map<String, Object> upsertAllManual(Map<String, Object> inData) {
    	Map<String, Object> result = new HashMap<>();
    	
    	//기존 컬렉션 제거
    	try {
		  webClient.delete()
		    .uri("/collections/manuals")
		    .retrieve()
		    .toBodilessEntity()
		    .block();
		} catch (Exception ignored) {
		  // 컬렉션이 없으면 무시
		}
    	
    	//컬렉션 생성
    	Map<String, Object> body = Map.of(
		  "vectors", Map.of("size", 1536, "distance", "Cosine")
		);

		webClient.put()
		  .uri("/collections/manuals")
		  .contentType(MediaType.APPLICATION_JSON)
		  .bodyValue(body)
		  .retrieve()
		  .toBodilessEntity()
		  .block();  

		//인덱스 생성
		createKeywordIndex("manualDvcdNm");
    	
    	//상품 조회
        List<Map<String, Object>> rows = sqlSession.selectList("com.mpx.minipx.mapper.QdrantMapper.qdrantSelectManual");

        //Qdrant에 데이터 적재
        final int batchSize = 10;
        List<Map<String, Object>> buffer = new ArrayList<>(batchSize);
        for (int i = 0; i < rows.size(); i++) {
        	Map<String, Object> manual = rows.get(i);

            String text = buildEmbeddingTextManual(manual);
            float[] vector = embed(text);

            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("manualSeq", manual.get("manualSeq"));
            payload.put("manualDvcdNm", manual.get("manualDvcdNm"));
            payload.put("text", text);

            Map<String, Object> point = new LinkedHashMap<>();
            point.put("id", manual.get("manualSeq"));
            point.put("vector", vector);
            point.put("payload", payload);

            buffer.add(point);
            if (buffer.size() >= batchSize || i == rows.size() - 1) {
            	upsertPoints(buffer, "manuals");
                buffer.clear();
                log.info("저장 중(" + (i + 1) + "/" + rows.size() + ")...");
            }
        }
        log.info("저장 완료(" + rows.size() + "/" + rows.size() + ")!!!");
        
        result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
        result.put("total", rows.size());
        result.put("msg", rows.size() + "건 저장하였습니다.");
    	return result;        
    }       
    
    /**
     * @메소드명: qdrantDeletePoint
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 22.
     * @설명: Qdrant 데이터 삭제
     */
    public void qdrantDeletePoint(Object seq, String collection_name) {
        try {
            Map<String, Object> body = Map.of(
                "points", List.of(Long.valueOf(String.valueOf(seq)))
            );

            webClient.post()
                .uri("/collections/" + collection_name + "/points/delete?wait=true")
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .toBodilessEntity()
                .block();

        } catch (Exception e) {
            throw new RuntimeException("Qdrant delete failed", e);
        }
    }
    
    /**
     * @메소드명: upsertPoints
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 17.
     * @설명: Qdrant에 데이터 저장
     */
    public void upsertPoints(List<Map<String, Object>> points, String collection_name) {
        try {
            Map<String, Object> body = Map.of("points", points);

            byte[] jsonBytes = objectMapper.writeValueAsBytes(body); // ✅ UTF-8 바이트로 생성됨

            webClient.put()
                    .uri("/collections/" + collection_name + "/points?wait=true")
                    .header(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8")
                    .accept(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromResource(new ByteArrayResource(jsonBytes)))
                    .retrieve()
                    .toBodilessEntity()
                    .block();

        } catch (Exception e) {
            throw new RuntimeException("Qdrant upsert failed", e);
        }
    }    
    
    /**
     * @메소드명: buildEmbeddingTextItem
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 17.
     * @설명: 상품 임베딩 텍스트 생성
     */
    private String buildEmbeddingTextItem(Map p) {
        // 너무 길게 하지 말고, 핵심 필드만
        return String.format(
            "itemNm:%s | itemTypeCodeNm:%s | itemDtlTypeCodeNm:%s | rmrk:%s",
            nullToEmpty((String)p.get("itemNm")),
            nullToEmpty((String)p.get("itemTypeCodeNm")),
            nullToEmpty((String)p.get("itemDtlTypeCodeNm")),
            nullToEmpty((String)p.get("rmrk"))
        );
    }
    
    /**
     * @메소드명: buildEmbeddingTextManual
     * @작성자: KimSangMin
     * @생성일: 2026. 1. 8.
     * @설명: 매뉴얼 임베딩 텍스트 생성
     */
    private String buildEmbeddingTextManual(Map p) {
        // 너무 길게 하지 말고, 핵심 필드만
        return String.format(
            "manualTitle:%s | manualDvcdNm:%s | manualContent:%s",
            nullToEmpty((String)p.get("manualTitle")),
            nullToEmpty((String)p.get("manualDvcdNm")),
            nullToEmpty((String)p.get("manualContent"))
        );
    }    

    /**
     * @메소드명: nullToEmpty
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 17.
     * @설명: null 방지
     */
    private String nullToEmpty(String s) {
        return (s == null) ? "" : s.trim();
    }
    
    /**
     * @메소드명: createKeywordIndex
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 17.
     * @설명: 컬렉션에 인덱스 생성
     */
    private void createKeywordIndex(String field) {
	  Map<String, Object> idx = Map.of(
	    "field_name", field,
	    "field_schema", "keyword"
	  );

	  webClient.put()
	    .uri("/collections/items/index")
	    .contentType(MediaType.APPLICATION_JSON)
	    .bodyValue(idx)
	    .retrieve()
	    .toBodilessEntity()
	    .block();
	}    
    
    /**
     * @메소드명: embed
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 17.
     * @설명: 문자열 임베딩 처리
     */
    @SuppressWarnings("unchecked")
    public float[] embed(String text) {
        Map<String, Object> body = Map.of(
                "model", model,
                "input", text
        );

        Map<String, Object> resp = embedWebClient.post()
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
    
