package com.ksm.hpp.framework.util;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.bcel.Const;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ksm.hpp.controller.com.BaseController;
import com.ksm.hpp.controller.com.ExcelController;
import com.ksm.hpp.framework.exception.ConfigurationException;

//Controller.java에서 사용하는 Response관련 유틸
public class ResponseUtil {
	
	protected static final Log log = LogFactory.getLog(BaseController.class);
	
	/**
	 * @메소드명: setResAuto
	 * @작성자: 김상민
	 * @생성일: 2023. 1. 13. 오후 12:50:07
	 * @설명: 컨트롤러에서 클라이언트로 넘어가는 response 객체를 설정하고 출력
	 */
	public static void setResAuto(HttpServletResponse response, Map<String, Object> inData, Map<String, Object> outData) throws Exception {
		if(inData.containsKey(Constant.EXCEL_FILENM)) {
			//엑셀 다운로드 요청인 경우
			inData.put(Constant.EXCEL_DATA, outData.get(Constant.OUT_DATA));
			XSSFWorkbook workbook = ExcelController.createExcel(inData);
		
			String fileName = (String) inData.get(Constant.EXCEL_FILENM);
			String downloadFileName = URLEncoder.encode(fileName + ".xlsx", "UTF-8") + "\";";
			response.setHeader("Content-Disposition", "attachment; filename=\"" + downloadFileName.replaceAll("\\+", "%20"));
			response.setContentType("application.octet-stream; charset=UTF-8");
			
			ServletOutputStream sos = response.getOutputStream();
			workbook.write(sos);
			workbook.close();
			sos.close();		
		} else {
			Gson gson = new Gson();
			String json = gson.toJson(outData);
			response.getWriter().print(json);	//결과 json형태로 담아서 보내기
			response.setContentType("application/x-json; charset=UTF-8");			
		}
	}

	/**
	* @메소드명: getExceptionResultMap
	* @작성자: KimSangMin
	* @생성일: 2023. 1. 25. 오후 2:39:09
	* @설명: Exception에 맞는 에러 메세지 반환하는 메서드
	*/
	public static Map<String, Object> getExceptionResultMap(Exception e) {
		Map<String, Object> result = null;
		
		if(e instanceof ConfigurationException) {
			result = new HashMap<String, Object>();
			result.put(Constant.RESULT, Constant.RESULT_FAILURE);
			result.put(Constant.OUT_RESULT_MSG, e.getMessage());
		} else {
			result = new HashMap<String, Object>();
			result.put(Constant.RESULT, Constant.RESULT_FAILURE);
			result.put(Constant.OUT_RESULT_MSG, "정의되지 않은 오류 발생");
		}
		
		return result;
	}
}



	