package com.example.backend.model;

import lombok.Data;
import lombok.NoArgsConstructor; // 인자 없는 기본 생성자를 자동으로 생성
import lombok.AllArgsConstructor; // 모든 필드를 인자로 받는 생성자를 자동으로 생성

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Counselor {
    private Long id;              // 상담사의 고유 ID (데이터베이스의 PK)
    private String username;      // 상담사 로그인 ID
    private String password;      // 상담사 비밀번호 (실제로는 해시된 값이 저장됩니다)
    private String email;         // 상담사 이메일
    private String name;          // 상담사 이름
    private String role;          // 상담사 역할 (예: "ADMIN", "COUNSELOR")
}
