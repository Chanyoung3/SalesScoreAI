package com.example.backend.service;

import com.example.backend.model.Consultation;
import com.example.backend.repository.ConsultationRepository;
import org.springframework.beans.factory.annotation.Autowired; // 의존성 자동 주입
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile; // 파일 업로드 처리를 위한 클래스
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ConsultationService {

    private static final Logger logger = LoggerFactory.getLogger(ConsultationService.class); // 로깅을 위한 Logger 객체
    private final ConsultationRepository consultationRepository; // 상담 데이터 접근을 위한 리포지토리
    private final OllamaAiService ollamaAiService; // Ollama AI 서비스 (AI 분석 요청)

    // 상담 내용을 저장할 파일 디렉토리 경로 (프로젝트 루트에 'uploads' 폴더 생성)
    private final String UPLOAD_DIR = "./uploads/";

    @Autowired // 생성자 주입을 통해 ConsultationRepository와 OllamaAiService를 주입받음
    public ConsultationService(ConsultationRepository consultationRepository, OllamaAiService ollamaAiService) {
        this.consultationRepository = consultationRepository;
        this.ollamaAiService = ollamaAiService;
        // 서비스 초기화 시 업로드 디렉토리가 없으면 생성
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
            logger.info("Upload directory created or already exists: {}", UPLOAD_DIR);
        } catch (Exception e) {
            logger.error("Failed to create upload directory: {}", e.getMessage(), e);
            // 디렉토리 생성 실패는 애플리케이션 시작에 심각한 영향을 줄 수 있으므로 예외를 다시 던질 수 있음
            // throw new RuntimeException("Failed to initialize upload directory", e);
        }
    }

    // 상담 내용을 업로드하고 Ollama AI로 분석을 요청하는 메서드
    public Consultation uploadConsultation(Long counselorId, String customerInfo, MultipartFile file) throws Exception {
        // 1. 업로드된 파일을 서버에 저장
        String filePath = saveFile(file); // 파일 저장 경로 반환
        // 파일의 내용을 문자열로 읽어온다. (AI 분석에 사용)
        String rawContent = new String(file.getBytes());

        // 2. Consultation (상담) 객체를 생성하고 기본 정보를 설정
        Consultation consultation = new Consultation();
        consultation.setCounselorId(counselorId); // 상담사 ID 설정
        // 현재 날짜와 시간을 "YYYY-MM-DD HH:mm:ss" 형식으로 포매팅하여 설정
        consultation.setConsultationDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        consultation.setCustomerInfo(customerInfo); // 고객 정보 설정
        consultation.setTranscriptFilePath(filePath); // 저장된 파일 경로 설정
        consultation.setRawTranscriptContent(rawContent); // 원본 상담 내용 텍스트 저장

        // 3. Ollama AI 분석 요청:
       //========================================

        // 4. Ollama AI 분석 결과를 Consultation 객체에 설정
        //=======================================

        // 5. 모든 정보가 설정된 Consultation 객체를 데이터베이스에 저장하고 반환
        return consultationRepository.save(consultation);
    }

    // ID로 특정 상담 정보를 조회하는 메서드
    public Optional<Consultation> getConsultationById(Long id) {
        return consultationRepository.findById(id);
    }

    // 특정 상담사 ID에 해당하는 모든 상담 목록을 조회하는 메서드
    public List<Consultation> getConsultationsByCounselorId(Long counselorId) {
        return consultationRepository.findByCounselorId(counselorId);
    }

    // 모든 상담 목록을 조회하는 메서드 (관리자용)
    public List<Consultation> getAllConsultations() {
        return consultationRepository.findAll();
    }

    // 업로드된 파일을 서버의 지정된 디렉토리에 저장하는 프라이빗 메서드
    private String saveFile(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("업로드된 파일이 비어 있습니다.");
        }
        // 파일명 중복을 피하기 위해 현재 시간을 접두사로 붙인다.
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        // 파일을 저장할 전체 경로를 생성
        Path path = Paths.get(UPLOAD_DIR + fileName);
        // 업로드된 파일의 입력 스트림을 읽어 지정된 경로에 파일로 복사
        Files.copy(file.getInputStream(), path);
        return path.toString(); // 저장된 파일의 전체 경로를 문자열로 반환
    }
}