package com.example.backend.controller;

import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.RegisterRequest;
import com.example.backend.model.Counselor;
import com.example.backend.service.CounselorService;
import org.springframework.beans.factory.annotation.Autowired; // 의존성 자동 주입
import org.springframework.http.HttpStatus; // HTTP 상태 코드
import org.springframework.http.ResponseEntity; // HTTP 응답 객체
import org.springframework.web.bind.annotation.*; // REST 컨트롤러 관련 어노테이션 임포트

@RestController
@RequestMapping("/api/auth") // 이 컨트롤러의 모든 API 엔드포인트는 "/api/auth"로 시작
public class AuthController {

    private final CounselorService counselorService; // 상담사 관련 비즈니스 로직을 처리하는 서비스

    @Autowired
    public AuthController(CounselorService counselorService) {
        this.counselorService = counselorService;
    }

    // 회원가입 API 엔드포인트 (POST /api/auth/register)
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        // RegisterRequest DTO에서 받은 정보로 Counselor 모델 객체를 생성
        Counselor newCounselor = new Counselor();
        newCounselor.setUsername(request.getUsername());
        newCounselor.setPassword(request.getPassword()); // 비밀번호는 서비스 계층에서 해시화
        newCounselor.setEmail(request.getEmail());
        newCounselor.setName(request.getName());
        // 역할이 제공되지 않으면 기본값으로 "COUNSELOR"를 설정
        newCounselor.setRole(request.getRole() != null ? request.getRole() : "COUNSELOR");

        // CounselorService를 통해 상담사를 등록하고 결과를 받음
        return counselorService.registerCounselor(newCounselor)
                // 등록 성공 시: HTTP 201 Created 상태 코드와 함께 등록된 상담사 정보를 반환
                .<ResponseEntity<?>>map(counselor -> ResponseEntity.status(HttpStatus.CREATED).body(counselor))
                // 등록 실패 시 (예: 사용자명 중복): HTTP 400 Bad Request 상태 코드와 함께 오류 메시지를 반환
                .orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body("회원가입에 실패했거나 사용자명이 이미 존재합니다."));
    }

    // 로그인 API 엔드포인트 (POST /api/auth/login)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return counselorService.login(request.getUsername(), request.getPassword())
                // 로그인 성공 시: HTTP 200 OK 상태 코드와 함께 성공 메시지 및 상담사 ID를 반환
                // (실제 서비스에서는 JWT 토큰 등을 반환해야 합니다.)
                .<ResponseEntity<?>>map(counselor -> ResponseEntity.ok().body("로그인 성공! 상담사 ID: " + counselor.getId()))
                // 로그인 실패 시: HTTP 401 Unauthorized 상태 코드와 함께 인증 실패 메시지를 반환
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 사용자명 또는 비밀번호입니다."));
    }
}