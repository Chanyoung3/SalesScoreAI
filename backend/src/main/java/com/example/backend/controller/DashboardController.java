package com.example.backend.controller;

import com.example.backend.dto.DashboardViewResponse; // DTO 임포트
import com.example.backend.service.DashboardService; // Service 임포트
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/dashboard") // 대시보드 API의 기본 경로
public class DashboardController {

    private final DashboardService dashboardService;

    @Autowired
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * 모든 대시보드 뷰 데이터를 조회합니다. (관리자용 또는 전체 요약 화면용)
     * URL: GET /api/dashboard
     *
     * @return 모든 대시보드 뷰 데이터 목록
     */
    @GetMapping
    public ResponseEntity<List<DashboardViewResponse>> getAllDashboardData() {
        List<DashboardViewResponse> data = dashboardService.getAllDashboardViews();
        return ResponseEntity.ok(data);
    }

    /**
     * 특정 상담 ID로 대시보드 뷰 데이터를 조회합니다.
     * URL: GET /api/dashboard/{counselId}
     *
     * @param counselId 조회할 상담 ID
     * @return 해당 상담 ID의 대시보드 뷰 데이터
     */
    @GetMapping("/{counselId}")
    public ResponseEntity<?> getDashboardDataByCounselId(@PathVariable String counselId) {
        Optional<DashboardViewResponse> data = dashboardService.getDashboardViewByCounselId(counselId);
        return data.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    /**
     * 특정 상담사 이름으로 대시보드 뷰 데이터를 조회합니다.
     * URL: GET /api/dashboard/counselor/{counselorName}
     *
     * @param counselorName 조회할 상담사 이름
     * @return 해당 상담사의 대시보드 뷰 데이터 목록
     */
    @GetMapping("/counselor/{counselorName}")
    public ResponseEntity<List<DashboardViewResponse>> getDashboardDataByCounselorName(@PathVariable String counselorName) {
        List<DashboardViewResponse> data = dashboardService.getDashboardViewsByCounselorName(counselorName);
        return ResponseEntity.ok(data);
    }
}