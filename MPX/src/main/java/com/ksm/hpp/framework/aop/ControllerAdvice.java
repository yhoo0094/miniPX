package com.ksm.hpp.framework.aop;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ksm.hpp.framework.util.Constant;
import com.ksm.hpp.framework.util.RequestUtil;
import com.ksm.hpp.framework.util.ResponseUtil;

/**
* @파일명: ControllerAdvice.java
* @패키지: com.ksm.hpp.framework.aop
* @작성자: KimSangMin
* @생성일: 2023. 1. 20. 오후 2:46:47
* @설명: 컨트롤러 관련 aop
 */
@Component//자동으로 Bean 등록
@Aspect
public class ControllerAdvice {
	
	protected static final Logger logger = Logger.getLogger(ControllerAdvice.class);
	
	@Around("execution(* com.ksm.hpp.controller..*Controller.*(..))")
	public static Object around(ProceedingJoinPoint joinpoint) throws Throwable {
		long start = System.currentTimeMillis();	//요청 시작 시간
		
		Object result = null; 						//출력 결과 객체
		Object[] args = joinpoint.getArgs();		//메서드 파라미터
		HttpServletRequest request = null;			//request 객체
		HttpServletResponse response = null;		//response 객체
		StringBuilder logStr = new StringBuilder();	//log를 위한 StringBuilder
		MethodSignature methodSig = (MethodSignature) joinpoint.getSignature(); //메서드 시그니처
		@SuppressWarnings("unused")
		Class<?> returnType = methodSig.getReturnType();	//메서드 반환타입
		
		//reqeust 객체 추출
		for(Object arg : args) {
			if(arg instanceof HttpServletRequest) {
				request = (HttpServletRequest) arg;
				request.setCharacterEncoding("UTF-8");
			} else if(arg instanceof HttpServletResponse) {
				response = (HttpServletResponse) arg;
			}
		}
		
		String methodNm = joinpoint.getSignature().toString();
		methodNm = methodNm.substring(methodNm.indexOf("controller.") + 11);
		
		logStr.append(methodNm);	//메서드 로그
		if(request != null) {
			logStr.append("/" + request.getRemoteAddr()); //ip로그
			logStr.append("/" + RequestUtil.getParameterStrForLog(request)); //파라미터 로그
			
			request.setAttribute(Constant.IN_LOG_STR, logStr);
		} else {
			logStr.append("/request not found");
		}
		
		//실행
		try {
			result = joinpoint.proceed();//타겟메서드 실행
		} catch(Exception e) {
			e.printStackTrace();
			
			//에러 로그
			logStr.append("\nController Exception: " + e.getClass() + " logStr: " + logStr.toString());
			logStr.append(": " + e.getMessage());
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintWriter pw = new PrintWriter(baos);
			pw.flush();
			
			//void 반환인 경우 json 출력 형식이므로 에러 json 출력
			if("void".equals(returnType.getName())) {
				Gson gson = new Gson();
				JsonObject jsonObject = new JsonObject();
				
				//Exception 종류에 맞는 에러 메세지 생성
				Map<String,Object> exceptionResultMap = ResponseUtil.getExceptionResultMap(e);
				jsonObject.addProperty(Constant.RESULT, (String) exceptionResultMap.get(Constant.RESULT));
				jsonObject.addProperty(Constant.OUT_RESULT_MSG, (String) exceptionResultMap.get(Constant.OUT_RESULT_MSG));
				result = jsonObject;
				
				if(response != null) {
					response.setContentType("application/x-json charset=UTF-8");
					response.getWriter().print(result);
				}
			};
		} finally {
			long runningTime = System.currentTimeMillis() - start;
			logger.info("요청 처리시간: " + runningTime);		//요청 처리시간
			logger.info(logStr);
		}
		return result;
	}
}
