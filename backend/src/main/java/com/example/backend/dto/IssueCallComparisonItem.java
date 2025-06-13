package com.example.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssueCallComparisonItem {
    private String item;              // 항목 (예: "불법추심", "금지문구")
    private Float targetFrequency;    // 대상자 빈도 (%)
    private Float overallFrequency;   // 전체 빈도 (%)
}