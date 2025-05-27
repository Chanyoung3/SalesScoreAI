import React, { useState } from 'react';
import Consultations from "./consultations";
import axios from 'axios';

function Login() {
  const [user, setUser] = useState({
    userid: "",
    password: "",
  });
  const [isAuthenticated, setAuth] = useState(false);
  const [open, setOpen] = useState(false);

  const handleChange = (e) => {
    setUser({ ...user, [e.target.name]: e.target.value });
  };

  const handleLogin = (e) => {
    e.preventDefault();
    // 서버 연결
    // /*
    axios
      .post(process.env.REACT_APP_API_URL + "/auth/login", user, {
        headers: { "Content-Type": "application/json" },
      })
      .then((res) => {
        const jwtToken = res.headers.authorization;
        if (jwtToken) {
          sessionStorage.setItem("jwt", jwtToken);
          setAuth(true);
        }
      })
      .catch(() => {
        setOpen(true);
      });
     // */

    console.log("로그인 시도:", user);
    //setAuth(true);
  };

  const handleLogout = () => {
    setAuth(false);
  };

  if (isAuthenticated) {
    return <Consultations logOut={handleLogout} />;
  } else {
    return (
      <div className="login-container">
        <h2>로그인</h2>
        <form onSubmit={handleLogin}>
          <div>
            <label>아이디</label>
            <input
              type="text"
              name="userid"
              value={user.userid}
              onChange={handleChange}
            />
          </div>
          <div>
            <label>비밀번호</label>
            <input
              type="password"
              name="password"
              value={user.password}
              onChange={handleChange}
            />
          </div>
          <button type="submit">로그인</button>
        </form>
        {open && <p style={{ color: 'red' }}>로그인 실패. 아이디와 비밀번호를 확인하세요.</p>}
      </div>
    );
  }
}

export default Login;
