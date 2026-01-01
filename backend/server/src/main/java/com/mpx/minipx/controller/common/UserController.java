package com.mpx.minipx.controller.common;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mpx.minipx.framework.util.Constant;
import com.mpx.minipx.framework.util.JwtUtil;
import com.mpx.minipx.service.common.UserService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @Value("${jwt.secret}")
    private String jwtSecret;       
    
    /**
     * @throws Exception 
     * @메소드명: login
     * @작성자: KimSangMin
     * @생성일: 2025. 6. 19.
     * @설명: 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, Object> inData, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	Map<String, Object> result = new HashMap<>();
    	
    	//기기정보
    	String remoteAddr = request.getRemoteAddr();
    	inData.put("remoteAddr", remoteAddr);
    	
    	result = userService.login(inData, request);

    	if(Constant.RESULT_SUCCESS.equals(result.get(Constant.RESULT))) {
			response.addCookie((Cookie)result.get("accessToken"));	// 쿠키를 응답에 추가
			response.addCookie((Cookie)result.get("refreshToken"));	// 쿠키를 응답에 추가
		}        
    	
        return ResponseEntity.ok(result);
    }
    
    /**
     * @메소드명: logout
     * @작성자: KimSangMin
     * @생성일: 2025. 7. 3.
     * @설명: 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
    	
    	// 사용자 ID 추출
        String accessToken = JwtUtil.extractTokenFromCookies(request, "accessToken");
        Claims claims;
        try {
            claims = JwtUtil.validateToken(accessToken, jwtSecret);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid token");
        }        
        String userId = (String) claims.get("userId");    
        
        // DB에서 refreshToken 제거
        userService.logout(userId, request);

        // 쿠키 삭제 (accessToken + refreshToken)
        Cookie accessDel = new Cookie("accessToken", null);
        accessDel.setMaxAge(0);
        accessDel.setPath("/");
        accessDel.setHttpOnly(true);
        response.addCookie(accessDel);

        Cookie refreshDel = new Cookie("refreshToken", null);
        refreshDel.setMaxAge(0);
        refreshDel.setPath("/");
        refreshDel.setHttpOnly(true);
        response.addCookie(refreshDel);

        return ResponseEntity.ok("로그아웃 성공");
    }  
    
    /**
     * @메소드명: changePassword
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 23.
     * @설명: 비밀번호 변경
     */
    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, Object> inData, HttpServletRequest request, HttpServletResponse response) {
    	
        // ---------- 사용자 정보 추출 ----------
        String accessToken = JwtUtil.extractTokenFromCookies(request, "accessToken");
        Claims claims;
        try {
            claims = JwtUtil.validateToken(accessToken, jwtSecret);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid token");
        }
        inData.put("userId", (String) claims.get("userId"));
        inData.put("userSeq", claims.get("userSeq"));
        
        Map<String, Object> result = userService.changePassword(inData);

        return ResponseEntity.ok(result);
    }      
    
    /**
     * @메소드명: getLoginInfo
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 30.
     * @설명: 사용자 정보 재조회
     */
    @PostMapping("/getLoginInfo")
    public ResponseEntity<?> getLoginInfo(@RequestBody Map<String, Object> inData, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	Map<String, Object> result = new HashMap<>();
    	
    	// 사용자 ID 추출
        String accessToken = JwtUtil.extractTokenFromCookies(request, "accessToken");
        Claims claims;
        try {
            claims = JwtUtil.validateToken(accessToken, jwtSecret);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid token");
        }        
        inData.put("userSeq", claims.get("userSeq"));
    	result = userService.getLoginInfo(inData, request);
    	
        return ResponseEntity.ok(result);
    }    
}