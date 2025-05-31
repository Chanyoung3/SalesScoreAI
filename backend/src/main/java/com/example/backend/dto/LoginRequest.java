package com.example.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    private String username; // 로그인 요청 시 클라이언트가 보낼 사용자명
    private String password; // 로그인 요청 시 클라이언트가 보낼 비밀번호
}