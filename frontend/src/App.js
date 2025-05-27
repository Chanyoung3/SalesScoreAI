import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Container from "@mui/material/Container";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import Login from './components/Login';
import Consultations from './components/consultations';

const queryClient = new QueryClient();

function App() {
  const isAuthenticated = !!sessionStorage.getItem("jwt");

  return (
    <Container maxWidth="xl">
      <QueryClientProvider client={queryClient}>
      <BrowserRouter>
          <Routes>
            <Route
              path="/"
              element={isAuthenticated ? <Navigate to="/consultations" /> : <Login />}
            />
            <Route
              path="/consultations"
              element={isAuthenticated ? <Consultations /> : <Navigate to="/" />}
            />
          </Routes>
        </BrowserRouter>
      </QueryClientProvider>
    </Container>
  );
}

export default App;