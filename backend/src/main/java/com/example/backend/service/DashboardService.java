package com.example.backend.service;

import com.example.backend.dto.DashboardViewResponse;
import com.example.backend.repository.DashboardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class DashboardService {

    private final DashboardRepository dashboardRepository;

    @Autowired
    public DashboardService(DashboardRepository dashboardRepository) {
        this.dashboardRepository = dashboardRepository;
    }

    // 모든 대시보드 뷰 데이터를 조회하는 메서드
    public List<DashboardViewResponse> getAllDashboardViews() {
        return dashboardRepository.findAllDashboardViews();
    }

    // 특정 상담 ID로 대시보드 뷰 데이터를 조회하는 메서드
    public Optional<DashboardViewResponse> getDashboardViewByCounselId(String counselId) {
        return dashboardRepository.findDashboardViewByCounselId(counselId);
    }

    // 특정 상담사 이름으로 대시보드 뷰 데이터를 조회하는 메서드
    public List<DashboardViewResponse> getDashboardViewsByCounselorName(String counselorName) {
        return dashboardRepository.findDashboardViewsByCounselorName(counselorName);
    }
}