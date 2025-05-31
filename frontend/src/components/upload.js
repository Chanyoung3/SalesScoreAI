import React, { useState } from 'react';
import { Box, Button, Typography, Input } from '@mui/material';

function Upload() {
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

  const handleUpload = () => {
    if (fileName) {
      alert(`파일 "${fileName}" 업로드 처리`);
    } else {
      alert('파일을 선택해주세요.');
    }
  };

  return (
    <Box
      sx={{
        maxWidth: 400,
        margin: '0 auto',
        mt: 5,
        p: 3,
        border: '1px solid #ccc',
        borderRadius: 2,
        textAlign: 'center',
      }}
    >
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
  );
}

export default Upload;
