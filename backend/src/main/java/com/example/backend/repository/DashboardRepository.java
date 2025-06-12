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
        dashboard.setDatetime(rs.getString("datetime"));
        dashboard.setCounselor(rs.getString("counselor"));
        dashboard.setCounselId(rs.getString("counsel_id"));
        dashboard.setOverdueGuidanceStatus(rs.getBoolean("미납안내_여부"));
        dashboard.setPaymentInducementStatus(rs.getBoolean("납부유도_여부"));
        dashboard.setInducementMethod(rs.getString("유도_방식"));
        dashboard.setCustomerResponse(rs.getString("고객_반응"));
        dashboard.setOverallJudgment(rs.getString("종합_판단"));
        return dashboard;
    };

    // 모든 대시보드 뷰 데이터를 조회하는 메서드
    public List<DashboardViewResponse> findAllDashboardViews() {
        String sql = "SELECT datetime, counselor, counsel_id, `미납안내_여부`, `납부유도_여부`, `유도_방식`, `고객_반응`, `종합_판단` FROM counsel_dashboard_view ORDER BY datetime DESC";
        // MySQL에서 특수문자나 공백이 있는 컬럼명은 `백틱`으로 감싸야 합니다.
        // 하지만 SQLAlchemy는 이미 백틱 없이도 잘 생성합니다.
        // 여기서는 SQL 쿼리에 그대로 한글 컬럼명을 사용합니다.
        return jdbcTemplate.query(sql, rowMapper);
    }

    // 특정 counsel_id에 해당하는 대시보드 뷰 데이터를 조회하는 메서드
    public Optional<DashboardViewResponse> findDashboardViewByCounselId(String counselId) {
        String sql = "SELECT datetime, counselor, counsel_id, `미납안내_여부`, `납부유도_여부`, `유도_방식`, `고객_반응`, `종합_판단` FROM counsel_dashboard_view WHERE counsel_id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, counselId));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // 특정 상담사 이름으로 대시보드 뷰 데이터를 조회하는 메서드 (counselor 필드가 '미정'이 아닌 경우 유용)
    public List<DashboardViewResponse> findDashboardViewsByCounselorName(String counselorName) {
        String sql = "SELECT datetime, counselor, counsel_id, `미납안내_여부`, `납부유도_여부`, `유도_방식`, `고객_반응`, `종합_판단` FROM counsel_dashboard_view WHERE counselor = ? ORDER BY datetime DESC";
        return jdbcTemplate.query(sql, rowMapper, counselorName);
    }
}