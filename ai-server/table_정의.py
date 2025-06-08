from sqlalchemy import Table, Column, String, Boolean, Float, DateTime, Text, MetaData, JSON

metadata = MetaData()


# 백엔드로부터 상담 텍스트 파일을 받음

# LLM 분석
# T1: 미납 안내 스크립트 이행 여부
# T2: 고객의 납부에 대한 감정, 의향, 상황 판별
# T3: 본인확인 답변
# T4: 상담사 끝인사
# T5: 욕설/비속어


# 1차 결과 테이블: 프롬프트별 분석 결과 저장

counsel_analysis_raw = Table(
    "counsel_analysis_raw", metadata,
    Column("counsel_id", String, primary_key=True),
    Column("task_code", String),
    Column("result_json", JSON),
    Column("source_text", Text),
    Column("evaluated_at", DateTime)
)

# 2차 집계 테이블: 항목별 종합 평가 및 판단 로직 적용

counsel_summary = Table(
    "counsel_summary", metadata,
    Column("counsel_id", String, primary_key=True),
    Column("script_followed", Boolean),
    Column("customer_emotion", String),
    Column("payment_willingness", String),
    Column("situation_type", String),
    Column("auto_transfer_guided", Boolean),
    Column("virtual_account_guided", Boolean),
    Column("payment_positive_response", Boolean),
    Column("guided_method", String),
    Column("overall_score", Float),
    Column("summary_comment", Text)
)

# 3차 화면용 테이블: 사용자 UI에 필요한 필드 가공

counsel_dashboard_view = Table(
    "counsel_dashboard_view", metadata,
    Column("datetime", DateTime),
    Column("counselor", String),
    Column("counsel_id", String, primary_key=True),
    Column("미납안내_여부", Boolean),
    Column("납부유도_여부", Boolean),
    Column("유도_방식", String),
    Column("고객_반응", String),
    Column("종합_판단", String)
)