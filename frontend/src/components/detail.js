import React from "react";
import { TextField, Box, Typography, Grid, Tooltip, Button } from "@mui/material";

function Script({ identityInfo }){
  return(
    <>
    <Typography variant="h6" gutterBottom
        sx={{ color: 'white', backgroundColor: '#404040', display: 'inline-block', padding: '4px 8px', borderRadius: '4px' }}>
        스트립트 준수
      </Typography>
      <Box display="grid" maxWidth={500} gridTemplateColumns="150px 1fr" rowGap={2} columnGap={2} mt={2} mb={3}>
        <Typography variant="subtitle1" fontWeight="bold" alignSelf="center">
          본인확인
        </Typography>
        <TextField variant="outlined" value={identityInfo} InputProps={{ readOnly: true }}
          size="small" sx={{ '& fieldset': { borderColor: '#2076E4' } }} />

        <Typography variant="subtitle1" fontWeight="bold" alignSelf="center">
          소속+상담사 소개
        </Typography>
        <TextField variant="outlined" value={identityInfo} InputProps={{ readOnly: true }}
          size="small" sx={{ '& fieldset': { borderColor: '#2076E4' } }} />

        <Typography variant="subtitle1" fontWeight="bold" alignSelf="center">
          끝인사
        </Typography>
        <TextField variant="outlined" value={identityInfo} InputProps={{ readOnly: true }}
          size="small" sx={{ '& fieldset': { borderColor: '#2076E4' } }} />

        <Typography variant="subtitle1" fontWeight="bold" alignSelf="center">
          업무필수안내
        </Typography>
        <TextField variant="outlined" value={identityInfo} InputProps={{ readOnly: true }}
          size="small" sx={{ '& fieldset': { borderColor: '#2076E4' } }} />
      </Box>
      </>
  );
}

function ScriptTable(){
  const rowData = [
    { label: '이중출금 안내', result: '해당사항없음' },
    { label: '가상계좌 입금시간 안내(오후11시)', result: '누락' },
    {
      label: '단기연체등록일 통보',
      result: '이행',
    },
    {
      label: '출금동의 및 계좌변경 녹취',
      result: '이행',
    },
    {
      label: '1회성 즉시출금시 1회성임 안내',
      result: '이행',
    },
    {
      label: '결제일 변경 시 경과이자, 적용일자, 월할부금',
      result: '해당사항없음',
    },
    {
      label: '계좌 등록 시 적용일자, 해당 은행에서 계좌등록 안내 문자 수신 통보됨 안내',
      result: '해당사항없음',
    },
  ];

  return(
    <>
    <Box sx={{ maxWidth: 'fit-content', display: 'grid', gridTemplateColumns: '3fr 1fr', gap: 0, border: '1px solid #404040', margin: '0 auto', }}>
        {rowData.map((row, index) => (
          <React.Fragment key={index}>
            <Box sx={{ border: '1px solid #404040', p: 1, fontSize: 14, display: 'flex', alignItems: 'center', }}>
              {row.label}
            </Box>
            <Box sx={{ border: '1px solid #404040', p: 1, fontSize: 14, color: '#2076E4', }}>
              {row.tooltip ? (
                <Tooltip
                  title={
                    <Box>
                      {row.tooltip.map((item, i) => (
                        <Typography key={i} fontSize={13}>
                          • {item}
                        </Typography>
                      ))}
                    </Box>
                  }
                  arrow
                  placement="left"
                >
                  <span>{row.result}</span>
                </Tooltip>
              ) : (
                row.result
              )}
            </Box>
          </React.Fragment>
        ))}
      </Box>
    </>
  );
}

function RestScript(){
  const categories = [
    {
      title: '오안내',
      items: [
        { text: '당일 가상계좌 또는 즉시출금 요청이나 익일 자동이체 계좌 납부안내', value: 'Y' },
        { text: '당일 가상계좌 또는 즉시출금 요청 시 1일 연체이자 추가됨 미안내', value: 'N' },
        { text: '통장잔고 있다고 하나 가상계좌 안내', value: 'N' },
      ],
    },
    {
      title: '불법추심',
      items: [
        { text: '추심원의 신분 밝히지 않음', value: 'N' },
        { text: '주위에 알림', value: 'Y' },
        { text: '대신 변제 요구', value: 'N' },
        { text: '빌려서 변제 요구', value: 'N' },
        { text: '법절차 진행중이라는 거짓표시 및 검찰 등을 사칭', value: 'N' },
      ],
    },
    {
      title: '금지문구',
      items: [
        { text: '욕설', value: 'Y' },
        { text: '비속어', value: 'N' },
      ],
    },
    {
      title: '납부의사',
      items: [
        { text: '납부의사', value: 'Y' },
      ],
    },
  ];
  
  return(
    <>
    <Box sx={{ width: '100%', mt: 3 }}>
        <Grid container spacing={4} justifyContent="space-between">
          <Grid item sx={{ width: '48%' }}>
            {[categories[0], categories[1]].map((category, idx) => (
              <Box key={idx} sx={{ mb: 4 }}>
                <Box sx={{ backgroundColor: '#404040', color: 'white', px: 2, py: 1, borderRadius: '4px', display: 'inline-block', mb: 1, }}>
                  <Typography variant="subtitle1" fontWeight="bold">
                    {category.title}
                  </Typography>
                </Box>
                {category.items.map((item, i) => (
                  <Grid container alignItems="center" justifyContent="space-between" key={i} sx={{ mb: 1 }}>
                    <Grid item xs={10}>
                      <Typography variant="body2">{item.text}</Typography>
                    </Grid>
                    <Grid item xs={2} sx={{ textAlign: 'center' }}>
                      <Button variant="outlined" sx={{ minWidth: 40, px: 2 }}>
                        {item.value}
                      </Button>
                    </Grid>
                  </Grid>
                ))}
              </Box>
            ))}
          </Grid>

          <Grid item sx={{ width: '48%', marginLeft: 'auto' }}>
            {[categories[2], categories[3]].map((category, idx) => (
              <Box key={idx} sx={{ mb: 4, }}>
                <Box sx={{ backgroundColor: '#404040', color: 'white', px: 2, py: 1, borderRadius: '4px', display: 'inline-block', mb: 1 }} >
                  <Typography variant="subtitle1" fontWeight="bold">
                    {category.title}
                  </Typography>
                </Box>
                {category.items.map((item, i) => (
                  <Grid container alignItems="center" justifyContent="space-between" key={i} sx={{ mb: 1 }} >
                    <Grid item xs={10}>
                      <Typography variant="body2">{item.text}</Typography>
                    </Grid>
                    <Grid item xs={2} sx={{ textAlign: 'center' }}>
                      <Button variant="outlined" sx={{ minWidth: 40, px: 2 }}>
                        {item.value}
                      </Button>
                    </Grid>
                  </Grid>
                ))}
              </Box>
            ))}
          </Grid>
        </Grid>
      </Box>
    </>
  );
}

function Detail() {
  return (
    <Box>
      <Script />
      <ScriptTable />
      <RestScript />
    </Box>
  );
}

export default Detail;
