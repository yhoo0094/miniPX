package com.mpx.minipx.service.market;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mpx.minipx.controller.common.BaseController;
import com.mpx.minipx.dto.market.ItemImage;
import com.mpx.minipx.framework.util.Constant;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemNewService {

    protected static final Log log = LogFactory.getLog(BaseController.class);

    @Autowired
    private SqlSessionTemplate sqlSession;

    // ★ application.properties 에서 주입
    @Value("${item.new.image-path}")
    private String imageBasePath;

    /**
     * @메소드명: getNewOrder
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 23.
     * @설명: 신규상품주문 조회
     */    
    public Map<String, Object> getNewOrder(Map<String, Object> inData) {
    	Map<String, Object> result = new HashMap<String, Object>(); 
    	Map<String, Object> map = sqlSession.selectOne("com.mpx.minipx.mapper.ItemNewMapper.getNewOrder", inData);
    	
    	//조회하는 사용자와 주문자가 일치하지 않을 경우(관리자는 조회 가능)
    	if( !"admin".equals(inData.get("userId"))
    			&& (inData.get("userSeq") == null || map.get("userSeq") == null 
    			|| !inData.get("userSeq").equals(map.get("userSeq").toString()))) {
    		result.put(Constant.RESULT, Constant.RESULT_FAILURE);
    		result.put(Constant.RESULT_DETAIL, Constant.UNAUTHORIZED_ACCESS);
    		result.put(Constant.OUT_RESULT_MSG, "본인 주문 내용만 조회할 수 있습니다.");
    	}
    	
    	result.put(Constant.OUT_DATA, map);
    	result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
    	return result;
    }    
    
    /**
     * @메소드명: insertNewOrder
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 10.
     * @설명: 신규상품주문 등록
     */
    public Map<String, Object> insertNewOrder(Map<String, Object> inData, MultipartFile imageFile) {
        Map<String, Object> result = new HashMap<>();

        // 1. 이미지 파일 저장 처리
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                // 경로 객체 생성
                Path dirPath = Paths.get(imageBasePath);

                // 디렉터리가 없다면 생성
                if (!Files.exists(dirPath)) {
                    Files.createDirectories(dirPath);
                }

                // 원본 파일명 & 확장자 추출
                String originalFilename = imageFile.getOriginalFilename();
                String ext = "";
                if (originalFilename != null && originalFilename.lastIndexOf(".") != -1) {
                    ext = originalFilename.substring(originalFilename.lastIndexOf("."));
                }

                // 저장용 파일명 (UUID + 확장자)
                String savedFileName = UUID.randomUUID().toString() + ext;

                // 전체 경로
                Path targetPath = dirPath.resolve(savedFileName);

                // 실제 파일 저장
                imageFile.transferTo(targetPath.toFile());

                // ★ DB에 넣을 값 세팅
                inData.put("img", savedFileName);         // 예: 파일명

            } catch (IOException e) {
                log.error("신규상품주문 이미지 저장 중 오류", e);
                result.put(Constant.RESULT, Constant.RESULT_FAILURE);
                result.put(Constant.OUT_RESULT_MSG, "이미지 저장 중 오류가 발생했습니다.");
                return result;
            }
        }

        // 2. 신규상품주문 INSERT
        int affected = sqlSession.update("com.mpx.minipx.mapper.ItemNewMapper.insertNewOrder", inData);

        if (affected > 0) {
            result.put(Constant.OUT_DATA, inData);
            result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
        } else {
            result.put(Constant.OUT_DATA, affected);
            result.put(Constant.RESULT, Constant.RESULT_FAILURE);
            result.put(Constant.OUT_RESULT_MSG, "신규상품주문 등록에 실패했습니다.");
        }

        return result;
    }
    
    /**
     * @메소드명: updateNewOrder
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 23.
     * @설명: 신규상품주문 수정
     */    
    public Map<String, Object> updateNewOrder(Map<String, Object> inData, MultipartFile imageFile) {
        Map<String, Object> result = new HashMap<>();

        // 1. 이미지 파일 저장 처리
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                // 경로 객체 생성
                Path dirPath = Paths.get(imageBasePath);

                // 디렉터리가 없다면 생성
                if (!Files.exists(dirPath)) {
                    Files.createDirectories(dirPath);
                }

                // 원본 파일명 & 확장자 추출
                String originalFilename = imageFile.getOriginalFilename();
                String ext = "";
                if (originalFilename != null && originalFilename.lastIndexOf(".") != -1) {
                    ext = originalFilename.substring(originalFilename.lastIndexOf("."));
                }

                // 저장용 파일명 (UUID + 확장자)
                String savedFileName = UUID.randomUUID().toString() + ext;

                // 전체 경로
                Path targetPath = dirPath.resolve(savedFileName);

                // 실제 파일 저장
                imageFile.transferTo(targetPath.toFile());

                // ★ DB에 넣을 값 세팅
                inData.put("img", savedFileName);         // 예: 파일명

            } catch (IOException e) {
                log.error("신규상품주문 이미지 저장 중 오류", e);
                result.put(Constant.RESULT, Constant.RESULT_FAILURE);
                result.put(Constant.OUT_RESULT_MSG, "이미지 저장 중 오류가 발생했습니다.");
                return result;
            }
        }

        // 2. 신규상품주문 INSERT
        int affected = sqlSession.update("com.mpx.minipx.mapper.ItemNewMapper.updateNewOrder", inData);

        if (affected > 0) {
            result.put(Constant.OUT_DATA, affected);
            result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
        } else {
            result.put(Constant.OUT_DATA, affected);
            result.put(Constant.RESULT, Constant.RESULT_FAILURE);
            result.put(Constant.OUT_RESULT_MSG, "신규상품주문 등록에 실패했습니다.");
        }

        return result;
    }    
    
    /**
     * @메소드명: getItemNewImage
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 15.
     * @설명: 신규상품주문 이미지 조회
     */
	public ItemImage getItemNewImage(String img) throws Exception {
		//String img = sqlSession.selectOne("com.mpx.minipx.mapper.ItemNewMapper.getItemNewImage", orderSeq);
	
	    if (img == null || img.isBlank()) {
	        throw new IllegalStateException("이미지 경로 없음");
	    }
	    
	    Path filePath = Paths.get(imageBasePath + img);
	    if (!Files.exists(filePath)) {
	        throw new IllegalStateException("이미지 파일 없음: " + filePath);
	    }
	
	    Resource resource = new UrlResource(filePath.toUri());
	    if (!resource.isReadable()) {
	        throw new IllegalStateException("이미지 파일 읽기 불가: " + filePath);
	    }
	
	    MediaType mediaType = MediaTypeFactory.getMediaType(resource)
	            .orElse(MediaType.APPLICATION_OCTET_STREAM);
	
	    return new ItemImage(resource, mediaType, filePath.getFileName().toString());
	}       
}
