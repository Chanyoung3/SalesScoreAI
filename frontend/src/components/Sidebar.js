import React from 'react';
import { useNavigate } from 'react-router-dom';
import '../css/Sidebar.css'

function Sidebar({ logOut }) {
    const navigate = useNavigate();

    return (
      <aside className="sidebar">
        <h2>메뉴</h2>
        <div className="sidebar-buttons">
          <button
            className="sidebar-button"
            onClick={() => navigate('/consultations')}
          >
            상담 리스트
          </button>
          <button
            className="sidebar-button"
            onClick={() => navigate('/evaluation')}
          >
            상담 평가
          </button>
          <button
            className="sidebar-button"
            onClick={() => navigate('/upload')}
          >
            파일 업로드
          </button>
          <button
            className="sidebar-button"
            onClick={logOut}
          >
            로그아웃
          </button>
        </div>
      </aside>
    );
  }
  
  export default Sidebar;  