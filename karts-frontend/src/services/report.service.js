import axios from 'axios';

export const getRevenueReport = async (start, end) => {
    const res = await axios.get(`/report/by-laps`, {
      params: { startDate: start, endDate: end },
    });
    return res.data.data; // <-- Asegura que sea un arreglo
  };
  
  export const getPeopleReport = async (start, end) => {
    const res = await axios.get(`/report/by-people`, {
      params: { startDate: start, endDate: end },
    });
    return res.data.data; // <-- Asegura que sea un arreglo
  };
