package com.ksm.hpp.controller.admin;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.google.gson.Gson;
import com.ksm.hpp.controller.com.BaseController;
import com.ksm.hpp.framework.util.RequestUtil;
import com.ksm.hpp.framework.util.ResponseUtil;
import com.ksm.hpp.service.admin.ManageAuthService;
import com.ksm.hpp.service.com.CommonService;

@Controller
@RequestMapping("/admin")
public class ManageAuthController extends BaseController {
	
	@Resource(name = "ManageAuthService")
	protected ManageAuthService manageAuthService;
	
	@Resource(name = "CommonService")
	protected CommonService commonService;	
	
	String url = "admin/manageAuth";
	
	/**
	* @메소드명: selectMnuListLv1
	* @작성자: KimSangMin
	* @생성일: 2023. 5. 23. 오후 7:45:55
	* @설명: 메뉴 목록 조회
	*/
	@RequestMapping("selectMnuListLv1.do")
	public void selectMnuListLv1(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> outData = manageAuthService.selectMnuListLv1((StringBuilder)request.getAttribute("IN_LOG_STR"), inData);
		
		ResponseUtil.setResAuto(response, inData, outData);
	}	
	
	/**
	* @메소드명: selectRoleListAuth
	* @작성자: KimSangMin
	* @생성일: 2023. 5. 23. 오후 8:16:08
	* @설명: 권한그룹 목록 조회
	*/
	@RequestMapping("selectRoleListAuth.do")
	public void selectRoleListAuth(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> outData = manageAuthService.selectRoleListAuth((StringBuilder)request.getAttribute("IN_LOG_STR"), inData);
		
		ResponseUtil.setResAuto(response, inData, outData);
	}	
	
	/**
	* @메소드명: selectMnuListTable
	* @작성자: KimSangMin
	* @생성일: 2023. 5. 23. 오후 8:56:50
	* @설명: 메뉴 목록 조회
	*/
	@RequestMapping("selectMnuListTable.do")
	public void selectMnuListTable(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> outData = manageAuthService.selectMnuListTable((StringBuilder)request.getAttribute("IN_LOG_STR"), inData);
		
		ResponseUtil.setResAuto(response, inData, outData);
	}		
	
	/**
	* @메소드명: updateAuth
	* @작성자: KimSangMin
	* @생성일: 2023. 5. 27. 오후 7:58:30
	* @설명: 권한 수정
	*/
	@RequestMapping("/updateAuth.do")
	public void updateAuth(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> loginInfo = RequestUtil.getLoginInfo(request);
		inData.put("loginInfo", loginInfo);
		
		//권한 확인
		inData.put("url", url);				//메뉴 경로
		inData.put("isRange", true);		//권한등급이 정확히 일치해야 하는지(true: >=, false: ==)
		inData.put("reqAuthGrade", 99);		//필요 권한등급
		commonService.authChk((StringBuilder)request.getAttribute("IN_LOG_STR"), request, inData);
		
		Map<String, Object> outData = manageAuthService.updateAuth((StringBuilder)request.getAttribute("IN_LOG_STR"), inData);

		Gson gson = new Gson();
		String json = gson.toJson(outData);
		response.getWriter().print(json);	//결과 json형태로 담아서 보내기
		response.setContentType("application/x-json; charset=UTF-8");
	}		
	
}


	