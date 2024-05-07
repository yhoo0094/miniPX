package com.ksm.hpp.controller.mart;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ksm.hpp.controller.com.BaseController;
import com.ksm.hpp.framework.util.RequestUtil;
import com.ksm.hpp.framework.util.ResponseUtil;
import com.ksm.hpp.service.com.CommonService;
import com.ksm.hpp.service.mart.MartService;

@Controller
@RequestMapping("/mart")
public class MartController extends BaseController {
	
	@Resource(name = "MartService")
	protected MartService martService;

	@Resource(name = "CommonService")
	protected CommonService commonService;
	
	String url = "mart/mart";
	
	/**
	* @메소드명: selectItemList
	* @작성자: KimSangMin
	* @생성일: 2024. 4. 30. 오후 3:45:45
	* @설명: 상품 리스트 조회
	 */
	@RequestMapping("/selectItemList.do")
	public void selectItemList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> outData = martService.selectItemList((StringBuilder)request.getAttribute("IN_LOG_STR"), inData);
		
		ResponseUtil.setResAuto(response, inData, outData);
	}	
	
	/**
	* @메소드명: selectTypeList
	* @작성자: KimSangMin
	* @생성일: 2024. 5. 2. 오후 4:26:16
	* @설명: 분류 목록 조회
	 */
	@RequestMapping("/selectTypeList.do")
	public void selectTypeList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> outData = martService.selectTypeList((StringBuilder)request.getAttribute("IN_LOG_STR"), inData);
		
		ResponseUtil.setResAuto(response, inData, outData);
	}		
	
	/**
	* @메소드명: selectDtlTypeList
	* @작성자: KimSangMin
	* @생성일: 2024. 5. 2. 오후 5:53:51
	* @설명: 상세 분류 목록 조회
	 */
	@RequestMapping("/selectDtlTypeList.do")
	public void selectDtlTypeList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> outData = martService.selectDtlTypeList((StringBuilder)request.getAttribute("IN_LOG_STR"), inData);
		
		ResponseUtil.setResAuto(response, inData, outData);
	}	
}


	