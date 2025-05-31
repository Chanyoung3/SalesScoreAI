package com.example.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationUploadResponse {
    private Long consultationId; // 업로드된 상담의 고유 ID
    private String message;      // 응답 메시지 (예: "성공적으로 업로드되었습니다!")
    private Integer score;       // Ollama AI가 측정한 상담 점수
    private String feedback;     // Ollama AI가 제공하는 피드백 내용
}