package com.mpx.minipx.framework.util;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;

import com.mpx.minipx.entity.TbUser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public class JwtUtil {
	/**
	 * @메소드명: generateToken
	 * @작성자: KimSangMin
	 * @생성일: 2025. 6. 23.
	 * @설명: JWT 토큰 생성(String 매개변수)
	 */
//	public static String generateToken(String str, String base64Secret) {
//        // Base64 디코딩 후 SecretKey 객체로 변환 (HS512 기준, 512bit 이상 필수)
//        byte[] keyBytes = Decoders.BASE64.decode(base64Secret);		// Base64 인코딩된 문자열을 바이트 배열로 디코딩
//        SecretKey key = Keys.hmacShaKeyFor(keyBytes);				// 디코딩된 바이트로 HMAC SHA512 키 생성	
//        
//        return Jwts.builder()
//                .setSubject(str)  // 사용자 ID
//                .setIssuedAt(new Date())  // 발급 시간
//                .setExpiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000))  // 1시간 만료
//                .signWith(key, SignatureAlgorithm.HS512)  // 서명 알고리즘 설정
//                .compact();        
//    } 	
	
	/**
	 * @메소드명: generateToken
	 * @작성자: KimSangMin
	 * @생성일: 2025. 6. 23.
	 * @설명: JWT 토큰 생성(TbUser 매개변수)
	 */
	public static String generateToken(TbUser tbUser, String base64Secret, long validity) {
        // Base64 디코딩 후 SecretKey 객체로 변환 (HS512 기준, 512bit 이상 필수)
        byte[] keyBytes = Decoders.BASE64.decode(base64Secret);		// Base64 인코딩된 문자열을 바이트 배열로 디코딩
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);				// 디코딩된 바이트로 HMAC SHA512 키 생성	
        
        return Jwts.builder()
                .setSubject(tbUser.getUserId())
                .claim("userSeq", tbUser.getUserSeq())
                .claim("userId", tbUser.getUserId())
                .claim("userNm", tbUser.getUserNm())
                .claim("roleSeq", tbUser.getRoleSeq())
                .setIssuedAt(new Date())  // 발급 시간
                .setExpiration(new Date(System.currentTimeMillis() + validity))
                .signWith(key, SignatureAlgorithm.HS512)  // 서명 알고리즘 설정
                .compact();        
    }
	
	/**
	 * @메소드명: refreshToken
	 * @작성자: KimSangMin
	 * @생성일: 2025. 7. 1.
	 * @설명: JWT 토큰 재생성(TbUser 매개변수)
	 */
	public static String refreshToken(Claims claims, String base64Secret) {
	    byte[] keyBytes = Decoders.BASE64.decode(base64Secret);
	    SecretKey key = Keys.hmacShaKeyFor(keyBytes);

	    return Jwts.builder()
	            .setSubject(claims.getSubject())
	            .claim("userSeq", claims.get("userSeq"))
	            .claim("userId", claims.get("userId"))
	            .claim("userNm", claims.get("userNm"))
	            .claim("roleSeq", claims.get("roleSeq"))
	            .setIssuedAt(new Date())
	            .setExpiration(new Date(System.currentTimeMillis() + Constant.REFRESH_TOKEN_VALIDITY)) // 갱신된 유효기간(30일)
	            .signWith(key, SignatureAlgorithm.HS512)
	            .compact();
	}	
	
	/**
	 * @메소드명: validateToken
	 * @작성자: KimSangMin
	 * @생성일: 2025. 6. 23.
	 * @설명: JWT 토큰 검증
	 */
    public static Claims validateToken(String token, String base64Secret) throws SecurityException {
        try {
            // Base64 디코딩 후 SecretKey 객체로 변환 (HS512 기준, 512bit 이상 필수)
            byte[] keyBytes = Decoders.BASE64.decode(base64Secret);		// Base64 인코딩된 문자열을 바이트 배열로 디코딩
            SecretKey key = Keys.hmacShaKeyFor(keyBytes);				// 디코딩된 바이트로 HMAC SHA512 키 생성

            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
        } catch (ExpiredJwtException e) {
            throw e;
        } catch (Exception e) {
            throw new SecurityException("Invalid JWT token", e);
        }
    }
    
    /**
     * @메소드명: isTokenValid
     * @작성자: KimSangMin
     * @생성일: 2025. 6. 23.
     * @설명: JWT 토큰 검증 Boolean 타입 반환
     */
    public static boolean isTokenValid(String token, String base64Secret) {
        try {
            validateToken(token, base64Secret);
            return true;
        } catch (SecurityException e) {
            return false;
        }
    }    
    
    /**
     * @메소드명: extractTokenFromCookies
     * @작성자: KimSangMin
     * @생성일: 2025. 7. 1.
     * @설명: 쿠키에서 토큰 추출하기
     */
    public static String extractTokenFromCookies(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if (cookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }    
}

	