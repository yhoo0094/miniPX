package com.mpx.minipx.service.common;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.mpx.minipx.dto.common.RunProductsQueryArgs;
import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import com.openai.models.responses.ResponseFunctionToolCall;
import com.openai.models.responses.ResponseInputItem;
import com.openai.models.responses.ResponseOutputMessage;

@Service
public class OpenAIService {

    private final OpenAIClient openAIClient;
    private final ProductQueryService productQueryService;

    public OpenAIService(OpenAIClient openAIClient,
                         ProductQueryService productQueryService) {
        this.openAIClient = openAIClient;
        this.productQueryService = productQueryService;
    }

    /**
     * 그냥 LLM에 질문만 하는 기본 버전
     */
    public String ask(String userInput) {
        ResponseCreateParams params = ResponseCreateParams.builder()
                .model(ChatModel.GPT_4_1_MINI)
                .input(userInput)
                .build();

        Response response = openAIClient.responses().create(params);
        return extractText(response);
    }

    /**
     * DB 조회가 필요한 질문 (툴 사용)
     */
    public String askWithDbTools(String userInput) {

        // 1) 대화 input 리스트 준비 (Responses API 예제 패턴)
        List<ResponseInputItem> inputs = new ArrayList<>();
        inputs.add(
            ResponseInputItem.ofMessage(
                ResponseInputItem.Message.builder()
                    .role(ResponseInputItem.Message.Role.USER)
                    .addInputTextContent(userInput)
                    .build()
            )
        );

        // 2) 기본 파라미터 빌더 (툴 등록까지)
        ResponseCreateParams.Builder builder = ResponseCreateParams.builder()
                .model(ChatModel.GPT_4_1_MINI)
                .instructions("""
                	    너는 이커머스 사이트의 상품 정보를 조회하는 데이터 분석 assistant다.
                	    사용자는 한국어로 질문하고, 너는 DB 스키마에 맞는 SQL을 만든 뒤
                	    run_products_query(RunProductsQueryArgs) 함수를 사용해 실제 데이터를 조회해야 한다.

                	    [DB 스키마]

                	    테이블명: TB_ITEM  -- 상품 정보 관리 테이블

                	    컬럼:
                	    - ITEM_SEQ (INT, PK, NOT NULL, AUTO_INCREMENT)
                	      : 상품일련번호 (기본 키)

                	    - ITEM_NM (VARCHAR(300), NULL 허용)
                	      : 상품명

                	    - PRICE (INT, NULL 허용)
                	      : 상품 1개 판매 가격

                	    - UNIT (INT, NULL 허용)
                	      : 판매단위 (예: 1, 10, 100 등)

                	    - RMRK (VARCHAR(3000), NULL 허용)
                	      : 비고 / 상품 설명

                	    - IMG (VARCHAR(100), NULL 허용)
                	      : 상품 이미지 파일명

                	    - ITEM_TYPE_CODE (VARCHAR(10), NULL 허용)
                	      : 상품 분류 코드 (대분류)

                	    - ITEM_DTL_TYPE_CODE (VARCHAR(10), NULL 허용)
                	      : 상품 상세 분류 코드 (소분류)

                	    - SOLD_OUT_YN (VARCHAR(1), NULL 허용, 기본값 'N')
                	      : 품절 여부. 'Y' = 품절, 'N' = 판매 가능

                	    - USE_YN (VARCHAR(1), NULL 허용, 기본값 'Y')
                	      : 사용 여부. 'Y' = 사용 중, 'N' = 미사용/삭제 상태

                	    - FST_REG_SEQ (INT, NOT NULL)
                	      : 최초등록작업일련번호

                	    - FST_REG_DTTI (TIMESTAMP, NOT NULL, 기본값 NOW())
                	      : 최초 등록일시

                	    - LST_UPD_SEQ (INT, NOT NULL)
                	      : 최종수정작업일련번호

                	    - LST_UPD_DTTI (TIMESTAMP, NOT NULL, 기본값 NOW())
                	      : 최종 수정일시


                	    [쿼리 작성 규칙]

                	    - 항상 TB_ITEM 테이블만 사용한다.
                	    - SQL은 반드시 SELECT 문만 작성한다.
                	      INSERT, UPDATE, DELETE, ALTER, DROP 등은 절대 작성하지 않는다.
                	    - 컬럼명은 위에 정의된 컬럼만 사용하고, 존재하지 않는 컬럼명을 만들어내지 않는다.
                	    - 일반적으로 "현재 판매 중인 상품"은 USE_YN = 'Y' 인 행을 의미한다.
                	    - 품절 상품을 제외하고 싶을 때는 SOLD_OUT_YN = 'N' 조건을 함께 사용한다.
                	      예: WHERE USE_YN = 'Y' AND SOLD_OUT_YN = 'N'
                	    - "가장 저렴한 상품"을 찾을 때는 PRICE가 가장 낮은 한 건을
                	      ORDER BY PRICE ASC LIMIT 1 으로 조회한다.
                	    - "가장 비싼 상품"은 ORDER BY PRICE DESC LIMIT 1 으로 조회한다.
                	    - 사용자가 범위를 지정하지 않으면 특별한 기간 조건은 걸지 않는다.
                	    - SQL을 만든 뒤에는 run_products_query 함수를 호출하여 실제 데이터를 조회하고,
                	      그 조회 결과를 기반으로 사람이 이해하기 쉬운 한국어 문장으로 요약해서 답변한다.
                	    """)
                .addTool(RunProductsQueryArgs.class)
                .maxOutputTokens(1024)
                .input(ResponseCreateParams.Input.ofResponse(inputs));

        // 3) 첫 번째 호출 (툴 콜이 나오길 기대)
        Response firstResponse = openAIClient.responses().create(builder.build());

        boolean hasToolCall = false;

        // 4) output에서 function call이 있는지 확인
        for (var item : firstResponse.output()) {
            if (item.isFunctionCall()) {
                hasToolCall = true;

                ResponseFunctionToolCall functionCall = item.asFunctionCall();

                // 함수 이름은 기본적으로 클래스 이름 (예: "RunProductsQueryArgs")
                if ("RunProductsQueryArgs".equals(functionCall.name())) {

                    // LLM이 채운 arguments → RunProductsQueryArgs 로 파싱
                    RunProductsQueryArgs args = functionCall.arguments(RunProductsQueryArgs.class);

                    // 실제 DB 조회 호출
                    var rows = productQueryService.runReadOnlyQuery(args.sql);

                    // 4-1) 이 function call 자체를 input 리스트에 추가
                    inputs.add(ResponseInputItem.ofFunctionCall(functionCall));

                    // 4-2) function call 결과를 function_call_output 으로 추가
                    inputs.add(
                        ResponseInputItem.ofFunctionCallOutput(
                            ResponseInputItem.FunctionCallOutput.builder()
                                .callId(functionCall.callId())
                                .outputAsJson(rows) // ← List<Map<String,Object>> 를 JSON 으로 전달
                                .build()
                        )
                    );
                }
            } else {
                ResponseOutputMessage outMsg = item.message().get();
                inputs.add(convertOutputMessageToInput(outMsg));
            }
        }

        // 5) 툴 콜이 전혀 없었으면, 첫 답변의 텍스트만 그대로 반환
        if (!hasToolCall) {
            return extractText(firstResponse);
        }

        // 6) 툴 결과까지 포함된 inputs로 두 번째 호출 → 최종 자연어 답변
        builder.input(ResponseCreateParams.Input.ofResponse(inputs));
        Response finalResponse = openAIClient.responses().create(builder.build());

        return extractText(finalResponse);
    }
    
    // ==========================================================
    //       ResponseOutputMessage → ResponseInputItem.Message 변환
    // ==========================================================
    private ResponseInputItem convertOutputMessageToInput(ResponseOutputMessage outputMsg) {

        ResponseInputItem.Message.Builder msgBuilder =
                ResponseInputItem.Message.builder()
                        .role(outputMsg._role());

        // 텍스트 콘텐츠만 복사 (추가 타입 있으면 필요 시 확장)
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
}
