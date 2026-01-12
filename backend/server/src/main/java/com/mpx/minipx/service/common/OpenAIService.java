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
			1) 사용자 질문 분류(의도 파악)
			- 질문이 아래 중 무엇인지 먼저 판별한다.
        		* 상품 문의/추천/재고/가격/비교/검색 → AiGetItemList 또는 AiGetDetailItemList
        		* 쇼핑몰 사용 방법/정책/기능 안내(회원가입/주문/결제/배송/취소/반품/장바구니 등) → AiGetManualList
        		* 둘 다 해당될 수 있으면 우선 사용방법(매뉴얼) → 상품 순으로 처리하거나, 질문의 핵심에 따라 하나를 먼저 호출한다.
			2) 상품 검색 1차: AiGetItemList를 “우선” 호출
			- 상품 관련 질문이면 기본적으로 AiGetItemList를 먼저 호출한다.
			- 파라미터 구성 규칙
        		* itemSeq가 명확하면 itemSeq로 조회를 최우선한다.
				* 상품명이 일부라도 주어지면 itemNm으로 조회한다.
				* 사용자가 “식품/화장품/기타”를 명시하면 itemType을 넣고, 없으면 생략한다.
				* 품절 상품을 굳이 포함해달라는 요청이 명시적으로 있지 않으면 excludeSoldOut=true를 유지한다.
				* “싼 순/비싼 순/최신순” 같은 요구가 있으면 sort에 매핑한다.
				* 기본 limit는 과도하게 크지 않게(예: 10~30) 잡고, 필요 시 offset으로 추가 조회한다.
			3) 0건 처리(강제 규칙): AiGetItemList 결과가 0건이면 즉시 AiGetDetailItemList 호출
			- AiGetItemList 결과가 0건이면 설명 없이 곧바로 AiGetDetailItemList를 호출한다.
        		* userQuery에는 사용자의 원문 질문을 그대로 넣는다.
        		* 사용자가 itemType을 명시했으면 함께 넣고, 아니면 생략한다.
			4) 상품 검색 2차/보강: 필요한 경우 추가 툴 호출(여러 번 왕복 가능)
			- 다음 상황이면 추가 호출로 보강한다.
				* 결과가 너무 많아 특정이 어려움 → 정렬 변경/키워드 정교화/limit 조정/페이지네이션(offset)으로 재조회
				* 사용자가 “다른 추천 더”, “비슷한 상품”, “비교해줘” 요청 → 조건을 바꿔 재조회
				* 사용자가 특정 상품(번호/이름)을 선택함 → 그 상품을 중심으로 재조회(가능하면 itemSeq 사용)
				* “상세 설명/성분/사용법/주의사항/특징” 등 설명이 핵심인 질문 → AiGetDetailItemList를 추가로 호출해 상세정보를 확보
			5) 매뉴얼 검색: AiGetManualList 호출 규칙
			- 사용방법/이용가이드 성격이면 AiGetManualList를 호출한다.
				* userQuery에는 사용자 원문 질문을 넣는다.
			- 매뉴얼 결과가 여러 건이면 제목 기준으로 먼저 요약 목록을 제시하고, 사용자 질문에 가장 관련 높은 항목의 내용을 우선 정리한다.
			6) 툴 결과 해석 및 답변 작성 규칙
			- 답변은 반드시 툴 결과(JSON)를 근거로 사람이 이해하기 쉬운 한국어로 정리한다.
			- 상품 목록을 제시할 때 포함할 정보(가능한 범위)
        		* 상품명, 가격(낱개가격/판매단위/총가격), 상품분류/상세분류, 품절여부, 판매량, 등록일
			- 사용자가 비교를 요청하면 동일 기준(가격/단위/재고/판매량 등) 으로 표처럼 구조화해 설명한다(단, 실제 표 형식 강제는 아님).
			- 결과가 부족하거나 모호하면 추측하지 말고 추가 툴 호출로 근거를 확보한다.
			7) 중요 고지(강제 규칙): 상품상세정보를 참고한 경우 경고 문구 추가
			- AiGetDetailItemList의 상품상세정보를 참고하여 답변을 작성했다면, 답변 마지막에 두 줄 공백을 두고 아래 문구를 정확히 그대로 추가한다.
        		* !주의: 상품의 자세한 정보는 AI를 통해 생성되어 오류가 있을 수 있습니다.
			8) 결과가 없을 때의 사용자 안내
			- AiGetDetailItemList까지 조회했는데도 적합한 상품/정보가 없으면,
				* “현재 조건으로는 결과를 찾지 못했다”고 말하고,
				* 사용자가 바로 선택할 수 있게 대안 검색 키워드/카테고리(식품/화장품/기타) 제안 또는 “원하는 특징(예: 향/용량/가격대/용도)”을 짧게 물어본다.
			- 단, 가능한 한 먼저 재검색(조건 변경) 툴 호출을 시도한 뒤 부족할 때만 질문한다.  
            
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
    	
            2) AiGetDetailItemList
               - 용도: 자세한 설명이 포함된 상품 목록 조회
               - 파라미터:
            	 * userQuery: 문의내용
            	 * itemType (optional): 상품 분류("식품" | "화장품" | "기타")
               - 리턴: 상품일련번호/상품명/낱개가격/판매단위/가격/상품분류/상품상세분류/품절여부/판매량/등록일/상품상세정보      

            3) AiGetManualList
               - 용도: 쇼핑몰 이용 방법 조회
               - 파라미터:
            	 * userQuery: 문의내용
               - 리턴: 매뉴얼일련번호/매뉴얼제목/매뉴얼내용/매뉴얼구분
            """;

        ResponseCreateParams.Builder builder =
            ResponseCreateParams.builder()
                .model(ChatModel.GPT_4_1_MINI)
                .instructions(instructions)
                .addTool(AiGetItemList.class)
                .addTool(AiGetDetailItemList.class)
                .addTool(AiGetManualList.class)
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
                    List<Map<String, Object>> hits = qdrantService.searchItems(qv, topK, args.itemType);

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
                case "AiGetManualList": {
                	AiGetManualList args = functionCall.arguments(AiGetManualList.class);
                    int topK = 30;
                    int finalLimit = 15;

                    // 1) 질문 임베딩
                    float[] qv = qdrantService.embed(args.userQuery);

                    // 2) Qdrant 검색
                    List<Map<String, Object>> hits = qdrantService.searchManuals(qv, topK, args.manualDvcdNm);

                    // 3) manualSeq 목록 + 점수 매핑(순서 유지)
                    List<Integer> manualSeqList = new ArrayList<>();
                    Map<Integer, Double> scoreMap = new LinkedHashMap<>();

                    for (Map<String, Object> h : hits) {
                        Object idObj = h.get("id"); // Qdrant point id = MANUAL_SEQ로 넣었으니 그대로 사용
                        if (idObj == null) continue;

                        int manualSeq = (idObj instanceof Number) ? ((Number) idObj).intValue()
                                                                : Integer.parseInt(String.valueOf(idObj));

                        manualSeqList.add(manualSeq);

                        Object scoreObj = h.get("score");
                        double score = (scoreObj instanceof Number) ? ((Number) scoreObj).doubleValue() : 0.0;
                        scoreMap.put(manualSeq, score);
                    }

                    if (manualSeqList.isEmpty()) return List.of();

                    // 4) DB 정보 조회
                    Map<String, Object> param = new LinkedHashMap<>();
                    param.put("manualSeqList", manualSeqList);

                    List<Map<String, Object>> list = sqlSession.selectList("com.mpx.minipx.mapper.AiMapper.aiGetDetailManualList", param);

                    // 5) 점수 붙이고 최종 후보 개수 제한
                    for (Map<String, Object> r : list) {
                        Integer manualSeq = ((Number) r.get("매뉴얼일련번호")).intValue();
                        r.put("score", scoreMap.getOrDefault(manualSeq, 0.0));
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
		String userQuery,
		String itemType
    ) {}    
    
    //매뉴얼 정보 조회
    public record AiGetManualList(
		String userQuery,		//사용자 질문
		String manualDvcdNm
    ) {}      
}
