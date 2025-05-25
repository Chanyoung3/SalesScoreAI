-- counselors 테이블 생성 (상담사 정보 저장)
CREATE TABLE counselors(
    id INTEGER PRIMARY KEY AUTOINCREMENT, -- 상담사 고유 ID (자동 증가)
    username VARCHAR(50) NOT NULL UNIQUE,       -- 사용자명 (로그인 시 사용, 중복 불가)
    password VARCHAR(50) NOT NULL,              -- 비밀번호 (해시된 형태로 저장)
    email VARCHAR(50) UNIQUE,                   -- 이메일 주소 (중복 불가)
    name VARCHAR(50),                           -- 상담사 이름
    role VARCHAR(50) NOT NULL DEFAULT 'COUNSELOR' -- 상담사 역할 ('ADMIN' 또는 'COUNSELOR', 기본값은 'COUNSELOR')
);

-- consultations 테이블 생성 (상담 내용 및 결과 저장)
CREATE TABLE consultations (
    id INTEGER PRIMARY KEY AUTOINCREMENT, -- 상담 고유 ID (자동 증가)
    counselor_id INTEGER NOT NULL,        -- 상담사 ID (counselors 테이블의 id와 연결)
    consultation_date DATE NOT NULL,      -- 상담 일시 (YYYY-MM-DD HH:MM:SS 형식)
    customer_info VARCHAR(50),                   -- 고객 정보 (예: 고객 이름, 연락처 등)
    transcript_file_path TEXT,            -- 업로드된 상담 텍스트 파일의 서버 내 저장 경로
    raw_transcript_content TEXT,          -- 상담 텍스트 내용 전체 (파일로 관리할 경우 선택 사항)
    ollama_score INTEGER,                 -- Ollama AI가 측정한 상담 점수 (0-100)
    ollama_feedback TEXT,                 -- Ollama AI가 제공하는 피드백 내용
    created_at DATE DEFAULT CURRENT_TIMESTAMP, -- 레코드 생성 시각 (기본값: 현재 시각)
    updated_at DATE DEFAULT CURRENT_TIMESTAMP, -- 레코드 마지막 업데이트 시각 (기본값: 현재 시각)
    FOREIGN KEY (counselor_id) REFERENCES counselors(id) -- counselor_id가 counselors 테이블의 id를 참조하도록 외래 키 설정
    );