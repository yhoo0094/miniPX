package com.ksm.hpp.framework.interceptor;

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

public class LoginCheckInterceptor extends HandlerInterceptorAdapter {

	protected static final Log log = LogFactory.getLog("Application");
	protected static final Logger logger = Logger.getLogger(LoginCheckInterceptor.class);
	
	@Resource(name = "CommonService")
	protected CommonService commonService;	
	
	/**
	 * @작성자: 김상민
	 * @생성일: 2023. 1. 11. 오전 9:43:09
	 * @설명: 클라이언트 요청 발생할 때 로그인 여부를 체크하는 인터셉터
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		Map<String, Object> loginInfo = RequestUtil.getLoginInfo(request);
		if(loginInfo == null) {
			response.sendRedirect("/login");
			return false; // 추가 처리 중단
		} else {
			HttpSession session = request.getSession();
			int sessionTime = (Integer) session.getAttribute(Constant.SESSION_TIME);
			session.setMaxInactiveInterval(sessionTime);
		}
		
		return super.preHandle(request, response, handler);
	}
	
}

	