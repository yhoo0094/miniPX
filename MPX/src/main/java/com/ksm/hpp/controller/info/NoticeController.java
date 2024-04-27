package com.ksm.hpp.controller.info;

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
import com.ksm.hpp.service.com.CommonService;
import com.ksm.hpp.service.info.NoticeService;

@Controller
@RequestMapping("/notice")
public class NoticeController extends BaseController {
	
	@Resource(name = "NoticeService")
	protected NoticeService noticeService;

	@Resource(name = "CommonService")
	protected CommonService commonService;
	
	String url = "info/notice";
	
	/**
	 * @메소드명: selectNotice
	 * @작성자: 김상민
	 * @생성일: 2022. 12. 21. 오전 10:47:34
	 * @설명: 공지사항 조회
	 */
	@RequestMapping("/selectNotice.do")
	public void selectNotice(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> outData = noticeService.selectNotice((StringBuilder)request.getAttribute("IN_LOG_STR"), inData);
		
		ResponseUtil.setResAuto(response, inData, outData);
	}	
	
	/**
	 * @메소드명: insertNotice
	 * @작성자: 김상민
	 * @생성일: 2022. 11. 22. 오전 8:43:39
	 * @설명: 공지사항 등록
	 */
	@RequestMapping("/insertNotice.do")
	public void insertNotice(MultipartHttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> loginInfo = RequestUtil.getLoginInfo(request);
		inData.put("loginInfo", loginInfo);
		
		//권한 확인
		inData.put("url", url);				//메뉴 경로
		inData.put("isRange", true);		//권한등급이 정확히 일치해야 하는지
		inData.put("reqAuthGrade", 99);		//필요 권한등급
		commonService.authChk((StringBuilder)request.getAttribute("IN_LOG_STR"), request, inData);
		
		List<MultipartFile> fileList = request.getFiles("files"); 
		Map<String, Object> outData = noticeService.insertNotice((StringBuilder)request.getAttribute("IN_LOG_STR"), inData, fileList);
		
		Gson gson = new Gson();
		String json = gson.toJson(outData);
		response.getWriter().print(json);	//결과 json형태로 담아서 보내기
		response.setContentType("application/x-json; charset=UTF-8");
	}		
	
	/**
	 * @메소드명: updateNotice
	 * @작성자: 김상민
	 * @생성일: 2023. 1. 3. 오전 10:43:28
	 * @설명: 공지사항 수정
	 */
	@RequestMapping("/updateNotice.do")
	public void updateNotice(MultipartHttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> loginInfo = RequestUtil.getLoginInfo(request);
		inData.put("loginInfo", loginInfo);
		
		//권한 확인
		inData.put("url", url);				//메뉴 경로
		inData.put("isRange", true);		//권한등급이 정확히 일치해야 하는지
		inData.put("reqAuthGrade", 99);		//필요 권한등급
		commonService.authChk((StringBuilder)request.getAttribute("IN_LOG_STR"), request, inData);
		
		List<MultipartFile> fileList = request.getFiles("files"); 
		Map<String, Object> outData = noticeService.updateNotice((StringBuilder)request.getAttribute("IN_LOG_STR"), inData, fileList);
		
		Gson gson = new Gson();
		String json = gson.toJson(outData);
		response.getWriter().print(json);	//결과 json형태로 담아서 보내기
		response.setContentType("application/x-json; charset=UTF-8");
	}		
	
	/**
	 * @메소드명: deleteNotice
	 * @작성자: 김상민
	 * @생성일: 2023. 1. 4. 오전 11:45:20
	 * @설명: 게시글 삭제
	 */
	@RequestMapping("/deleteNotice.do")
	public void deleteNotice(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> loginInfo = RequestUtil.getLoginInfo(request);
		inData.put("loginInfo", loginInfo);
		
		//권한 확인
		inData.put("url", url);				//메뉴 경로
		inData.put("isRange", true);		//권한등급이 정확히 일치해야 하는지
		inData.put("reqAuthGrade", 99);		//필요 권한등급
		commonService.authChk((StringBuilder)request.getAttribute("IN_LOG_STR"), request, inData);
		
		Map<String, Object> outData = noticeService.deleteNotice((StringBuilder)request.getAttribute("IN_LOG_STR"), inData);
		ResponseUtil.setResAuto(response, inData, outData);
	}		
	
	/**
	* 
	* @메소드명: increaseHit
	* @작성자: KimSangMin
	* @생성일: 2023. 4. 26. 오후 6:38:51
	* @설명:
	*/
	@RequestMapping("/increaseHit.do")
	public void increaseHit(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> loginInfo = RequestUtil.getLoginInfo(request);
		inData.put("loginInfo", loginInfo);
		
		Map<String, Object> outData = noticeService.increaseHit((StringBuilder)request.getAttribute("IN_LOG_STR"), inData);
		
		Gson gson = new Gson();
		String json = gson.toJson(outData);
		response.getWriter().print(json);	//결과 json형태로 담아서 보내기
		response.setContentType("application/x-json; charset=UTF-8");
	}	
}


	