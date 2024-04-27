package com.ksm.hpp.controller.admin;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.Gson;
import com.ksm.hpp.controller.com.BaseController;
import com.ksm.hpp.framework.exception.ConfigurationException;
import com.ksm.hpp.framework.util.RequestUtil;
import com.ksm.hpp.framework.util.ResponseUtil;
import com.ksm.hpp.service.admin.RequestLogService;
import com.ksm.hpp.service.com.CommonService;

@Controller
@RequestMapping("/admin")
public class RequestLogController extends BaseController {
	
	@Resource(name = "RequestLogService")
	protected RequestLogService requestLogService;
	
	@Resource(name = "CommonService")
	protected CommonService commonService;
	
	String url = "admin/requestLog";	
	
	/**
	* @메소드명: selectRequestLog
	* @작성자: KimSangMin
	* @생성일: 2023. 1. 30. 오후 2:58:09
	* @설명: 서버에 발생한 요청 기록 조회
	*/
	@RequestMapping("/selectRequestLog.do")
	public void selectRequestLog(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> loginInfo = RequestUtil.getLoginInfo(request);
		inData.put("loginInfo", loginInfo);
		
		//권한 확인
		inData.put("url", url);				//메뉴 경로
		inData.put("isRange", true);		//권한등급이 정확히 일치해야 하는지
		inData.put("reqAuthGrade", 1);		//필요 권한등급
		commonService.authChk((StringBuilder)request.getAttribute("IN_LOG_STR"), request, inData);
		
		//게스트 계정 제약 사항 확인
		String roleNm = (String) loginInfo.get("roleNm"); 
		if(roleNm.equals("게스트")) {
			String loginUserId = (String) loginInfo.get("userId");
			String searchUserId = (String) inData.get("userId");
			String loginIp = (String) loginInfo.get("ip");
			String searchUserIp = (String) inData.get("ip");
			
			if(!loginUserId.equals(searchUserId) || !loginIp.equals(searchUserIp)) {
				throw new ConfigurationException("본인의 로그만 확인 가능합니다.");
			}
		}		
		
		Map<String, Object> outData = requestLogService.selectRequestLog((StringBuilder)request.getAttribute("IN_LOG_STR"), inData);
		ResponseUtil.setResAuto(response, inData, outData);
	}	
}


	