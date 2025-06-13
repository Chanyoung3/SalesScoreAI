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
}