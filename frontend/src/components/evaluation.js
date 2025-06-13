import { TextField, Box, Grid, Typography, IconButton, Button } from "@mui/material";
import { BarChart, Bar, XAxis, YAxis, Tooltip, CartesianGrid, LabelList, Cell } from 'recharts';
import { LocalizationProvider } from '@mui/x-date-pickers';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import ArrowBackIosNewIcon from '@mui/icons-material/ArrowBackIosNew';
import ArrowForwardIosIcon from '@mui/icons-material/ArrowForwardIos';
import { addMonths } from "date-fns";
import dayjs from 'dayjs';
import ko from 'date-fns/locale/ko';
import React, { useEffect, useState } from 'react';
import Header from "./Header";
import Sidebar from "./Sidebar";
import '../App.css';
import axios from 'axios';

function Evaluation({ logOut }) {
  const [searchText, setSearchText] = useState("");
  const [startDate, setStartDate] = useState(new Date());;
  const [scriptScoreData, setScriptScoreData] = useState([]);
  const [issueCallData, setIssueCallData] = useState([]);
  const [counselorScore1, setcounselorScore1] = useState(0);
  const [allScore1, setallScore1] = useState(0);
  const [counselorScore2, setcounselorScore2] = useState(0);
  const [allScore2, setallScore2] = useState(0);

  const handleMonthChange = (offset) => {
    const newDate = addMonths(startDate, offset);
    setStartDate(newDate);
  };

  useEffect(() => {
    console.log("초기 searchText:", searchText);
    const counselorId = searchText.trim();
    const yearMonth = dayjs(startDate).format('YYYY-MM');
    const params = new URLSearchParams();
    if (counselorId) params.append("counselorId", counselorId);
    params.append("yearMonth", yearMonth);

    axios.get(`${process.env.REACT_APP_API_URL}/dashboard/statistics?${params.toString()}`)
      .then((res) => {
        const data = res.data;
        setScriptScoreData(data.scriptScores);
        setIssueCallData(data.issueCalls);
        setcounselorScore1(data.specificCounselorOverallAvgScore);
        setallScore1(data.overallOverallAvgScore);
        setcounselorScore2(data.specificCounselorLowScoreCallFrequency);
        setallScore2(data.overallLowScoreCallFrequency);
      })
      .catch((err) => {
        console.error("통계 데이터 불러오기 실패", err);
      });
  }, [searchText, startDate]);

  // 반올림 함수 (숫자 유지)
  const round2 = (num) => (typeof num === 'number' ? Math.round(num * 100) / 100 : 0);

  // 스크립트 점수 데이터 변환
  // tdata1: 스크립트 점수 테이블 데이터
  // tdata1, tdata2 각 항목 점수도 소수점 2자리 처리
  const tdata1 = scriptScoreData.map(item => ({
    항목: item.item,
    대상자: round2(item.targetAvgMonthlyScore),
    전체: round2(item.overallAvgMonthlyScore)
  }));

  const tdata2 = issueCallData.map(item => ({
    항목: item.item,
    대상자: round2(item.targetFrequency),
    전체: round2(item.overallFrequency)
  }));

  // data2, data3 점수도 소수점 2자리까지만
  const data2 = [
    { name: '대상자', value: round2(counselorScore1) },
    { name: '전체', value: round2(allScore1) }
  ];

  const data3 = [
    { name: '대상자', value: round2(counselorScore2) },
    { name: '전체', value: round2(allScore2) }
  ];

  return (
    <>
      <Box sx={{ width: "100%", margin: "0 auto", height: "100vh", display: "flex", flexDirection: "column" }} >
        <Header />
        <Box sx={{ display: "flex", flexGrow: 1, height: "calc(100vh - 64px)", width: "100%" }} >
          <Box sx={{ width: "280px", flexShrink: 0 }}>
            <Sidebar logOut={logOut} />
          </Box>
          <Box sx={{ flexGrow: 1, p: 2, overflowY: "auto" }}>
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
                <Box display="flex" alignItems="flex-start" justifyContent="center" gap={2}>
                  <Box display="flex" flexDirection="column" alignItems="center">
                    <Typography variant="subtitle1" mb={1}>스크립트 Score</Typography>
                    <BarChart
                      width={300}
                      height={200}
                      data={data2}
                      layout="vertical"
                      margin={{ top: 20, right: 20, left: 20, bottom: 5 }}
                    >
                      <CartesianGrid strokeDasharray="3 3" />
                      <XAxis type="number" />
                      <YAxis dataKey="name" type="category" />
                      <Tooltip />
                      <Bar dataKey="value" barSize={20}>
                        {data2.map((entry, index) => (
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
                      data={data3}
                      layout="vertical"
                      margin={{ top: 20, right: 20, left: 20, bottom: 5 }}
                    >
                      <CartesianGrid strokeDasharray="3 3" />
                      <XAxis type="number" />
                      <YAxis dataKey="name" type="category" />
                      <Tooltip />
                      <Bar dataKey="value" barSize={20}>
                        {data3.map((entry, index) => (
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

                    {tdata1.map((row, idx) => (
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

                    {tdata2.map((row, idx) => (
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
          </Box>
        </Box>
      </Box>
    </>
  );
}

export default Evaluation;