import React, { useState } from 'react';
import { Box, Button, Typography, Input } from '@mui/material';
import Header from "./Header";
import Sidebar from "./Sidebar";
import axios from 'axios';

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
  
    // 파일명에서 counselorId 추출
    const fileNameParts = file.name.split('_');
    const counselorId = fileNameParts[2];  // 3402가 들어있다고 가정
  
    const formData = new FormData();
    formData.append('file', file);
    formData.append('counselorId', counselorId);
    formData.append('customerInfo', '고객정보'); // 필요시 적절한 값 넣기
  
    try {
      const response = await axios.post(
        process.env.REACT_APP_API_URL + '/consultations/upload',
        formData
      );
  
      console.log("파일 업로드 성공", response.data);
      alert('등록 완료되었습니다.');
      setFileName('');
    } catch (error) {
      console.error("파일 업로드 실패", error);
      alert('파일 업로드 중 오류 발생');
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
