package com.mpx.minipx.dto.common;

//lombok 사용 예시
import lombok.Data;

@Data
public class AiAnswerResult {
	long sessionId;
	long turnId;
	int turnNo;
	String answer;
}

