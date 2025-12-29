package com.mpx.minipx.framework.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        	.cors(cors -> cors.configurationSource(corsConfigurationSource())) 
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
            	.requestMatchers( "/api/user/login"
            					, "/api/auth/check"
            					, "/api/auth/reissue"
            					, "/css/**", "/js/**").permitAll()  // 로그인 등은 허용
                .anyRequest().authenticated()
//                .anyRequest().permitAll()  // 개발 중 일시적 허용
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }    
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // ✅ 패턴 기반 허용 (쿠키 사용 가능)
        config.setAllowedOriginPatterns(List.of(
            "http://localhost:5173",
            "http://127.0.0.1:5173",
            "http://10.219.35.67:5173",			//로컬 ip
            "http://43.200.173.163",        	//운영(80포트)
            "https://43.200.173.163",        	//운영(443포트)
            "https://*.ngrok-free.dev",
            "https://*.ngrok-free.app"            
        ));

        // ✅ 모든 메서드 허용 (OPTIONS 포함)
        config.setAllowedMethods(List.of(
            "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));

        // ✅ 모든 헤더 허용
        config.setAllowedHeaders(List.of("*"));

        // ✅ 응답에서 노출할 헤더
        config.setExposedHeaders(List.of(
            "Authorization",
            "Set-Cookie"
        ));

        // ✅ 쿠키(JWT refreshToken) 사용 중이므로 반드시 true
        config.setAllowCredentials(true);

        // ✅ 캐시 시간
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

}
	