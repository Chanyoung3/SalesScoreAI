package com.example.backend.model;

import lombok.Data;
import lombok.NoArgsConstructor; // 인자 없는 기본 생성자를 자동으로 생성합니다.
import lombok.AllArgsConstructor; // 모든 필드를 인자로 받는 생성자를 자동으로 생성합니다.

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Consultation {
    private Long id;                  // 상담의 고유 ID (데이터베이스의 PK)
    private Long counselorId;         // 해당 상담을 진행한 상담사의 ID (외래 키)
    private String consultationDate;  // 상담이 이루어진 날짜 및 시간 (YYYY-MM-DD HH:MM:SS 형식의 문자열)
    private String customerInfo;      // 고객 정보 (예: 고객 이름, 연락처 등)
    private String transcriptFilePath; // 업로드된 상담 내용 텍스트 파일의 서버 내 저장 경로
    private String rawTranscriptContent; // 상담 내용의 원본 텍스트 (파일로 저장 시 필수로 필요하지 않을 수 있음)
    private Integer ollamaScore;      // O llama AI가 측정한 상담 점수 (정수, 0-100)
    private String ollamaFeedback;    // O llama AI가 제공하는 피드백 내용
    private String createdAt;         // 레코드 생성 시각 (데이터베이스에서 자동 설정)
    private String updatedAt;         // 레코드 마지막 업데이트 시각 (데이터베이스에서 자동 설정)
}