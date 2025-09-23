# SalesScoreAI

LLM(Large Language Model)을 활용한 상담 평가 시스템

---

## 📝 프로젝트 소개

1. 본 프로젝트는 **기업 고객센터 상담 대화를 AI로 평가**하는 
   **LLM 기반 스코어 관리자 프로그램**을 개발하는 것을 목표로 합니다.
2. 고객과 상담원의 대화를 분석하여 **상담 품질, 규제 준수** 등을 평가하고, 
   평가 결과를 **챗봇 형태로 사용자에게 제공**합니다.
3. AI를 활용하여 **객관적으로 대화를 평가**하고, 관리자가 목적에 맞게 설정할 수 있도록 지원합니다.

---

## 🔍 개발 배경 및 필요성

### 1. 개발 배경
- 기존 대화 평가 솔루션은 평가 정확도가 낮거나, 관리자가 **수작업으로 평가 모델을 세팅**해야 하는 부담이 큼.
- 평가 결과도 단순 리포트 제공에 머물러 **사용성이 낮음**.

### 2. 필요성
- 최신 **LLM(대형 언어 모델)** 도입을 통해 **의미 파악 정확도를 높이고**, 관리자의 수작업 부담을 줄여야 함.
- AI 기반 솔루션으로 **효율적이고 객관적인 상담 평가**를 제공 가능.

<p align="center">
  <img src="https://github.com/user-attachments/assets/5a404503-f796-4314-9c24-d70c4d03944f" width="645" height="290" alt="작품 구성도" />
  <br>
  <strong>작품 구성도</strong>
</p>

---
## ⚙️ 주요 기능

-  **기업 내 독자 설치가 가능한 오픈소스 LLM**을 평가 엔진으로 사용
-  **입력한 상담 데이터를 LLM에서 평가 가능한 형식으로 전처리**
-  **LLM으로 상담 평가를 실행하고**, 평가 결과를 지정한 **평가 항목별로 출력**

---

## 🛠 기술 스택

- **Frontend**  
  <img src="https://img.shields.io/badge/React-61DAFB?style=for-the-badge&logo=react&logoColor=black">  
  <img src="https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black">  

- **Backend**  
  <img src="https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">  
  <img src="https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=java&logoColor=white">  

- **Database**  
  <img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white">  

- **AI / LLM Server**  
  <img src="https://img.shields.io/badge/Python-3776AB?style=for-the-badge&logo=python&logoColor=white">  
  <img src="https://img.shields.io/badge/Ollama-3776AB?style=for-the-badge&logo=ollama&logoColor=white">  
  Llama3.1:8b (Ollama 로컬 서버 실행)
  
---

## 📸 실행 화면

---

### 🚀 **V1: 초기 버전**
<p align="center">
  <img src="https://github.com/user-attachments/assets/0f6c60c5-adf5-4ce4-b3b3-704b2bc758e1" width="500" /><br>
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/3c6ab059-ac0a-49c2-a47e-7a9064e9deab" width="500" /><br>
   <em>📂 파일 업로드 및 분석결과 표시<br>
   사용자가 상담 데이터를 업로드하면 <b>LLaMA 모델</b>이 자동으로 분석을 진행합니다.<br>
   현재 버전에서는 결과를 글 형태로 정리하여 웹 UI에 표시합니다.
   </em>
</p>

---

### ⚡ **V2: 개선 버전**
<p align="center">
  <img src="https://github.com/user-attachments/assets/5ba1e775-e963-42d5-8c70-427728feaac4" width="700" /><br>
  <em>🔑 로그인 페이지<br>
   사용자 계정으로 로그인하는 화면
  </em>
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/1079b4f5-aa30-4586-9501-97f2b13b10dc" width="700" /><br>
  <em>📋 상담 리스트 페이지<br>
   업로드한 상담 데이터들을 리스트 형식으로 확인할 수 있는 화면
  </em>
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/70bdd767-f90e-40e0-a3a2-3bec3ff8584d" width="700" /><br>
  <em>📊 상담 평가 페이지<br>
   선택한 달의 로그인한 상담원과 다른 상담원들의 평가 점수를 비교할 수 있는 화면
  </em>
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/287193b5-5fe1-4230-8da4-204dab87aa26" width="700" /><br>
  <em>📂 파일 업로드 페이지<br>
   새로운 상담 파일을 업로드하는 화면
  </em>
</p>

---
## 👨‍👩‍👧‍👦 팀 구성

<div align="center">

<table>
  <tr>
    <td align="center">
      <a href="https://github.com/Chanyoung3">
        <img src="https://github.com/Chanyoung3.png" width="100" style="border-radius:10px;"><br/>
        <sub><b>Chanyoung3</b></sub><br/>
        <sub>팀장</sub>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/choi2y">
        <img src="https://github.com/choi2y.png" width="100" style="border-radius:10px;"><br/>
        <sub><b>choi2y</b></sub><br/>
        <sub>팀원</sub>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/HenryPark62">
        <img src="https://github.com/HenryPark62.png" width="100" style="border-radius:10px;"><br/>
        <sub><b>HenryPark62</b></sub><br/>
        <sub>팀원</sub>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/LEESEONWOOOO">
        <img src="https://github.com/LEESEONWOOOO.png" width="100" style="border-radius:10px;"><br/>
        <sub><b>LEESEONWOOOO</b></sub><br/>
        <sub>팀원</sub>
      </a>
    </td>
  </tr>
</table>

</div>

