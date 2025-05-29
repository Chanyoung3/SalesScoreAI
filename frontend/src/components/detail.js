import React from "react";
import { Dialog, DialogTitle, DialogContent, DialogActions, Button } from "@mui/material";

const Detail = ({ open, onClose, rowData }) => {
  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle>상세 정보</DialogTitle>
      <DialogContent>
        <pre>{JSON.stringify(rowData, null, 2)}</pre>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>닫기</Button>
      </DialogActions>
    </Dialog>
  );
};

export default Detail;
