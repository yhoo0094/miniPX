package com.mpx.minipx.controller.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mpx.minipx.entity.TbUser;
import com.mpx.minipx.framework.util.JwtUtil;
import com.mpx.minipx.service.UserService;

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
    private String jwtSecret;  // JWT 비밀 키 (application.properties에서 가져오기)       

    /**
     * @메소드명: getUserList
     * @작성자: KimSangMin
     * @생성일: 2025. 6. 19.
     * @설명:
     */
    @GetMapping("/getUserList")
    public List<TbUser>  getUserList() {
//    	List<Map<String, Object>> result = userService.getUserList();
    	List<TbUser> result = userService.getUserList();
        return result;
    }
    
    /**
     * @throws Exception 
     * @메소드명: login
     * @작성자: KimSangMin
     * @생성일: 2025. 6. 19.
     * @설명: 로그인
     */
    @PostMapping("/login.do")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, Object> inData, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	Map<String, Object> result = new HashMap<>();
    	
    	//기기정보
    	String remoteAddr = request.getRemoteAddr();
    	inData.put("remoteAddr", remoteAddr);
    	
    	result = userService.login(inData);

    	if((boolean) result.get("success")) {
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
    @PostMapping("/logout.do")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = JwtUtil.extractTokenFromCookies(request, "refreshToken");
        // 사용자 ID 추출
        Claims claims;
        try {
            claims = JwtUtil.validateToken(refreshToken, jwtSecret);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid refresh token");
        }        
        String userId = claims.getSubject();        
        
        // DB에서 refreshToken 제거
        userService.logout(userId);

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
}