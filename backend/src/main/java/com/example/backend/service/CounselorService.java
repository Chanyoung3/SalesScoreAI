package com.example.backend.service;

import com.example.backend.model.Counselor;
import com.example.backend.repository.CounselorRepository;
import org.springframework.beans.factory.annotation.Autowired; // 의존성 자동 주입을 위한 어노테이션
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // 비밀번호 암호화를 위한 클래스 임포트

import java.util.List;
import java.util.Optional; // Optional 클래스 임포트

@Service
public class CounselorService {

    private final CounselorRepository counselorRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired // 생성자 주입을 통해 CounselorRepository와 BCryptPasswordEncoder를 주입받음
    public CounselorService(CounselorRepository counselorRepository, BCryptPasswordEncoder passwordEncoder) {
        this.counselorRepository = counselorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 새로운 상담사를 등록하는 메서드
    public Optional<Counselor> registerCounselor(Counselor counselor) {
        // 1. 사용자명 중복 확인 (이미 존재하는 사용자명이라면 등록 실패)
        if (counselorRepository.findByUsername(counselor.getUsername()).isPresent()) {
            return Optional.empty();
        }
        // 2. 비밀번호 해시화: 보안을 위해 평문 비밀번호를 그대로 저장하지 않고 해시화
        counselor.setPassword(passwordEncoder.encode(counselor.getPassword()));
        // 3. 상담사 정보를 데이터베이스에 저장, 저장된 객체를 Optional로 감싸 반환
        return Optional.ofNullable(counselorRepository.save(counselor));
    }

    // 상담사 로그인을 처리하는 메서드
    public Optional<Counselor> login(String username, String rawPassword) {
        // 1. 사용자명으로 상담사 정보를 데이터베이스에서 조회
        Optional<Counselor> counselor = counselorRepository.findByUsername(username);
        // 2. 상담사가 존재하고, 입력된 비밀번호(rawPassword)와 저장된 해시된 비밀번호가 일치하는지 확인
        if (counselor.isPresent() && passwordEncoder.matches(rawPassword, counselor.get().getPassword())) {
            return counselor; // 로그인 성공 시 상담사 객체를 반환
        }
        return Optional.empty(); // 로그인 실패 시 Optional.empty()를 반환
    }

    // ID로 상담사 정보를 조회하는 메서드
    public Optional<Counselor> getCounselorById(Long id) {
        return counselorRepository.findById(id); // 리포지토리를 통해 상담사 정보를 조회
    }

    // 모든 상담사 정보를 조회하는 메서드 (관리자용)
    public List<Counselor> getAllCounselors() {
        return counselorRepository.findAll();
    }
}