package com.example.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScriptScoreComparisonItem {
    private String item;              // 항목 (예: "첫인사", "본인확인")
    private Float targetAvgMonthlyScore; // 대상자 월 평균 (점수)
    private Float overallAvgMonthlyScore; // 전체 월 평균 (점수)
}