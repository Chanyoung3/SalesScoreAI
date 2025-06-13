import React, { useState } from 'react';
import { Box, Button, Typography, Input, Paper } from '@mui/material';
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

    const fileNameParts = file.name.split('_');
    const counselorId = fileNameParts[2];

    const formData = new FormData();
    formData.append('file', file);
    formData.append('counselorId', counselorId);
    formData.append('customerInfo', '고객정보');

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
      <Box sx={{ width: "100%", height: "100vh", display: "flex", flexDirection: "column" }}>
        <Header />
        <Box sx={{ display: "flex", flexGrow: 1, minHeight: "calc(100vh - 64px)" }}>
          <Box sx={{ width: "280px", flexShrink: 0 }}>
            <Sidebar logOut={logOut} />
          </Box>

          <Box
            sx={{
              flexGrow: 1,
              p: 4,
              overflowY: "auto",
              display: "flex",
              justifyContent: "center",
              alignItems: "flex-start",  // 맨 위 정렬
              minHeight: "calc(100vh - 64px)", // Header 높이만큼 뺀 최소 높이
            }}
          >
            <Paper elevation={3} sx={{ p: 4, width: 500, borderRadius: 3, mt: 4 }}>
              <Typography
                variant="h5"
                gutterBottom
                sx={{ fontWeight: 600, color: "#333", textAlign: "center" }}
              >
                텍스트 파일 업로드
              </Typography>

              <input
                type="file"
                id="file-upload"
                accept=".txt"
                onChange={handleFileChange}
                style={{ display: "none" }}
              />
              <label htmlFor="file-upload">
                <Button
                  variant="outlined"
                  component="span"
                  fullWidth
                  sx={{
                    borderRadius: 2,
                    padding: "12px",
                    fontWeight: 600,
                    backgroundColor: "#f9fafb",
                    borderColor: "#ccc",
                    color: "#333",
                    "&:hover": { backgroundColor: "#f1f5f9" },
                  }}
                >
                  {fileName ? "다른 파일 선택" : "파일 선택"}
                </Button>
              </label>

              <Typography variant="body2" sx={{ mt: 1, color: "#555" }}>
                {fileName ? `선택된 파일: ${fileName}` : "파일이 선택되지 않았습니다."}
              </Typography>

              <Button
                variant="contained"
                fullWidth
                onClick={handleUpload}
                sx={{
                  mt: 3,
                  py: 1.5,
                  fontWeight: 600,
                  borderRadius: 2,
                  backgroundColor: "#2563eb",
                  "&:hover": { backgroundColor: "#1d4ed8" },
                }}
              >
                업로드
              </Button>
            </Paper>
          </Box>
        </Box>
      </Box>
    </>
  );
}

export default Upload;
