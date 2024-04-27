package com.ksm.hpp.controller.active;

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
import com.ksm.hpp.service.active.PlayService;
import com.ksm.hpp.service.com.CommonService;

@Controller
@RequestMapping("/play")
public class PlayController extends BaseController {
	
	@Resource(name = "PlayService")
	protected PlayService playService;

	@Resource(name = "CommonService")
	protected CommonService commonService;
	
	String url = "active/play";
	
	/**
	* @메소드명: selectPlay
	* @작성자: KimSangMin
	* @생성일: 2023. 12. 6. 오후 6:38:17
	* @설명: 놀이 조회
	*/
	@RequestMapping("/selectPlay.do")
	public void selectPlay(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> outData = playService.selectPlay((StringBuilder)request.getAttribute("IN_LOG_STR"), inData);
		
		ResponseUtil.setResAuto(response, inData, outData);
	}	
	
	/**
	* @메소드명: selectPlayHome
	* @작성자: KimSangMin
	* @생성일: 2023. 12. 12. 오후 5:53:47
	* @설명: 놀이 조회(홈 화면)
	*/
	@RequestMapping("/selectPlayHome.do")
	public void selectPlayHome(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> outData = playService.selectPlayHome((StringBuilder)request.getAttribute("IN_LOG_STR"), inData);
		
		ResponseUtil.setResAuto(response, inData, outData);
	}	
	
	/**
	* @메소드명: insertPlay
	* @작성자: KimSangMin
	* @생성일: 2023. 12. 5. 오후 8:48:43
	* @설명: 놀이 등록
	 */
	@RequestMapping("/insertPlay.do")
	public void insertPlay(MultipartHttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> loginInfo = RequestUtil.getLoginInfo(request);
		inData.put("loginInfo", loginInfo);
		
		//권한 확인
		inData.put("url", url);				//메뉴 경로
		inData.put("isRange", true);		//권한등급이 정확히 일치해야 하는지
		inData.put("reqAuthGrade", 99);		//필요 권한등급
		commonService.authChk((StringBuilder)request.getAttribute("IN_LOG_STR"), request, inData);
		
		List<MultipartFile> fileList = request.getFiles("files");
		Map<String, Object> outData = playService.insertPlay((StringBuilder)request.getAttribute("IN_LOG_STR"), inData, fileList);
		
		Gson gson = new Gson();
		String json = gson.toJson(outData);
		response.getWriter().print(json);	//결과 json형태로 담아서 보내기
		response.setContentType("application/x-json; charset=UTF-8");
	}
	
	/**
	* @메소드명: updatePlay
	* @작성자: KimSangMin
	* @생성일: 2023. 12. 11. 오후 5:12:04
	* @설명: 놀이 수정
	*/
	@RequestMapping("/updatePlay.do")
	public void updatePlay(MultipartHttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> loginInfo = RequestUtil.getLoginInfo(request);
		inData.put("loginInfo", loginInfo);
		
		//권한 확인
		inData.put("url", url);				//메뉴 경로
		inData.put("isRange", true);		//권한등급이 정확히 일치해야 하는지
		inData.put("reqAuthGrade", 99);		//필요 권한등급
		commonService.authChk((StringBuilder)request.getAttribute("IN_LOG_STR"), request, inData);
		
		List<MultipartFile> fileList = request.getFiles("files");
		Map<String, Object> outData = playService.updatePlay((StringBuilder)request.getAttribute("IN_LOG_STR"), inData, fileList);
		
		Gson gson = new Gson();
		String json = gson.toJson(outData);
		response.getWriter().print(json);	//결과 json형태로 담아서 보내기
		response.setContentType("application/x-json; charset=UTF-8");
	}	
	
	/**
	* @메소드명: deletePlay
	* @작성자: KimSangMin
	* @생성일: 2023. 12. 12. 오전 11:28:06
	* @설명: 놀이 삭제
	*/
	@RequestMapping("/deletePlay.do")
	public void deletePlay(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> loginInfo = RequestUtil.getLoginInfo(request);
		inData.put("loginInfo", loginInfo);
		
		//권한 확인
		inData.put("url", url);				//메뉴 경로
		inData.put("isRange", true);		//권한등급이 정확히 일치해야 하는지
		inData.put("reqAuthGrade", 99);		//필요 권한등급
		commonService.authChk((StringBuilder)request.getAttribute("IN_LOG_STR"), request, inData);
		
		Map<String, Object> outData = playService.deletePlay((StringBuilder)request.getAttribute("IN_LOG_STR"), inData);
		
		Gson gson = new Gson();
		String json = gson.toJson(outData);
		response.getWriter().print(json);	//결과 json형태로 담아서 보내기
		response.setContentType("application/x-json; charset=UTF-8");
	}	
}


	