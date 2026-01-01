package com.mpx.minipx.controller.common;

import com.mpx.minipx.dto.common.AiAnswerResult;
import com.mpx.minipx.framework.util.JwtUtil;
import com.mpx.minipx.service.common.OpenAIService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/openai")
public class OpenAIController {

    private final OpenAIService openAIService;

    public OpenAIController(OpenAIService openAIService) {
        this.openAIService = openAIService;
    }

    @Value("${jwt.secret}")
    private String jwtSecret;    
    
    /**
     * @메소드명: getAiAnswer
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 5.
     * @설명: AI에 답변 받기
     */
    @PostMapping("/getAiAnswer")
    public ResponseEntity<?> getAiAnswer(@RequestBody Map<String, Object> inData, HttpServletRequest request) {
    	
        // ---------- 사용자 정보 추출 ----------
        String accessToken = JwtUtil.extractTokenFromCookies(request, "accessToken");
        Claims claims;
        try {
            claims = JwtUtil.validateToken(accessToken, jwtSecret);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid token");
        }
        inData.put("loginUserId", (String) claims.get("userId"));
        inData.put("loginUserSeq", claims.get("userSeq"));    	
    	
        AiAnswerResult result = openAIService.getAiAnswer(inData);
        return ResponseEntity.ok(result);
    }
}	