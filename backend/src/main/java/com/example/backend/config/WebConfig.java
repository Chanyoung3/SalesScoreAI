package com.example.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 모든 API 경로에 대해 CORS를 허용하도록 설정 (/**는 모든 경로를 의미합니다.)
        registry.addMapping("/**")
                // 프론트엔드 애플리케이션이 실행되는 오리진(Origin)을 허용
                // 개발 중에는 localhost:3000(React/Vue 기본 포트) 등을 추가하고, 실제 배포 시에는 도메인을 추가
                .allowedOrigins("http://localhost:3000","http://211.188.58.30", "http://211.188.58.30:8080") // 여기에 프론트엔드 URL을 추가
                // 허용할 HTTP 메서드를 지정
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // 모든 요청 헤더를 허용
                .allowedHeaders("*")
                // 인증 정보(쿠키, HTTP 인증 헤더 등) 전송을 허용
                .allowCredentials(true);
    }
}