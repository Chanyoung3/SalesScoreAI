import React from "react";
import { TextField, Box, Typography, Grid } from "@mui/material";

function Detail({ identityInfo }) {
  return (
    <Box sx={{ mt: 3 }}>
      <Typography variant="h6" gutterBottom
      sx={{ color: 'white', backgroundColor: '#404040', display: 'inline-block', padding: '4px 8px', borderRadius: '4px' }}>
        스트립트 준수
      </Typography>.

      <Grid container spacing={2} alignItems="center">
        <Typography variant="subtitle1" fontWeight="bold">
          본인확인
        </Typography>
        <Grid item xs={8}>
          <TextField fullWidth variant="outlined" value={identityInfo} InputProps={{ readOnly: true }}size="small"/>
        </Grid>
      </Grid>
      <Grid container spacing={2} alignItems="center">
        <Typography variant="subtitle1" fontWeight="bold">
          소속+상담사 소개
        </Typography>
        <Grid item xs={8}>
          <TextField fullWidth variant="outlined" value={identityInfo} InputProps={{ readOnly: true }}size="small"/>
        </Grid>
      </Grid>
      <Grid container spacing={2} alignItems="center">
        <Typography variant="subtitle1" fontWeight="bold">
          끝인사
        </Typography>
        <Grid item xs={8}>
          <TextField fullWidth variant="outlined" value={identityInfo} InputProps={{ readOnly: true }}size="small"/>
        </Grid>
      </Grid>
      <Grid container spacing={2} alignItems="center">
        <Typography variant="subtitle1" fontWeight="bold">
          업무필수안내
        </Typography>
        <Grid item xs={8}>
          <TextField fullWidth variant="outlined" value={identityInfo} InputProps={{ readOnly: true }}size="small"/>
        </Grid>
      </Grid>
    </Box>
  );
}

export default Detail;
