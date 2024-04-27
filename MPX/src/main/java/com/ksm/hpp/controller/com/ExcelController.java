package com.ksm.hpp.controller.com;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.google.gson.Gson;
import com.ksm.hpp.framework.exception.ConfigurationException;
import com.ksm.hpp.framework.util.Constant;
import com.ksm.hpp.framework.util.RequestUtil;
import com.ksm.hpp.service.com.CommonService;

@Controller
@RequestMapping("/excel")
public class ExcelController {
	
	@Resource(name = "CommonService")
	protected CommonService commonService;
	
	/**
	 * @메소드명: downloadData
	 * @작성자: 김상민
	 * @생성일: 2023. 1. 13. 오후 1:40:53
	 * @설명: 엑셀 다운로드
	 */
	@RequestMapping("/downloadData.do")
	public void downloadData(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		XSSFWorkbook workbook = createExcel(inData);
		
		String fileName = (String) inData.get(Constant.EXCEL_FILENM);
		String downloadFileName = URLEncoder.encode(fileName + ".xlsx", "UTF-8") + "\";";
		response.setHeader("Content-Disposition", "attachment; filename=\"" + downloadFileName.replaceAll("\\+", "%20"));
		response.setContentType("application.octet-stream; charset=UTF-8");
		
		ServletOutputStream sos = response.getOutputStream();
		workbook.write(sos);
		workbook.close();
		sos.close();
	}
	
	/**
	* @메소드명: createExcel
	* @작성자: KimSangMin
	* @생성일: 2023. 1. 31. 오전 10:41:13
	* @설명: 엑셀 파일 생성
	 */
	@SuppressWarnings("unchecked")
	public static XSSFWorkbook createExcel(Map<String, Object> inData) {
		String sheetName = (String) inData.get(Constant.EXCEL_SHEETNM);
		ArrayList<Map<String, Object>> columns = (ArrayList<Map<String, Object>>) inData.get(Constant.EXCEL_COLUMN);
		ArrayList<Map<String, Object>> datas = (ArrayList<Map<String, Object>>) inData.get(Constant.EXCEL_DATA);
		
		//엑셀 관련 변수 정의
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet((String) inData.get(Constant.EXCEL_SHEETNM));    // sheet 생성
		XSSFRow row = sheet.createRow(0);
		XSSFCell cell = row.createCell(0);
		int rowCnt = 1;		//행 위치(첫 행은 비우기 위해 1부터 시작)
		int cellCnt = 1;	//열 위치(첫 행은 비우기 위해 1부터 시작, 행 바뀔 때마다 1로 초기화할 것)
		int colSize = columns.size();	//열 개수
		int dataSize = datas.size();	//행 개수
		List<XSSFCell> cellArr = new ArrayList<XSSFCell>();	//셀 저장을 위한 배열(행 생성할 때 초기화)
		
		//셀 스타일 생성
		XSSFCellStyle titleStyle = papangTitleStyle(workbook);		//제목 스타일
		XSSFCellStyle headerStyle = papangHeaderStyle(workbook);	//헤더 스타일
		XSSFCellStyle cellStyle = papangCellStyle(workbook);		//일반 셀 스타일
		
		//시트 상단에 제목 입력
		sheet.addMergedRegion(new CellRangeAddress(rowCnt, rowCnt, cellCnt, cellCnt + columns.size() -1));	//시작행, 끝행, 시작열, 끝열
		row = sheet.createRow(rowCnt++);
		
		for(int i = 0; i < colSize; i++) {
			cell = row.createCell(cellCnt++);
			cell.setCellStyle(titleStyle);
			cellArr.add(cell);
		}
		cellArr.get(0).setCellValue(sheetName);
		
		//헤더 입력
		rowCnt++;	//제목이랑 헤더 사이에 한 줄 띄우기
		row = sheet.createRow(rowCnt++);
		cellCnt = 1;
		for(int i = 0; i < colSize; i++) {
			cell = row.createCell(cellCnt++);
			cell.setCellStyle(headerStyle);
			cell.setCellValue((String)columns.get(i).get("title"));
		}
		
		//데이터 입력
		String dataKey;
		for(int i = 0; i < dataSize; i++) {
			row = sheet.createRow(rowCnt++);
			cellCnt = 1;
			for(int k = 0; k < colSize; k++) {
				cell = row.createCell(cellCnt++);
				cell.setCellStyle(cellStyle);
				dataKey = (String)columns.get(k).get("data");
				cell.setCellValue((String)datas.get(i).get(dataKey));
			}
		}	
		
		return workbook;
	}
	
	/**
	* @메소드명: upload
	* @작성자: KimSangMin
	* @생성일: 2023. 1. 17. 오후 5:29:59
	* @설명: 엑셀 업로드
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/upload.do")
	public void upload(MultipartHttpServletRequest request, HttpServletResponse response) throws Exception {	
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> outData = new HashMap<String, Object>();
		List<Map<String, Object>> excelUploadOptionList = (List<Map<String, Object>>) inData.get("EXCEL_UPLOAD_OPTION");
		MultipartFile file = request.getFile("file");
		
		String extension = FilenameUtils.getExtension(file.getOriginalFilename());
	    Workbook workbook = null;
	    if (extension.equals("xlsx")) {
	    	workbook = new XSSFWorkbook(file.getInputStream());
	    } else if (extension.equals("xls")) {
	    	workbook = new HSSFWorkbook(file.getInputStream());
	    } else {
	    	throw new ConfigurationException(extension + "은 지원하지 않는 확장자입니다.\n xlsx 혹은 xls 확장자를 사용해 주십시오.");
	    }
	    int sheetsCnt = workbook.getNumberOfSheets();
	    
	    //sheetList에 데이터 담기
	    List<List<Map<String, String>>> sheetList = new ArrayList<List<Map<String,String>>>();	//전체 시트 리스트
	    for(int i = 0; i < sheetsCnt; i++) {	//시트 수만큼 반복
	    	Map<String, Object> excelUploadOption = excelUploadOptionList.get(i);
	    	Sheet worksheet = workbook.getSheetAt(i);
	    	int rowOffset = (Integer) excelUploadOption.get("rowOffset");
	    	int colOffset = (Integer) excelUploadOption.get("colOffset");
	    	List<Map<String, Object>> colOptions = (List<Map<String, Object>>) excelUploadOption.get("colOptions"); 
	    	
	    	List<Map<String, String>> rowList = new ArrayList<Map<String,String>>();	//전체 행 리스트
	    	for (int k = 0 + rowOffset; k < worksheet.getPhysicalNumberOfRows() - rowOffset; k++) {		//행 수만큼 반복
		        Row row = worksheet.getRow(k);
		        int cellCnt = colOptions.size();
		        
		        Map<String, String> colMap = new HashMap<String, String>();		//한 행에 속한 컬럼 맵
		        for(int j = 0 + colOffset; j < cellCnt + colOffset; j++) {
		        	String colKey = (String) colOptions.get(j - colOffset).get("data");
		        	Cell cell = row.getCell(j);
		        	colMap.put(colKey, cell.getStringCellValue());
		        }
		        rowList.add(colMap);
		    }
	    	sheetList.add(rowList);
	    }
	    outData.put(Constant.RESULT, Constant.RESULT_SUCCESS);	  
	    outData.put(Constant.OUT_DATA, sheetList);	    
	    
		Gson gson = new Gson();
		String json = gson.toJson(outData);
		response.getWriter().print(json);	//결과 json형태로 담아서 보내기
		response.setContentType("application/x-json; charset=UTF-8");	    
	}
	
	/**
	 * @메소드명: papangTitleStyle
	 * @작성자: 김상민
	 * @생성일: 2023. 1. 13. 오후 1:41:05
	 * @설명: 엑셀 제목 스타일 지정
	 */
	private static XSSFCellStyle papangTitleStyle(XSSFWorkbook workbook) {
		XSSFCellStyle result = workbook.createCellStyle();
		result.setAlignment(HorizontalAlignment.CENTER);		//가운데 정렬
		result.setVerticalAlignment(VerticalAlignment.CENTER);	//세로 가운데 정렬
		result.setWrapText(true);	//true: 여러 줄에 표시하여 셀 내에서 모든 콘텐츠를 볼 수 있도록 함
		
		//폰트 설정
		Font font = new XSSFFont();
		font.setBold(true);
		result.setFont(font);
		
		//테두리
		BorderStyle borderStyle = BorderStyle.MEDIUM;
		result.setBorderTop(borderStyle);
		result.setBorderBottom(borderStyle);				
		result.setBorderLeft(borderStyle);
		result.setBorderRight(borderStyle);
		result.setBorderColor(XSSFCellBorder.BorderSide.BOTTOM , new XSSFColor(new byte[] {(byte) 0,(byte) 0,(byte) 0}, null));
		
		return result;
	}
	
	/**
	 * @메소드명: papangHeaderStyle
	 * @작성자: 김상민
	 * @생성일: 2023. 1. 13. 오후 1:41:05
	 * @설명: 엑셀 헤더 스타일 지정
	 */
	private static XSSFCellStyle papangHeaderStyle(XSSFWorkbook workbook) {
		XSSFCellStyle result = workbook.createCellStyle();
		result.setAlignment(HorizontalAlignment.CENTER);		//가운데 정렬
		result.setVerticalAlignment(VerticalAlignment.CENTER);	//세로 가운데 정렬
		result.setWrapText(false);	//true: 여러 줄에 표시하여 셀 내에서 모든 콘텐츠를 볼 수 있도록 함
		
		//폰트 설정
		Font font = new XSSFFont();
		font.setBold(true);
		result.setFont(font);
		
		//테두리
		BorderStyle borderStyle = BorderStyle.THIN;
		result.setBorderTop(borderStyle);
		result.setBorderBottom(borderStyle);				
		result.setBorderLeft(borderStyle);
		result.setBorderRight(borderStyle);
		result.setBorderColor(XSSFCellBorder.BorderSide.BOTTOM , new XSSFColor(new byte[] {(byte) 0,(byte) 0,(byte) 0}, null));
		
		return result;
	}	
	
	private static XSSFCellStyle papangCellStyle(XSSFWorkbook workbook) {
		XSSFCellStyle result = workbook.createCellStyle();
		result.setAlignment(HorizontalAlignment.CENTER);		//가운데 정렬
		result.setVerticalAlignment(VerticalAlignment.CENTER);	//세로 가운데 정렬
		result.setWrapText(false);	//true: 여러 줄에 표시하여 셀 내에서 모든 콘텐츠를 볼 수 있도록 함
		
		//폰트 설정
		Font font = new XSSFFont();
		font.setBold(true);
		result.setFont(font);
		
		//테두리
		BorderStyle borderStyle = BorderStyle.THIN;
		result.setBorderTop(borderStyle);
		result.setBorderBottom(borderStyle);				
		result.setBorderLeft(borderStyle);
		result.setBorderRight(borderStyle);
		result.setBorderColor(XSSFCellBorder.BorderSide.BOTTOM , new XSSFColor(new byte[] {(byte) 0,(byte) 0,(byte) 0}, null));
		
		return result;
	}		
	
}
