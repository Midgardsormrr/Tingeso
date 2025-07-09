import httpClient from "../http-common";

const formatDate = (date) => date.toISOString().split('T')[0];

const transformReportData = (apiData) => {
  if (!apiData) return null;

  // Extraer todos los meses únicos
  const allMonths = new Set();
  Object.values(apiData).forEach(categoryData => {
    Object.keys(categoryData).forEach(month => {
      allMonths.add(month);
    });
  });

  const sortedMonths = Array.from(allMonths).sort();

  // Crear filas para cada categoría
  const rows = Object.entries(apiData).map(([category, monthsData]) => {
    const row = { category };
    sortedMonths.forEach(month => {
      row[month] = monthsData[month] || 0;
    });
    return row;
  });

  // Calcular totales por mes
  const totalsRow = { category: 'TOTAL' };
  sortedMonths.forEach(month => {
    totalsRow[month] = rows.reduce((sum, row) => sum + (row[month] || 0), 0);
  });

  return {
    months: sortedMonths,
    rows: [...rows, totalsRow],
    rawData: apiData
  };
};

export const getRevenueReport = async (startDate, endDate) => {
  try {
    const params = {
      start: formatDate(startDate),
      end: formatDate(endDate),
      t: Date.now() // Evitar caché
    };

    const response = await httpClient.get('/reservations/report/by-laps', { params });
    console.log('Report data received:', response.data);
    return transformReportData(response.data);
  } catch (error) {
    console.error('Error fetching revenue report:', error);
    throw error;
  }
};

export const getPeopleReport = async (startDate, endDate) => {
  const params = {
    start: formatDate(startDate),
    end: formatDate(endDate),
    t: Date.now()
  };
  return httpClient.get('/reservations/report/by-people', { params })
    .then(res => transformReportData(res.data));
};

export default {
  getRevenueReport,
  getPeopleReport
};