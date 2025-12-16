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
    
    public static class AskRequest {
        public String question;

        // 필요시 getter/setter 추가 (Lombok 쓰시면 @Data 등으로 대체 가능)
    }

    /**
     * @메소드명: getAiAnswer
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 5.
     * @설명: AI에 답변 받기
     */
    @PostMapping("/getAiAnswer")
    public String getAiAnswer(@RequestBody AskRequest req) {
    	
        return openAIService.getAiAnswer(req.question);
    }
}	