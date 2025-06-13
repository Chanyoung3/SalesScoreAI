package com.example.backend.repository;

import com.example.backend.dto.DashboardViewResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;

@Repository
public class DashboardRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DashboardRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // RowMapper 정의: counsel_dashboard_view 테이블의 각 행을 DashboardViewResponse 객체로 매핑
    private final RowMapper<DashboardViewResponse> rowMapper = (rs, rowNum) -> {
        DashboardViewResponse dashboard = new DashboardViewResponse();
        dashboard.setConsultationDate(rs.getString("datetime"));
        dashboard.setCustomerNumber(rs.getString("customer_info"));
        dashboard.setCounselorNumber(rs.getLong("counselor_id"));
        dashboard.setCallNumber(rs.getString("counsel_id"));
        dashboard.setScore(rs.getFloat("overall_score"));
        dashboard.setMisguidance(rs.getBoolean("misguidance_status")); // Boolean
        dashboard.setForbiddenPhrases(rs.getBoolean("forbidden_phrases_status")); // Boolean
        dashboard.setIllegalCollection(rs.getBoolean("illegal_collection_status")); // Boolean
        dashboard.setPaymentIntention(rs.getBoolean("payment_intention_status")); // Boolean
        return dashboard;
    };

    // 모든 대시보드 뷰 데이터를 조회하는 메서드
    public List<DashboardViewResponse> findAllDashboardViews() {
        String sql = "SELECT datetime, customer_info, counselor_id, counsel_id, overall_score, misguidance_status, forbidden_phrases_status, illegal_collection_status, payment_intention_status FROM counsel_dashboard_view ORDER BY datetime DESC";
        return jdbcTemplate.query(sql, rowMapper);
    }

    // 특정 counsel_id에 해당하는 대시보드 뷰 데이터를 조회하는 메서드
    public Optional<DashboardViewResponse> findDashboardViewByCounselId(String counselId) {
        String sql = "SELECT datetime, customer_info, counselor_id, counsel_id, overall_score, misguidance_status, forbidden_phrases_status, illegal_collection_status, payment_intention_status FROM counsel_dashboard_view WHERE counsel_id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, counselId));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // 특정 상담사 ID에 해당하는 모든 대시보드 뷰 데이터를 조회하는 메서드
    public List<DashboardViewResponse> findDashboardViewsByCounselorId(Long counselorId) { // <-- 새로운 메서드 추가
        String sql = "SELECT datetime, customer_info, counselor_id, counsel_id, overall_score, misguidance_status, forbidden_phrases_status, illegal_collection_status, payment_intention_status FROM counsel_dashboard_view WHERE counselor_id = ? ORDER BY datetime DESC";
        return jdbcTemplate.query(sql, rowMapper, counselorId);
    }
}