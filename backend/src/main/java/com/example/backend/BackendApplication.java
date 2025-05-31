package com.example.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean; // @Bean 어노테이션 사용을 위해 임포트
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // 비밀번호 암호화 클래스 임포트

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Bean // 이 메서드가 반환하는 객체를 스프링 빈(Spring Bean)으로 등록하도록 지시합니다.
    public BCryptPasswordEncoder passwordEncoder() {
        // BCrypt 알고리즘을 사용하여 비밀번호를 안전하게 해싱하고 검증하는 인코더를 제공합니다.
        return new BCryptPasswordEncoder();
    }

}
