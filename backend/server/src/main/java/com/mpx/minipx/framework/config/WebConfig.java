package com.mpx.minipx.framework.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	
//    @Override
    /**
     * @메소드명: addCorsMappings
     * @작성자: KimSangMin
     * @생성일: 2025. 6. 23.
     * @설명: CorsMapping 
     */    
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/api/**") // 모든 /api 하위 URL에 대해
//                .allowedOrigins("http://localhost:5173") // Vue 개발 서버 도메인 허용
//                .allowedMethods("*") // GET, POST, PUT, DELETE 등 모두 허용
//                .allowedHeaders("*") // 모든 헤더 허용
//                .allowCredentials(true); // 인증정보(Cookie 등) 허용 여부
//    }
	
	
	//----------------securityFilterChain로 대체!----------------
}
