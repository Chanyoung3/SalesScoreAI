import React, { useState } from 'react';
import { Box, Button, Typography, Input } from '@mui/material';
import Header from "./Header";
import Sidebar from "./Sidebar";

function Upload({ logOut }) {
  const [fileName, setFileName] = useState('');

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file && file.name.endsWith('.txt')) {
      setFileName(file.name);
    } else {
      alert('txt 파일만 업로드할 수 있습니다.');
      setFileName('');
    }
  };

  const handleUpload = async () => {
    const fileInput = document.querySelector('input[type="file"]');
    const file = fileInput.files[0];
  
    if (!file) {
      alert('파일을 선택해주세요.');
      return;
    }
  
    const formData = new FormData();
    formData.append('file', file);
  
    try {
      const response = await fetch('http://localhost:5000/api/consultations/upload', { // flask 서버
        method: 'POST',
        body: formData,
      });
  
      if (!response.ok) {
        throw new Error('서버 응답 오류');
      }
  
      // 성공 시 결과 내용은 무시하고 간단히 알림만
      alert('등록 완료되었습니다.');
      setFileName(''); // 선택된 파일 초기화
    } catch (error) {
      alert('파일 업로드 중 오류 발생');
      console.error(error);
    }
  };

  return (
    <>
      <Box sx={{ width: "100%", margin: "0 auto", height: "100vh", display: "flex", flexDirection: "column" }} >
        <Header />
        <Box sx={{ display: "flex", flexGrow: 1, height: "calc(100vh - 64px)", width: "100%" }} >
          <Box sx={{ width: "280px", flexShrink: 0 }}>
            <Sidebar logOut={logOut}/>
          </Box>
          <Box sx={{ flexGrow: 1, p: 2, overflowY: "auto" }}>
            <Typography variant="h6" gutterBottom>
              텍스트 파일 업로드
            </Typography>

            <Input
              type="file"
              inputProps={{ accept: '.txt' }}
              onChange={handleFileChange}
              fullWidth
              sx={{ mb: 2 }}
            />

            <Typography variant="body2" sx={{ mb: 2 }}>
              {fileName ? `선택된 파일: ${fileName}` : '파일이 선택되지 않았습니다'}
            </Typography>

            <Button variant="contained" onClick={handleUpload}>
              업로드
            </Button>
          </Box>
        </Box>
      </Box>
    </>
  );
}

export default Upload;
