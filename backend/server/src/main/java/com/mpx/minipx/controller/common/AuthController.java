package com.mpx.minipx.controller.common;

import com.mpx.minipx.entity.TbUser;
import com.mpx.minipx.entity.TbToken;
import com.mpx.minipx.framework.util.Constant;
import com.mpx.minipx.framework.util.JwtUtil;
import com.mpx.minipx.repository.RedisPermissionRepository;
import com.mpx.minipx.repository.TbTokenRepository;
import com.mpx.minipx.repository.TbUserRepository;
import com.mpx.minipx.service.common.AuthService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	private final AuthService authService;	
	
    @Autowired
    private TbUserRepository tbUserRepository;	
    
    @Autowired
    private TbTokenRepository tbTokenRepository;    
    
    @Autowired
    private RedisPermissionRepository redisPermissionRepository;      

    @Value("${jwt.secret}")
    private String jwtSecret;

    @PostMapping("/check")
    public Map<String, Object> checkAuth(@RequestBody Map<String, Object> inData, HttpServletRequest request) {
    	Map<String, Object> result = new HashMap<>();
        String token = null;

        // 쿠키에서 token 추출
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    token = cookie.getValue();
                }
            }
        }
        
        if (token == null) {	// 쿠키에 token이 없을 때	
        	result.put("authenticated", false);
        	return result;  
        } else {
        	Claims claims = JwtUtil.validateToken(token, jwtSecret);
        	if (claims == null) {
        		result.put("authenticated", false);
        		return result;  
        	} else {
        		result.put("authenticated", true);
        	}
        }

        //권한 체크
        try {
        	Claims claims = JwtUtil.validateToken(token, jwtSecret);
        	String path = (String) inData.get("path");
        	if("/main".equals(path)) { //main 접속은 권한 체크 X
        		result.put("authenticated", true);	
        	} else if (token != null && claims != null) {
            	String userId = claims.getSubject();
            	Optional<Integer> authGrade = redisPermissionRepository.getPermission(userId, path);
            	if (authGrade.isPresent() && authGrade.get() > 0) {
            		result.put("authenticated", true);
            	} else {
            		result.put("authenticated", false);
            		return result;  
            	}
            } else {
                result.put("authenticated", false);
                return result;  
            }
        } catch (Exception e) {
            result.put("authenticated", false);
            return result;  
        }
        
        return result;        
    }
    
    /**
     * @메소드명: reissue
     * @작성자: KimSangMin
     * @생성일: 2025. 7. 2.
     * @설명: 토큰 재발급
     */
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@RequestBody Map<String, Object> inData, @CookieValue(value = "refreshToken", required = false) String refreshToken, HttpServletResponse response) {
    	Map<String, Object> result = new HashMap<>();
    	
        if (refreshToken == null) {
            return ResponseEntity.status(401).body("Refresh token not found");
        }   	
        
        Claims claims;
        try {
            claims = JwtUtil.validateToken(refreshToken, jwtSecret);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid refresh token");
        }        
        String userId = claims.getSubject();

        // 저장된 토큰과 비교
        Optional<TbToken> saved = tbTokenRepository.findByUserId(userId);
        if (saved.isPresent()) {
            String savedToken = saved.get().getToken(); // TbToken의 token 필드
            if (!refreshToken.equals(savedToken)) {
                return ResponseEntity.status(401).body("Invalid refresh token");
            }
        } else {
            return ResponseEntity.status(401).body("Refresh token not found");
        }

        TbUser user = tbUserRepository.findByUserId(userId); 
        
        // accessToken 생성
        String newAccessToken = JwtUtil.generateToken(user, jwtSecret, Constant.ACCESS_TOKEN_VALIDITY);
        Cookie accessCookie = new Cookie("accessToken", newAccessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true);
        accessCookie.setMaxAge((int)Constant.ACCESS_TOKEN_VALIDITY);
        accessCookie.setPath("/");     
        response.addCookie(accessCookie);
        
        
        // refreshToken 생성
        String newRefreshToken = JwtUtil.generateToken(user, jwtSecret, Constant.REFRESH_TOKEN_VALIDITY);
        Cookie refreshCookie = new Cookie("refreshToken", newRefreshToken);
        refreshCookie.setHttpOnly(true);  // JavaScript에서 쿠키에 접근 불가
        refreshCookie.setSecure(true);    // HTTPS에서만 전송
        refreshCookie.setMaxAge((int)Constant.REFRESH_TOKEN_VALIDITY);
        refreshCookie.setPath("/");       // 전체 경로에 대해 유효 
        response.addCookie(refreshCookie);	// 쿠키를 응답에 추가
        
        // refreshToken을 DB에 저장
        authService.saveRefreshToken(
    	    user.getUserId(),
    	    user.getUserSeq(),
    	    newRefreshToken,                     // JWT 문자열
    	    Constant.REFRESH_TOKEN_VALIDITY,  
    	    (String) inData.get("remoteAddr") // 사용자 기기 정보
    	);        
        
        return ResponseEntity.ok(result);
    }
    
}

	