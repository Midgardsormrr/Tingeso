import React, { useState } from 'react';
import { getPeopleReport } from '../services/report.service';

const ReportByPeople = () => {
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [report, setReport] = useState([]);

  const loadReport = async () => {
    try {
      const data = await getPeopleReport(startDate, endDate);
      setReport(data);
    } catch (error) {
      console.error('Error al cargar el reporte por personas:', error);
    }
  };

  return (
    <div>
      <h2>Reporte por Personas</h2>
      <input
        type="date"
        value={startDate}
        onChange={(e) => setStartDate(e.target.value)}
      />
      <input
        type="date"
        value={endDate}
        onChange={(e) => setEndDate(e.target.value)}
      />
      <button onClick={loadReport}>Cargar Reporte</button>

      <ul>
        {report.map((item, index) => (
          <li key={index}>{JSON.stringify(item)}</li>
        ))}
      </ul>
    </div>
  );
};

export default ReportByPeople;
