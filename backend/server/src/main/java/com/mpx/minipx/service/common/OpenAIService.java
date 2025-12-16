package com.mpx.minipx.service.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

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

    private final OpenAIClient openAIClient;
    private final ProductQueryService productQueryService;

    public OpenAIService(OpenAIClient openAIClient,
                         ProductQueryService productQueryService) {
        this.openAIClient = openAIClient;
        this.productQueryService = productQueryService;
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
        - 너는 SQL을 작성하지 않는다.
        - 데이터가 필요하면, 아래 제공된 "쿼리 카탈로그(함수/툴)" 중에서 골라 호출한다.
        - 툴을 호출하면 서버가 결과(JSON)를 돌려준다.
        - 필요한 경우 툴을 여러 번 호출해도 된다(여러 번 왕복 가능).
        - 툴 결과를 바탕으로 사람이 이해하기 쉬운 한국어로 요약/정리해서 답변한다.
        - 질문이 애매해서 파라미터가 부족하면, 사용자에게 추가 질문을 해도 된다.

        [쿼리 카탈로그(예시)]
        1) GetItemList
           - 용도: 상품 목록 조회(필터/정렬/페이징)
           - 파라미터:
             * keyword (optional): 상품명 검색어
             * itemType (optional): "식품" | "화장품" | "기타" 같은 분류명(서버에서 코드로 매핑)
             * excludeSoldOut (optional, default=true): 품절 제외 여부
             * onlyActive (optional, default=true): 판매중(USE_YN=Y)만 조회 여부
             * sort (optional): "PRICE_ASC" | "PRICE_DESC" | "NEWEST" | "NAME_ASC"
             * limit (optional, default=20, max=50)
             * offset (optional, default=0)
           - 리턴: 상품일련번호/상품명/가격/판매단위/품절여부/사용여부

        2) GetItemDetail
           - 용도: 상품 1건 상세 조회
           - 파라미터: itemSeq (required)
           - 리턴: 상품의 모든 주요 정보(설명/이미지/분류코드 등)

        3) GetCheapestItem
           - 용도: 가장 저렴한 상품 1건
           - 파라미터:
             * onlyActive (optional, default=true)
             * excludeSoldOut (optional, default=true)
           - 리턴: 1건

        4) GetMostExpensiveItem
           - 용도: 가장 비싼 상품 1건
           - 파라미터:
             * onlyActive (optional, default=true)
             * excludeSoldOut (optional, default=true)
           - 리턴: 1건

        5) GetPriceStatsByType
           - 용도: 상품 분류(대분류)별 가격 통계(예: 최저/최고/평균 등)
           - 파라미터:
             * onlyActive (optional, default=true)
             * excludeSoldOut (optional, default=true)
           - 리턴: ITEM_TYPE_CODE 별 min/max/avg/count
        """;

        // 3) 툴 등록 (서버가 제공하는 "함수 목록")
        ResponseCreateParams.Builder builder = ResponseCreateParams.builder()
            // 모델은 환경에 맞게 선택 (예: GPT_5_MINI / GPT_4_1_MINI 등)
            .model(ChatModel.GPT_4_1_MINI)
            .instructions(instructions)
            .addTool(GetItemListArgs.class)
            .addTool(GetItemDetailArgs.class)
            .addTool(GetCheapestItemArgs.class)
            .addTool(GetMostExpensiveItemArgs.class)
            .addTool(GetPriceStatsByTypeArgs.class)
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

        return "요청 처리가 복잡하여 여러 번 조회가 필요했지만, 최대 조회 횟수를 초과했습니다. 질문을 조금 더 구체적으로 해주세요.";
    }

    /**
     * tool name에 따라 서버 쿼리(카탈로그) 실행.
     * - 여기서는 "SQL 생성"이 아니라, 서버에 미리 정의된 안전한 쿼리/서비스 메서드를 호출.
     */
    private Object dispatchToolCall(ResponseFunctionToolCall functionCall) {

        String name = functionCall.name();

        try {
            switch (name) {
                case "GetItemListArgs": {
                    GetItemListArgs args = functionCall.arguments(GetItemListArgs.class);
                    return productQueryService.getItemList(args);
                }
//                case "GetItemDetailArgs": {
//                    GetItemDetailArgs args = functionCall.arguments(GetItemDetailArgs.class);
//                    return productQueryService.getItemDetail(args.itemSeq());
//                }
//                case "GetCheapestItemArgs": {
//                    GetCheapestItemArgs args = functionCall.arguments(GetCheapestItemArgs.class);
//                    return productQueryService.getCheapestItem(args.onlyActive(), args.excludeSoldOut());
//                }
//                case "GetMostExpensiveItemArgs": {
//                    GetMostExpensiveItemArgs args = functionCall.arguments(GetMostExpensiveItemArgs.class);
//                    return productQueryService.getMostExpensiveItem(args.onlyActive(), args.excludeSoldOut());
//                }
//                case "GetPriceStatsByTypeArgs": {
//                    GetPriceStatsByTypeArgs args = functionCall.arguments(GetPriceStatsByTypeArgs.class);
//                    return productQueryService.getPriceStatsByType(args.onlyActive(), args.excludeSoldOut());
//                }
                default:
                    // 알 수 없는 툴이면 에러를 툴 결과로 반환해 모델이 스스로 수정하게 함
                    return Map.of(
                        "error", "Unknown tool name: " + name,
                        "hint", "Use one of the registered tools only."
                    );
            }
        } catch (Exception e) {
            return Map.of(
                "error", "Tool execution failed",
                "tool", name,
                "message", e.getMessage()
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

    public record GetItemListArgs(
        String keyword,
        String itemType,
        Boolean excludeSoldOut,
        Boolean onlyActive,
        String sort,
        Integer limit,
        Integer offset
    ) {}

    public record GetItemDetailArgs(
        Integer itemSeq
    ) {}

    public record GetCheapestItemArgs(
        Boolean onlyActive,
        Boolean excludeSoldOut
    ) {}

    public record GetMostExpensiveItemArgs(
        Boolean onlyActive,
        Boolean excludeSoldOut
    ) {}

    public record GetPriceStatsByTypeArgs(
        Boolean onlyActive,
        Boolean excludeSoldOut
    ) {}
}
