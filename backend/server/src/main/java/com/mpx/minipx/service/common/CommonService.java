package com.mpx.minipx.service.common;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mpx.minipx.controller.common.BaseController;
import com.mpx.minipx.entity.TbCodeDetail;
import com.mpx.minipx.repository.TbCodeDetailRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommonService {
	
	protected static final Log log = LogFactory.getLog(BaseController.class);
	
    @Autowired
    private TbCodeDetailRepository tbCodeDetailRepository;
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${quill.image-path}")
    private String quillImagePath;     

    /**
     * @메소드명: getCodeList
     * @작성자: KimSangMin
     * @생성일: 2025. 7. 16.
     * @설명: 공통 코드 목록 조회
     */
    public List<TbCodeDetail> getCodeList(Map<String, Object> inData) {
//    	List<TbCodeDetail> result = tbCodeDetailRepository.findByCodeGroup((String) inData.get("codeGroup")); 
    	List<TbCodeDetail> result = tbCodeDetailRepository.findByIdCodeGroup((String) inData.get("codeGroup"));
    	return result;
    }
    
    /**
     * @메소드명: quillUploadImage
     * @작성자: KimSangMin
     * @생성일: 2026. 1. 4.
     * @설명: QuillEditor 이미지 업로드
     */    
    public String quillUploadImage(MultipartFile file) throws Exception {
        // 저장 디렉토리
        Path uploadDir = Paths.get(quillImagePath);

        // 디렉토리 없으면 생성
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // 확장자 추출
        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }

        // 저장 파일명
        String saveName = UUID.randomUUID() + ext;

        // 실제 저장 경로
        Path targetPath = uploadDir.resolve(saveName);

        // 파일 저장
        file.transferTo(targetPath.toFile());

        // 접근 URL 반환
        return "/api/common/getQuillImage/" + saveName;
    }    
    
}
