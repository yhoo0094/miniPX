package com.ksm.hpp.controller.admin;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.Gson;
import com.ksm.hpp.controller.com.BaseController;
import com.ksm.hpp.framework.util.RequestUtil;
import com.ksm.hpp.framework.util.ResponseUtil;
import com.ksm.hpp.service.admin.ManageMnuService;
import com.ksm.hpp.service.com.CommonService;

@Controller
@RequestMapping("/admin")
public class ManageMnuController extends BaseController {
	
	@Resource(name = "ManageMnuService")
	protected ManageMnuService manageMnuService;
	
	@Resource(name = "CommonService")
	protected CommonService commonService;	
	
	String url = "admin/manageMnu";	
	
	/**
	* @메소드명: selectMnuList
	* @작성자: KimSangMin
	* @생성일: 2023. 5. 13. 오후 2:33:14
	* @설명: 메뉴 목록 조회
	*/
	@RequestMapping("/selectMnuList.do")
	public void selectMnuList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> outData = manageMnuService.selectMnuList((StringBuilder)request.getAttribute("IN_LOG_STR"), inData);
		
		ResponseUtil.setResAuto(response, inData, outData);
	}	
	
	/**
	* @메소드명: updateAuth
	* @작성자: KimSangMin
	* @생성일: 2024. 1. 15. 오전 9:49:04
	* @설명: 메뉴 수정
	*/
	@RequestMapping("/updateMnu.do")
	public void updateMnu(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> loginInfo = RequestUtil.getLoginInfo(request);
		inData.put("loginInfo", loginInfo);
		
		//url 변수명 충돌로 인한 임시 변수
		inData.put("mnuUrl",inData.get("url"));				
		
		//권한 확인
		inData.put("url", url);				//메뉴 경로
		inData.put("isRange", true);		//권한등급이 정확히 일치해야 하는지(true: >=, false: ==)
		inData.put("reqAuthGrade", 99);		//필요 권한등급
		commonService.authChk((StringBuilder)request.getAttribute("IN_LOG_STR"), request, inData);
		
		Map<String, Object> outData = manageMnuService.updateMnu((StringBuilder)request.getAttribute("IN_LOG_STR"), inData);

		Gson gson = new Gson();
		String json = gson.toJson(outData);
		response.getWriter().print(json);	//결과 json형태로 담아서 보내기
		response.setContentType("application/x-json; charset=UTF-8");
	}			
}


	