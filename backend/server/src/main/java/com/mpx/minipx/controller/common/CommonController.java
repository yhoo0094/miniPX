package com.mpx.minipx.controller.common;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mpx.minipx.entity.TbCodeDetail;
import com.mpx.minipx.framework.util.Constant;
import com.mpx.minipx.service.common.CommonService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/common")
public class CommonController {

    private final CommonService commonService;

    public CommonController(CommonService commonService) {
        this.commonService = commonService;
    }
    
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
    @PostMapping("/getCodeList")
    public List<TbCodeDetail> getCodeList(@RequestBody Map<String, Object> inData, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	List<TbCodeDetail> result = commonService.getCodeList(inData);
        return result;
    }
    
    /**
     * @메소드명: quillUploadImage
     * @작성자: KimSangMin
     * @생성일: 2026. 1. 4.
     * @설명: QuillEditor 이미지 업로드
     */
    @PostMapping("/quillUploadImage")
    public Map<String, Object> quillUploadImage(@RequestParam("file") MultipartFile file) throws Exception {
        // 1) 확장자/사이즈 검증
        // 2) 저장 (로컬/디스크/S3 등)
        // 3) 접근 가능한 URL 반환
        String url = commonService.quillUploadImage(file);

        Map<String, Object> result = new HashMap<>();
        result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
        result.put(Constant.OUT_DATA, Map.of("url", url));
        return result;
    }    
    
    /**
     * @메소드명: getQuillImage
     * @작성자: KimSangMin
     * @생성일: 2026. 1. 4.
     * @설명: QuillEditor 이미지 조회
     */
    @GetMapping("/getQuillImage/{fileName}")
    public ResponseEntity<Resource> getQuillImage(@PathVariable String fileName) throws MalformedURLException {
        Path filePath = Paths.get(quillImagePath).resolve(fileName);
        Resource resource = new UrlResource(filePath.toUri());
        return ResponseEntity.ok()
          .contentType(MediaType.IMAGE_JPEG) // 확장자에 맞춰 결정
          .body(resource);
    }    
}