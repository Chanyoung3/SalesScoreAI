import React from "react";
import Header from "./components/Header";
import Sidebar from "./components/Sidebar";

function App() {
  return (
    <div className="h-screen flex flex-col">
      {/* 헤더 */}
      <Header />

      {/* 사이드바 + 본문 */}
      <div className="flex flex-1">
        <Sidebar />
        <main className="flex-1 p-6 bg-gray-100">
          <h1 className="text-2xl font-bold mb-4">본문 영역</h1>
          <p>여기에 콜 분석 결과, 그래프, 세부내용 등을 표시합니다.</p>
        </main>
      </div>
    </div>
  );
}

export default App; // ✅ 반드시 있어야 함
