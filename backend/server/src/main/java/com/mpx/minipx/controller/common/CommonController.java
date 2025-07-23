package com.mpx.minipx.controller.common;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mpx.minipx.entity.TbCodeDetail;
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
    private String jwtSecret;  // JWT 비밀 키 (application.properties에서 가져오기)       

    /**
     * @메소드명: getUserList
     * @작성자: KimSangMin
     * @생성일: 2025. 7. 16.
     * @설명: 공통 코드 목록 조회
     */
    @PostMapping("/getCodeList")
    public List<TbCodeDetail> getCodeList(@RequestBody Map<String, Object> inData, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	List<TbCodeDetail> result = commonService.getCodeList(inData);
        return result;
    }
}