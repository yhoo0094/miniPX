package com.ksm.hpp.controller.user;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.Gson;
import com.ksm.hpp.controller.com.BaseController;
import com.ksm.hpp.framework.util.RequestUtil;
import com.ksm.hpp.service.user.UserService;

@Controller
@RequestMapping("/user")
public class UserController extends BaseController {
	
	@Resource(name = "UserService")
	protected UserService userService;
	
	/**
	 * @메소드명: insertUser
	 * @작성자: 김상민
	 * @생성일: 2022. 11. 2. 오후 6:59:00
	 * @설명: 사용자 생성
	 */
	@RequestMapping("/insertUser.do")
	public void insertUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> outData = userService.insertUser((StringBuilder)request.getAttribute("IN_LOG_STR"), inData);

		Gson gson = new Gson();
		String json = gson.toJson(outData);
		response.getWriter().print(json);	//결과 json형태로 담아서 보내기
		response.setContentType("application/x-json; charset=UTF-8");
	}	
	
	/**
	 * @메소드명: login
	 * @작성자: 김상민
	 * @생성일: 2022. 11. 8. 오전 8:58:31
	 * @설명: 로그인
	 */
	@RequestMapping("/login.do")
	public void login(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> outData = userService.login((StringBuilder)request.getAttribute("IN_LOG_STR"), request, inData);
		
		Gson gson = new Gson();
		String json = gson.toJson(outData);
		
		response.setContentType("application/x-json; charset=UTF-8");
		response.getWriter().print(json);	//결과 json형태로 담아서 보내기
	}
	
	/**
	 * @메소드명: logout
	 * @작성자: 김상민
	 * @생성일: 2023. 1. 9. 오후 1:25:02
	 * @설명: 로그아웃
	 */
	@RequestMapping("/logout.do")
	public void logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> outData = userService.logout((StringBuilder)request.getAttribute("IN_LOG_STR"), request, inData);
		
		Gson gson = new Gson();
		String json = gson.toJson(outData);
		
		response.setContentType("application/x-json; charset=UTF-8");
		response.getWriter().print(json);	//결과 json형태로 담아서 보내기		
	}
	
	/**
	 * @메소드명: chkUniqId
	 * @작성자: 김상민
	 * @생성일: 2023. 1. 5. 오전 9:37:27
	 * @설명: 아이디 중복 체크
	 */
	@RequestMapping("/chkUniqId.do")
	public void chkUniqId(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> outData = userService.chkUniqId((StringBuilder)request.getAttribute("IN_LOG_STR"), inData);

		Gson gson = new Gson();
		String json = gson.toJson(outData);
		response.getWriter().print(json);	//결과 json형태로 담아서 보내기
		response.setContentType("application/x-json; charset=UTF-8");
	}	
	
	/**
	* @메소드명: chngUserPw
	* @작성자: KimSangMin
	* @생성일: 2024. 1. 18. 오후 2:00:03
	* @설명: 비밀번호 변경
	 */
	@RequestMapping("/chngUserPw.do")
	public void chngUserPw(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> loginInfo = RequestUtil.getLoginInfo(request);
		inData.put("loginInfo", loginInfo);
		
		Map<String, Object> outData = userService.chngUserPw((StringBuilder)request.getAttribute("IN_LOG_STR"), inData);

		Gson gson = new Gson();
		String json = gson.toJson(outData);
		response.getWriter().print(json);	//결과 json형태로 담아서 보내기
		response.setContentType("application/x-json; charset=UTF-8");
	}		
}
