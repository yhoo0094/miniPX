package com.ksm.hpp.controller.com;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ksm.hpp.framework.util.Constant;
import com.ksm.hpp.framework.util.RequestUtil;
import com.ksm.hpp.service.com.CommonService;
import com.ksm.hpp.service.info.NoticeService;

@Controller
public class PageController {

	@Resource(name = "CommonService")
	protected CommonService commonService;	
	
	@Resource(name = "NoticeService")
	protected NoticeService noticeService;
	
	/**
	* @메소드명: handleAwsRequest
	* @작성자: KimSangMin
	* @생성일: 2023. 10. 26. 오후 4:14:18
	* @설명: AWS 대상 그룹 상태 검사 API
	*/
	@RequestMapping("/aws")
    public ResponseEntity<String> handleAwsRequest() {
        return new ResponseEntity<>("GET 요청이 성공적으로 처리되었습니다.", HttpStatus.OK);
    }
	
	/**
	 * @throws IOException 
	 * @메소드명: home
	 * @작성자: 김상민
	 * @생성일: 2022. 10. 19. 오전 7:46:01
	 * @설명: 루트에서 홈페이지 이동
	 */
	@RequestMapping("/")
	public void root(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.sendRedirect("/home");
	}

	
	/**
	* @메소드명: home
	* @작성자: KimSangMin
	* @생성일: 2023. 1. 18. 오후 3:52:21
	* @설명: 홈페이지 이동
	 */
	@RequestMapping("/home")
	public String home(HttpServletRequest request, HttpServletResponse response) {
		return "com/home";
	}
	
	/**
	* @메소드명: login
	* @작성자: KimSangMin
	* @생성일: 2024. 4. 27. 오후 7:26:47
	* @설명: 로그인 페이지 이동
	 */
	@RequestMapping("/login")
	public String login(HttpServletRequest request, HttpServletResponse response) {
		return "com/login";
	}	
	
	/**
	 * @메소드명: pageController
	 * @작성자: 김상민
	 * @생성일: 2022. 10. 23. 오후 2:45:11
	 * @설명: 페이지 이동 관리 컨트롤러
	 * @throws Exception
	 */
	@RequestMapping("/{url1:^(?!error).*$}/{url2:^(?!.*\\.do$).*$}")
	public ModelAndView pageController(HttpServletRequest request, HttpServletResponse response, @PathVariable("url1") String url1, @PathVariable("url2") String url2) throws Exception {
		ModelAndView result = new ModelAndView();
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> loginInfo = RequestUtil.getLoginInfo(request);
		inData.put("loginInfo", loginInfo);

		do {
			//에러 발생해서 인터셉터에서 에러 페이지로 보낼 때
			if(request.getAttribute("errorPage") != null) {
				result.setViewName((String) request.getAttribute("errorPage"));
				break;
			}
		
			//읽기 권한검사
			inData.put("url", url1 + "/" + url2);
			Map<String, Object> readAuth = commonService.readAuthChk((StringBuilder)request.getAttribute("IN_LOG_STR"), request, inData);
			if(!(Boolean)readAuth.get("result")) {	
				//권한이 없을 경우
				result.setViewName("error/noAuth");
			} else {			
				//최종 권한 확인 성공한 경우
				Map<String, Object> mnuInfo = commonService.selectMnuInfo((StringBuilder)request.getAttribute("IN_LOG_STR"), inData);	//메뉴 정보 확인
				result.addObject("mnuNm", mnuInfo.get("mnuNm"));
				result.addObject("mnuUpperNm", mnuInfo.get("mnuUpperNm"));
				result.addObject("info", mnuInfo.get("info"));
				result.addObject("url", mnuInfo.get("url"));
				result.addObject("topUrl", mnuInfo.get("topUrl"));
				result.addObject("upperUrl", mnuInfo.get("upperUrl"));
				result.addObject("authGrade", readAuth.get("authGrade"));
				result.setViewName(url1 + "/" + url2);
			}
		} while(false);
		return result;
	}	
	
	/**
	* @메소드명: pageControllerLv3
	* @작성자: KimSangMin
	* @생성일: 2023. 11. 23. 오후 8:54:49
	* @설명: 페이지 이동 관리 컨트롤러(3개의 세그먼트)
	* @throws Exception
	 */
	@RequestMapping("/{url1:^(?!error|resources).*$}/{url2}/{url3:^(?!.*\\.css$).*$}")
	public ModelAndView pageControllerLv3(HttpServletRequest request, HttpServletResponse response, @PathVariable("url1") String url1, @PathVariable("url2") String url2, @PathVariable("url3") String url3) throws Exception {
		ModelAndView result = new ModelAndView();
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> loginInfo = RequestUtil.getLoginInfo(request);
		inData.put("loginInfo", loginInfo);

		do {
			//에러 발생해서 인터셉터에서 에러 페이지로 보낼 때
			if(request.getAttribute("errorPage") != null) {
				result.setViewName((String) request.getAttribute("errorPage"));
				break;
			}
		
			//읽기 권한검사
			inData.put("url", url1 + "/" + url2);
			Map<String, Object> readAuth = commonService.readAuthChk((StringBuilder)request.getAttribute("IN_LOG_STR"), request, inData);
			
			if(!(Boolean)readAuth.get("result")) {	
				//권한이 없을 경우
				result.setViewName("error/noAuth");
			} else if(url3.toLowerCase().indexOf("edit") != -1 && (int)readAuth.get("authGrade") < 2) {	
				//수정 페이지 이동인 경우 && 쓰기 권한이 없을 경우
				result.setViewName("error/noAuth");
			} else {			
				//최종 권한 확인 성공한 경우
				Map<String, Object> mnuInfo = commonService.selectMnuInfo((StringBuilder)request.getAttribute("IN_LOG_STR"), inData);	//메뉴 정보 확인
				result.addObject("mnuNm", mnuInfo.get("mnuNm"));
				result.addObject("mnuUpperNm", mnuInfo.get("mnuUpperNm"));
				result.addObject("info", mnuInfo.get("info"));
				result.addObject("url", mnuInfo.get("url"));
				result.addObject("topUrl", mnuInfo.get("topUrl"));
				result.addObject("upperUrl", mnuInfo.get("upperUrl"));
				result.addObject("authGrade", readAuth.get("authGrade"));
				Object param = (inData.get("param") != null)? inData.get("param") : ""; 
				result.addObject("param", param);
				result.setViewName(url1 + "/" + url3);
			}
		} while(false);
		return result;
	}	
	
	/**
	* @메소드명: popupController
	* @작성자: KimSangMin
	* @생성일: 2023. 11. 21. 오후 6:54:36
	* @설명:
	 */
	@RequestMapping("/popup.do")
	public ModelAndView popupController(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView();
		
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		
		result.setViewName("popup/" + (String) inData.get("view_nm"));
		result.addObject("param", inData);
		
		return result;
	}		
}
