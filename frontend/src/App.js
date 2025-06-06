// src/App.js
import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Header from './components/Header';
import Sidebar from './components/Sidebar';
import Consultations from './components/consultations';
import Evaluation from './components/evaluation';
import Upload from './components/upload';
import Detail from './components/detail';
import './App.css';

function App() {
  const logOut = () => {
    sessionStorage.removeItem("jwt");
    window.location.href = "/";
  };

  return (
    <BrowserRouter>
      <div className="flex flex-col h-screen">
        <Header />
        <div className="flex flex-1 overflow-hidden">
          <Sidebar />
          <div className="flex-1 overflow-y-auto">
            <Routes>
              <Route path="/" element={<Consultations logOut={logOut} />} />
              <Route path="/evaluation" element={<Evaluation />} />
              <Route path="/upload" element={<Upload />} />
              <Route path="/detail/:id" element={<Detail />} />
            </Routes>
          </div>
        </div>
      </div>
    </BrowserRouter>
  );
}

export default App;
