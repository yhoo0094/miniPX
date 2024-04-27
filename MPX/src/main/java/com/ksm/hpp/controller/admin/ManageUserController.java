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
import com.ksm.hpp.service.admin.ManageUserService;
import com.ksm.hpp.service.com.CommonService;

@Controller
@RequestMapping("/admin")
public class ManageUserController extends BaseController {
	
	@Resource(name = "ManageUserService")
	protected ManageUserService manageUserService;
	
	@Resource(name = "CommonService")
	protected CommonService commonService;	
	
	String url = "admin/manageUser";	
	
	/**
	* @메소드명: selectRoleList
	* @작성자: KimSangMin
	* @생성일: 2024. 1. 15. 오후 4:28:25
	* @설명: 권한그룹 select태그 만들기
	 */
	@RequestMapping("/makeRoleSelectTag.do")
	public void selectRoleList(HttpServletRequest request, HttpServletResponse response) throws Exception {		
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> loginInfo = RequestUtil.getLoginInfo(request);
		inData.put("loginInfo", loginInfo);
		
		//권한 확인
		inData.put("url", url);				//메뉴 경로
		inData.put("isRange", true);		//권한등급이 정확히 일치해야 하는지
		inData.put("reqAuthGrade", 1);		//필요 권한등급
		commonService.authChk((StringBuilder)request.getAttribute("IN_LOG_STR"), request, inData);	
		
		Map<String, Object> outData = manageUserService.makeRoleSelectTag((StringBuilder)request.getAttribute("IN_LOG_STR"), inData);
		ResponseUtil.setResAuto(response, inData, outData);		
	}	
	
	/**
	* @메소드명: selectUser
	* @작성자: KimSangMin
	* @생성일: 2024. 1. 15. 오후 5:58:07
	* @설명: 사용자 조회
	 */ 
	@RequestMapping("/selectUser.do")
	public void selectUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
			
			if(!loginUserId.equals(searchUserId)) {
				throw new ConfigurationException("본인의 계정만 확인 가능합니다.");
			}
		}			
		
		Map<String, Object> outData = manageUserService.selectUser((StringBuilder)request.getAttribute("IN_LOG_STR"), inData);
		ResponseUtil.setResAuto(response, inData, outData);
	}	
	
	/**
	* @메소드명: insertUserInfo
	* @작성자: KimSangMin
	* @생성일: 2024. 1. 16. 오후 3:30:02
	* @설명: 사용자 정보 등록
	 */
	@RequestMapping("/insertUserInfo.do")
	public void insertUserInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> loginInfo = RequestUtil.getLoginInfo(request);
		inData.put("loginInfo", loginInfo);
		
		//권한 확인
		inData.put("url", url);				//메뉴 경로
		inData.put("isRange", true);		//권한등급이 정확히 일치해야 하는지(true: >=, false: ==)
		inData.put("reqAuthGrade", 99);		//필요 권한등급
		commonService.authChk((StringBuilder)request.getAttribute("IN_LOG_STR"), request, inData);
		
		Map<String, Object> outData = manageUserService.insertUserInfo((StringBuilder)request.getAttribute("IN_LOG_STR"), inData);

		Gson gson = new Gson();
		String json = gson.toJson(outData);
		response.getWriter().print(json);	//결과 json형태로 담아서 보내기
		response.setContentType("application/x-json; charset=UTF-8");
	}		
	
	/**
	* @메소드명: updateUserInfo
	* @작성자: KimSangMin
	* @생성일: 2024. 1. 16. 오후 1:32:28
	* @설명: 사용자 정보 수정
	 */
	@RequestMapping("/updateUserInfo.do")
	public void updateUserInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> loginInfo = RequestUtil.getLoginInfo(request);
		inData.put("loginInfo", loginInfo);
		
		//권한 확인
		inData.put("url", url);				//메뉴 경로
		inData.put("isRange", true);		//권한등급이 정확히 일치해야 하는지(true: >=, false: ==)
		inData.put("reqAuthGrade", 99);		//필요 권한등급
		commonService.authChk((StringBuilder)request.getAttribute("IN_LOG_STR"), request, inData);
		
		Map<String, Object> outData = manageUserService.updateUserInfo((StringBuilder)request.getAttribute("IN_LOG_STR"), inData);

		Gson gson = new Gson();
		String json = gson.toJson(outData);
		response.getWriter().print(json);	//결과 json형태로 담아서 보내기
		response.setContentType("application/x-json; charset=UTF-8");
	}	
	
	/**
	* @메소드명: pwReset
	* @작성자: KimSangMin
	* @생성일: 2024. 1. 16. 오후 4:50:20
	* @설명: 비밀번호 초기화
	 */
	@RequestMapping("/pwReset.do")
	public void pwReset(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> loginInfo = RequestUtil.getLoginInfo(request);
		inData.put("loginInfo", loginInfo);
		
		//권한 확인
		inData.put("url", url);				//메뉴 경로
		inData.put("isRange", true);		//권한등급이 정확히 일치해야 하는지(true: >=, false: ==)
		inData.put("reqAuthGrade", 99);		//필요 권한등급
		commonService.authChk((StringBuilder)request.getAttribute("IN_LOG_STR"), request, inData);
		
		Map<String, Object> outData = manageUserService.pwReset((StringBuilder)request.getAttribute("IN_LOG_STR"), inData);

		Gson gson = new Gson();
		String json = gson.toJson(outData);
		response.getWriter().print(json);	//결과 json형태로 담아서 보내기
		response.setContentType("application/x-json; charset=UTF-8");
	}	
}


	