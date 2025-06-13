import { TextField, Radio, RadioGroup, FormControlLabel, FormLabel, Button, Box, Grid, Modal } from "@mui/material";
import { DateRangePicker } from '@mui/x-date-pickers-pro/DateRangePicker';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import { LocalizationProvider } from '@mui/x-date-pickers';
import { DataGrid } from "@mui/x-data-grid";
import { startOfMonth, parseISO, isBefore, isAfter } from 'date-fns';
import ko from 'date-fns/locale/ko';
import React, { useEffect, useState } from "react";
import Detail from './detail';
import Header from "./Header";
import Sidebar from "./Sidebar";
import '../App.css';
import axios from 'axios';

function Consultations({ logOut }) {
  const [searchText, setSearchText] = useState("");
  const [dateRange, setDateRange] = useState([
    startOfMonth(new Date()),  // 시작일: 이번달 1일
    new Date()                 // 종료일: 오늘
  ]);

  const [misguide, setMisguide] = useState("ALL");
  const [bannedWords, setBannedWords] = useState("ALL");
  const [illegalCollection, setIllegalCollection] = useState("ALL");
  const [paymentIntention, setPaymentIntention] = useState("ALL");
  const [openPopup, setOpenPopup] = useState(false);
  const [selectedRow, setSelectedRow] = useState(null);

  const booleanFormatter = (params) => {
    if (params === null || params === undefined) return "-";
    if (params === true) return "Y";
    if (params === false) return "N";
    return "-";
  };

  // 컬럼
  const columns = [
    { field: "consultationDate", headerName: "상담일자", flex: 1, disableColumnMenu: true },
    { field: "customerNumber", headerName: "고객번호", flex: 1, disableColumnMenu: true },
    { field: "counselorNumber", headerName: "상담사번호", flex: 1, sortable: false, disableColumnMenu: true },
    { field: "callNumber", headerName: "Call 번호", flex: 1, sortable: false, disableColumnMenu: true },
    { field: "score", headerName: "스크립트 Score", flex: 1, disableColumnMenu: true },
    { field: "misguidance", headerName: "오안내", flex: 1, sortable: false, disableColumnMenu: true, valueFormatter: booleanFormatter },
    { field: "forbiddenPhrases", headerName: "금지문구", flex: 1, sortable: false, disableColumnMenu: true, valueFormatter: booleanFormatter },
    { field: "illegalCollection", headerName: "불법추심", flex: 1, sortable: false, disableColumnMenu: true, valueFormatter: booleanFormatter },
    { field: "paymentIntention", headerName: "납부의사", flex: 1, sortable: false, disableColumnMenu: true, valueFormatter: booleanFormatter },
  ];

  const [data, setData] = useState([]);

  useEffect(() => {
    axios
      .get(`${process.env.REACT_APP_API_URL}/dashboard`)
      .then((res) => {
        setData(res.data);
      })
      .catch((err) => {
        console.error("데이터 불러오기 실패", err);
      });
  }, []);

  const handleSearch = async () => {
    const counselorId = searchText.trim();
  
    try {
      let res;
      if (counselorId) {
        res = await axios.get(`${process.env.REACT_APP_API_URL}/dashboard/counselor_id/${counselorId}`);
      } else {
        res = await axios.get(`${process.env.REACT_APP_API_URL}/dashboard`);
      }
  
      // 응답 통일: 배열 형태로 변환
      const responseData = Array.isArray(res.data) ? res.data : [res.data];
  
      console.log("서버 응답 데이터:", responseData);
  
      const filteredData = responseData.filter(item => {
        const consultationDate = new Date(item.consultationDate);
        const startDate = dateRange[0];
        const endDate = dateRange[1];
  
        // 날짜 범위 필터링
        const isInDateRange =
          (!startDate || !isBefore(consultationDate, startDate)) &&
          (!endDate || !isAfter(consultationDate, endDate));
  
        // boolean 필터링 헬퍼 함수
        const matchesBooleanFilter = (fieldValue, filterValue) => {
          if (filterValue === "ALL") return true;
          if (filterValue === "Y") return fieldValue === true;
          if (filterValue === "N") return fieldValue === false;
          return true;
        };
  
        // 각 boolean 필터 조건 체크
        const isMisguideOk = matchesBooleanFilter(item.misguidance, misguide);
        const isBannedWordsOk = matchesBooleanFilter(item.forbiddenPhrases, bannedWords);
        const isIllegalCollectionOk = matchesBooleanFilter(item.illegalCollection, illegalCollection);
        const isPaymentIntentionOk = matchesBooleanFilter(item.paymentIntention, paymentIntention);
  
        return isInDateRange && isMisguideOk && isBannedWordsOk && isIllegalCollectionOk && isPaymentIntentionOk;
      });
  
      setData(filteredData);
    } catch (e) {
      console.error("검색 중 오류 발생:", e);
      alert('검색 중 오류 발생');
    }
  };
  

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
                    <Box display="flex" alignItems="center" gap={1}>
                      <label>연월일</label>
                      <DateRangePicker
                        localeText={{ start: '시작일', end: '종료일' }}
                        value={dateRange}
                        onChange={(newValue) => setDateRange(newValue)}
                        format="yyyy년MM월dd일"
                        slotProps={{
                          textField: {
                            size: 'small',
                            sx: {
                              height: 40, '& .MuiInputBase-root': { height: 40, },
                              '& input': { padding: '10px 14px', },
                            }
                          }
                        }}
                      />
                    </Box>
                  </Grid>
                </Grid>

                <Box mt={2} borderTop={1} pt={2}>
                  <Grid container spacing={2} justifyContent="center">
                    <Grid item xs={6}>
                      <FormLabel>금지문구</FormLabel>
                      <RadioGroup row value={bannedWords} onChange={(e) => setBannedWords(e.target.value)}>
                        <FormControlLabel value="ALL" control={<Radio />} label="ALL" />
                        <FormControlLabel value="Y" control={<Radio />} label="Y" />
                        <FormControlLabel value="N" control={<Radio />} label="N" />
                      </RadioGroup>

                      <FormLabel>납부의사</FormLabel>
                      <RadioGroup row value={paymentIntention} onChange={(e) => setPaymentIntention(e.target.value)}>
                        <FormControlLabel value="ALL" control={<Radio />} label="ALL" />
                        <FormControlLabel value="Y" control={<Radio />} label="Y" />
                        <FormControlLabel value="N" control={<Radio />} label="N" />
                      </RadioGroup>
                    </Grid>

                    <Grid item xs={6}>
                      <FormLabel>오안내</FormLabel>
                      <RadioGroup row value={misguide} onChange={(e) => setMisguide(e.target.value)}>
                        <FormControlLabel value="ALL" control={<Radio />} label="ALL" />
                        <FormControlLabel value="Y" control={<Radio />} label="Y" />
                        <FormControlLabel value="N" control={<Radio />} label="N" />
                      </RadioGroup>

                      <FormLabel>불법추심</FormLabel>
                      <RadioGroup row value={illegalCollection} onChange={(e) => setIllegalCollection(e.target.value)}>
                        <FormControlLabel value="ALL" control={<Radio />} label="ALL" />
                        <FormControlLabel value="Y" control={<Radio />} label="Y" />
                        <FormControlLabel value="N" control={<Radio />} label="N" />
                      </RadioGroup>

                      <Box textAlign="right" mt={2}>
                        <Button variant="contained" color="primary" onClick={handleSearch}>검색</Button>
                      </Box>
                    </Grid>
                  </Grid>
                </Box>
              </Box>

              <Box mt={2} sx={{ height: 730, width: "100%" }}>
                <DataGrid
                  rows={data}
                  columns={columns}
                  getRowId={(row) => row.callNumber}  // 또는 고유값 사용 (ex: row.customerNumber 등)
                  disableRowSelectionOnClick
                  onRowClick={(params) => {
                    setSelectedRow(params.row);
                    setOpenPopup(true);
                  }}
                />
              </Box>

              <Modal
                open={openPopup}
                onClose={() => setOpenPopup(false)}
                aria-labelledby="detail-popup"
                aria-describedby="detail-popup-description"
              >
                <Box
                  sx={{
                    position: "absolute",
                    top: "50%",
                    left: "50%",
                    transform: "translate(-50%, -50%)",
                    bgcolor: "background.paper",
                    boxShadow: 24,
                    p: 4,
                    width: {
                      xs: "95%",
                      sm: 700,
                      md: 1000,
                    },
                    maxHeight: "80vh",
                    overflowY: "auto",
                    borderRadius: 2,
                  }}
                >

                  <Detail />
                  <Button
                    onClick={() => setOpenPopup(false)}
                    variant="outlined"
                    sx={{ mb: 2 }}
                  >
                    닫기
                  </Button>
                </Box>
              </Modal>
            </LocalizationProvider>
          </Box>
        </Box>
      </Box>
    </>
  );
}


export default Consultations;
