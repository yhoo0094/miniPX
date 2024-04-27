package com.ksm.hpp.controller.com;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.google.gson.Gson;
import com.ksm.hpp.framework.util.Configuration;
import com.ksm.hpp.framework.util.OSValidator;
import com.ksm.hpp.framework.util.OS_Type;
import com.ksm.hpp.framework.util.RequestUtil;
import com.ksm.hpp.framework.util.ResponseUtil;
import com.ksm.hpp.service.com.CommonService;

@Controller
@RequestMapping("/common")
public class CommonController {
	
	@Resource(name = "CommonService")
	protected CommonService commonService;

	/**
	* @메소드명: ckUploadImage
	* @작성자: KimSangMin
	* @생성일: 2023. 1. 26. 오후 5:47:15
	* @설명: CK에디터 이미지 업로드
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/ckUploadImage.do")
	public void ckUploadImage(MultipartHttpServletRequest request, HttpServletResponse response) throws Exception {
		JSONObject jsonObject = new JSONObject();
	    PrintWriter printWriter = null;
		
		String originalImageName = ""; //원본이름
	    String imageName = ""; //저장본이름
	    String extension = ""; //확장자

	    MultipartFile file = request.getFile("upload");
        originalImageName = file.getOriginalFilename();
        extension = FilenameUtils.getExtension(originalImageName);
        imageName = "img_" + UUID.randomUUID() + "." + extension;	  
        
        StringBuffer tmp = new StringBuffer();
        
        //파일 저장 경로 만들기 (추후 구현)

		//현재 날짜 구하기
		LocalDate date = LocalDate.now();
		DateTimeFormatter dtfDate = DateTimeFormatter.ofPattern("yyyyMMdd");
		String nowDate = date.format(dtfDate);	
		
		OS_Type os = OSValidator.getOS();	//OS타입 구하기(UNKNOWN(0), WINDOWS(1), LINUX(2), MAC(3), SOLARIS(4))
		Configuration conf = new Configuration();
		String filePath = new String(conf.getString("Global." + os + ".getComEditorImagePath").getBytes("ISO-8859-1"), "UTF-8") + nowDate;	
		
		File dir = new File(filePath);
		if(!dir.isDirectory()) {	//해당 경로가 디렉토리인지 확인
			if(!dir.exists()) {		//해당 경로 디렉토리가 있는지 확인
				dir.mkdirs();		//해당 디렉토리가 없으면 생성
			}
		}		
		
        File imageUpload = new File(filePath + "/" + imageName);
        file.transferTo(imageUpload);
	    
        //jsonObject.put("url", "/resources/images/editor/" + nowDate + "/" + imageName);
        jsonObject.put("url", "/common/editor/" + nowDate + "/" + imageName);
        
        //리턴 response 작성
//        printWriter = response.getWriter();
//        response.setContentType("text/html");
//        printWriter.println(jsonObject);
        
		Gson gson = new Gson();
		String json = gson.toJson(jsonObject);
		response.getWriter().print(json);	//결과 json형태로 담아서 보내기
		response.setContentType("text/html");
	}	
	
	/**
	* @메소드명: showImage
	* @작성자: KimSangMin
	* @생성일: 2023. 1. 30. 오전 9:57:43
	* @설명: 에디터 이미지 조회
	 */
	@ResponseBody
	@GetMapping("/{dir1}/{dir2}/{filename}.{extension}")
	public org.springframework.core.io.Resource showImage(@PathVariable String dir1, @PathVariable String dir2, @PathVariable String filename, @PathVariable String extension) throws Exception {
		OS_Type os = OSValidator.getOS();	//OS타입 구하기(UNKNOWN(0), WINDOWS(1), LINUX(2), MAC(3), SOLARIS(4))
		Configuration conf = new Configuration();
		String filePath = new String(conf.getString("Global." + os + ".getComNasPath").getBytes("ISO-8859-1"), "UTF-8");
		
	 	return new UrlResource("file:///" + filePath + dir1 + "/" + dir2 + "/" + filename + "." + extension);
	 }	
	
	/**
	* @메소드명: selectCodeList
	* @작성자: KimSangMin
	* @생성일: 2023. 11. 24. 오후 9:40:49
	* @설명: 공통 코드 조회
	 */
	@RequestMapping("/selectCodeList.do")
	public void selectCodeList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> outData = commonService.selectCodeList((StringBuilder)request.getAttribute("IN_LOG_STR"), inData);
		
		ResponseUtil.setResAuto(response, inData, outData);
	}
	
	/**
	* @메소드명: selectSideBarList
	* @작성자: KimSangMin
	* @생성일: 2023. 11. 30. 오전 8:35:49
	* @설명: 사이드바 메뉴 목록 조회
	*/
	@RequestMapping("/selectSideBarList.do")
	public void selectSideBarList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> outData = commonService.selectSideBarList((StringBuilder)request.getAttribute("IN_LOG_STR"), inData);
		
		ResponseUtil.setResAuto(response, inData, outData);
	}
	
	/**
	* @메소드명: selectNavMnuList
	* @작성자: KimSangMin
	* @생성일: 2023. 12. 4. 오후 4:42:32
	* @설명: 네비게이션 메뉴 목록 조회
	*/
	@RequestMapping("/selectNavMnuList.do")
	public void selectNavMnuList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> outData = commonService.selectNavMnuList((StringBuilder)request.getAttribute("IN_LOG_STR"), inData);
		
		ResponseUtil.setResAuto(response, inData, outData);
	}
	
	/**
	* @메소드명: getImage
	* @작성자: KimSangMin
	* @생성일: 2023. 12. 6. 오후 7:58:27
	* @설명: 이미지 조회
	 */
	@RequestMapping("/getImage.do")
	public ResponseEntity<byte[]> getImage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> inData = RequestUtil.getParameterMap(request);
		Map<String, Object> outData = commonService.getImage((StringBuilder)request.getAttribute("IN_LOG_STR"), inData);
		
		String atcFilePath = (String) outData.get("atcFilePath");
		String saveAtcFileNm = (String) outData.get("saveAtcFileNm");
		
        // NAS 경로에서 이미지 파일을 읽어옵니다.
        File imageFile = new File(atcFilePath + saveAtcFileNm);
        
        // 이미지 파일을 byte 배열로 읽어옵니다.
        byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
        
        // HTTP 응답으로 이미지를 전송합니다.
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG); // 이미지 타입에 맞게 설정
        
        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }
	
	/**
	* @메소드명: apitest
	* @작성자: KimSangMin
	* @생성일: 2023. 11. 3. 오후 5:55:29
	* @설명: 예방접종 정보를 가져오기 위한 api 테스트(form 기반)
	*/
	@RequestMapping("/apitest.do")
	public void apitest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// HttpClient 초기화
        //HttpClient httpClient = HttpClients.createDefault();
        
        HttpClient httpClient = HttpClients.custom().setRedirectStrategy(new LaxRedirectStrategy()).build();
        HttpPost httpPost = new HttpPost("https://nip.kdca.go.kr/irhp/mngm/goBthVcnSchedule.do");

        try {
            // 폼 데이터 설정
            List<NameValuePair> formData = new ArrayList<>();
            formData.add(new BasicNameValuePair("menuLv", "3"));     
            formData.add(new BasicNameValuePair("menuCd", "322"));     
            formData.add(new BasicNameValuePair("menuSection", "3"));     
            formData.add(new BasicNameValuePair("_csrf", "afe6b4b9-4637-43f5-b9c7-95f5fbda8715"));    
            formData.add(new BasicNameValuePair("relBirdte", "20231103"));     
            formData.add(new BasicNameValuePair("bthYear", "2023"));     
            formData.add(new BasicNameValuePair("bthMonth", "11"));     
            formData.add(new BasicNameValuePair("bthDay", "03"));     
            formData.add(new BasicNameValuePair("_csrf", "afe6b4b9-4637-43f5-b9c7-95f5fbda8715"));     
            
            // 더 많은 폼 데이터 필드를 추가할 수 있습니다.

            // 폼 데이터를 HTTP 요청 엔티티로 변환
            HttpEntity entity = new UrlEncodedFormEntity(formData);

            // HTTP POST 요청 엔티티 설정
            httpPost.setEntity(entity);

            // HTTP 요청 실행 및 응답 가져오기
            HttpResponse response1 = httpClient.execute(httpPost);

            // 응답 엔티티에서 데이터 추출
            HttpEntity responseEntity = response1.getEntity();
            String responseBody = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8); // 문자 인코딩 지정

            // 화면 정보를 responseBody 변수에 저장하고 활용
            System.out.println(responseBody);

        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	/**
	* @메소드명: apitest2
	* @작성자: KimSangMin
	* @생성일: 2023. 11. 3. 오후 5:56:01
	* @설명: 예방접종 정보를 가져오기 위한 테스트2
	*/
	@RequestMapping("/apitest2.do")
	public void apitest2(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
            // 웹 사이트 URL 설정
            String url = "https://nip.kdca.go.kr/irhp/mngm/goVcntMngm.do?menuLv=3&menuCd=322&menuSection=3";

            // Jsoup을 사용하여 웹 페이지에 연결
            Document doc = Jsoup.connect(url).get();
            System.out.println("Text: " + doc);

            // 원하는 요소를 선택하기 위한 CSS 선택자를 사용하여 데이터 추출
            Elements elements = doc.select(".class-name"); // 클래스 이름을 사용한 예시

            // 선택한 요소에서 데이터 추출 및 출력
            for (Element element : elements) {
                System.out.println("Text: " + element.text());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
}
