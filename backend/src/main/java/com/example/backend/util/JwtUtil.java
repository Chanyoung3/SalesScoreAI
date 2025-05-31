package com.example.backend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}") // application.properties에서 JWT 시크릿 키를 주입받는다.
    private String secret;

    @Value("${jwt.expiration}") // application.properties에서 토큰 만료 시간을 주입받는다. (초 단위)
    private Long expiration;

    private Key key; // JWT 서명에 사용할 키

    @PostConstruct // 스프링 빈 초기화 시점에 키를 생성합니다.
    public void init() {
        // secret 문자열을 기반으로 안전한 키를 생성합니다. (HS256 알고리즘에 적합)
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    // JWT 토큰을 생성하는 메서드
    public String generateToken(String username, String role, Long counselorId) {
        Map<String, Object> claims = new HashMap<>();
        // JWT Payload에 사용자 이름, 역할, 상담사 ID를 클레임으로 추가합니다.
        claims.put("role", role);
        claims.put("counselorId", counselorId); // 프론트엔드에서 상담사 ID를 바로 사용할 수 있도록 추가

        return Jwts.builder()
                .setClaims(claims) // 클레임 설정
                .setSubject(username) // 토큰의 주체(Subject)를 사용자 이름으로 설정
                .setIssuedAt(new Date(System.currentTimeMillis())) // 토큰 발행 시간
                // 토큰 만료 시간 = 현재 시간 + 설정된 만료 시간(초)
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(key, SignatureAlgorithm.HS256) // HS256 알고리즘과 생성된 키로 서명
                .compact(); // JWT 문자열 생성
    }

    // 토큰에서 클레임을 추출하는 메서드 (토큰 검증용)
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    // 토큰에서 사용자 이름을 추출하는 메서드
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // 토큰에서 역할(role)을 추출하는 메서드
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    // 토큰에서 상담사 ID를 추출하는 메서드
    public Long extractCounselorId(String token) {
        return extractAllClaims(token).get("counselorId", Long.class);
    }

    // 토큰이 만료되었는지 확인하는 메서드
    public Boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    // 토큰 유효성 검사 (사용자 이름 및 만료 여부)
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
}