package com.mpx.minipx.service.market;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;

import com.mpx.minipx.controller.common.BaseController;
import com.mpx.minipx.dto.market.ItemImage;
import com.mpx.minipx.framework.util.Constant;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemService {
	
	protected static final Log log = LogFactory.getLog(BaseController.class);
//	protected static final Logger logger = LogManager.getLogger(ControllerAdvice.class);
	
    @Autowired
    private SqlSessionTemplate sqlSession;      
	
    @Value("${jwt.secret}")
    private String jwtSecret;  // JWT 비밀 키 (application.properties에서 가져오기)    
    
    @Value("${item.image-path}")
    private String imageBasePath;    

    /**
     * @메소드명: getItemList
     * @작성자: KimSangMin
     * @생성일: 2025. 7. 17.
     * @설명: 상품 목록 조회
     */
    public Map<String, Object> getItemList(Map<String, Object> inData) {
    	Map<String, Object> result = new HashMap<String, Object>(); 
    	List<Map<String, Object>> list = sqlSession.selectList("com.mpx.minipx.mapper.ItemMapper.getItemList", inData);
    	result.put(Constant.OUT_DATA, list);
    	result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
    	return result;
    }
    
    /**
     * @메소드명: getItemImage
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 6.
     * @설명: 상품 이미지 조회
     */
    public ItemImage getItemImage(String img) throws Exception {
    	//String imageName = sqlSession.selectOne("com.mpx.minipx.mapper.ItemMapper.getItemImage", itemSeq);

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
