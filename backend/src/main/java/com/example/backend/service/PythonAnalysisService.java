package com.example.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PythonAnalysisService { // 클래스 이름 변경

    private static final Logger logger = LoggerFactory.getLogger(PythonAnalysisService.class);
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    // application.properties에서 파이썬 분석 서비스의 URL을 주입받습니다.
    @Value("${python.analysis.api.url}")
    private String pythonAnalysisApiUrl;

    public PythonAnalysisService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 파이썬 분석 서비스에 상담 분석을 요청합니다.
     * 이 메서드는 비동기적으로 호출되어, 파이썬 서비스가 분석을 완료한 후 DB를 업데이트할 것입니다.
     *
     * @param counselId 상담 ID
     * @param rawText 상담 내용 원본 텍스트
     */
    public void analyzeConsultation(Long counselId, String rawText) {
        logger.info("파이썬 분석 서비스 호출 중. URL: {}", pythonAnalysisApiUrl);

        // 파이썬 서비스에 보낼 요청 바디 (JSON)
        // 파이썬 서비스는 이 JSON을 받아 분석을 시작합니다.
        String requestBody = String.format("{\"counsel_id\": \"%s\", \"raw_text\": \"%s\"}",
                String.valueOf(counselId), escapeJson(rawText)); // counsel_id는 Long을 String으로 변환

        try {
            // WebClient를 사용하여 파이썬 서비스의 POST /analyze_consultation 엔드포인트로 요청을 보냅니다.
            // Python 서비스가 응답을 즉시 반환하지 않고, 분석을 백그라운드에서 진행할 것이므로,
            // 여기서는 응답 본문을 특별히 파싱하지 않고 요청만 보냅니다.
            webClient.post()
                    .uri(pythonAnalysisApiUrl + "/analyze_consultation") // 파이썬 서비스의 엔드포인트
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> clientResponse.bodyToMono(String.class).flatMap(
                                    errorBody -> Mono.error(new RuntimeException(
                                            String.format("파이썬 서비스 오류 (%d): %s", clientResponse.statusCode().value(), errorBody)
                                    ))
                            ))
                    .bodyToMono(String.class) // 응답 본문이 있다면 String으로 받음
                    .subscribe(
                            successBody -> logger.info("파이썬 서비스 응답: {}", successBody), // 성공 응답 처리
                            error -> logger.error("파이썬 서비스 호출 실패: {}", error.getMessage(), error) // 오류 응답 처리
                    );
            // .block(); // 여기서는 블로킹하지 않고 비동기로 요청을 보냅니다.
        } catch (Exception e) {
            logger.error("파이썬 분석 서비스 호출 중 예외 발생: {}", e.getMessage(), e);
            // 예외 발생 시 추가적인 로깅 또는 알림 처리
        }
    }

    // JSON 문자열 내부의 따옴표나 개행 문자 등 특수 문자를 이스케이프 처리하는 헬퍼 메서드입니다.
    private String escapeJson(String text) {
        return text.replace("\\", "\\\\") // 역슬래시 먼저 이스케이프
                .replace("\"", "\\\"") // 큰따옴표 이스케이프
                .replace("\n", "\\n") // 개행 문자 이스케이프
                .replace("\t", "\\t") // 탭 문자 이스케이프
                .replace("\r", "\\r"); // 캐리지 리턴 이스케이프
    }

    // 파이썬 서비스가 즉시 반환하는 결과가 있다면 이 클래스를 사용할 수 있지만,
    // 여기서는 파이썬이 비동기로 DB 업데이트를 하므로 이 클래스는 더 이상 사용되지 않을 수 있습니다.
    // public static class PythonAnalysisResult { ... }
}