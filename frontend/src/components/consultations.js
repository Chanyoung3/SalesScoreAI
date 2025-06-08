import { TextField, Radio, RadioGroup, FormControlLabel, FormLabel, Button, Box, Grid, Modal } from "@mui/material";
import { DateRangePicker } from '@mui/x-date-pickers-pro/DateRangePicker';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import { LocalizationProvider } from '@mui/x-date-pickers';
import { DataGrid } from "@mui/x-data-grid";
import ko from 'date-fns/locale/ko';
import React, { useEffect, useState } from "react";
import Detail from './detail';
import Header from "./Header";
import Sidebar from "./Sidebar";
import '../App.css';

function Consultations({ logOut }) {
  const [searchText, setSearchText] = useState("");
  const [dateRange, setDateRange] = useState([null, null]);
  const [valid, setValid] = useState("ALL");
  const [misguide, setMisguide] = useState("ALL");
  const [bannedWords, setBannedWords] = useState("ALL");
  const [illegalCollection, setIllegalCollection] = useState("ALL");
  const [paymentIntention, setPaymentIntention] = useState("ALL");
  const [openPopup, setOpenPopup] = useState(false);
  
  const data = [
    { id: 1, date: "20250401", cnumber: "01067783418", agentId: "5250023", callId: "CALL-1234", valid: "유효", scriptScore: 15, misguide: "N", bannedWords: "N", illegalCollection: "N", paymentIntention: "Y" },
    { id: 2, date: "20250401", cnumber: "01082216452", agentId: "5250023", callId: "CALL-1235", valid: "유효", scriptScore: 18, misguide: "Y", bannedWords: "Y", illegalCollection: "N", paymentIntention: "N" },
    { id: 3, date: "20250401", cnumber: "01029477196", agentId: "5250018", callId: "CALL-1236", valid: "유효", scriptScore: 20, misguide: "N", bannedWords: "N", illegalCollection: "Y", paymentIntention: "Y" },
    { id: 4, date: "20250401", cnumber: "01093590056", agentId: "5250018", callId: "CALL-1237", valid: "유효", scriptScore: 20, misguide: "N", bannedWords: "N", illegalCollection: "Y", paymentIntention: "Y" }
  ];

  // 컬럼
  const columns = [
    { field: "date", headerName: "상담일자", flex: 1, disableColumnMenu: true },
    { field: "cnumber", headerName: "고객번호", flex: 1, disableColumnMenu: true },
    { field: "agentId", headerName: "상담사번호", flex: 1, sortable: false, disableColumnMenu: true },
    { field: "callId", headerName: "Call 번호", flex: 1, sortable: false, disableColumnMenu: true },
    { field: "valid", headerName: "유효 여부", flex: 1, sortable: false, disableColumnMenu: true },
    { field: "scriptScore", headerName: "스크립트 Score", flex: 1, disableColumnMenu: true },
    { field: "misguide", headerName: "오안내", flex: 1, sortable: false, disableColumnMenu: true },
    { field: "bannedWords", headerName: "금지문구", flex: 1, sortable: false, disableColumnMenu: true },
    { field: "illegalCollection", headerName: "불법추심", flex: 1, sortable: false, disableColumnMenu: true },
    { field: "paymentIntention", headerName: "납부의사", flex: 1, sortable: false, disableColumnMenu: true },
  ];

  /*
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    axios
      .get(`${process.env.REACT_APP_API_URL}/consultations/all`)
      .then((res) => {
        setData(res.data); // 받아온 데이터 저장
        setLoading(false);
      })
      .catch((err) => {
        console.error("데이터 불러오기 실패", err);
        setLoading(false);
      });
  }, []);

  if (loading) return <div>로딩 중...</div>;

   const handleSearch = async () => {
    const searchParams = {
      id: 'CALL-1234',
      bannedWords: 'Y',
      valid: 'Y',
    };

    try {
      const result = await searchConsultations(searchParams);
      setData(result);
    } catch (e) {
      alert('검색 중 오류 발생');
    }
  };

*/

  return (
    <>
      <Box sx={{ width: "100%", margin: "0 auto", height: "100vh", display: "flex", flexDirection: "column" }} >
        <Header />
        <Box sx={{ display: "flex", flexGrow: 1, height: "calc(100vh - 64px)", width: "100%" }} >
          <Box sx={{ width: "280px", flexShrink: 0 }}>
            <Sidebar logOut={logOut}/>
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
                      <FormLabel>유효여부</FormLabel>
                      <RadioGroup row value={valid} onChange={(e) => setValid(e.target.value)}>
                        <FormControlLabel value="ALL" control={<Radio />} label="ALL" />
                        <FormControlLabel value="Y" control={<Radio />} label="Y" />
                        <FormControlLabel value="N" control={<Radio />} label="N" />
                      </RadioGroup>

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

              <Box mt={2} sx={{ height: 400, width: "100%" }}>
                <DataGrid
                  rows={data}
                  columns={columns}
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
