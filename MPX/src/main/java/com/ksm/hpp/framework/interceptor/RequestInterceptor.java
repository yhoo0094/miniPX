package com.ksm.hpp.framework.interceptor;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.ksm.hpp.framework.util.Constant;
import com.ksm.hpp.framework.util.RequestUtil;
import com.ksm.hpp.service.com.CommonService;

/**
* @파일명: RequestInterceptor.java
* @패키지: com.ksm.hpp.framework.interceptor
* @작성자: KimSangMin
* @생성일: 2023. 1. 18. 오후 2:19:11
* @설명: 서버에 발생한 요청을 기록하기 위한 인터셉터
 */
public class RequestInterceptor extends HandlerInterceptorAdapter {

	protected static final Log log = LogFactory.getLog("Application");
	protected static final Logger logger = Logger.getLogger(RequestInterceptor.class);
	
	@Resource(name = "CommonService")
	protected CommonService commonService;	

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		inData.remove("userPw");	//비밀번호 정보는 제거
		inData.put("param", inData.toString());
		String uri = request.getRequestURI();
		inData.put("uri", uri);
		inData.put("reqTypeCode", uri.split("/")[1]);
		
		HttpSession session = request.getSession();
		Map<String, Object> loginInfo = RequestUtil.getLoginInfo(request);
		
		if(loginInfo == null) {
			loginInfo = new HashMap<String, Object>();
			loginInfo.put("userId", null);
			
			//로그인 IP 파악
			String ip = RequestUtil.getIpAddr(request);
			loginInfo.put("ip", ip);
		}
		inData.put("loginInfo", loginInfo);
		
		Map<String, Object> outData = commonService.insertReqLog((StringBuilder)request.getAttribute("IN_LOG_STR"), inData);
		
		return super.preHandle(request, response, handler);
	}
	
}

	