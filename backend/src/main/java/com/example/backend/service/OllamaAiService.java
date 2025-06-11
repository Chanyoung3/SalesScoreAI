// src/main/java/com/ollamacounseling/backend/service/OllamaAiService.java
package com.example.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Service
public class OllamaAiService {

    private static final Logger logger = LoggerFactory.getLogger(OllamaAiService.class);
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${ollama.api.url}")
    private String ollamaApiUrl;
    @Value("${ollama.model.name}")
    private String ollamaModelName;

    // 생성자 주입을 통해 WebClient.Builder를 주입받아 WebClient 인스턴스를 생성
    // WebClient.Builder는 Spring Boot가 자동으로 구성하여 제공
    public OllamaAiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build(); // 빌더를 사용하여 WebClient 인스턴스 생성
        this.objectMapper = new ObjectMapper(); // ObjectMapper 인스턴스 생성
    }
    public OllamaAnalysisResult analyzeConsultation(String consultationText) {
        logger.info("Ollama AI API 호출 중. 모델: {}, URL: {}", ollamaModelName, ollamaApiUrl);

        // Ollama AI에게 보낼 프롬프트를 구성
        // **[올라마 프롬프트 담당자 연동 필요]**:
        String prompt = String.format("다음 상담 내용을 분석하여 메뉴얼 준수 여부를 판단하고 점수(0-100)와 피드백을 제공해줘. 응답은 JSON 형식으로 부탁해. 예시: {\"score\": 85, \"feedback\": \"친절하고 명확하게 응대했습니다.\"}\n\n상담 내용:\n%s", consultationText);

        // Ollama API 요청 바디를 JSON 문자열로 구성
        // "model": 사용할 AI 모델 이름
        // "prompt": AI에게 보낼 프롬프트 내용 (특수 문자 이스케이프 처리 필요)
        // "stream": false: 스트리밍 응답이 아닌 단일 응답을 받도록 설정
        // "format": "json": Ollama AI에게 응답을 JSON 형식으로 받도록 요청
        String requestBody = String.format("{\"model\": \"%s\", \"prompt\": \"%s\", \"stream\": false, \"format\": \"json\"}",
                ollamaModelName, escapeJson(prompt)); // 프롬프트 내 특수 문자 이스케이프 처리

        try {
            // WebClient를 사용하여 Ollama 서버의 '/api/generate' 엔드포인트로 POST 요청을 보냅니다.
            // 이 엔드포인트는 Ollama AI 모델에게 텍스트 생성을 요청하는 표준 엔드포인트입니다.
            String responseBody = webClient.post()
                    .uri(ollamaApiUrl + "/api/generate") // Ollama AI의 텍스트 생성 엔드포인트 URL
                    .header("Content-Type", "application/json") // 요청 헤더에 Content-Type 설정
                    .bodyValue(requestBody) // 요청 바디에 JSON 문자열 설정
                    .retrieve()
                    .bodyToMono(String.class) // 응답 바디를 String 타입의 Mono(리액티브 스트림)로 변환
                    .block(); // Mono 결과를 블로킹하여 기다립니다. (실제 운영 환경에서는 비동기 처리(subscribe 등)를 권장)

            logger.debug("Ollama API 응답: {}", responseBody);

            // Ollama AI의 응답을 JSON으로 파싱
            // Ollama의 일반적인 응답 구조는 {"model": "...", "response": "{\"score\": 85, ...}", ...} 형태
            // 따라서 'response' 필드에 담긴 실제 JSON 문자열을 다시 파싱
            JsonNode rootNode = objectMapper.readTree(responseBody); // 전체 응답 JSON을 파싱
            JsonNode responseNode = rootNode.path("response");

            // 'response' 필드가 문자열(예: "{\"score\": 85, ...}")이므로, 이 문자열을 다시 JSON으로 파싱
            JsonNode actualResultNode = objectMapper.readTree(responseNode.asText());

            // 파싱된 JSON에서 'score'와 'feedback' 값을 추출
            // path("필드명").asInt(-1): 필드 값이 없으면 -1을 기본값으로 반환
            int score = actualResultNode.path("score").asInt(-1);
            // path("필드명").asText("기본값"): 필드 값이 없으면 "기본값"을 반환
            String feedback = actualResultNode.path("feedback").asText("분석 결과 없음");

            // 분석 결과를 담은 OllamaAnalysisResult 객체를 반환
            return new OllamaAnalysisResult(score, feedback);

        } catch (Exception e) { // Ollama API 호출 중 예외 발생 시 (네트워크 오류, JSON 파싱 오류 등)
            logger.error("Ollama AI API 호출 중 오류 발생: {}", e.getMessage(), e); // 오류 로그
            return new OllamaAnalysisResult(0, "Ollama AI 분석 중 오류 발생: " + e.getMessage());
        }
    }

    // JSON 문자열 내부의 따옴표나 개행 문자 등 특수 문자를 이스케이프 처리하는 헬퍼 메서드.
    private String escapeJson(String text) {
        return text.replace("\"", "\\\"") // 큰따옴표를 역슬래시 큰따옴표로 이스케이프
                .replace("\n", "\\n") // 개행 문자를 역슬래시 n으로 이스케이프
                .replace("\t", "\\t") // 탭 문자를 역슬래시 t로 이스케이프
                .replace("\r", "\\r"); // 캐리지 리턴 문자를 역슬래시 r로 이스케이프
    }

    // Ollama AI 분석 결과를 담기 위한 내부 데이터 클래스
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OllamaAnalysisResult {
        private int score;    // 분석된 점수 (정수)
        private String feedback; // 분석된 피드백 내용 (문자열)
    }
}