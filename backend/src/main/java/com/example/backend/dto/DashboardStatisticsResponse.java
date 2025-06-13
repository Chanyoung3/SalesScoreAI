package com.example.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatisticsResponse {
    private List<ScriptScoreComparisonItem> scriptScores; // 항목별 스크립트 Score 비교
    private List<IssueCallComparisonItem> issueCalls;     // 항목별 문제소지 콜 비교(%)

    private Float specificCounselorOverallAvgScore;    // 1. 그 상담사의 모든 스코어 평균
    private Float overallOverallAvgScore;              // 2. 모든 상담사의 모든 평균 점수
    private Float specificCounselorLowScoreCallFrequency; // 3. 그 상담사의 50점 미만 콜 빈도 (%)
    private Float overallLowScoreCallFrequency;         // 4. 모든 상담사의 50점 미만 콜 빈도 (%)
}