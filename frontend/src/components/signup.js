import React, { useState, useEffect } from 'react';
import { useNavigate } from "react-router-dom";
import "../css/Login.css"; // 기존 css 재활용
import axios from 'axios';

function Register() {
  const [user, setUser] = useState({
    username: "",
    password: "",
    name: "",
    email: "",
    role: "COUNSELOR"  // 기본 role 설정
  });

  const [open, setOpen] = useState(false);
  const navigate = useNavigate();
  useEffect(() => {
    document.body.classList.add("login-body");

    return () => {
      document.body.classList.remove("login-body");
    };
  }, []);
  const handleChange = (e) => {
    setUser({ ...user, [e.target.name]: e.target.value });
  };

  const handleRegister = (e) => {
    e.preventDefault();

    axios
      .post(process.env.REACT_APP_API_URL + "/auth/register", user, {
        headers: { "Content-Type": "application/json" },
      })
      .then((res) => {
        console.log("회원가입 성공", res.data);
        alert("회원가입이 완료되었습니다.");
        navigate("/"); // 회원가입 후 로그인 화면으로 이동
      })
      .catch((err) => {
        console.error("회원가입 실패", err);
        setOpen(true);
      });
  };

  return (
    <div className="login-container">
      <h2>회원가입</h2>
      <form onSubmit={handleRegister}>
        <div className="form-row">
          <label>아이디</label>
          <input
            type="text"
            name="username"
            value={user.username}
            onChange={handleChange}
            required
          />
        </div>
        <div className="form-row">
          <label>비밀번호</label>
          <input
            type="password"
            name="password"
            value={user.password}
            onChange={handleChange}
            required
          />
        </div>
        <div className="form-row">
          <label>이름</label>
          <input
            type="text"
            name="name"
            value={user.name}
            onChange={handleChange}
            required
          />
        </div>
        <div className="form-row">
          <label>이메일</label>
          <input
            type="email"
            name="email"
            value={user.email}
            onChange={handleChange}
            required
          />
        </div>
        <div className="form-row">
          <label>권한</label>
          <select name="role" value={user.role} onChange={handleChange}>
            <option value="COUNSELOR">COUNSELOR</option>
            <option value="ADMIN">ADMIN</option>
          </select>
        </div>
        {open && (
        <p style={{ color: 'red' }}>
          회원가입 실패. 입력 정보를 확인하세요.
        </p>
      )}
      <br />
        <button className="button" type="submit">회원가입</button>
        <button className="signbutton" onClick={() => navigate("/")}>로그인으로</button>
      </form>
    </div>
  );
}

export default Register;
