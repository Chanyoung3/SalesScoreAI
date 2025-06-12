package com.example.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardViewResponse {
    // Python 테이블의 datetime 컬럼
    private String datetime; // YYYY-MM-DD HH:MM:SS 형식 문자열
    private String counselor;
    private String counselId; // Python에서 String(50)으로 다루므로 String으로 받음 (Java의 Long ID와 매핑)
    private Boolean overdueGuidanceStatus; // 미납 안내 여부
    private Boolean paymentInducementStatus; // 납부 유도 여부
    private String inducementMethod; // 유도 방식
    private String customerResponse; // 고객 반응
    private String overallJudgment; // 종합 판단
}