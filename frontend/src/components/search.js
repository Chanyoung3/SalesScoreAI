import axios from 'axios';

export const searchConsultations = async (searchParams) => {
  try {
    const response = await axios.post(
      `${process.env.REACT_APP_API_URL}/api/consultations/search`, // Spring 서버 주소
      searchParams,
      {
        headers: {
          'Content-Type': 'application/json',
        },
      }
    );
    return response.data;
  } catch (error) {
    console.error('검색 요청 실패:', error);
    throw error;
  }
};
