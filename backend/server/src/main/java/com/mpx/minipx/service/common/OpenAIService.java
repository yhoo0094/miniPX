package com.mpx.minipx.service.common;

import java.sql.Timestamp;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpx.minipx.controller.common.BaseController;
import com.mpx.minipx.dto.common.AiAnswerResult;
import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.*;

/**
 * 세션 유지 + 히스토리 포함 AI Service
 */
@Service
public class OpenAIService {

    protected static final Log log = LogFactory.getLog(BaseController.class);

    private final OpenAIClient openAIClient;
    private final QdrantService qdrantService;

    @Autowired
    private SqlSessionTemplate sqlSession;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public OpenAIService(OpenAIClient openAIClient,
                         QdrantService qdrantService) {
        this.openAIClient = openAIClient;
        this.qdrantService = qdrantService;
    }

    /* ================================
       Entry Point
       ================================ */
    public AiAnswerResult getAiAnswer(Map<String, Object> inData) {
    	AiAnswerResult aiAnswerResult = new AiAnswerResult();
    	
    	//파라미터 선언
    	Long loginUserSeq = ((Number)inData.get("loginUserSeq")).longValue();
    	Long sessionId = inData.get("sessionId") == null ? null : ((Number) inData.get("sessionId")).longValue();
    	String userInput = (String) inData.get("question");
    	long ensuredSessionId = ensureSession(loginUserSeq, sessionId, userInput);
 	
    	
    	//토큰 사용량 초과 여부 확인
    	Boolean isTokenOver = sqlSession.selectOne("com.mpx.minipx.mapper.AiMapper.isTokenOver", inData);
    	if(isTokenOver) {
            aiAnswerResult.setSessionId(ensuredSessionId);
            aiAnswerResult.setAnswer("월간 질문한도를 초과하였습니다. 관리자에게 문의해주세요.");
            return aiAnswerResult;      		
    	}
    	
        int turnNo = nextTurnNo(ensuredSessionId);
        long turnId = createTurn(loginUserSeq, ensuredSessionId, turnNo, userInput);       	

        int eventSeq = 1;

        // 1) 히스토리 로딩
        List<ResponseInputItem> inputs =
            loadConversationHistoryAsInputs(ensuredSessionId);

        // 2) 이번 사용자 질문 추가
        inputs.add(
            ResponseInputItem.ofMessage(
                ResponseInputItem.Message.builder()
                    .role(ResponseInputItem.Message.Role.USER)
                    .addInputTextContent(userInput)
                    .build()
            )
        );

        insertEvent(loginUserSeq, turnId, eventSeq++, "USER_MSG", "user",
            null, null, userInput, null);

        // 3) OpenAI 설정
        String instructions = """
            너는 이커머스 쇼핑몰의 AI 상담/데이터 분석 assistant다.
            사용자는 한국어로 질문한다.

            매우 중요:
            - 데이터가 필요하면, 아래 제공된 "툴 카탈로그" 중에서 골라 호출한다.
            - 툴을 호출하면 서버가 결과(JSON)를 돌려준다.
            - 필요한 경우 툴을 여러 번 호출해도 된다(여러 번 왕복 가능).
            - 툴 결과를 바탕으로 사람이 이해하기 쉬운 한국어로 요약/정리해서 답변한다.
            
    		[검색/툴 호출 절차 - 매우 중요]
    		1) 사용자의 질문에서 핵심 키워드(예: 향/맛/성분/효능/용도/피부타입/원산지/무알콜/저당 등)를 추출한다.
    		2) 1차 조회: AiGetItemList를 호출해 "상품명/분류" 기반으로 빠르게 후보를 찾는다.
    		3) 2차 조회(필수): AiGetDetailItemList(userQuery=사용자 원문 질문)을 호출하여 상품상세정보를 기반으로 후보를 찾는다.
    		4) 최종 답변 규칙:
    		   - "없습니다/등록되지 않았습니다" 같은 결론은
    		     AiGetItemList + AiGetDetailItemList까지 확인한 뒤에만 말한다.
    		   - 가격에 대한 질문은 가격(낱개가격 * 판매단위)를 기준으로 답변한다.        
            
            [툴 카탈로그]
            1) AiGetItemList
    	       - 용도: 간단한 상품 목록 조회
    	       - 파라미터:
    	       * itemSeq (optional): 상품일련번호
    	       * itemNm (optional): 상품명
    	       * itemType (optional): 상품 분류("식품" | "화장품" | "기타")
    	       * sort (optional, default="상품명_오름차순"): 정렬 기준("단위가격_오름차순" | "단위가격_내림차순" | "등록일_내림차순")
    	       * excludeSoldOut (optional, default=true): 품절 제외 여부
    	       * limit (optional)
    	       * offset (optional)
    	       - 리턴: 상품일련번호/상품명/낱개가격/판매단위/가격/상품분류/상품상세분류/품절여부/판매량/등록일
    	       - 매우 중요
    		   * 결과가 0건이면: 즉시 AiGetDetailItemList를 호출한다.
    	
            2) AiGetDetailItemList
               - 용도: 자세한 설명이 포함된 상품 목록 조회
               - 파라미터:
            	 * userQuery: 문의내용
               - 리턴: 상품일련번호/상품명/낱개가격/판매단위/가격/상품분류/상품상세분류/품절여부/판매량/등록일/상품상세정보      
    	       - 매우 중요
    		   * 상품상세정보를 참고한 경우 답변 마지막에 한 줄 띄우고 '!주의: 상품의 자세한 정보는 AI를 통해 생성되어 오류가 있을 수 있습니다.'라는 문구를 추가한다.                       
            """;

        ResponseCreateParams.Builder builder =
            ResponseCreateParams.builder()
                .model(ChatModel.GPT_4_1_MINI)
                .instructions(instructions)
                .addTool(AiGetItemList.class)
                .addTool(AiGetDetailItemList.class)
                .maxOutputTokens(1024);

        final int MAX_ROUNDS = 5;

        try {
            for (int round = 0; round < MAX_ROUNDS; round++) {

                builder.input(ResponseCreateParams.Input.ofResponse(inputs));
                Response response = openAIClient.responses().create(builder.build());
                addTurnUsage(loginUserSeq, turnId, response);

                boolean hasToolCall = false;
                List<ResponseFunctionToolCall> toolCalls = new ArrayList<>();

                for (var item : response.output()) {

                    if (item.isFunctionCall()) {
                        hasToolCall = true;
                        ResponseFunctionToolCall fc = item.asFunctionCall();

                        inputs.add(ResponseInputItem.ofFunctionCall(fc));

                        long toolCallId = insertToolCall(loginUserSeq, turnId, round, fc);
                        cacheToolCallId(fc.callId(), toolCallId);

                        insertEvent(loginUserSeq, turnId, eventSeq++, "TOOL_CALL", "tool",
                            fc.name(), fc.callId(), null, safeToJson(fc));

                        toolCalls.add(fc);
                        continue;
                    }

                    if (item.message().isPresent()) {
                        ResponseOutputMessage msg = item.message().get();
                        inputs.add(convertOutputMessageToInput(msg));

                        String txt = extractTextFromMessage(msg);
                        if (txt != null) {
                            insertEvent(loginUserSeq, turnId, eventSeq++, "ASSIST_MSG",
                                "assistant", null, null, txt, null);
                        }
                    }
                }

                if (!hasToolCall) {
                    String answer = extractText(response);
                    updateTurnFinish(loginUserSeq, turnId, answer, round + 1, "01", null, null);
                    touchSession(ensuredSessionId);
                    
                    aiAnswerResult.setSessionId(ensuredSessionId);
                    aiAnswerResult.setTurnId(turnId);
                    aiAnswerResult.setTurnNo(turnNo);
                    aiAnswerResult.setAnswer(answer);
                    return aiAnswerResult;
                }

                for (ResponseFunctionToolCall fc : toolCalls) {
                    long start = System.currentTimeMillis();
                    Object result = dispatchToolCall(fc);
                    long end = System.currentTimeMillis();

                    inputs.add(
                        ResponseInputItem.ofFunctionCallOutput(
                            ResponseInputItem.FunctionCallOutput.builder()
                                .callId(fc.callId())
                                .outputAsJson(result)
                                .build()
                        )
                    );

                    long toolCallId = getToolCallId(fc.callId());
                    insertToolResult(toolCallId, result);
                    updateToolCallTiming(loginUserSeq, toolCallId, start, end);

                    insertEvent(loginUserSeq, turnId, eventSeq++, "TOOL_OUTPUT", "tool",
                        fc.name(), fc.callId(), null, safeToJson(result));
                }
            }

            String answer = "요청이 복잡하여 처리하지 못했습니다.";
            updateTurnFinish(loginUserSeq, turnId, answer, MAX_ROUNDS, "03", null, null);
            
            aiAnswerResult.setSessionId(ensuredSessionId);
            aiAnswerResult.setTurnId(turnId);
            aiAnswerResult.setTurnNo(turnNo);
            aiAnswerResult.setAnswer(answer);
            return aiAnswerResult;            
        } catch (Exception e) {
            updateTurnFinish(loginUserSeq, turnId, "오류가 발생했습니다.",
                null, "04", "OPENAI_ERROR", e.getMessage());
            throw e;
        }
    }

    /* ================================
       History Loader
       ================================ */
    private List<ResponseInputItem> loadConversationHistoryAsInputs(long sessionId) {

        List<Map<String, Object>> events =
            sqlSession.selectList(
                "com.mpx.minipx.mapper.AiMapper.selectRecentEventsBySession",
                Map.of("sessionId", sessionId, "limit", 30)
            );

        List<ResponseInputItem> inputs = new ArrayList<>();

        for (Map<String, Object> e : events) {
            String type = (String) e.get("EVENT_TYPE");
            String role = (String) e.get("ROLE");

            try {
                switch (type) {
                    case "USER_MSG":
                    case "ASSIST_MSG":
                        inputs.add(
                            ResponseInputItem.ofMessage(
                                ResponseInputItem.Message.builder()
                                	.role(ResponseInputItem.Message.Role.USER)
                                    .addInputTextContent((String)e.get("CONTENT_TEXT"))
                                    .build()
                            )
                        );
                        break;

                    case "TOOL_CALL":
//                        inputs.add(
//                            ResponseInputItem.ofFunctionCall(
//                                objectMapper.readValue(
//                                    (String)e.get("CONTENT_JSON"),
//                                    ResponseFunctionToolCall.class
//                                )
//                            )
//                        );
                        break;

                    case "TOOL_OUTPUT":
//                        inputs.add(
//                            ResponseInputItem.ofFunctionCallOutput(
//                                ResponseInputItem.FunctionCallOutput.builder()
//                                    .callId((String)e.get("CALL_ID"))
//                                    .outputAsJson(
//                                        objectMapper.readValue(
//                                            (String)e.get("CONTENT_JSON"),
//                                            Object.class
//                                        )
//                                    )
//                                    .build()
//                            )
//                        );
                        break;
                }
            } catch (Exception ignore) {}
        }
        return inputs;
    }

    /* ================================
       Session / Turn helpers
       ================================ */
    private long ensureSession(Long loginUserSeq, Long sessionId, String userInput) {
        if (sessionId == null || sessionId <= 0) {
            return createSession(loginUserSeq, userInput);
        }
        Integer exists = sqlSession.selectOne(
            "com.mpx.minipx.mapper.AiMapper.existsAiSession",
            Map.of("sessionId", sessionId)
        );
        return (exists == null || exists == 0)
            ? createSession(loginUserSeq, userInput)
            : sessionId;
    }

    private long createSession(Long loginUserSeq, String firstInput) {
        Map<String,Object> p = new HashMap<>();
        p.put("loginUserSeq", loginUserSeq);
        p.put("title", summarizeTitle(firstInput));
        p.put("llmModel", "gpt-4.1-mini");
        p.put("fstRegSeq", 0);
        p.put("lstUpdSeq", 0);

        sqlSession.insert("com.mpx.minipx.mapper.AiMapper.insertAiSession", p);
        return ((Number)p.get("sessionId")).longValue();
    }

    private int nextTurnNo(long sessionId) {
        Integer max = sqlSession.selectOne(
            "com.mpx.minipx.mapper.AiMapper.selectMaxTurnNo",
            Map.of("sessionId", sessionId)
        );
        return max == null ? 1 : max + 1;
    }

    private long createTurn(Long loginUserSeq, long sessionId, int turnNo, String input) {
        Map<String,Object> p = new HashMap<>();
        p.put("sessionId", sessionId);
        p.put("turnNo", turnNo);
        p.put("userInput", input);
        p.put("loginUserSeq", loginUserSeq);

        sqlSession.insert("com.mpx.minipx.mapper.AiMapper.insertAiTurn", p);
        return ((Number)p.get("turnId")).longValue();
    }

    private void touchSession(long sessionId) {
        sqlSession.update(
            "com.mpx.minipx.mapper.AiMapper.touchAiSession",
            Map.of("sessionId", sessionId, "lstUpdSeq", 0)
        );
    }

    /* ================================
       Tool / Event helpers
       ================================ */
    private final Map<String, Long> toolCallIdMap = new HashMap<>();

    private void cacheToolCallId(String callId, long id) {
        toolCallIdMap.put(callId, id);
    }
    private long getToolCallId(String callId) {
        return toolCallIdMap.get(callId);
    }

    private long insertToolCall(long loginUserSeq, long turnId, int round, ResponseFunctionToolCall fc) {
        Map<String,Object> p = new HashMap<>();
        p.put("turnId", turnId);
        p.put("roundNo", round);
        p.put("callId", fc.callId());
        p.put("toolNm", fc.name());
        p.put("argumentsJson", safeToJson(fc));
        p.put("loginUserSeq", loginUserSeq);

        sqlSession.insert("com.mpx.minipx.mapper.AiMapper.insertAiToolCall", p);
        return ((Number)p.get("toolCallId")).longValue();
    }

    private void insertToolResult(long toolCallId, Object result) {
        boolean error = (result instanceof Map<?,?> m && m.containsKey("error"));

        Map<String,Object> p = new HashMap<>();
        p.put("toolCallId", toolCallId);
        p.put("resultJson", safeToJson(result));
        p.put("resultRows", (result instanceof List<?> l) ? l.size() : null);
        p.put("errorYn", error ? "Y" : "N");
        p.put("errorMessage", error ? String.valueOf(((Map<?,?>)result).get("message")) : null);
        p.put("fstRegSeq", 0);
        p.put("lstUpdSeq", 0);

        sqlSession.insert("com.mpx.minipx.mapper.AiMapper.insertAiToolResult", p);
    }

    private void updateToolCallTiming(long loginUserSeq, long toolCallId, long start, long end) {
        Map<String,Object> p = new HashMap<>();
        p.put("toolCallId", toolCallId);
        p.put("startDtti", new Timestamp(start));
        p.put("endDtti", new Timestamp(end));
        p.put("durationMs", (int)(end - start));
        p.put("loginUserSeq", loginUserSeq);

        sqlSession.update("com.mpx.minipx.mapper.AiMapper.updateAiToolCallTiming", p);
    }

    private void insertEvent(long loginUserSeq, long turnId, int seq, String type, String role,
                             String toolNm, String callId,
                             String text, String json) {

        Map<String,Object> p = new HashMap<>();
        p.put("turnId", turnId);
        p.put("seqNo", seq);
        p.put("eventType", type);
        p.put("role", role);
        p.put("toolNm", toolNm);
        p.put("callId", callId);
        p.put("contentText", text);
        p.put("contentJson", json);
        p.put("loginUserSeq", loginUserSeq);

        sqlSession.insert("com.mpx.minipx.mapper.AiMapper.insertAiEvent", p);
    }

    private void updateTurnFinish(long loginUserSeq, long turnId, String answer, Integer rounds,
                                  String rscd, String errCd, String errMsg) {
        Map<String,Object> p = new HashMap<>();
        p.put("turnId", turnId);
        p.put("finalAnswer", answer);
        p.put("roundCount", rounds);
        p.put("aiFinishRscd", rscd);
        p.put("errorCode", errCd);
        p.put("errorMessage", errMsg);
        p.put("loginUserSeq", loginUserSeq);

        sqlSession.update("com.mpx.minipx.mapper.AiMapper.updateAiTurnFinish", p);
    }

    private String safeToJson(Object o) {
        try { return objectMapper.writeValueAsString(o); }
        catch (Exception e) { return "{\"json_error\":\"" + e.getMessage() + "\"}"; }
    }

    private String extractTextFromMessage(ResponseOutputMessage msg) {
        StringBuilder sb = new StringBuilder();
        msg.content().forEach(c -> c.outputText().ifPresent(t -> sb.append(t.text())));
        return sb.isEmpty() ? null : sb.toString();
    }

    private String summarizeTitle(String s) {
        if (s == null) return null;
        return s.length() <= 30 ? s : s.substring(0, 30);
    }
    
    /**
     * @메소드명: addTurnUsage
     * @작성자: KimSangMin
     * @생성일: 2026. 1. 1.
     * @설명: 토큰 사용량 계산
     */
    private void addTurnUsage(long loginUserSeq, long turnId, Response response) {
        // SDK에 맞춰 꺼내야 함 (아래는 개념 예시)
        Integer inputTokens = null;
        Integer outputTokens = null;
        Integer totalTokens = null;

        try {
            // 예시: response.usage()가 Optional 이라고 가정
            var usageOpt = response.usage();
            if (usageOpt != null && usageOpt.isPresent()) {
                var usage = usageOpt.get();
                inputTokens = (int) usage.inputTokens();
                outputTokens = (int) usage.outputTokens();
                totalTokens = (int) usage.totalTokens();
            }
        } catch (Exception ignore) {
            // usage가 없거나 SDK getter가 다르면 무시
        }

        Map<String, Object> p = new HashMap<>();
        p.put("turnId", turnId);
        p.put("loginUserSeq", loginUserSeq);
        p.put("inputTokens", inputTokens);
        p.put("outputTokens", outputTokens);
        p.put("totalTokens", totalTokens);

        sqlSession.update("com.mpx.minipx.mapper.AiMapper.addAiTurnUsage", p);
    }
    
    
    /**
     * @메소드명: dispatchToolCall
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 17.
     * @설명: tool name에 따라 툴 실행.
     */
    private Object dispatchToolCall(ResponseFunctionToolCall functionCall) {
        String name = functionCall.name();
        log.info("--------------------------------------------------------------------");
        log.info("----------------------------AI TOOL 호출 " + name + "----------------------------");
        log.info("--------------------------------------------------------------------");

        try {
            switch (name) {
                case "AiGetItemList": {
                    AiGetItemList args = functionCall.arguments(AiGetItemList.class);
                    List<Map<String, Object>> list = sqlSession.selectList("com.mpx.minipx.mapper.AiMapper.aiGetItemList", args);
                    return list;
                }
                case "AiGetDetailItemList": {
                	AiGetDetailItemList args = functionCall.arguments(AiGetDetailItemList.class);
                	
                    int topK = 30;
                    int finalLimit = 15;

                    // 1) 질문 임베딩
                    float[] qv = qdrantService.embed(args.userQuery);

                    // 2) Qdrant 검색
                    List<Map<String, Object>> hits = qdrantService.search(qv, topK, null);

                    // 3) itemSeq 목록 + 점수 매핑(순서 유지)
                    List<Integer> itemSeqList = new ArrayList<>();
                    Map<Integer, Double> scoreMap = new LinkedHashMap<>();

                    for (Map<String, Object> h : hits) {
                        Object idObj = h.get("id"); // Qdrant point id = ITEM_SEQ로 넣었으니 그대로 사용
                        if (idObj == null) continue;

                        int itemSeq = (idObj instanceof Number) ? ((Number) idObj).intValue()
                                                                : Integer.parseInt(String.valueOf(idObj));

                        itemSeqList.add(itemSeq);

                        Object scoreObj = h.get("score");
                        double score = (scoreObj instanceof Number) ? ((Number) scoreObj).doubleValue() : 0.0;
                        scoreMap.put(itemSeq, score);
                    }

                    if (itemSeqList.isEmpty()) return List.of();

                    // 4) DB 정보 조회
                    Map<String, Object> param = new LinkedHashMap<>();
                    param.put("itemSeqList", itemSeqList);

                    List<Map<String, Object>> list = sqlSession.selectList("com.mpx.minipx.mapper.AiMapper.aiGetDetailItemList", param);

                    // 5) 점수 붙이고 최종 후보 개수 제한
                    for (Map<String, Object> r : list) {
                        Integer itemSeq = ((Number) r.get("상품일련번호")).intValue();
                        r.put("score", scoreMap.getOrDefault(itemSeq, 0.0));
                    }

                    if (list.size() > finalLimit) {
                        return list.subList(0, finalLimit);
                    }
                    return list;                	
                }                
                default:
                    // 알 수 없는 툴이면 에러를 툴 결과로 반환해 모델이 스스로 수정하게 함
                    return Map.of(
                        "error", "Unknown tool name: " + java.util.Objects.toString(name, "(null)"),
                        "hint", "Use one of the registered tools only."
                    );
            }
        } catch (Exception e) {
            return Map.of(
                    "error", "Tool execution failed",
                    "tool", java.util.Objects.toString(name, "(null)"),
                    "message", java.util.Objects.toString(e.getMessage(), "(no message)"),
                    "exception", e.getClass().getName()
                );
        }
    }
    
    // ==========================================================
    //       ResponseOutputMessage → ResponseInputItem.Message 변환
    // ==========================================================
    private ResponseInputItem convertOutputMessageToInput(ResponseOutputMessage outputMsg) {

        ResponseInputItem.Message.Builder msgBuilder =
            ResponseInputItem.Message.builder()
                .role(outputMsg._role());

        outputMsg.content().forEach(c ->
            c.outputText().ifPresent(txt ->
                msgBuilder.addInputTextContent(txt.text())
            )
        );

        return ResponseInputItem.ofMessage(msgBuilder.build());
    }   
    
    /**
     * 공통: Response에서 첫 번째 text만 뽑는 헬퍼
     */
    private String extractText(Response response) {
        return response.output().stream()
            .flatMap(item -> item.message().stream())
            .flatMap(msg -> msg.content().stream())
            .flatMap(c -> c.outputText().stream())
            .map(ot -> ot.text())
            .findFirst()
            .orElse("(no answer)");
    }

    // ==========================================================
    //  Tool Args(예시): OpenAI SDK가 함수 스키마로 노출할 클래스들
    //  ※ 실제로는 별도 파일로 분리 권장
    // ==========================================================

    //상품 목록 조회
    public record AiGetItemList(
    	Integer itemSeq,	
        String itemNm,
        String itemType,
        String sort,
        Boolean excludeSoldOut,
        Integer limit,
        Integer offset
    ) {}

    //상품 설명을 기반으로 문의와 연관된 상품 목록 조회
    public record AiGetDetailItemList(
		String userQuery
    ) {}    
}
