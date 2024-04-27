package com.ksm.hpp.controller.admin;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ksm.hpp.controller.com.BaseController;
import com.ksm.hpp.framework.exception.ConfigurationException;
import com.ksm.hpp.framework.util.RequestUtil;
import com.ksm.hpp.framework.util.ResponseUtil;
import com.ksm.hpp.service.admin.AdminHomeService;
import com.ksm.hpp.service.com.CommonService;

@Controller
@RequestMapping("/admin")
public class AdminHomeController extends BaseController {
	
	@Resource(name = "AdminHomeService")
	protected AdminHomeService adminHomeService;
	
	@Resource(name = "CommonService")
	protected CommonService commonService;
	
	String url = "admin/adminHome";
	
	/**
	* @메소드명: drawLoginLogChart
	* @작성자: KimSangMin
	* @생성일: 2024. 1. 19. 오후 2:16:56
	* @설명: 접속기록 그래프 그리기
	 */
	@RequestMapping("/drawLoginLogChart.do")
	public void drawLoginLogChart(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> loginInfo = RequestUtil.getLoginInfo(request);
		inData.put("loginInfo", loginInfo);
		
		//권한 확인
		inData.put("url", url);				//메뉴 경로
		inData.put("isRange", true);		//권한등급이 정확히 일치해야 하는지
		inData.put("reqAuthGrade", 1);		//필요 권한등급
		commonService.authChk((StringBuilder)request.getAttribute("IN_LOG_STR"), request, inData);
		
		Map<String, Object> outData = adminHomeService.drawLoginLogChart((StringBuilder)request.getAttribute("IN_LOG_STR"), inData);
		ResponseUtil.setResAuto(response, inData, outData);
	}	
	
	/**
	* @메소드명: drawRequestLogChart
	* @작성자: KimSangMin
	* @생성일: 2024. 1. 22. 오후 1:26:04
	* @설명: 요청기록 그래프 그리기
	 */
	@RequestMapping("/drawRequestLogChart.do")
	public void drawRequestLogChart(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> loginInfo = RequestUtil.getLoginInfo(request);
		inData.put("loginInfo", loginInfo);
		
		//권한 확인
		inData.put("url", url);				//메뉴 경로
		inData.put("isRange", true);		//권한등급이 정확히 일치해야 하는지
		inData.put("reqAuthGrade", 1);		//필요 권한등급
		commonService.authChk((StringBuilder)request.getAttribute("IN_LOG_STR"), request, inData);
		
		Map<String, Object> outData = adminHomeService.drawRequestLogChart((StringBuilder)request.getAttribute("IN_LOG_STR"), inData);
		ResponseUtil.setResAuto(response, inData, outData);
	}		
}


	