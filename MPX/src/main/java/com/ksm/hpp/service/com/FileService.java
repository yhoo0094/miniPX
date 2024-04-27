package com.ksm.hpp.service.com;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.Response;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ksm.hpp.framework.exception.ConfigurationException;
import com.ksm.hpp.framework.util.Configuration;
import com.ksm.hpp.framework.util.Constant;
import com.ksm.hpp.framework.util.FileUtil;
import com.ksm.hpp.framework.util.OSValidator;
import com.ksm.hpp.framework.util.OS_Type;

@Service("FileService")
public class FileService extends BaseService {
	
	@Autowired
	SqlSession sqlSession; //SqlSession 빈 DI	
	
	List<String> excludedExtsList = Arrays.asList("exe");	//첨부파일 제외되는 확장자
	
	/**
	 * @메소드명: selectFile
	 * @작성자: 김상민
	 * @생성일: 2022. 12. 27. 오후 5:27:44
	 * @설명: 파일목록 조회
	 */
	public Map<String, Object> selectFile(StringBuilder logStr, Map<String, Object> inData) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		
		List<Object> list = sqlSession.selectList("mapper.com.FileMapper.selectFile", inData);
		result.put("list", list);
		
		return result;
	}

	/**
	 * @메소드명: downloadFile
	 * @작성자: 김상민
	 * @생성일: 2022. 12. 27. 오후 5:28:45
	 * @설명: 파일 다운로드
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> downloadFile(StringBuilder logStr, Map<String, Object> inData) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		
		List<Object> list = sqlSession.selectList("mapper.com.FileMapper.selectFile", inData);
		if(list.isEmpty()) {	//DB조회를 실패한 경우
			throw new ConfigurationException("해당 파일을 찾을 수 없습니다.");
		}
		
		Map<String, Object> fileInfo = new HashMap<String, Object>();
		fileInfo = (Map<String, Object>) list.get(0);

		result.put("fileInfo", fileInfo);
		return result;
	}	
	
	/**
	 * @메소드명: insertFile
	 * @작성자: 김상민
	 * @생성일: 2022. 12. 14. 오후 2:22:01
	 * @설명: 첨부파일 저장
	 */
	public Map<String, Object> insertFile(StringBuilder logStr, Map<String, Object> inData, List<MultipartFile> fileList) throws Exception{
		Map<String, Object> result = new HashMap<String, Object>();
		
		//현재 시간 구하기
		LocalTime time = LocalTime.now();
		DateTimeFormatter dtfTime = DateTimeFormatter.ofPattern("HHmmss");
		String nowTime = time.format(dtfTime);
		
		do {
			if(fileList == null) {
				result.put(Constant.RESULT, Constant.RESULT_FAILURE);
				result.put(Constant.OUT_RESULT_MSG, "첨부파일이 존재하지 않습니다.");	
				break;
			}
			
			//오늘 날짜로 디렉토리 생성
			String atcFilePath = this.makeDir();
			
			//파일 검증
			validFileLoop: for(MultipartFile multiFile : fileList) {	// validFileLoop로 반복문 라벨 지정
				String atcFileNm = multiFile.getOriginalFilename();	//원본 파일명
				int pos = atcFileNm.lastIndexOf(".");
				if(pos == -1) {
					result.put(Constant.RESULT, Constant.RESULT_FAILURE);
					result.put(Constant.OUT_RESULT_MSG, atcFileNm + " 파일의 확장자가 존재하지 않습니다.");	
					break validFileLoop; // 라벨로 지정된 반복문을 벗어납니다.				
				}
				String atcFileExts = atcFileNm.substring(pos + 1);	//확장자명			
				
				//허용된 확장자인지 검증
				if(excludedExtsList.contains(atcFileExts)) {
					result.put(Constant.RESULT, Constant.RESULT_FAILURE);
					result.put(Constant.OUT_RESULT_MSG, atcFileExts + "는 허용되지 않은 확장자입니다.");	
					break validFileLoop; // 라벨로 지정된 반복문을 벗어납니다.					
				}			
			}
			
			//파일 저장
			for(MultipartFile multiFile : fileList) {
				InputStream fi = multiFile.getInputStream();
				String atcFileNm = multiFile.getOriginalFilename();	//원본 파일명
				long atcFileCapaVal = multiFile.getSize();			//파일 사이즈
				int pos = atcFileNm.lastIndexOf(".");
				String atcFileExts = atcFileNm.substring(pos + 1);	//확장자명
				
				//파일명에 현재 시간 입력(파일명_현재시간.확장자)
				String saveAtcFileNm = atcFileNm.substring(0, pos) + "_" + nowTime + "." + atcFileExts;
				//중복된 파일명 변경
				saveAtcFileNm = this.getUniqueFileName(atcFilePath, saveAtcFileNm);
				
				//업로드할 파일명(경로+파일명)
				File uploadFile = new File(atcFilePath + saveAtcFileNm);
				FileOutputStream fo = new FileOutputStream(uploadFile);
				
				//물리적인 공간에 파일 저장
				FileUtil.saveFileOri(fi, fo);
				
				inData.put("atcFileNm", atcFileNm);					//파일명
				inData.put("saveAtcFileNm", saveAtcFileNm);			//파일저장명
				inData.put("atcFilePath", atcFilePath);				//파일경로
				inData.put("atcFileCapaVal", atcFileCapaVal);		//파일용량
				inData.put("atcFileExts", atcFileExts);				//파일확장자
				
				sqlSession.insert("mapper.com.FileMapper.insertFile", inData);
			}
			
			//파일 삭제
			deleteFile(inData);
		} while(false);
		
		return result;
	}	

	/**
	 * @메소드명: deleteFile
	 * @작성자: 김상민
	 * @생성일: 2023. 1. 4. 오후 1:23:17
	 * @설명: 첨부파일 삭제
	 */
	public Map<String, Object> deleteFile(Map<String, Object> inData) throws Exception{
		Map<String, Object> result = new HashMap<String, Object>();
		String delFiles = (String) inData.get("delFiles");
		if(delFiles != null && !"".equals(delFiles)) {
			//삭제된 파일 목록이 null이나 빈값이 아닐 경우
			String[] delFilesArr = delFiles.split(",");
			inData.put("delFilesArr", delFilesArr);
			
			List<Map<String, Object>> list = sqlSession.selectList("mapper.com.FileMapper.selectDelFile", inData);
			for(Map<String, Object> delFile : list) {
				File file = new File((String) delFile.get("atcFilePath"), (String) delFile.get("saveAtcFileNm"));
				file.delete();
			}

			int cnt = sqlSession.delete("mapper.com.FileMapper.deleteFile", inData);
		};
		return result;		
	}
	
	/**
	 * @메소드명: deleteFileAll
	 * @작성자: 김상민
	 * @생성일: 2023. 1. 4. 오후 1:26:58
	 * @설명: 첨부파일 전체 삭제
	 */
	public Map<String, Object> deleteFileAll(Map<String, Object> inData) throws Exception{
		Map<String, Object> result = new HashMap<String, Object>();

		List<Map<String, Object>> list = sqlSession.selectList("mapper.com.FileMapper.selectFile", inData);
		for(Map<String, Object> delFile : list) {
			File file = new File((String) delFile.get("atcFilePath"), (String) delFile.get("saveAtcFileNm"));
			file.delete();
		}	
		int cnt = sqlSession.delete("mapper.com.FileMapper.deleteFile", inData);
		
		result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
		return result;	
	}
	
	/**
	 * @메소드명: getUniqueFileName
	 * @작성자: 김상민
	 * @생성일: 2022. 12. 14. 오후 3:59:44
	 * @설명: 중복된 파일명 변경
	 */
	public String getUniqueFileName(String path, String file) {
		File tmp = new File(path + file.toLowerCase());
		String fileName = file.toLowerCase();
		int i = 1;
		while(tmp.exists()) {
			int pos = file.lastIndexOf(".");
			String exts = file.substring(pos + 1);	//확장자
			fileName = fileName.substring(0, pos) + "(" + i + ")" + "." + exts;	//파일명_현재시간(i).확장자
			tmp = new File(path + fileName);
			i++;
		}
		return fileName;
	}
	
	/**
	 * @메소드명: makeDir
	 * @작성자: 김상민
	 * @생성일: 2022. 12. 14. 오후 2:37:37
	 * @설명: 오늘 날짜의 디렉토리 생성
	 */
	public String makeDir() throws ConfigurationException {
		//현재 날짜 구하기
		LocalDate date = LocalDate.now();
		DateTimeFormatter dtfDate = DateTimeFormatter.ofPattern("yyyyMMdd");
		String nowDate = date.format(dtfDate);	
		
		String atcFilePath = "";	//첨부파일 저장 경로
		
		//기존 소스 변형
		OS_Type os = OSValidator.getOS();	//OS타입 구하기(UNKNOWN(0), WINDOWS(1), LINUX(2), MAC(3), SOLARIS(4))
		Configuration conf = new Configuration();
		atcFilePath = conf.getString("Global." + os + ".getComFilePath") + nowDate + "/";	
		File dir = new File(atcFilePath);
		if(!dir.isDirectory()) {	//해당 경로가 디렉토리인지 확인
			if(!dir.exists()) {		//해당 경로 디렉토리가 있는지 확인
				dir.mkdirs();		//해당 디렉토리가 없으면 생성
			}
		}
		
		return atcFilePath;
	}
	
	/**
	* @메소드명: makeDir
	* @작성자: KimSangMin
	* @생성일: 2023. 12. 13. 오후 3:34:04
	* @설명: 이미지 디렉토리 생성
	 */
	public String makeImageDir(String dirName) throws ConfigurationException {
		String atcFilePath = "";	//파일 저장 경로
		
		OS_Type os = OSValidator.getOS();	//OS타입 구하기(UNKNOWN(0), WINDOWS(1), LINUX(2), MAC(3), SOLARIS(4))
		Configuration conf = new Configuration();
		atcFilePath = conf.getString("Global." + os + ".getComImagePath") + dirName + "/";	
		File dir = new File(atcFilePath);
		if(!dir.isDirectory()) {	//해당 경로가 디렉토리인지 확인
			if(!dir.exists()) {		//해당 경로 디렉토리가 있는지 확인
				dir.mkdirs();		//해당 디렉토리가 없으면 생성
			}
		}
		
		return atcFilePath;
	}
	
	/**
	* @메소드명: saveImage
	* @작성자: KimSangMin
	* @생성일: 2023. 12. 13. 오후 3:21:32
	* @설명: 이미지 파일 저장
	 */
	public Map<String, Object> saveImage(StringBuilder logStr, Map<String, Object> inData, MultipartFile image, String dirName) throws Exception{
		Map<String, Object> result = new HashMap<String, Object>();
		
		//현재 시간 구하기
		LocalTime time = LocalTime.now();
		DateTimeFormatter dtfTime = DateTimeFormatter.ofPattern("HHmmss");
		String nowTime = time.format(dtfTime);
		
		do {
			if(image == null) {
				result.put(Constant.RESULT, Constant.RESULT_FAILURE);
				result.put(Constant.OUT_RESULT_MSG, "이미지가 존재하지 않습니다.");	
				break;
			}
			
			//이미지 디렉토리 생성
			String atcFilePath = this.makeImageDir(dirName);
			
			//파일 확장자 검증
			String atcFileNm = image.getOriginalFilename();	//원본 파일명
			int pos = atcFileNm.lastIndexOf(".");
			if(pos == -1) {
				result.put(Constant.RESULT, Constant.RESULT_FAILURE);
				result.put(Constant.OUT_RESULT_MSG, atcFileNm + " 파일의 확장자가 존재하지 않습니다.");	
				break;					
			}
			String atcFileExts = atcFileNm.substring(pos + 1);	//확장자명			
			
			//허용된 확장자인지 검증
			if(excludedExtsList.contains(atcFileExts)) {	
				result.put(Constant.RESULT, Constant.RESULT_FAILURE);
				result.put(Constant.OUT_RESULT_MSG, atcFileExts + "는 허용되지 않은 확장자입니다.");	
				break;					
			}				
			
			InputStream fi = image.getInputStream();
			//파일명에 현재 시간 입력(파일명_현재시간.확장자)
			String saveAtcFileNm = atcFileNm.substring(0, pos) + "_" + nowTime + "." + atcFileExts;
			//중복된 파일명 변경
			saveAtcFileNm = this.getUniqueFileName(atcFilePath, saveAtcFileNm);
			
			//업로드할 파일명(경로+파일명)
			File uploadFile = new File(atcFilePath + saveAtcFileNm);
			FileOutputStream fo = new FileOutputStream(uploadFile);
			
			//물리적인 공간에 파일 저장
			FileUtil.saveFileOri(fi, fo);
			
			result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
			result.put("saveAtcFileNm", saveAtcFileNm);
		} while(false);
		
		return result;
	}
}
