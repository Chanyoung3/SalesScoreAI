import { TextField, Box, Grid, Typography, IconButton } from "@mui/material";
import { BarChart, Bar, XAxis, YAxis, Tooltip, CartesianGrid, LabelList, Cell } from 'recharts';
import { LocalizationProvider } from '@mui/x-date-pickers';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import ArrowBackIosNewIcon from '@mui/icons-material/ArrowBackIosNew';
import ArrowForwardIosIcon from '@mui/icons-material/ArrowForwardIos';
import { addMonths } from "date-fns";
import dayjs from 'dayjs';
import ko from 'date-fns/locale/ko';
import { useState } from "react";
import React from 'react';
import '../App.css';

function Evaluation(){
    const [searchText, setSearchText] = useState("");
    const [startDate, setStartDate] =useState(new Date());;
    const data = [
        { name: '대상자', value: 400 },
        { name: '전체', value: 300 },
      ];

    const handleMonthChange = (offset) => {
      const newDate = addMonths(startDate, offset);
      setStartDate(newDate);
    };

    const data1 = [
      { 항목: '첫인사', 대상자: 4, 전체: 4.25 },
      { 항목: '본인확인', 대상자: 4.3, 전체: 4.38 },
      { 항목: '필수안내', 대상자: 3.4, 전체: 3.92 },
      { 항목: '끝인사', 대상자: 5, 전체: 4.44 },
    ];
    
    const data2 = [
      { 항목: '불법추심', 대상자: 0.18, 전체: 0.12 },
      { 항목: '금지문구', 대상자: 0.46, 전체: 0.30 },
      { 항목: '민원성', 대상자: 0.37, 전체: 0.38 },
    ];

    return(
        <LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={ko}>
            <Box border={1} borderColor="#ccc" padding={2} marginBottom={2}>
            <Grid container spacing={2} alignItems="center" justifyContent="center">
                <Grid item xs={12} sm={6} md={4}>
                <Box display="flex" alignItems="center" gap={1}>
                    <label>상담사</label>
                    <TextField
                    variant="outlined"
                    size="small"
                    value={searchText}
                    onChange={(e) => setSearchText(e.target.value)}
                    />
                </Box>
                </Grid>
    
                <Grid item xs={12} sm={6} md={4}>
                    <Box display="flex" alignItems="center" gap={2}>
                        <label>연월</label>
                        <IconButton size="small" onClick={() => handleMonthChange(-1)}>
                          <ArrowBackIosNewIcon fontSize="small" />
                        </IconButton>

                        <Typography variant="subtitle1" sx={{ mx: 1, flexGrow: 1, textAlign: 'center' }}>
                          {dayjs(startDate).format('YYYY년 M월')}
                        </Typography>

                        <IconButton size="small" onClick={() => handleMonthChange(1)}>
                          <ArrowForwardIosIcon fontSize="small" />
                        </IconButton>
                    </Box>
                </Grid>
            </Grid>
            </Box>
            
            <Box>
            <Box display="flex" alignItems="flex-start"  justifyContent="center" gap={2}>
              <Box display="flex" flexDirection="column" alignItems="center">
                <Typography variant="subtitle1" mb={1}>유효 콜 수</Typography>
                <BarChart
                  width={300}
                  height={200}
                  data={data}
                  layout="vertical"
                  margin={{ top: 20, right: 20, left: 20, bottom: 5 }}
                >
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis type="number" />
                  <YAxis dataKey="name" type="category" />
                  <Tooltip />
                  <Bar dataKey="value" barSize={20}>
                    {data.map((entry, index) => (
                      <Cell
                        key={`cell-${index}`}
                        fill={index === 0 ? '#C0504D' : '#4F81BD'}
                      />
                    ))}
                  <LabelList
                      dataKey="value"
                      position="left"
                      content={({ x, y, width, height, value, index }) => {
                        return (
                          <text
                            x={x - 10}
                            y={y + height / 2 + 4}
                            fontSize={14}
                            textAnchor="end"
                          >
                          </text>
                        );
                      }}
                    />
                  </Bar>
                </BarChart>
              </Box>
              
              <Box width="1px" height="240px" bgcolor="black" mx={2} />

              <Box display="flex" flexDirection="column" alignItems="center">
                <Typography variant="subtitle1" mb={1}>스크립트 Score</Typography>
                <BarChart
                  width={300}
                  height={200}
                  data={data}
                  layout="vertical"
                  margin={{ top: 20, right: 20, left: 20, bottom: 5 }}
                >
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis type="number" />
                  <YAxis dataKey="name" type="category" />
                  <Tooltip />
                  <Bar dataKey="value" barSize={20}>
                    {data.map((entry, index) => (
                      <Cell
                        key={`cell-${index}`}
                        fill={index === 0 ? '#C0504D' : '#4F81BD'}
                      />
                    ))}
                  <LabelList
                      dataKey="value"
                      position="left"
                      content={({ x, y, width, height, value, index }) => {
                        return (
                          <text
                            x={x - 10}
                            y={y + height / 2 + 4}
                            fontSize={14}
                            textAnchor="end"
                          >
                          </text>
                        );
                      }}
                    />
                  </Bar>
                </BarChart>
              </Box>

              <Box width="1px" height="240px" bgcolor="black" mx={2} />

              <Box display="flex" flexDirection="column" alignItems="center">
                <Typography variant="subtitle1" mb={1}>문제소지콜 빈도</Typography>
                <BarChart
                  width={300}
                  height={200}
                  data={data}
                  layout="vertical"
                  margin={{ top: 20, right: 20, left: 20, bottom: 5 }}
                >
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis type="number" />
                  <YAxis dataKey="name" type="category" />
                  <Tooltip />
                  <Bar dataKey="value" barSize={20}>
                    {data.map((entry, index) => (
                      <Cell
                        key={`cell-${index}`}
                        fill={index === 0 ? '#C0504D' : '#4F81BD'}
                      />
                    ))}
                  <LabelList
                      dataKey="value"
                      position="left"
                      content={({ x, y, width, height, value, index }) => {
                        return (
                          <text
                            x={x - 10}
                            y={y + height / 2 + 4}
                            fontSize={14}
                            textAnchor="end"
                          >
                          </text>
                        );
                      }}
                    />
                  </Bar>
                </BarChart>
              </Box>
            </Box>
            <Box width="100%" height="1px" bgcolor="black" mt={3} />
          </Box>

          <Box display="flex" gap={2}>
          <Box
            display="grid"
            gridTemplateRows="auto 1fr"
            width="50%"
            border={1}
            margin={5}
            borderColor="grey.500"
            borderRadius={1}
            overflow="hidden"
          >
            <Box
              sx={{
                fontWeight: 'bold',
                fontSize: 18,
                p: 1,
                borderBottom: '1px solid',
                borderColor: 'grey.500',
                textAlign: 'center',
              }}
            >
              항목별 스크립트 Score 비교
            </Box>

            <Box
              display="grid"
              gridTemplateColumns="1fr 1fr 1fr"
              sx={{ textAlign: 'center' }}
            >
              <Box borderBottom={1} borderColor="grey.500" p={1} fontWeight="bold">항목</Box>
              <Box borderBottom={1} borderColor="grey.500" p={1} fontWeight="bold">대상자 월 평균</Box>
              <Box borderBottom={1} borderColor="grey.500" p={1} fontWeight="bold">전체 월 평균</Box>

              {data1.map((row, idx) => (
                <React.Fragment key={idx}>
                  <Box borderBottom={1} borderColor="grey.300" p={1}>{row.항목}</Box>
                  <Box borderBottom={1} borderColor="grey.300" p={1}>{row.대상자}</Box>
                  <Box borderBottom={1} borderColor="grey.300" p={1}>{row.전체}</Box>
                </React.Fragment>
              ))}
            </Box>
          </Box>

            <Box width="1px" height="300px" bgcolor="black" mx={2} />
            
            <Box
            display="grid"
            gridTemplateRows="auto 1fr"
            width="50%"
            border={1}
            margin={5}
            borderColor="grey.500"
            borderRadius={1}
            overflow="hidden"
          >
            <Box
              sx={{
                fontWeight: 'bold',
                fontSize: 18,
                p: 1,
                borderBottom: '1px solid',
                borderColor: 'grey.500',
                textAlign: 'center',
              }}
            >
              항목별 문제소지 콜 비교 (%)
            </Box>

            <Box
              display="grid"
              gridTemplateColumns="1fr 1fr 1fr"
              sx={{ textAlign: 'center' }}
            >
              <Box borderBottom={1} borderColor="grey.500" p={1} fontWeight="bold">항목</Box>
              <Box borderBottom={1} borderColor="grey.500" p={1} fontWeight="bold">대상자 빈도</Box>
              <Box borderBottom={1} borderColor="grey.500" p={1} fontWeight="bold">전체 빈도</Box>

              {data2.map((row, idx) => (
                <React.Fragment key={idx}>
                  <Box borderBottom={1} borderColor="grey.300" p={1}>{row.항목}</Box>
                  <Box borderBottom={1} borderColor="grey.300" p={1}>{row.대상자}</Box>
                  <Box borderBottom={1} borderColor="grey.300" p={1}>{row.전체}</Box>
                </React.Fragment>
              ))}
            </Box>
          </Box>
          </Box>
        </LocalizationProvider>
  );
}

export default Evaluation;