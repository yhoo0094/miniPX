package com.mpx.minipx.service.common;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mpx.minipx.controller.common.BaseController;
import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import com.openai.models.responses.ResponseFunctionToolCall;
import com.openai.models.responses.ResponseInputItem;
import com.openai.models.responses.ResponseOutputMessage;

/**
 * LLM이 SQL을 직접 만들지 않고,
 * 서버가 제공하는 "쿼리 카탈로그(툴/함수 목록)" 중에서 골라 호출하는 구조.
 * 필요하면 tool call을 여러 번 수행(왕복)할 수 있도록 루프 처리.
 */
@Service
public class OpenAIService {
	protected static final Log log = LogFactory.getLog(BaseController.class);	

    private final OpenAIClient openAIClient;
    private final ProductQueryService productQueryService;
    private final QdrantService qdrantService;
    
    
    
    
    @Autowired
    private SqlSessionTemplate sqlSession;      

    public OpenAIService(OpenAIClient openAIClient,
                         ProductQueryService productQueryService,
                         QdrantService qdrantService) {
        this.openAIClient = openAIClient;
        this.productQueryService = productQueryService;
        this.qdrantService = qdrantService;
    }

    public String getAiAnswer(String userInput) {

        // 1) 대화 input 리스트 준비
        List<ResponseInputItem> inputs = new ArrayList<>();
        inputs.add(
            ResponseInputItem.ofMessage(
                ResponseInputItem.Message.builder()
                    .role(ResponseInputItem.Message.Role.USER)
                    .addInputTextContent(userInput)
                    .build()
            )
        );

        // 2) "쿼리 카탈로그 기반" instructions
        String instructions = """
        너는 이커머스 쇼핑몰의 AI 상담/데이터 분석 assistant다.
        사용자는 한국어로 질문한다.

        매우 중요:
        - 데이터가 필요하면, 아래 제공된 "툴 카탈로그" 중에서 골라 호출한다.
        - 툴을 호출하면 서버가 결과(JSON)를 돌려준다.
        - 필요한 경우 툴을 여러 번 호출해도 된다(여러 번 왕복 가능).
        - 툴 결과를 바탕으로 사람이 이해하기 쉬운 한국어로 요약/정리해서 답변한다.
        - 질문이 애매해서 파라미터가 부족하면, 사용자에게 추가 질문을 해도 된다.
        
        규칙:
        - 가격에 대한 질문은 단위가격(가격 * 판매단위)를 기준으로 답변한다.
        - 모든 상품은 판매단위에 맞춰서 판매된다.
        
		[검색/툴 호출 절차 - 매우 중요]
		1) 사용자의 질문에서 핵심 키워드(예: 향/맛/성분/효능/용도/피부타입/원산지/무알콜/저당 등)를 추출한다.
		2) 1차 조회: AiGetItemList를 호출해 "상품명/분류" 기반으로 빠르게 후보를 찾는다.
		3) 2차(필수) 보강 조회: 아래 조건 중 하나라도 만족하면, 답변 전에 반드시 AiGetDetailItemList(userQuery=사용자 원문 질문)을 추가 호출한다.
		   - AiGetItemList 결과가 0건이거나 너무 적어서(예: 0~2개) 단정하기 어려운 경우
		   - 사용자 질문이 "향/성분/기능/효과/특징/사용법/주의사항" 등 상품명만으로 판별하기 어려운 속성을 포함하는 경우
		   - AiGetItemList 결과가 있어도, 그 결과만으로 사용자 조건(예: 체리향)을 충족한다고 확신할 수 없는 경우
		4) 최종 답변 규칙:
		   - "없습니다/재고가 없습니다/등록되지 않았습니다" 같은 결론은
		     AiGetItemList + AiGetDetailItemList까지 확인한 뒤에만 말한다.
		   - 두 툴 결과가 충돌하면, 더 자세한 정보를 가진 AiGetDetailItemList 결과를 우선한다.        
        
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
        """;

        // 3) 툴 등록 (서버가 제공하는 "함수 목록")
        ResponseCreateParams.Builder builder = ResponseCreateParams.builder()
            // 모델은 환경에 맞게 선택 (예: GPT_5_MINI / GPT_4_1_MINI 등)
            .model(ChatModel.GPT_4_1_MINI)
            .instructions(instructions)
            .addTool(AiGetItemList.class)
            .addTool(AiGetDetailItemList.class)
            .maxOutputTokens(1024);

        // 4) 멀티 라운드 루프: tool call이 나오면 실행 후 결과를 inputs에 붙이고 다시 호출
        final int MAX_ROUNDS = 5;
        for (int round = 0; round < MAX_ROUNDS; round++) {

            builder.input(ResponseCreateParams.Input.ofResponse(inputs));
            Response response = openAIClient.responses().create(builder.build());

            boolean hadAnyToolCallThisRound = false;
            List<ResponseFunctionToolCall> functionCalls = new ArrayList<>();

            // 응답 output을 inputs로 누적 + function call 수집
            for (var item : response.output()) {

                if (item.isFunctionCall()) {
                    hadAnyToolCallThisRound = true;
                    functionCalls.add(item.asFunctionCall());
                    // function call도 inputs에 포함(대화 히스토리로)
                    inputs.add(ResponseInputItem.ofFunctionCall(item.asFunctionCall()));
                    continue;
                }

                // 일반 메시지면 inputs에 누적
                if (item.message().isPresent()) {
                    ResponseOutputMessage outMsg = item.message().get();
                    inputs.add(convertOutputMessageToInput(outMsg));
                }
            }

            // tool call이 없으면 => 이번 응답이 최종 자연어 답변
            if (!hadAnyToolCallThisRound) {
                return extractText(response);
            }

            // tool call 실행 후 결과를 function_call_output으로 inputs에 추가
            for (ResponseFunctionToolCall fc : functionCalls) {
                Object toolResult = dispatchToolCall(fc);

                inputs.add(
                    ResponseInputItem.ofFunctionCallOutput(
                        ResponseInputItem.FunctionCallOutput.builder()
                            .callId(fc.callId())
                            .outputAsJson(toolResult) // 결과(JSON) 주입
                            .build()
                    )
                );
            }

            // 다음 라운드로 continue: tool 결과를 본 모델이 추가 툴을 부를 수도 있고 최종 답변을 만들 수도 있음
        }

        return "요청 처리가 복잡하여 처리에 실패했습니다. 조금 더 자세히 설명해 주시겠어요?";
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
