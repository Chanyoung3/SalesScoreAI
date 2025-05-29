import { TextField, Box, Grid } from "@mui/material";
import { BarChart, Bar, XAxis, YAxis, Tooltip, CartesianGrid } from 'recharts';
import { LocalizationProvider } from '@mui/x-date-pickers';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import ko from 'date-fns/locale/ko';
import { useState } from "react";
import '../App.css';

function Evaluation(){
    const [searchText, setSearchText] = useState("");
    const [startDate, setStartDate] = useState(null);
    const data = [
        { name: 'A', value: 400 },
        { name: 'B', value: 300 },
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
                        <DatePicker
                        views={['year', 'month']}
                        value={startDate}
                        onChange={(newValue) => setStartDate(newValue)}
                        format="yyyy년MM월"
                        slotProps={{
                            textField: {
                              size: 'small',
                              sx: { height: 40, '& .MuiInputBase-root': { height: 40, },
                                '& input': { padding: '10px 14px', },
                              }
                            }
                          }}
                        />
                    </Box>
                </Grid>
            </Grid>
            </Box>
            
            <BarChart
                width={500}
                height={200}
                data={data}
                layout="vertical"  // 이게 가로막대(수평) 그래프 핵심
                margin={{ top: 20, right: 30, left: 20, bottom: 5 }}
                >
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis type="number" />
                <YAxis dataKey="name" type="category" />
                <Tooltip />
                <Bar dataKey="value" fill="#8884d8" />
            </BarChart>
        </LocalizationProvider>
  );
}

export default Evaluation;