package com.ksm.hpp.controller.com;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.Gson;
import com.ksm.hpp.framework.exception.ConfigurationException;
import com.ksm.hpp.framework.util.RequestUtil;
import com.ksm.hpp.service.com.FileService;

@Controller
@RequestMapping("/file")
public class FileController {
	
	@Resource(name = "FileService")
	protected FileService fileService;
	
	/**
	 * @메소드명: selectFile
	 * @작성자: 김상민
	 * @생성일: 2022. 12. 27. 오후 5:27:09
	 * @설명: 파일목록 조회
	 */
	@RequestMapping("/selectFile")
	public void selectFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> outData = fileService.selectFile((StringBuilder)request.getAttribute("IN_LOG_STR"), inData);
		
		Gson gson = new Gson();
		String json = gson.toJson(outData);
		response.getWriter().print(json);	//결과 json형태로 담아서 보내기
		response.setContentType("application/x-json; charset=UTF-8");
	}
	
	/**
	 * @메소드명: downloadFile
	 * @작성자: 김상민
	 * @생성일: 2022. 12. 27. 오후 5:27:02
	 * @설명: 파일 다운로드
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/downloadFile")
	public void downloadFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> outData = fileService.downloadFile((StringBuilder)request.getAttribute("IN_LOG_STR"), inData);
		
		Map<String, Object> fileInfo = new HashMap<String, Object>();
		fileInfo = (Map<String, Object>) outData.get("fileInfo");
		
		String atcFilePath = (String) fileInfo.get("atcFilePath");
		String saveAtcFileNm = (String) fileInfo.get("saveAtcFileNm");
		String atcFileNm = (String) fileInfo.get("atcFileNm");
		File file = new File(atcFilePath + saveAtcFileNm);
		if(file == null || !file.exists() || !file.isFile()) {
			//파일이 null이거나, 파일이 존재하지 않거나, 형식이 파일이 아니면 
			throw new ConfigurationException("해당 파일이 서버에 존재하지 않습니다.");
		}
		
		//신규 참고 소스
		//User-Agent : 어떤 운영체제로 어떤 브라우저를 통해 서버(홈페이지)에 접근하는지 확인함
		String strClient = request.getHeader("User-Agent");	
		String fileName;
		if ((strClient.contains("MSIE")) || (strClient.contains("Trident")) || (strClient.contains("Edge"))) {	
			//인터넷 익스플로러 10이하 버전, 11버전, 엣지에서 인코딩		
			fileName = URLEncoder.encode(atcFileNm, "UTF-8");
		} else {
		    //나머지 브라우저에서 인코딩
		    fileName = new String(atcFileNm.getBytes("UTF-8"), "iso-8859-1");			
		}
		//형식을 모르는 파일첨부용 contentType
		response.setContentType("application/octet-stream");	
		//다운로드와 다운로드될 파일이름		
		response.setHeader("Content-Disposition", "attachment; filename=\""+ fileName + "\"");

		//파일복사
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
		FileCopyUtils.copy(in, response.getOutputStream());
		in.close();
		response.getOutputStream().flush();
		response.getOutputStream().close();	
		
		/* 
		//기존 프레임워크 소스
		String strClient = request.getHeader("User-Agent");	
		if(strClient.indexOf("MSIE 5.5") != -1) {
			response.setHeader("Content-Disposition", "filename=" + ATC_FILE_NM + ";");
		} else {
			response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(ATC_FILE_NM, "utf-8"));
			response.setHeader("Content-Type", "application/octet-stream; charset=UTF-8");
		}
		response.setHeader("Content-Length", "" + file.length());
		response.setHeader("Content-Transfer-Encoding", "binary;");
		response.setHeader("Pragma", "no-cache;");
		response.setHeader("Expires", "-1;");
		
		InputStream fi = new FileInputStream(file);
		OutputStream fo = response.getOutputStream();
		
		int read = 0;
		byte[] bytes = new byte[4096];
		
		while((read = fi.read(bytes)) != -1) {
			fo.write(bytes, 0, read);
		}
		fo.flush();
		
		if(fi != null) {fi.close();};
		if(fo != null) {fo.close();};
		*/
	}	
}
