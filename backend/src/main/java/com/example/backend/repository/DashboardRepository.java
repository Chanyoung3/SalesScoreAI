package com.example.backend.repository;

import com.example.backend.dto.DashboardViewResponse;
import com.example.backend.dto.ScriptScoreComparisonItem;
import com.example.backend.dto.IssueCallComparisonItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

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
        dashboard.setMisguidance(rs.getBoolean("misguidance_status"));
        dashboard.setForbiddenPhrases(rs.getBoolean("forbidden_phrases_status"));
        dashboard.setIllegalCollection(rs.getBoolean("illegal_collection_status"));
        dashboard.setPaymentIntention(rs.getBoolean("payment_intention_status"));
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

    // 항목별 스크립트 Score 비교 데이터를 조회합니다.
    public List<ScriptScoreComparisonItem> getScriptScoreComparison(Long counselorId, String yearMonth) {
        // 월별 필터링을 위한 날짜 범위 설정
        String startDate = yearMonth + "-01 00:00:00";
        String endDate = YearMonth.parse(yearMonth, DateTimeFormatter.ofPattern("yyyy-MM")).atEndOfMonth().atTime(23, 59, 59).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        List<ScriptScoreComparisonItem> results = new ArrayList<>();

        // 각 항목별 평균 점수를 계산하는 SQL (counsel_summary 테이블에서 가져옴)
        // T6-T9 점수가 counsel_summary에 float으로 저장되어 있다고 가정
        // counselorId가 null이면 전체, 아니면 특정 상담사
        String baseSql = "SELECT AVG(first_impression_score), AVG(identity_verification_score), AVG(mandatory_info_score), AVG(closing_greeting_score) FROM counsel_summary cs JOIN consultations c ON cs.counsel_id = c.id WHERE DATE_FORMAT(c.consultation_date, '%Y-%m-%d %H:%i:%s') BETWEEN ? AND ?";
        String whereClause = (counselorId != null) ? " AND c.counselor_id = ?" : "";

        // 대상자 월 평균 쿼리
        Object[] targetParams = (counselorId != null) ? new Object[]{startDate, endDate, counselorId} : new Object[]{startDate, endDate};
        Float[] targetAverages = jdbcTemplate.queryForObject(baseSql + whereClause, targetParams, (rs, rowNum) -> new Float[]{
                rs.getFloat(1), rs.getFloat(2), rs.getFloat(3), rs.getFloat(4)
        });

        // 전체 월 평균 쿼리
        Object[] overallParams = new Object[]{startDate, endDate};
        Float[] overallAverages = jdbcTemplate.queryForObject(baseSql, overallParams, (rs, rowNum) -> new Float[]{
                rs.getFloat(1), rs.getFloat(2), rs.getFloat(3), rs.getFloat(4)
        });

        // 결과 DTO 리스트에 추가
        results.add(new ScriptScoreComparisonItem("첫인사", targetAverages[0], overallAverages[0]));
        results.add(new ScriptScoreComparisonItem("본인확인", targetAverages[1], overallAverages[1]));
        results.add(new ScriptScoreComparisonItem("필수안내", targetAverages[2], overallAverages[2]));
        results.add(new ScriptScoreComparisonItem("끝인사", targetAverages[3], overallAverages[3]));

        return results;
    }

    // 항목별 문제소지 콜 비교(%) 데이터를 조회합니다.
    public List<IssueCallComparisonItem> getIssueCallComparison(Long counselorId, String yearMonth) {
        String startDate = yearMonth + "-01 00:00:00";
        String endDate = YearMonth.parse(yearMonth, DateTimeFormatter.ofPattern("yyyy-MM")).atEndOfMonth().atTime(23, 59, 59).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        List<IssueCallComparisonItem> results = new ArrayList<>();

        // 각 문제소지 항목의 총 콜 수 (True인 경우)를 계산하는 SQL
        // counsel_summary에 is_misguidance, is_forbidden_phrases, is_illegal_collection, is_civil_complaint가 Boolean으로 저장되어 있다고 가정
        String countSql = "SELECT SUM(CASE WHEN is_misguidance = TRUE THEN 1 ELSE 0 END), " +
                "SUM(CASE WHEN is_forbidden_phrases = TRUE THEN 1 ELSE 0 END), " +
                "SUM(CASE WHEN is_illegal_collection = TRUE THEN 1 ELSE 0 END), " +
                "SUM(CASE WHEN is_civil_complaint = TRUE THEN 1 ELSE 0 END), " +
                "COUNT(counsel_id) " + // 전체 콜 수
                "FROM counsel_summary cs JOIN consultations c ON cs.counsel_id = c.id WHERE DATE_FORMAT(c.consultation_date, '%Y-%m-%d %H:%i:%s') BETWEEN ? AND ?";
        String whereClause = (counselorId != null) ? " AND c.counselor_id = ?" : "";

        // 대상자 빈도 쿼리
        Object[] targetParams = (counselorId != null) ? new Object[]{startDate, endDate, counselorId} : new Object[]{startDate, endDate};
        Float[] targetCounts = jdbcTemplate.queryForObject(countSql + whereClause, targetParams, (rs, rowNum) -> new Float[]{
                rs.getFloat(1), rs.getFloat(2), rs.getFloat(3), rs.getFloat(4), rs.getFloat(5) // misguidance, forbidden, illegal, civil_complaint, total_calls
        });

        // 전체 빈도 쿼리
        Object[] overallParams = new Object[]{startDate, endDate};
        Float[] overallCounts = jdbcTemplate.queryForObject(countSql, overallParams, (rs, rowNum) -> new Float[]{
                rs.getFloat(1), rs.getFloat(2), rs.getFloat(3), rs.getFloat(4), rs.getFloat(5)
        });

        // 비율 계산 (0으로 나누는 오류 방지)
        float targetTotalCalls = (targetCounts[4] > 0) ? targetCounts[4] : 1; // 0이면 1로 나누어 오류 방지
        float overallTotalCalls = (overallCounts[4] > 0) ? overallCounts[4] : 1;

        results.add(new IssueCallComparisonItem("오안내", (targetCounts[0] / targetTotalCalls) * 100, (overallCounts[0] / overallTotalCalls) * 100));
        results.add(new IssueCallComparisonItem("금지문구", (targetCounts[1] / targetTotalCalls) * 100, (overallCounts[1] / overallTotalCalls) * 100));
        results.add(new IssueCallComparisonItem("불법추심", (targetCounts[2] / targetTotalCalls) * 100, (overallCounts[2] / overallTotalCalls) * 100));
        results.add(new IssueCallComparisonItem("민원성", (targetCounts[3] / targetTotalCalls) * 100, (overallCounts[3] / overallTotalCalls) * 100));

        return results;
    }

    // 특정 기간 내의 전체 상담 점수 평균을 조회
    public Float getAvgOverallScore(Long counselorId, String yearMonth) {
        String startDate = yearMonth + "-01 00:00:00";
        String endDate = YearMonth.parse(yearMonth, DateTimeFormatter.ofPattern("yyyy-MM")).atEndOfMonth().atTime(23, 59, 59).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String sql = "SELECT AVG(cs.overall_score) FROM counsel_summary cs JOIN consultations c ON cs.counsel_id = c.id WHERE c.consultation_date BETWEEN ? AND ?";
        String whereClause = (counselorId != null) ? " AND c.counselor_id = ?" : "";

        Object[] params = (counselorId != null) ? new Object[]{startDate, endDate, counselorId} : new Object[]{startDate, endDate};
        return jdbcTemplate.queryForObject(sql + whereClause, params, Float.class);
    }

    // 특정 기간 내의 50점 미만 콜 빈도(%)를 조회
    public Float getLowScoreCallFrequency(Long counselorId, String yearMonth) {
        String startDate = yearMonth + "-01 00:00:00";
        String endDate = YearMonth.parse(yearMonth, DateTimeFormatter.ofPattern("yyyy-MM")).atEndOfMonth().atTime(23, 59, 59).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String sql = "SELECT (SUM(CASE WHEN cs.overall_score < 50 THEN 1 ELSE 0 END) * 100.0) / COUNT(cs.counsel_id) FROM counsel_summary cs JOIN consultations c ON cs.counsel_id = c.id WHERE c.consultation_date BETWEEN ? AND ?";
        String whereClause = (counselorId != null) ? " AND c.counselor_id = ?" : "";

        Object[] params = (counselorId != null) ? new Object[]{startDate, endDate, counselorId} : new Object[]{startDate, endDate};
        // COUNT가 0일 경우를 대비하여 NULL 허용 또는 0 반환 처리
        Float result = jdbcTemplate.queryForObject(sql + whereClause, params, Float.class);
        return (result != null && !result.isNaN()) ? result : 0.0f; // NaN 방지 및 null 처리
    }
}
