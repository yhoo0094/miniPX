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
import com.ksm.hpp.service.admin.ManageRoleService;
import com.ksm.hpp.service.com.CommonService;

@Controller
@RequestMapping("/admin")
public class ManageRoleController extends BaseController {
	
	@Resource(name = "ManageRoleService")
	protected ManageRoleService manageRoleService;
	
	@Resource(name = "CommonService")
	protected CommonService commonService;	
	
	String url = "admin/manageRole";
	
	/**
	* @메소드명: selectRoleList
	* @작성자: KimSangMin
	* @생성일: 2023. 5. 4. 오후 9:50:14
	* @설명: 권한그룹 목록 조회
	*/
	@RequestMapping("/selectRoleList.do")
	public void selectRoleList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> outData = manageRoleService.selectRoleList((StringBuilder)request.getAttribute("IN_LOG_STR"), inData);
		
		ResponseUtil.setResAuto(response, inData, outData);
	}	
	
	
	/**
	* @메소드명: selectGroupUser
	* @작성자: KimSangMin
	* @생성일: 2023. 5. 9. 오후 7:56:43
	* @설명: 권한그룹에 속한 사용자 목록 조회
	*/
	@RequestMapping("/selectGroupUser.do")
	public void selectGroupUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		
		Map<String, Object> loginInfo = RequestUtil.getLoginInfo(request);
		inData.put("loginInfo", loginInfo);
		
		//게스트 계정 제약 사항 확인(게스트 권한인 경우 사용자 그룹만 조회 가능)
		String loginRoleNm = (String) loginInfo.get("roleNm"); 
		String roleSeq = (String) inData.get("roleSeq");
		if(loginRoleNm.equals("게스트") && roleSeq != null && !roleSeq.equals("1")) {
			throw new ConfigurationException("게스트 권한인 경우 사용자 그룹만 조회 가능합니다.");
		}		
		
		Map<String, Object> outData = manageRoleService.selectGroupUser((StringBuilder)request.getAttribute("IN_LOG_STR"), inData);
		ResponseUtil.setResAuto(response, inData, outData);
	}		
	
	/**
	* @메소드명: deleteGroupUser
	* @작성자: KimSangMin
	* @생성일: 2023. 5. 11. 오후 7:41:48
	* @설명: 권한그룹 사용자 제거
	*/
	@RequestMapping("/deleteGroupUser.do")
	public void deleteGroupUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> loginInfo = RequestUtil.getLoginInfo(request);
		inData.put("loginInfo", loginInfo);
		
		//권한 확인
		inData.put("url", url);				//메뉴 경로
		inData.put("isRange", true);		//권한등급이 정확히 일치해야 하는지
		inData.put("reqAuthGrade", 99);		//필요 권한등급
		commonService.authChk((StringBuilder)request.getAttribute("IN_LOG_STR"), request, inData);
		
		//array 대괄호 제거
		String userIdList = (String) inData.get("userIdList");
		userIdList = userIdList.replace("[", "");
		userIdList = userIdList.replace("]", "");
		inData.put("userIdList", userIdList);
		
		Map<String, Object> outData = manageRoleService.deleteGroupUser((StringBuilder)request.getAttribute("IN_LOG_STR"), inData);
		ResponseUtil.setResAuto(response, inData, outData);
	}		
	
	
}


	