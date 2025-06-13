package com.example.backend.controller;

import com.example.backend.dto.ConsultationUploadResponse;
import com.example.backend.model.Consultation;
import com.example.backend.service.ConsultationService;
import org.springframework.beans.factory.annotation.Autowired; // 의존성 자동 주입
import org.springframework.http.HttpStatus; // HTTP 상태 코드
import org.springframework.http.ResponseEntity; // HTTP 응답 객체
import org.springframework.web.bind.annotation.*; // REST 컨트롤러 관련 어노테이션 임포트
import org.springframework.web.multipart.MultipartFile; // 파일 업로드 처리를 위한 클래스 임포트
import java.util.List;

@RestController
@RequestMapping("/api/consultations") // 이 컨트롤러의 모든 API 엔드포인트는 "/api/consultations"로 시작
public class ConsultationController {

    private final ConsultationService consultationService; // 상담 관련 비즈니스 로직을 처리하는 서비스

    @Autowired
    public ConsultationController(ConsultationService consultationService) {
        this.consultationService = consultationService;
    }

    // 상담 내용을 업로드하고 Ollama AI로 분석을 요청하는 API 엔드포인트 (POST /api/consultations/upload)
    // @RequestParam: 폼 데이터 또는 쿼리 파라미터로 전송된 값을 매핑
    // MultipartFile: 파일 업로드를 처리
    @PostMapping("/upload")
    public ResponseEntity<?> uploadConsultation(
            @RequestParam("counselorId") Long counselorId,    // 상담사 ID
            @RequestParam("customerInfo") String customerInfo, // 고객 정보
            @RequestParam("file") MultipartFile file) {        // 상담 내용 파일
        try {
            // ConsultationService를 호출하여 파일 업로드, Ollama AI 분석, DB 저장을 수행
            Consultation uploadedConsultation = consultationService.uploadConsultation(counselorId, customerInfo, file);
            // 성공 응답을 위한 DTO를 생성
            ConsultationUploadResponse response = new ConsultationUploadResponse(
                    uploadedConsultation.getId(),
                    "상담 내용이 성공적으로 업로드 및 분석되었습니다!",
                    uploadedConsultation.getOllamaScore(),
                    uploadedConsultation.getOllamaFeedback()
            );
            // HTTP 201 Created 상태 코드와 함께 응답 DTO를 반환
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            // 업로드 또는 분석 중 오류 발생 시 HTTP 500 Internal Server Error 상태 코드와 함께 오류 메시지를 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("상담 업로드 중 오류 발생: " + e.getMessage());
        }
    }

    //특정 상담 ID로 상담 정보를 조회하는 API 엔드포인트입니다.
    @GetMapping("/{id}")
    public ResponseEntity<?> getConsultationById(@PathVariable Long id) {
        return consultationService.getConsultationById(id)
                // 조회 성공 시: Optional에 값이 존재하면 이 map 블록이 실행
                .map(ResponseEntity::ok)
                // 조회 실패 시 (상담 정보를 찾을 수 없음): Optional에 값이 없으면 이 orElseGet 블록이 실행
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    // 특정 상담사 ID에 해당하는 모든 상담 목록을 조회하는 API 엔드포인트 (GET /api/consultations/counselor/{counselorId})
    @GetMapping("/counselor/{counselorId}")
    public ResponseEntity<List<Consultation>> getConsultationsByCounselor(
            @PathVariable Long counselorId) {
        // ConsultationService를 통해 특정 상담사의 모든 상담 목록을 조회
        List<Consultation> consultations = consultationService.getConsultationsByCounselorId(counselorId);
        // HTTP 200 OK 상태 코드와 함께 상담 목록을 반환
        return ResponseEntity.ok(consultations);
    }

    // 모든 상담 목록을 조회하는 API 엔드포인트 (GET /api/consultations/) - 관리자용
    @GetMapping("/")
    public ResponseEntity<List<Consultation>> getAllConsultations() {
        // ConsultationService를 통해 모든 상담 목록을 조회
        List<Consultation> consultations = consultationService.getAllConsultations();
        // HTTP 200 OK 상태 코드와 함께 상담 목록을 반환
        return ResponseEntity.ok(consultations);
    }

    @PostMapping("/detail")
    public ResponseEntity<List<Consultation>> getConsultationDetails(){
        List<Consultation> consultations = consultationService.getAllConsultations();
        return ResponseEntity.ok(consultations);
    }
}