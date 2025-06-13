package com.example.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardViewResponse {
    private String consultationDate;     // 상담일자 (datetime)
    private String customerNumber;       // 고객번호 (customer_info)
    private Long counselorNumber;        // 상담사번호 (counselor_id)
    private String callNumber;           // Call번호 (counsel_id)
    private Float score;                 // Score (overall_score)
    private Boolean misguidance;          // 오안내 (misguidance_status)
    private Boolean forbiddenPhrases;     // 금지문구 (forbidden_phrases_status)
    private Boolean illegalCollection;    // 불법추심 (illegal_collection_status)
    private Boolean paymentIntention;     // 납부의사 (payment_intention_status)
}