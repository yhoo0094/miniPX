package com.ksm.hpp.controller.market;

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
import com.ksm.hpp.service.com.CommonService;
import com.ksm.hpp.service.market.EnterpriseService;

@Controller
@RequestMapping("/enterprise")
public class EnterpriseController extends BaseController {
	
	@Resource(name = "EnterpriseService")
	protected EnterpriseService enterpriseService;

	@Resource(name = "CommonService")
	protected CommonService commonService;
	
	String url = "market/enterprise";
	
	/**
	* @메소드명: selectEnterprise
	* @작성자: KimSangMin
	* @생성일: 2023. 12. 14. 오후 3:12:47
	* @설명: 기업장터 조회
	 */
	@RequestMapping("/selectEnterprise.do")
	public void selectEnterprise(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> outData = enterpriseService.selectEnterprise((StringBuilder)request.getAttribute("IN_LOG_STR"), inData);
		
		ResponseUtil.setResAuto(response, inData, outData);
	}	
	
	/**
	* @메소드명: selectEnterprise
	* @작성자: KimSangMin
	* @생성일: 2023. 12. 18. 오후 4:31:29
	* @설명:기업장터 조회(홈 화면)
	 */
	@RequestMapping("/selectEnterpriseHome.do")
	public void selectEnterpriseHome(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> outData = enterpriseService.selectEnterpriseHome((StringBuilder)request.getAttribute("IN_LOG_STR"), inData);
		
		ResponseUtil.setResAuto(response, inData, outData);
	}		
	
	/**
	* @메소드명: insertEnterprise
	* @작성자: KimSangMin
	* @생성일: 2023. 12. 13. 오후 3:17:19
	* @설명: 기업장터 등록
	*/
	@RequestMapping("/insertEnterprise.do")
	public void insertEnterprise(MultipartHttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> loginInfo = RequestUtil.getLoginInfo(request);
		inData.put("loginInfo", loginInfo);
		
		//권한 확인
		inData.put("url", url);				//메뉴 경로
		inData.put("isRange", true);		//권한등급이 정확히 일치해야 하는지
		inData.put("reqAuthGrade", 99);		//필요 권한등급
		commonService.authChk((StringBuilder)request.getAttribute("IN_LOG_STR"), request, inData);
		
		MultipartFile image = request.getFile("image");
		Map<String, Object> outData = enterpriseService.insertEnterprise((StringBuilder)request.getAttribute("IN_LOG_STR"), inData, image);
		
		Gson gson = new Gson();
		String json = gson.toJson(outData);
		response.getWriter().print(json);	//결과 json형태로 담아서 보내기
		response.setContentType("application/x-json; charset=UTF-8");
	}
	
	/**
	* @메소드명: updateEnterprise
	* @작성자: KimSangMin
	* @생성일: 2023. 12. 19. 오전 10:56:32
	* @설명: 기업장터 수정
	*/
	@RequestMapping("/updateEnterprise.do")
	public void updateEnterprise(MultipartHttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> loginInfo = RequestUtil.getLoginInfo(request);
		inData.put("loginInfo", loginInfo);
		
		//권한 확인
		inData.put("url", url);				//메뉴 경로
		inData.put("isRange", true);		//권한등급이 정확히 일치해야 하는지
		inData.put("reqAuthGrade", 99);		//필요 권한등급
		commonService.authChk((StringBuilder)request.getAttribute("IN_LOG_STR"), request, inData);
		
		MultipartFile image = request.getFile("image");
		Map<String, Object> outData = enterpriseService.updateEnterprise((StringBuilder)request.getAttribute("IN_LOG_STR"), inData, image);
		
		Gson gson = new Gson();
		String json = gson.toJson(outData);
		response.getWriter().print(json);	//결과 json형태로 담아서 보내기
		response.setContentType("application/x-json; charset=UTF-8");
	}	
	
	/**
	* @메소드명: deleteEnterprise
	* @작성자: KimSangMin
	* @생성일: 2023. 12. 18. 오후 5:42:33
	* @설명: 기업장터 삭제
	 */
	@RequestMapping("/deleteEnterprise.do")
	public void deleteEnterprise(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> loginInfo = RequestUtil.getLoginInfo(request);
		inData.put("loginInfo", loginInfo);
		
		//권한 확인
		inData.put("url", url);				//메뉴 경로
		inData.put("isRange", true);		//권한등급이 정확히 일치해야 하는지
		inData.put("reqAuthGrade", 99);		//필요 권한등급
		commonService.authChk((StringBuilder)request.getAttribute("IN_LOG_STR"), request, inData);
		
		Map<String, Object> outData = enterpriseService.deleteEnterprise((StringBuilder)request.getAttribute("IN_LOG_STR"), inData);
		
		Gson gson = new Gson();
		String json = gson.toJson(outData);
		response.getWriter().print(json);	//결과 json형태로 담아서 보내기
		response.setContentType("application/x-json; charset=UTF-8");
	}	
}


	