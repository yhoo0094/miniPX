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
	
	@Autowired
	private AuthService authService;	
	
    @Autowired
    private TbUserRepository tbUserRepository;	
    
    @Autowired
    private TbTokenRepository tbTokenRepository;    
    
    @Autowired
    private RedisPermissionRepository redisPermissionRepository;      

    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * @메소드명: checkAuth
     * @작성자: KimSangMin
     * @생성일: 2025. 7. 14.
     * @설명: 페이지 이동에 대한 권한 체크
     */
    @PostMapping("/check")
    public Map<String, Object> checkAuth(@RequestBody Map<String, Object> inData, HttpServletRequest request, HttpServletResponse response) {
    	Map<String, Object> result = new HashMap<>();

    	String token = JwtUtil.extractTokenFromCookies(request, "accessToken");    	
    	
    	// 쿠키에 token이 없을 때
        if (token == null) {		
        	result.put("authenticated", false);
        	return result;  
        }
        
        //토큰 유효성 체크
        Claims claims = JwtUtil.validateToken(token, jwtSecret);
        if (claims == null) {
        	result.put("authenticated", false);
        	return result;  
        }

        //권한 체크
        String path = (String) inData.get("path");
        if("/main".equals(path)) {	//main 접속은 권한 체크 X
        	result.put("authGrade", 1);
        } else {
        	String userId = claims.getSubject();
        	Optional<Integer> authGrade = redisPermissionRepository.getPermission(userId, path);
        	if (!authGrade.isPresent()) {	//해당 경로에 대한 권한 정보가 없음
        		result.put("authenticated", false);
        		return result; 
        	} else {
        		result.put("authGrade", authGrade.get());
        	}
        }
        
        result.put("authenticated", true);
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
//        accessCookie.setSecure(true);		//HTTPS에서만 전송(주석 풀어야함)
        accessCookie.setSecure(false);		//개발용도        
        accessCookie.setMaxAge((int)Constant.REFRESH_TOKEN_VALIDITY);
        accessCookie.setPath("/");     
        response.addCookie(accessCookie);
        
        // refreshToken 생성
        String newRefreshToken = JwtUtil.generateToken(user, jwtSecret, Constant.REFRESH_TOKEN_VALIDITY);
        Cookie refreshCookie = new Cookie("refreshToken", newRefreshToken);
        refreshCookie.setHttpOnly(true);  // JavaScript에서 쿠키에 접근 불가
//        refreshCookie.setSecure(true);		//HTTPS에서만 전송(주석 풀어야함)
        refreshCookie.setSecure(false);		//개발용도         
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

	