package com.example.backend.service;

import com.example.backend.dto.DashboardViewResponse;
import com.example.backend.dto.DashboardStatisticsResponse;
import com.example.backend.dto.ScriptScoreComparisonItem;
import com.example.backend.dto.IssueCallComparisonItem;
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

    // 특정 상담사 ID에 해당하는 모든 대시보드 뷰 데이터를 조회하는 메서드입니다.
    public List<DashboardViewResponse> getDashboardViewsByCounselorId(Long counselorId) {
        return dashboardRepository.findDashboardViewsByCounselorId(counselorId);
    }

    // 대시보드 통계 데이터를 조회합니다.
    public DashboardStatisticsResponse getDashboardStatistics(Long counselorId, String yearMonth) {
        List<ScriptScoreComparisonItem> scriptScores = dashboardRepository.getScriptScoreComparison(counselorId, yearMonth);
        List<IssueCallComparisonItem> issueCalls = dashboardRepository.getIssueCallComparison(counselorId, yearMonth);

        // --- 새로운 4가지 통계 데이터 계산 및 추가 ---
        Float specificCounselorOverallAvgScore = dashboardRepository.getAvgOverallScore(counselorId, yearMonth);
        Float overallOverallAvgScore = dashboardRepository.getAvgOverallScore(null, yearMonth); // 전체 상담사 평균
        Float specificCounselorLowScoreCallFrequency = dashboardRepository.getLowScoreCallFrequency(counselorId, yearMonth);
        Float overallLowScoreCallFrequency = dashboardRepository.getLowScoreCallFrequency(null, yearMonth); // 전체 상담사 50점 미만 빈도

        DashboardStatisticsResponse response = new DashboardStatisticsResponse(scriptScores,
                issueCalls,
                specificCounselorOverallAvgScore,
                overallOverallAvgScore,
                specificCounselorLowScoreCallFrequency,
                overallLowScoreCallFrequency);

        return response;
    }

    // 전체 상담사에 대한 대시보드 통계 데이터를 조회합니다.
    public DashboardStatisticsResponse getOverallDashboardStatistics(String yearMonth) {
        return getDashboardStatistics(null, yearMonth);
    }
}