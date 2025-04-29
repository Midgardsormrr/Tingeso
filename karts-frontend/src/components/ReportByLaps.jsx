import React, { useState } from 'react';
import { getRevenueReport } from '../services/report.service';
import './ReportByLaps.css';

const ReportByLaps = () => {
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [report, setReport] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const loadReport = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await getRevenueReport(startDate, endDate);
      setReport(data || []);
    } catch (error) {
      console.error('Error al cargar el reporte por vueltas:', error);
      setError('Error al cargar el reporte');
      setReport([]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="report-container">
      <h2 className="report-header">Reporte por Vueltas</h2>
      
      <div className="input-group">
        <input
          type="date"
          value={startDate}
          onChange={(e) => setStartDate(e.target.value)}
          className="date-input"
        />
        <input
          type="date"
          value={endDate}
          onChange={(e) => setEndDate(e.target.value)}
          className="date-input"
        />
        <button 
          onClick={loadReport} 
          disabled={loading}
          className={`load-button ${loading ? 'disabled' : ''}`}
        >
          {loading ? 'Cargando...' : 'Cargar Reporte'}
        </button>
      </div>

      {error && <p className="error-message">{error}</p>}

      {loading && <p className="loading-message">Cargando reporte...</p>}

      {report && (
        <ul className="report-list">
          {report.length > 0 ? (
            report.map((item, index) => (
              <li key={index} className="report-item">
                <pre>{JSON.stringify(item, null, 2)}</pre>
              </li>
            ))
          ) : (
            <p className="no-data">No hay datos para mostrar</p>
          )}
        </ul>
      )}
    </div>
  );
};

export default ReportByLaps;