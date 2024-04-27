package com.ksm.hpp.controller.board;

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
import com.ksm.hpp.service.board.FreeBoardService;
import com.ksm.hpp.service.com.CommonService;

@Controller
@RequestMapping("/freeBoard")
public class FreeBoardController extends BaseController {
	
	@Resource(name = "FreeBoardService")
	protected FreeBoardService freeBoardService;

	@Resource(name = "CommonService")
	protected CommonService commonService;
	
	String url = "board/freeBoard";
	
	/**
	* @메소드명: selectFreeBoard
	* @작성자: KimSangMin
	* @생성일: 2023. 11. 23. 오후 8:19:03
	* @설명: 자유게시판 조회
	*/
	@RequestMapping("/selectFreeBoard.do")
	public void selectFreeBoard(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> outData = freeBoardService.selectFreeBoard((StringBuilder)request.getAttribute("IN_LOG_STR"), inData);
		
		ResponseUtil.setResAuto(response, inData, outData);
	}	
	
	/**
	* @메소드명: insertFreeBoard
	* @작성자: KimSangMin
	* @생성일: 2023. 11. 25. 오후 5:02:30
	* @설명: 자유게시판 등록
	*/
	@RequestMapping("/insertFreeBoard.do")
	public void insertFreeBoard(MultipartHttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> loginInfo = RequestUtil.getLoginInfo(request);
		inData.put("loginInfo", loginInfo);
		
		//권한 확인
		inData.put("url", url);				//메뉴 경로
		inData.put("isRange", true);		//권한등급이 정확히 일치해야 하는지
		inData.put("reqAuthGrade", 2);		//필요 권한등급
		commonService.authChk((StringBuilder)request.getAttribute("IN_LOG_STR"), request, inData);
		
		List<MultipartFile> fileList = request.getFiles("files"); 
		Map<String, Object> outData = freeBoardService.insertFreeBoard((StringBuilder)request.getAttribute("IN_LOG_STR"), inData, fileList);
		
		Gson gson = new Gson();
		String json = gson.toJson(outData);
		response.getWriter().print(json);	//결과 json형태로 담아서 보내기
		response.setContentType("application/x-json; charset=UTF-8");
	}
	
	/**
	* @메소드명: updateFreeBoard
	* @작성자: KimSangMin
	* @생성일: 2023. 11. 28. 오후 7:49:41
	* @설명: 자유게시판 수정
	 */
	@RequestMapping("/updateFreeBoard.do")
	public void updateFreeBoard(MultipartHttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> loginInfo = RequestUtil.getLoginInfo(request);
		inData.put("loginInfo", loginInfo);
		
		//권한 확인
		inData.put("url", url);				//메뉴 경로
		inData.put("isRange", true);		//권한등급이 정확히 일치해야 하는지
		inData.put("reqAuthGrade", 2);		//필요 권한등급
		commonService.authChk((StringBuilder)request.getAttribute("IN_LOG_STR"), request, inData);
		
		List<MultipartFile> fileList = request.getFiles("files"); 
		Map<String, Object> outData = freeBoardService.updateFreeBoard((StringBuilder)request.getAttribute("IN_LOG_STR"), inData, fileList);
		
		Gson gson = new Gson();
		String json = gson.toJson(outData);
		response.getWriter().print(json);	//결과 json형태로 담아서 보내기
		response.setContentType("application/x-json; charset=UTF-8");
	}
	
	/**
	* @메소드명: deleteFreeBoard
	* @작성자: KimSangMin
	* @생성일: 2023. 11. 28. 오후 8:28:02
	* @설명: 자유게시판 삭제
	*/
	@RequestMapping("/deleteFreeBoard.do")
	public void deleteFreeBoard(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> loginInfo = RequestUtil.getLoginInfo(request);
		inData.put("loginInfo", loginInfo);
		
		//권한 확인
		inData.put("url", url);				//메뉴 경로
		inData.put("isRange", true);		//권한등급이 정확히 일치해야 하는지
		inData.put("reqAuthGrade", 2);		//필요 권한등급
		commonService.authChk((StringBuilder)request.getAttribute("IN_LOG_STR"), request, inData);
		
		Map<String, Object> outData = freeBoardService.deleteFreeBoard((StringBuilder)request.getAttribute("IN_LOG_STR"), inData);
		
		Gson gson = new Gson();
		String json = gson.toJson(outData);
		response.getWriter().print(json);	//결과 json형태로 담아서 보내기
		response.setContentType("application/x-json; charset=UTF-8");
	}	
	
	/**
	* @메소드명: increaseHit
	* @작성자: KimSangMin
	* @생성일: 2023. 11. 28. 오후 9:31:07
	* @설명: 자유게시판 조회수 증가
	*/
	@RequestMapping("/increaseHit.do")
	public void increaseHit(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> loginInfo = RequestUtil.getLoginInfo(request);
		inData.put("loginInfo", loginInfo);
		
		Map<String, Object> outData = freeBoardService.increaseHit((StringBuilder)request.getAttribute("IN_LOG_STR"), inData);
		
		Gson gson = new Gson();
		String json = gson.toJson(outData);
		response.getWriter().print(json);	//결과 json형태로 담아서 보내기
		response.setContentType("application/x-json; charset=UTF-8");
	}	
}


	