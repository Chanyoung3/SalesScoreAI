package com.example.backend.repository;

import com.example.backend.model.Consultation;
import org.springframework.jdbc.core.JdbcTemplate; // 스프링의 JDBC 템플릿 사용
import org.springframework.jdbc.core.RowMapper; // ResultSet의 각 행을 객체로 매핑하기 위한 인터페이스
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired; // 의존성 자동 주입을 위한 어노테이션
import org.springframework.jdbc.support.GeneratedKeyHolder; // 자동 생성된 키 값을 가져오기 위한 클래스
import org.springframework.jdbc.support.KeyHolder;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class ConsultationRepository {

    private final JdbcTemplate jdbcTemplate; // JDBC 템플릿 객체

    @Autowired
    public ConsultationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Consultation> rowMapper = (rs, rowNum) -> {
        Consultation consultation = new Consultation();
        consultation.setId(rs.getLong("id"));                     // 'id' 컬럼 값 설정
        consultation.setCounselorId(rs.getLong("counselor_id"));   // 'counselor_id' 컬럼 값 설정
        consultation.setConsultationDate(rs.getString("consultation_date")); // 'consultation_date' 컬럼 값 설정
        consultation.setCustomerInfo(rs.getString("customer_info")); // 'customer_info' 컬럼 값 설정
        consultation.setTranscriptFilePath(rs.getString("transcript_file_path")); // 'transcript_file_path' 컬럼 값 설정
        consultation.setRawTranscriptContent(rs.getString("raw_transcript_content")); // 'raw_transcript_content' 컬럼 값 설정
        // getObject() 사용: Integer 타입으로 가져오며, DB값이 NULL인 경우에도 안전하게 처리
        consultation.setOllamaScore(rs.getObject("ollama_score", Integer.class));
        consultation.setOllamaFeedback(rs.getString("ollama_feedback")); // 'ollama_feedback' 컬럼 값 설정
        consultation.setCreatedAt(rs.getString("created_at"));   // 'created_at' 컬럼 값 설정
        consultation.setUpdatedAt(rs.getString("updated_at"));   // 'updated_at' 컬럼 값 설정
        return consultation; // 매핑된 Consultation 객체 반환
    };

    // 새로운 상담을 저장하거나 기존 상담 정보를 업데이트
    public Consultation save(Consultation consultation) {
        if (consultation.getId() == null) { // ID가 없으면 새로운 상담 등록 (INSERT)
            String sql = "INSERT INTO consultations (counselor_id, consultation_date, customer_info, transcript_file_path, raw_transcript_content, ollama_score, ollama_feedback) VALUES (?, ?, ?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder(); // 자동 생성된 키(ID)를 저장할 객체

            jdbcTemplate.update(connection -> {
                // PreparedStatement를 생성하며 자동 생성된 키를 반환하도록 설정
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, consultation.getCounselorId());
                ps.setString(2, consultation.getConsultationDate());
                ps.setString(3, consultation.getCustomerInfo());
                ps.setString(4, consultation.getTranscriptFilePath());
                ps.setString(5, consultation.getRawTranscriptContent());
                // setObject: Integer 값이 null일 수도 있으므로 setInt 대신 setObject 사용
                ps.setObject(6, consultation.getOllamaScore());
                ps.setString(7, consultation.getOllamaFeedback());
                return ps;
            }, keyHolder);

            // 생성된 키(ID)를 Consultation 객체에 설정
            Number generatedId = keyHolder.getKey();
            if (generatedId != null) {
                consultation.setId(generatedId.longValue());
            }
            return consultation;
        } else { // ID가 있으면 기존 상담 정보 업데이트 (UPDATE)
            String sql = "UPDATE consultations SET counselor_id = ?, consultation_date = ?, customer_info = ?, transcript_file_path = ?, raw_transcript_content = ?, ollama_score = ?, ollama_feedback = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
            jdbcTemplate.update(sql,
                    consultation.getCounselorId(),
                    consultation.getConsultationDate(),
                    consultation.getCustomerInfo(),
                    consultation.getTranscriptFilePath(),
                    consultation.getRawTranscriptContent(),
                    consultation.getOllamaScore(),
                    consultation.getOllamaFeedback(),
                    consultation.getId());
            return consultation;
        }
    }

    // ID로 상담 정보를 찾아 반환
    public Optional<Consultation> findById(Long id) {
        String sql = "SELECT * FROM consultations WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, id));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // 특정 상담사의 모든 상담 목록을 찾아 반환 (최신순 정렬)
    public List<Consultation> findByCounselorId(Long counselorId) {
        String sql = "SELECT * FROM consultations WHERE counselor_id = ? ORDER BY consultation_date DESC";
        return jdbcTemplate.query(sql, rowMapper, counselorId);
    }

    // 모든 상담 목록을 찾아 반환 (최신순 정렬)
    public List<Consultation> findAll() {
        String sql = "SELECT * FROM consultations ORDER BY consultation_date DESC";
        return jdbcTemplate.query(sql, rowMapper);
    }
}