import React, { useState } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Container from "@mui/material/Container";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import Login from './components/Login';
import Consultations from './components/consultations';
import Evaluation from './components/evaluation';
import Detail from './components/detail';
import Upload from './components/upload'
import Register from './components/signup';

const queryClient = new QueryClient();

function App() {

  const [isAuthenticated, setIsAuthenticated] = useState(!!sessionStorage.getItem("jwt"));

  const handleLogin = (jwt) => {
    sessionStorage.setItem("jwt", jwt);
    setIsAuthenticated(true);
  };

  const handleLogout = () => {
    sessionStorage.removeItem("jwt");
    setIsAuthenticated(false);
  };

  return (
    <Container maxWidth="xl">
      <QueryClientProvider client={queryClient}>
      <BrowserRouter>
          <Routes>
             <Route
              path="/"
              element={
                isAuthenticated ? (
                  <Navigate to="/consultations" />
                ) : (
                  <Login onLogin={handleLogin} />
                )
              }
            />
             <Route
              path="/register"
              element={<Register />}
            />
            <Route
              path="/consultations"
              element={isAuthenticated ? <Consultations logOut={handleLogout}/> : <Navigate to="/" />}
            />
            <Route
              path="/evaluation"
              element={isAuthenticated ? <Evaluation logOut={handleLogout}/> : <Navigate to="/" />}
            />
            <Route
              path="/detail"
              element= {<Detail logOut={handleLogout}/>}
            />
            <Route
              path="/upload"
              element={isAuthenticated ? <Upload logOut={handleLogout}/> : <Navigate to="/" />}
            />
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </BrowserRouter>
      </QueryClientProvider>
    </Container>
  );
}

export default App;