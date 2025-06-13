package com.example.backend.controller;

import com.example.backend.dto.DashboardViewResponse;
import com.example.backend.dto.DashboardStatisticsResponse;
import com.example.backend.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @Autowired
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    // 모든 대시보드 뷰 데이터를 조회합니다. (관리자용 또는 전체 요약 화면용)
    @GetMapping
    public ResponseEntity<List<DashboardViewResponse>> getAllDashboardData() {
        List<DashboardViewResponse> data = dashboardService.getAllDashboardViews();
        return ResponseEntity.ok(data);
    }

    // 특정 상담 ID로 대시보드 뷰 데이터를 조회합니다.
    @GetMapping("/{counselId}")
    public ResponseEntity<?> getDashboardDataByCounselId(@PathVariable String counselId) {
        Optional<DashboardViewResponse> data = dashboardService.getDashboardViewByCounselId(counselId);
        return data.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    // 특정 상담사 ID에 해당하는 모든 대시보드 뷰 데이터를 조회하는 API 엔드포인트입니다.
    @GetMapping("/counselor_id/{counselorId}")
    public ResponseEntity<List<DashboardViewResponse>> getDashboardDataByCounselorId(@PathVariable Long counselorId) {
        List<DashboardViewResponse> data = dashboardService.getDashboardViewsByCounselorId(counselorId);
        return ResponseEntity.ok(data);
    }

    // 항목별 통계 데이터를 조회합니다. (특정 상담사 또는 전체)
    @GetMapping("/statistics")
    public ResponseEntity<DashboardStatisticsResponse> getDashboardStatistics(
            @RequestParam(required = false) Long counselorId,
            @RequestParam String yearMonth) {

        DashboardStatisticsResponse response;
        if (counselorId != null) {
            response = dashboardService.getDashboardStatistics(counselorId, yearMonth);
        } else {
            response = dashboardService.getOverallDashboardStatistics(yearMonth);
        }
        return ResponseEntity.ok(response);
    }
}