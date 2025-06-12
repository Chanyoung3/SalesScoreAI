# analytics.py

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
from sqlalchemy import Integer # Integer 타입 임포트 (consultations_java.id를 위해)


# --- 설정 ---
PROMPT_DIR = Path("prompt") # prompt 파일이 있는 경로
MODEL_NAME = "llama3.1:8b" # Ollama 모델 이름. (만약 '3.1:8b' 사용 시 주석 해제 후 변경)

# DB_PATH: MySQL 데이터베이스 연결 문자열
# 여기를 'ollama_counseling_db'로 통일하거나, 'salesscoreai_db'가 실제로 생성되었는지 확인하세요.
DB_PATH = "mysql+pymysql://ollama_user:A6$TMTR=-g!t@localhost:3306/salesscoreai_db?charset=utf8mb4" # <-- DB 이름 통일 권장!

app = Flask(__name__)

engine = create_engine(DB_PATH, echo=False, future=True)
metadata = MetaData()


# --- 테이블 정의 (이전과 동일) ---
counsel_analysis_raw = Table(
    "counsel_analysis_raw", metadata,
    Column("counsel_id", String(50)),
    Column("task_code", String(10)),
    Column("result_json", JSON),
    Column("source_text", Text),
    Column("evaluated_at", DateTime),
    PrimaryKeyConstraint("counsel_id", "task_code")
)

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
    Column("id", Integer, primary_key=True),
    Column("ollama_score", Integer),
    Column("ollama_feedback", Text),
    extend_existing=True
)


# --- 프롬프트 호출 (Ollama 직접 호출) ---
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
            print(f"[{datetime.now()}] [Ollama Call] 실행 오류 (Return Code: {result.returncode}): {result.stderr.strip()}")
            return ""

        ollama_raw_response = result.stdout.strip()
        print(f"[{datetime.now()}] --- Ollama Raw Response Start (Task: {prompt.splitlines()[0][:min(len(prompt.splitlines()[0]), 50)]}...) ---")
        print(ollama_raw_response)
        print(f"[{datetime.now()}] --- Ollama Raw Response End ---")
        return ollama_raw_response
    except Exception as e:
        print(f"[{datetime.now()}] [Ollama Call] 실행 예외: {e}")
        return ""

def try_parse_json(text):
    """주어진 텍스트를 JSON으로 파싱을 시도합니다."""
    try:
        parsed_data = json.loads(text)
        return parsed_data
    except (json.JSONDecodeError, ValueError, TypeError) as e:
        print(f"[{datetime.now()}] [JSON Parse] 파싱 실패: {e}. 원본 텍스트: '{text[:100]}...'")
        # Ollama가 JSON을 반환하지 않을 때, "output" 필드에 원본 텍스트를 할당합니다.
        return {"parse_error": True, "raw_text": text, "output": text.strip()}
    except Exception as e:
        print(f"[{datetime.now()}] [JSON Parse] 기타 오류: {e}. 원본 텍스트: '{text[:100]}...'")
        return {"parse_error": True, "raw_text": text, "output": text.strip()}


# --- 분석 및 DB 저장 ---
def analyze_dialogue_and_save(counsel_id, dialogue):
    """
    제공된 대화 텍스트를 Ollama AI로 분석하고, 그 결과를 DB에 저장합니다.
    """
    # 각 태스크별 결과 (parsed_json_result)를 임시로 저장할 딕셔너리
    # 이 딕셔너리가 다음 summarize_results 함수로 전달될 때 사용됩니다.
    task_results_for_summary = {}

    for task_code in TASK_CODES:
        prompt_template = load_prompt(task_code)
        formatted_prompt = prompt_template.replace("{input}", dialogue)

        result_text = query_ollama_local(formatted_prompt, MODEL_NAME)

        # parsed_json_result 변수는 항상 try_parse_json의 결과로 초기화됩니다.
        # result_text가 비어있어도 try_parse_json은 {"output": "", "parse_error": True}를 반환합니다.
        parsed_json_result = try_parse_json(result_text)

        # --- 이전 오류의 직접적인 원인 (정의 전 사용) 해결 ---
        # print(f"Parsed JSON for {counsel_id}-{task_code}: {parsed_json}") # <-- 이 라인 삭제 또는 주석 처리 (아래로 옮김)

        print(f"[{datetime.now()}] Parsed JSON for {counsel_id}-{task_code}: {parsed_json_result}") # <-- 변수 정의 후 출력

        # 결과를 task_results_for_summary에 저장 (이후 summarize_results에서 사용)
        task_results_for_summary[task_code] = parsed_json_result

        with engine.begin() as conn:
            existing = conn.execute(
                counsel_analysis_raw.select().where(
                    (counsel_analysis_raw.c.counsel_id == str(counsel_id)) &
                    (counsel_analysis_raw.c.task_code == task_code)
                )
            ).fetchone()
            if existing:
                print(f"[{datetime.now()}] [{counsel_id}] - {task_code} 이미 존재, 스킵.")
                continue

            output_dir = Path("output")
            output_dir.mkdir(exist_ok=True)
            backup_path = output_dir / f"{counsel_id}_{task_code}.json"
            with open(backup_path, "w", encoding="utf-8") as f:
                json.dump(parsed_json_result, f, ensure_ascii=False, indent=2)

            conn.execute(counsel_analysis_raw.insert().values(
                counsel_id=str(counsel_id),
                task_code=task_code,
                result_json=parsed_json_result,
                source_text=dialogue,
                evaluated_at=datetime.now(timezone.utc)
            ))
        print(f"[{datetime.now()}] [{counsel_id}] - {task_code} 완료.")

    # 모든 태스크 분석 후, summarize_results와 build_dashboard_view를 호출
    # 이 함수들은 이제 task_results_for_summary를 직접 파라미터로 받지 않고
    # DB에서 데이터를 다시 조회하므로, 별도로 전달할 필요 없음.


def summarize_results(counsel_id):
    """1차 분석 결과를 집계하여 counsel_summary 테이블에 저장하고, Java consultations 테이블을 업데이트합니다."""
    with engine.begin() as conn:
        existing = conn.execute(
            counsel_summary.select().where(counsel_summary.c.counsel_id == str(counsel_id))
        ).fetchone()
        if existing:
            print(f"[{datetime.now()}] [{counsel_id}] 요약 이미 존재, 스킵.")
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
                current_result_json = row.result_json
                if isinstance(current_result_json, str):
                    try:
                        current_result_json = json.loads(current_result_json)
                        print(f"[{datetime.now()}] [DB Load] {counsel_id}-{task_code} result_json이 문자열로 로드되어 재파싱 성공.")
                    except json.JSONDecodeError:
                        print(f"[{datetime.now()}] [DB Load] {counsel_id}-{task_code} result_json 재파싱 실패, 기본값 사용.")
                        current_result_json = {"parse_error": True, "output": "DB 로드 오류/비JSON"}
                task_results[task_code] = current_result_json

        # --- 점수 계산 로직 (T1-T5 중 몇 개 성공했는지) ---
        # 프롬프트는 JSON을 반환하지 않고 "output" 필드만 포함하는 텍스트 응답을 가정합니다.
        # 각 태스크의 "output" 필드 값이 "NA"가 아니면 성공으로 간주합니다.
        score_count = 0
        feedback_parts = []

        # T1: "안전한 신용관리바랍니다. 감사합니다" 문장 존재 여부
        t1_output = task_results.get("T1", {}).get("output", "").strip()
        t1_success = (t1_output != "NA" and t1_output != "")
        if t1_success: score_count += 1; feedback_parts.append(f"[T1] 종료 멘트 성공: '{t1_output}'")
        else: feedback_parts.append("[T1] 종료 멘트 실패: 해당 멘트 없음")

        # T2: 고객 납부 의사 문장 존재 여부
        t2_output = task_results.get("T2", {}).get("output", "").strip()
        t2_success = (t2_output != "NA" and t2_output != "")
        if t2_success: score_count += 1; feedback_parts.append(f"[T2] 납부 의사 언급 성공: '{t2_output}'")
        else: feedback_parts.append("[T2] 납부 의사 언급 실패: 해당 멘트 없음")

        # T3: 긍정 답변 존재 여부
        t3_output = task_results.get("T3", {}).get("output", "").strip()
        t3_success = (t3_output != "NA" and t3_output != "")
        if t3_success: score_count += 1; feedback_parts.append(f"[T3] 고객 긍정 답변 확인: '{t3_output}'")
        else: feedback_parts.append("[T3] 고객 긍정 답변 없음")

        # T4: "수고하세요" 포함 여부 (이 멘트가 없어야 점수)
        t4_output = task_results.get("T4", {}).get("output", "").strip()
        t4_contains_discourtesy = (t4_output != "NA" and t4_output != "")
        if not t4_contains_discourtesy: score_count += 1; feedback_parts.append("[T4] '수고하세요' 멘트 없음: 성공")
        else: feedback_parts.append(f"[T4] '수고하세요' 멘트 포함: '{t4_output}' (감점 요소)")


        # T5: 욕설/비속어 포함 여부 (이 멘트가 없어야 점수)
        t5_output = task_results.get("T5", {}).get("output", "").strip()
        t5_contains_swear = (t5_output != "NA" and t5_output != "")
        if not t5_contains_swear: score_count += 1; feedback_parts.append("[T5] 욕설/비속어 없음: 성공")
        else: feedback_parts.append(f"[T5] 욕설/비속어 포함: '{t5_output}' (감점 요소)")

        # 각 태스크 당 20점씩 부여 (총 5개 태스크)
        calculated_score = score_count * 20
        # 종합 피드백 생성
        combined_feedback = " | ".join(feedback_parts)


        # summary 딕셔너리 구성 (counsel_summary 테이블용)
        # T2의 결과에서 직접 'emotion', 'willingness', 'situation', 'guided_method' 필드를 가져옵니다.
        # 해당 필드가 없으면 기본값인 "판단불가"를 사용합니다.
        t2_result = task_results.get("T2", {}) # T2 결과를 가져옴
        summary = {
            "counsel_id": str(counsel_id),
            "script_followed": t1_success,
            "customer_emotion": t2_result.get("emotion", "판단불가"), # T2 결과에서 emotion 가져옴
            "payment_willingness": t2_result.get("willingness", "판단불가"), # T2 결과에서 willingness 가져옴
            "situation_type": t2_result.get("situation", "판단불가"), # T2 결과에서 situation 가져옴
            "auto_transfer_guided": False,
            "virtual_account_guided": False,
            "payment_positive_response": t3_success,
            "guided_method": t2_result.get("guided_method", "판단불가"), # T2 결과에서 guided_method 가져옴
            "overall_score": float(calculated_score),
            "summary_comment": combined_feedback
        }
        print(f"[{datetime.now()}] [{counsel_id}] 요약 결과 생성: {summary}")

        # 요약 결과를 DB에 저장
        conn.execute(counsel_summary.insert().values(**summary))
        print(f"[{datetime.now()}] [{counsel_id}] 요약 저장 완료.")

        # --- Java consultations 테이블 업데이트 ---
        try:
            java_consultation_id = int(counsel_id)
            java_overall_score = int(calculated_score) # consultations_java는 Integer 컬럼
            java_summary_comment = combined_feedback

            conn.execute(
                consultations_java.update().where(consultations_java.c.id == java_consultation_id).values(
                    ollama_score=java_overall_score,
                    ollama_feedback=java_summary_comment
                )
            )
            print(f"[{datetime.now()}] Java consultations 테이블 업데이트 완료: counsel_id={counsel_id}, score={java_overall_score}, feedback='{java_summary_comment}'")
        except Exception as e:
            import traceback
            print(f"[{datetime.now()}] [DB Update] Java consultations 테이블 업데이트 오류: {e}")
            print(traceback.format_exc())


def build_dashboard_view(counsel_id, counselor_name="미정"):
    """집계 결과를 기반으로 화면용 대시보드 뷰 테이블을 생성합니다."""
    with engine.begin() as conn:
        existing = conn.execute(
            counsel_dashboard_view.select().where(
                counsel_dashboard_view.c.counsel_id == str(counsel_id)
            )
        ).fetchone()
        if existing:
            print(f"[{datetime.now()}] [{counsel_id}] 대시보드 이미 존재, 스킵.")
            return

        row = conn.execute(
            counsel_summary.select().where(counsel_summary.c.counsel_id == str(counsel_id))
        ).fetchone()
        if not row:
            print(f"[{datetime.now()}] [{counsel_id}] 요약 데이터 없음. 대시보드 생성 불가.")
            return

        # 대시보드 데이터 구성
        # counsel_summary에서 직접 값을 가져옵니다.
        dashboard = {
            "datetime": datetime.now(timezone.utc),
            "counselor": counselor_name,
            "counsel_id": str(counsel_id),
            "미납안내_여부": row.script_followed,
            "납부유도_여부": row.payment_positive_response,
            "유도_방식": row.guided_method,
            "고객_반응": row.customer_emotion,
            "종합_판단": "성공" if row.payment_positive_response else "실패"
        }

        # 대시보드 뷰 DB에 저장
        conn.execute(counsel_dashboard_view.insert().values(**dashboard))
        print(f"[{datetime.now()}] [{counsel_id}] 대시보드 저장 완료.")


# --- Flask 웹 API 엔드포인트 ---
@app.route('/analyze_consultation', methods=['POST'])
def analyze_consultation_api():
    """
    Java 백엔드로부터 상담 내용 텍스트를 받아 Ollama AI 분석을 수행하고 결과를 DB에 저장합니다.
    """
    data = request.get_json()

    if not data:
        print(f"[{datetime.now()}] [API Call] Error: No JSON data.")
        return jsonify({"status": "error", "message": "JSON 데이터가 필요합니다."}), 400

    counsel_id = data.get("counsel_id")
    raw_text = data.get("raw_text")
    counselor_name = data.get("counselor_name", "미정")

    if not counsel_id or not raw_text:
        print(f"[{datetime.now()}] [API Call] Error: Missing counsel_id or raw_text.")
        return jsonify({"status": "error", "message": "counsel_id와 raw_text는 필수입니다."}), 400

    print(f"[{datetime.now()}] [API Call] Starting analysis for counsel_id: {counsel_id}, counselor: {counselor_name}")
    try:
        analyze_dialogue_and_save(counsel_id, raw_text)
        summarize_results(counsel_id)
        build_dashboard_view(counsel_id, counselor_name)

        print(f"[{datetime.now()}] [API Call] Analysis pipeline completed for counsel_id: {counsel_id}")
        return jsonify({"status": "success", "message": f"상담 ID {counsel_id} 분석 완료 및 DB 저장"}), 200
    except Exception as e:
        import traceback
        print(f"[{datetime.now()}] [API Endpoint] API 처리 중 오류 발생: {e}")
        print(traceback.format_exc())
        return jsonify({"status": "error", "message": f"분석 처리 중 오류 발생: {str(e)}"}), 500

# 메인 실행 (Flask 애플리케이션 시작)
if __name__ == "__main__":
    print(f"[{datetime.now()}] Python 분석 서비스: 테이블 확인/생성 중...")
    metadata.create_all(engine, tables=[counsel_analysis_raw, counsel_summary, counsel_dashboard_view])
    print(f"[{datetime.now()}] Python 분석 서비스: 테이블 확인/생성 완료.")

    app.run(host='0.0.0.0', port=5000, debug=True)