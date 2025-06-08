# analysis_service.py (Ollama 프롬프트 담당 조원과 협업하여 완성할 파일)

import os
import json
import subprocess
from datetime import datetime, timezone
from pathlib import Path
from sqlalchemy import (
    create_engine, Table, Column, String, Boolean, Float, DateTime,
    Text, MetaData, JSON, PrimaryKeyConstraint,
)
from sqlalchemy.orm import sessionmaker
from sqlalchemy.dialects import mysql

from flask import Flask, request, jsonify

PROMPT_DIR = Path("prompt")
MODEL_NAME = "llama3:2"
#3.1:8b

DB_PATH = "mysql+pymysql://ollama_user:A6$TMTR=-g!t@localhost:3306/salesscoreai_db?charset=utf8mb4"

app = Flask(__name__)

engine = create_engine(DB_PATH, echo=False, future=True)
metadata = MetaData()


# 1차: LLM 결과 원본 저장 테이블 (MySQL용으로 유지)
counsel_analysis_raw = Table(
    "counsel_analysis_raw", metadata,
    Column("counsel_id", String(50)),
    Column("task_code", String(10)),
    Column("result_json", JSON),
    Column("source_text", Text),
    Column("evaluated_at", DateTime),
    PrimaryKeyConstraint("counsel_id", "task_code")
)

# 2차: 집계 테이블 (MySQL용으로 유지)
counsel_summary = Table(
    "counsel_summary", metadata,
    Column("counsel_id", String(50), primary_key=True),
    Column("script_followed", Boolean),
    Column("customer_emotion", String(50)),
    Column("payment_willingness", String(50)),
    Column("situation_type", String(50)),
    Column("auto_transfer_guided", Boolean),
    Column("virtual_account_guided", Boolean),
    Column("payment_positive_response", Boolean),
    Column("guided_method", String(50)),
    Column("overall_score", Float),
    Column("summary_comment", Text)
)

# 3차: 화면용 테이블 (MySQL용으로 유지)
counsel_dashboard_view = Table(
    "counsel_dashboard_view", metadata,
    Column("datetime", DateTime),
    Column("counselor", String(50)),
    Column("counsel_id", String(50), primary_key=True),
    Column("미납안내_여부", Boolean),
    Column("납부유도_여부", Boolean),
    Column("유도_방식", String(50)),
    Column("고객_반응", String(50)),
    Column("종합_판단", String(50))
)

consultations_java = Table(
    "consultations", metadata,
    Column("id", String(50), primary_key=True),
    Column("ollama_score", Float),
    Column("ollama_feedback", Text),
    extend_existing=True
)


# 테이블 생성
# metadata.create_all(engine) # 이 줄은 Python 스크립트를 처음 실행할 때만 사용하고, Java가 이미 테이블을 만들었으므로 주석 처리하거나 주의해서 사용
# Session = sessionmaker(bind=engine) # session은 엔드포인트 내에서 생성하는 것이 좋습니다.


# 프롬프트 호출 (Ollama 직접 호출)
TASK_CODES = ["T1", "T2", "T3", "T4", "T5"]

def load_prompt(task_code):
    """지정된 태스크 코드에 해당하는 프롬프트 텍스트 파일을 로드합니다."""
    with open(PROMPT_DIR / f"{task_code}.txt", encoding="utf-8") as f:
        return f.read()

def query_ollama_local(prompt, model=MODEL_NAME):
    """Ollama CLI를 사용하여 로컬 Ollama 모델에 프롬프트를 쿼리합니다."""
    try:
        result = subprocess.run(
            ["ollama", "run", model],
            input=prompt,
            text=True,
            capture_output=True,
            timeout=300
        )
        if result.returncode != 0:
            print(f"Ollama 실행 오류 (Return Code: {result.returncode}): {result.stderr}")
            return ""
        return result.stdout.strip()
    except Exception as e:
        print(f"Ollama 실행 예외: {e}")
        return ""

def try_parse_json(text):
    """주어진 텍스트를 JSON으로 파싱을 시도합니다."""
    try:
        return json.loads(text)
    except:
        return {"parse_error": True, "raw_text": text}


# 분석 및 DB 저장
def analyze_dialogue_and_save(counsel_id, dialogue):
    """
    제공된 대화 텍스트를 Ollama AI로 분석하고, 그 결과를 DB에 저장합니다.
    이 함수는 Flask 엔드포인트에서 호출됩니다.
    """
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
                    (counsel_analysis_raw.c.counsel_id == str(counsel_id)) &
                    (counsel_analysis_raw.c.task_code == task_code)
                )
            ).fetchone()
            if existing:
                print(f"{counsel_id} - {task_code} 이미 존재")
                continue

            # 결과 백업 저장 (파일로 저장, 분석 오류 대비)
            output_dir = Path("output")
            output_dir.mkdir(exist_ok=True) # 폴더 없으면 생성
            backup_path = output_dir / f"{counsel_id}_{task_code}.json"
            with open(backup_path, "w", encoding="utf-8") as f:
                json.dump(parsed_json, f, ensure_ascii=False, indent=2)

            # DB에 원본 LLM 결과 저장
            conn.execute(counsel_analysis_raw.insert().values(
                counsel_id=str(counsel_id),
                task_code=task_code,
                result_json=parsed_json,
                source_text=dialogue,
                evaluated_at=datetime.now(timezone.utc)
            ))
        print(f"{counsel_id} - {task_code} 완료")


def summarize_results(counsel_id):
    """1차 분석 결과를 집계하여 counsel_summary 테이블에 저장합니다."""
    with engine.begin() as conn:
        # 이미 요약된 결과가 있는지 확인
        existing = conn.execute(
            counsel_summary.select().where(counsel_summary.c.counsel_id == str(counsel_id))
        ).fetchone()
        if existing:
            print(f"{counsel_id} 요약 이미 존재")
            return

        task_results = {}
        for task_code in TASK_CODES:
            row = conn.execute(
                counsel_analysis_raw.select().where(
                    (counsel_analysis_raw.c.counsel_id == str(counsel_id)) &
                    (counsel_analysis_raw.c.task_code == task_code)
                )
            ).fetchone()
            if row and row.result_json:
                task_results[task_code] = row.result_json

        # T2 태스크의 결과를 기반으로 요약 정보 구성
        summary = {
            "counsel_id": str(counsel_id),
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

        # 요약 결과를 DB에 저장
        conn.execute(counsel_summary.insert().values(**summary))
        print(f"{counsel_id} 요약 저장 완료")

        try:
            java_consultation_id = int(counsel_id)
            java_overall_score = summary.get("overall_score")
            java_summary_comment = summary.get("summary_comment")

            conn.execute(
                consultations_java.update().where(consultations_java.c.id == java_consultation_id).values(
                    ollama_score=java_overall_score,
                    ollama_feedback=java_summary_comment
                )
            )
            print(f"Java consultations 테이블 업데이트 완료: counsel_id={counsel_id}, score={java_overall_score}")
        except Exception as e:
            print(f"Java consultations 테이블 업데이트 오류: {e}")


def build_dashboard_view(counsel_id):
    """집계 결과를 기반으로 화면용 대시보드 뷰 테이블을 생성합니다."""
    with engine.begin() as conn:
        existing = conn.execute(
            counsel_dashboard_view.select().where(
                counsel_dashboard_view.c.counsel_id == str(counsel_id)
            )
        ).fetchone()
        if existing:
            print(f"{counsel_id} 대시보드 이미 존재")
            return

        row = conn.execute(
            counsel_summary.select().where(counsel_summary.c.counsel_id == str(counsel_id))
        ).fetchone()
        if not row:
            print(f"{counsel_id} 요약 없음")
            return

        # 대시보드 데이터 구성
        dashboard = {
            "datetime": datetime.now(timezone.utc),
            "counselor": "미정",
            "counsel_id": str(counsel_id),
            "미납안내_여부": row.script_followed,
            "납부유도_여부": row.payment_positive_response,
            "유도_방식": row.guided_method,
            "고객_반응": row.customer_emotion,
            "종합_판단": "성공" if row.payment_positive_response else "실패"
        }

        # 대시보드 뷰 DB에 저장
        conn.execute(counsel_dashboard_view.insert().values(**dashboard))
        print(f"{counsel_id} 대시보드 저장 완료")


# --- Flask 웹 API 엔드포인트 ---
@app.route('/analyze_consultation', methods=['POST'])
def analyze_consultation_api():
    """
    Java 백엔드로부터 상담 내용 텍스트를 받아 Ollama AI 분석을 수행하고 결과를 DB에 저장합니다.
    요청 JSON 예시: {"counsel_id": "123", "raw_text": "안녕하세요 고객님..."}
    """
    data = request.get_json()

    if not data:
        return jsonify({"status": "error", "message": "JSON 데이터가 필요합니다."}), 400

    counsel_id = data.get("counsel_id")
    raw_text = data.get("raw_text")
    # counselor_name = data.get("counselor_name", "미정") # 상담사 이름 (필요시 Java에서 추가로 전달받기)

    if not counsel_id or not raw_text:
        return jsonify({"status": "error", "message": "counsel_id와 raw_text는 필수입니다."}), 400

    try:
        # 분석 파이프라인 실행
        analyze_dialogue_and_save(counsel_id, raw_text)
        summarize_results(counsel_id)
        build_dashboard_view(counsel_id)

        return jsonify({"status": "success", "message": f"상담 ID {counsel_id} 분석 완료 및 DB 저장"}), 200
    except Exception as e:
        print(f"API 처리 중 오류 발생: {e}")
        return jsonify({"status": "error", "message": f"분석 처리 중 오류 발생: {str(e)}"}), 500

# 메인 실행 (Flask 애플리케이션 시작)
if __name__ == "__main__":
    print("Python 분석 서비스: 테이블 확인/생성 중...")
    MetaData().create_all(engine, tables=[counsel_analysis_raw, counsel_summary, counsel_dashboard_view])
    print("Python 분석 서비스: 테이블 확인/생성 완료.")

    app.run(host='0.0.0.0', port=5000, debug=True)