import React, { useState } from 'react';
import { useNavigate } from "react-router-dom";
import Consultations from "./consultations";
import "../css/Login.css";
import axios from 'axios';

function Login({ onLogin }) {
  const [user, setUser] = useState({
    username: "",
    password: "",
  });
  const [isAuthenticated, setAuth] = useState(false);
  const [open, setOpen] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => {
    setUser({ ...user, [e.target.name]: e.target.value });
  };

  const handleLogin = (e) => {
    e.preventDefault();

    axios
      .post(process.env.REACT_APP_API_URL + "/auth/login", user, {
        headers: { "Content-Type": "application/json" },
      })
      .then((res) => {
        const jwtToken = res.headers.authorization;
        if (jwtToken) {
          onLogin(jwtToken);     // ✅ App의 상태 갱신
          navigate("/consultations");
        }
      })
        
      .catch(() => {
        setOpen(true);
      });

    console.log("로그인 시도:", user);
  };

  const handleLogout = () => {
    sessionStorage.removeItem("jwt");
    setAuth(false);
    navigate("/");
  };

  if (isAuthenticated) {
    return <Consultations logOut={handleLogout} user={user} />;
  } else {
    return (
      <div className="login-container">
      <h2>로그인</h2>
      <form onSubmit={handleLogin}>
        <div className="form-row">
          <label>아이디</label>
          <input
            type="text"
            name="username"
            value={user.username}
            onChange={handleChange}
          />
        </div>
        <div className="form-row">
          <label>비밀번호</label>
          <input
            type="password"
            name="password"
            value={user.password}
            onChange={handleChange}
          />
        </div>
        <br />
        <button className="button" type="submit">로그인</button>
      </form>
      <br />
      <button className="signbutton">회원가입</button>
      {open && (
        <p style={{ color: 'red' }}>
          로그인 실패. 아이디와 비밀번호를 확인하세요.
        </p>
      )}
    </div>
    
    );
  }
}

export default Login;