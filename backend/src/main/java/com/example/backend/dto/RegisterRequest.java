package com.example.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String username; // 회원가입 요청 시 클라이언트가 보낼 사용자명
    private String password; // 회원가입 요청 시 클라이언트가 보낼 비밀번호
    private String email;    // 회원가입 요청 시 클라이언트가 보낼 이메일
    private String name;     // 회원가입 요청 시 클라이언트가 보낼 이름
    private String role;     // 회원가입 요청 시 클라이언트가 보낼 역할 (예: "COUNSELOR" 또는 "ADMIN")
}
