package com.mpx.minipx.controller.common;

import com.mpx.minipx.service.common.OpenAIService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/openai")
public class OpenAIController {

    private final OpenAIService openAIService;

    public OpenAIController(OpenAIService openAIService) {
        this.openAIService = openAIService;
    }

    /**
     * GET 방식 호출 예:
     *   GET /api/openai/ask?q=유니콘 이야기 해줘
     */
    @GetMapping("/ask")
    public String askGet(@RequestParam("q") String question) {
        return openAIService.ask(question);
    }

    /**
     * POST 방식 호출 예:
     *   POST /api/openai/ask
     */
    public static class AskRequest {
        public String question;

        // 필요시 getter/setter 추가 (Lombok 쓰시면 @Data 등으로 대체 가능)
    }

    /**
     * @메소드명: askPost
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 5.
     * @설명: LLM에 바로 질문하기
     */
    @PostMapping("/ask")
    public String askPost(@RequestBody AskRequest req) {
    	
        return openAIService.askWithDbTools(req.question);
    }
}	