import os
import json
import subprocess
from datetime import datetime, timezone
from pathlib import Path
from sqlalchemy import (
    create_engine, Table, Column, String, Boolean, Float, DateTime,
    Text, MetaData, JSON, PrimaryKeyConstraint
)
from sqlalchemy.orm import sessionmaker


# 설정
DATA_DIR = Path("data")
PROMPT_DIR = Path("prompt")
MODEL_NAME = "llama3:70b"
DB_PATH = "sqlite:///counsel_analysis.db"


# DB 및 테이블 정의
engine = create_engine(DB_PATH, echo=False, future=True)
metadata = MetaData()


# 1차: LLM 결과 원본 저장 테이블
counsel_analysis_raw = Table(
    "counsel_analysis_raw", metadata,
    Column("counsel_id", String),
    Column("task_code", String),
    Column("result_json", JSON),
    Column("source_text", Text),
    Column("evaluated_at", DateTime),
    PrimaryKeyConstraint("counsel_id", "task_code")
)

# 2차: 집계 테이블
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

# 3차: 화면용 테이블
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

# 테이블 생성
metadata.create_all(engine)
Session = sessionmaker(bind=engine)
session = Session()


# 프롬프트 호출

TASK_CODES = ["T1", "T2", "T3", "T4", "T5"]

def load_prompt(task_code):
    with open(PROMPT_DIR / f"{task_code}.txt", encoding="utf-8") as f:
        return f.read()

def query_ollama_local(prompt, model=MODEL_NAME):
    try:
        result = subprocess.run(
            ["ollama", "run", model],
            input=prompt,
            text=True,
            capture_output=True,
            timeout=300  # 충분한 시간 확보
        )
        return result.stdout.strip()
    except Exception as e:
        print(f"Ollama 실행 오류: {e}")
        return ""

def try_parse_json(text):
    try:
        return json.loads(text)
    except:
        return {"parse_error": True, "raw_text": text}


# 분석 및 DB 저장

def analyze_file(file_path):
    with open(file_path, encoding="utf-8") as f:
        dialogue = f.read()
    counsel_id = file_path.stem

    for task_code in TASK_CODES:
        prompt_template = load_prompt(task_code)
        prompt = prompt_template.replace("{input}", dialogue)

        result_text = query_ollama_local(prompt)
        if not result_text:
            print(f"{counsel_id}-{task_code} 응답 없음")
            continue

        parsed_json = try_parse_json(result_text)

        with engine.begin() as conn:
            existing = conn.execute(
                counsel_analysis_raw.select().where(
                    (counsel_analysis_raw.c.counsel_id == counsel_id) &
                    (counsel_analysis_raw.c.task_code == task_code)
                )
            ).fetchone()
            if existing:
                print(f"{counsel_id} - {task_code} 이미 존재")
                continue

            # 결과 백업 저장
            output_dir = Path("output")
            output_dir.mkdir(exist_ok=True)
            backup_path = output_dir / f"{counsel_id}_{task_code}.json"
            with open(backup_path, "w", encoding="utf-8") as f:
                json.dump(parsed_json, f, ensure_ascii=False, indent=2)

            conn.execute(counsel_analysis_raw.insert().values(
                counsel_id=counsel_id,
                task_code=task_code,
                result_json=parsed_json,
                source_text=dialogue,
                evaluated_at=datetime.now(timezone.utc)
            ))
        print(f"{counsel_id} - {task_code} 완료")


def summarize_results(counsel_id):
    with engine.connect() as conn:
        existing = conn.execute(
            counsel_summary.select().where(counsel_summary.c.counsel_id == counsel_id)
        ).fetchone()
        if existing:
            print(f"{counsel_id} 요약 이미 존재")
            return

        task_results = {}
        for task_code in TASK_CODES:
            row = conn.execute(
                counsel_analysis_raw.select().where(
                    (counsel_analysis_raw.c.counsel_id == counsel_id) &
                    (counsel_analysis_raw.c.task_code == task_code)
                )
            ).fetchone()
            if row and row.result_json:
                task_results[task_code] = row.result_json

        summary = {
            "counsel_id": counsel_id,
            "script_followed": task_results.get("T1", {}).get("script_followed"),
            "customer_emotion": task_results.get("T2", {}).get("emotion"),
            "payment_willingness": task_results.get("T2", {}).get("willingness"),
            "situation_type": task_results.get("T2", {}).get("situation"),
            "auto_transfer_guided": task_results.get("T2", {}).get("auto_transfer"),
            "virtual_account_guided": task_results.get("T2", {}).get("virtual_account"),
            "payment_positive_response": task_results.get("T2", {}).get("positive_response"),
            "guided_method": task_results.get("T2", {}).get("guided_method"),
            "overall_score": task_results.get("T2", {}).get("score", 0.0),
            "summary_comment": task_results.get("T2", {}).get("comment", "")
        }

        conn.execute(counsel_summary.insert().values(**summary))
        print(f"{counsel_id} 요약 저장 완료")


def build_dashboard_view(counsel_id):
    with engine.connect() as conn:
        existing = conn.execute(
            counsel_dashboard_view.select().where(
                counsel_dashboard_view.c.counsel_id == counsel_id
            )
        ).fetchone()
        if existing:
            print(f"{counsel_id} 대시보드 이미 존재")
            return

        row = conn.execute(
            counsel_summary.select().where(counsel_summary.c.counsel_id == counsel_id)
        ).fetchone()
        if not row:
            print(f"{counsel_id} 요약 없음")
            return

        dashboard = {
            "datetime": datetime.now(timezone.utc),
            "counselor": "미정",
            "counsel_id": counsel_id,
            "미납안내_여부": row.script_followed,
            "납부유도_여부": row.payment_positive_response,
            "유도_방식": row.guided_method,
            "고객_반응": row.customer_emotion,
            "종합_판단": "성공" if row.payment_positive_response else "실패"
        }

        conn.execute(counsel_dashboard_view.insert().values(**dashboard))
        print(f"{counsel_id} 대시보드 저장 완료")


# 메인 실행

if __name__ == "__main__":
    for file_path in DATA_DIR.glob("*.txt"):
        analyze_file(file_path)
        summarize_results(file_path.stem)
        build_dashboard_view(file_path.stem)

    print("전체 상담 분석 + 요약 + 대시보드 완료")