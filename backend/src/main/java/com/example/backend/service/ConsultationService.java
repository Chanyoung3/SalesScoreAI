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
import com.example.backend.repository.CounselorRepository;

import java.io.IOException;


@Service
public class ConsultationService {

    private static final Logger logger = LoggerFactory.getLogger(ConsultationService.class); // 로깅을 위한 Logger 객체
    private final ConsultationRepository consultationRepository; // 상담 데이터 접근을 위한 리포지토리
    // private final OllamaAiService ollamaAiService; // Ollama AI 서비스 (AI 분석 요청)
    private final PythonAnalysisService pythonAnalysisService; // 새로 정의할 파이썬 분석 서비스
    private final CounselorRepository counselorRepository;

    // 상담 내용을 저장할 파일 디렉토리 경로 (프로젝트 루트에 'uploads' 폴더 생성)
    private final String UPLOAD_DIR = "./uploads/";

    @Autowired
    public ConsultationService(ConsultationRepository consultationRepository, PythonAnalysisService pythonAnalysisService,CounselorRepository counselorRepository) {
        this.consultationRepository = consultationRepository;
        this.pythonAnalysisService = pythonAnalysisService; // PythonAnalysisService 주입
        this.counselorRepository = counselorRepository;
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
            logger.info("Upload directory created or already exists: {}", UPLOAD_DIR);
        } catch (IOException e) {
            logger.error("Failed to create upload directory: {}", e.getMessage(), e);
        }
    }

    // 상담 내용을 업로드하고 Ollama AI로 분석을 요청하는 메서드
    public Consultation uploadConsultation(Long counselorId, String customerInfo, MultipartFile file) throws Exception {
        // 업로드된 파일을 서버에 저장
        String filePath = saveFile(file); // 파일 저장 경로 반환
        // 파일의 내용을 문자열로 읽어온다. (AI 분석에 사용)
        String rawContent = new String(file.getBytes());

        // Consultation (상담) 객체를 생성하고 기본 정보를 설정
        Consultation consultation = new Consultation();
        consultation.setCounselorId(counselorId); // 상담사 ID 설정
        // 현재 날짜와 시간을 "YYYY-MM-DD HH:mm:ss" 형식으로 포매팅하여 설정
        consultation.setConsultationDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        consultation.setCustomerInfo(customerInfo); // 고객 정보 설정
        consultation.setTranscriptFilePath(filePath); // 저장된 파일 경로 설정
        consultation.setRawTranscriptContent(rawContent); // 원본 상담 내용 텍스트 저장

        // Ollama AI 분석 결과를 Consultation 객체에 설정합니다.
        consultation.setOllamaScore(null);
        consultation.setOllamaFeedback(null);

        // 데이터베이스에 먼저 저장하여 ID를 할당받습니다.
        Consultation savedConsultation = consultationRepository.save(consultation);

        // --- 상담사 이름 조회 ---
        String counselorName = counselorRepository.findById(counselorId) // counselorId로 상담사 조회
                .map(c -> c.getName()) // 상담사 객체에서 이름(name)을 추출
                .orElse("미정"); // 이름을 찾지 못하면 기본값 "미정"

        // 파이썬 분석 서비스에 분석 요청 (비동기 처리)
        logger.info("파이썬 분석 서비스에 상담 ID {} 분석 요청 중...", savedConsultation.getId());
        try {
            // 파이썬 서비스는 비동기로 분석을 수행하고, 완료되면 DB를 업데이트할 것입니다.
            // 여기서는 응답을 기다리지 않고 요청만 보냅니다.
            // 필요한 경우 PythonAnalysisService의 analyzeConsultation 메서드가 Promise/CompletableFuture 등을 반환하도록 할 수 있습니다.
            pythonAnalysisService.analyzeConsultation(savedConsultation.getId(), rawContent, counselorName);
            logger.info("파이썬 분석 서비스 요청 완료. (비동기 처리)");
        } catch (Exception e) {
            logger.error("파이썬 분석 서비스 호출 중 오류 발생: {}", e.getMessage(), e);
        }
        // 프론트엔드에는 초기 저장된 Consultation 객체를 반환 (점수/피드백은 아직 NULL)
        return savedConsultation;
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