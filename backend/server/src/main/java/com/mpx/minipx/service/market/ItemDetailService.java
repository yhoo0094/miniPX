package com.mpx.minipx.service.market;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mpx.minipx.controller.common.BaseController;
import com.mpx.minipx.framework.util.Constant;
import com.mpx.minipx.service.common.QdrantService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemDetailService {
    private final QdrantService qdrantService;
	
	protected static final Log log = LogFactory.getLog(BaseController.class);
//	protected static final Logger logger = LogManager.getLogger(ControllerAdvice.class);
	
    @Autowired
    private SqlSessionTemplate sqlSession;      
	
    @Value("${jwt.secret}")
    private String jwtSecret;  // JWT 비밀 키 (application.properties에서 가져오기)    
    
    @Value("${item.image-path}")
    private String imageBasePath;    
    
    /**
     * @메소드명: getItemDetail
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 19.
     * @설명: 상품상세 조회
     */
    public Map<String, Object> getItemDetail(Map<String, Object> inData) {
    	Map<String, Object> result = new HashMap<String, Object>(); 
    	Map<String, Object> map = sqlSession.selectOne("com.mpx.minipx.mapper.ItemDetailMapper.getItemDetail", inData);
    	result.put(Constant.OUT_DATA, map);
    	result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
    	return result;
    }    
    
    /**
     * @메소드명: requestOrder
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 20.
     * @설명: 주문 등록
     */    
    public Map<String, Object> requestOrder(Map<String, Object> inData) {
        Map<String, Object> result = new HashMap<>();
        
        int affected = sqlSession.insert("com.mpx.minipx.mapper.ItemDetailMapper.insertOrder", inData);
        result.put(Constant.OUT_DATA, affected);
        result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
        return result;
    }      
    
    /**
     * @메소드명: upsertBasket
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 20.
     * @설명: 장바구니 등록
     */    
    public Map<String, Object> upsertBasket(Map<String, Object> inData) {
        Map<String, Object> result = new HashMap<>();
        
        int affected = sqlSession.insert("com.mpx.minipx.mapper.ItemDetailMapper.upsertBasket", inData);
        result.put(Constant.OUT_DATA, affected);
        result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
        return result;
    }      
    
    /**
     * @메소드명: saveItemDetail
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 20.
     * @설명: 아이템 정보 저장
     */    
    public Map<String, Object> upsertItem(Map<String, Object> inData, MultipartFile imageFile) {
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
                log.error("이미지 저장 중 오류", e);
                result.put(Constant.RESULT, Constant.RESULT_FAILURE);
                result.put(Constant.OUT_RESULT_MSG, "이미지 저장 중 오류가 발생했습니다.");
                return result;
            }
        }

        // 2. 아이템 정보 저장
        int affected = 0;
        if(inData.get("itemSeq") != null && !inData.get("itemSeq").equals("")) {
        	affected = sqlSession.update("com.mpx.minipx.mapper.ItemDetailMapper.updateItem", inData);
        } else {
        	affected = sqlSession.insert("com.mpx.minipx.mapper.ItemDetailMapper.insertItem", inData);
        }

        if (affected > 0) {
        	String useYn = String.valueOf(inData.get("useYn"));
        	
            if ("N".equalsIgnoreCase(useYn)) {
                qdrantService.qdrantDeleteItem(inData.get("itemSeq"));
            } else {
                qdrantService.qdrantUpsertItem(inData);
            } 
        	
            result.put(Constant.OUT_DATA, affected);
            result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
        } else {
            result.put(Constant.OUT_DATA, affected);
            result.put(Constant.RESULT, Constant.RESULT_FAILURE);
            result.put(Constant.OUT_RESULT_MSG, "신규상품주문 등록에 실패했습니다.");
        }

        return result;
    }    
}
