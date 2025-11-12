package com.mpx.minipx.framework.security;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mpx.minipx.framework.util.JwtUtil;
import com.mpx.minipx.repository.TbTokenRepository;
import com.mpx.minipx.repository.TbUserRepository;
import com.mpx.minipx.service.common.AuthService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
	
    private final String jwtSecret;

    public JwtAuthFilter(
        @Value("${jwt.secret}") String jwtSecret
    ) {
        this.jwtSecret = jwtSecret;
    }
    
    //doFilterInternal 제외할 경로
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.equals("/api/auth/reissue")
        	|| path.equals("/api/user/login")
        	;
    }    

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

    	String token = JwtUtil.extractTokenFromCookies(request, "accessToken");

        if (token != null) {
            try {
                Claims claims = JwtUtil.validateToken(token, jwtSecret);
                // SecurityContext에 인증 정보 등록
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(claims.getSubject(), null, new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (ExpiredJwtException e) {
            	// 액세스 토큰이 만료되었을 경우 401 반환
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            } catch (JwtException | IllegalArgumentException e) {
                // 조작된 토큰, 구조 잘못된 토큰 등
                // 로깅만 하고 다음 필터로 넘김 → Security가 403 반환
            }
        }

        // token == null인 경우에도 요청 허용: 이후 SecurityContext에서 인증 여부 체크됨
        filterChain.doFilter(request, response);
    }
}


	