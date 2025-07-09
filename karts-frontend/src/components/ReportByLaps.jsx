import React, { useState, useEffect } from 'react';
import { getRevenueReport } from '../services/report.service';

const ReportByLaps = () => {
  const [dates, setDates] = useState({
    start: '',
    end: ''
  });
  const [report, setReport] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [displayDates, setDisplayDates] = useState({
    start: '',
    end: ''
  });

  const formatDate = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    const day = date.getDate().toString().padStart(2, '0');
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const year = date.getFullYear();
    return `${day}-${month}-${year}`;
  };

  useEffect(() => {
    const today = new Date();
    const firstDay = new Date(today.getFullYear(), today.getMonth(), 1);
    const lastDay = new Date(today.getFullYear(), today.getMonth() + 1, 0);

    const startDate = firstDay.toISOString().split('T')[0];
    const endDate = lastDay.toISOString().split('T')[0];

    setDates({ start: startDate, end: endDate });
    setDisplayDates({ start: formatDate(startDate), end: formatDate(endDate) });
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!dates.start || !dates.end) {
      setError("Ambas fechas son requeridas");
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const data = await getRevenueReport(new Date(dates.start), new Date(dates.end));

      if (!data || Object.keys(data).length === 0) {
        throw new Error("No se encontraron datos para el período seleccionado");
      }

      setReport(data);
      setDisplayDates({
        start: formatDate(dates.start),
        end: formatDate(dates.end)
      });
    } catch (err) {
      setError(err.message || "Error al cargar el reporte");
    } finally {
      setLoading(false);
    }
  };

  const formatCurrency = (val) => {
    if (typeof val !== 'number') return '0';
    return val.toLocaleString("es-CL", {
      minimumFractionDigits: 0,
      maximumFractionDigits: 0
    });
  };

  const renderTable = (data) => {
    if (!data) return null;

    const rows = typeof data === 'string' ? JSON.parse(data) : data;
    const months = ["Enero", "Febrero", "Marzo"];
    const rowKeys = Object.keys(rows);

    const rowTotals = rowKeys.map(key =>
      months.reduce((sum, month) => sum + (rows[key][month] || 0), 0)
    );

    const colTotals = months.map(month =>
      rowKeys.reduce((sum, key) => sum + (rows[key][month] || 0), 0)
    );

    const grandTotal = colTotals.reduce((a, b) => a + b, 0);

    return (
      <table className="report-table">
        <thead>
          <tr>
            <th>Número de vueltas o tiempo máximo permitido</th>
            {months.map(month => <th key={month}>{month}</th>)}
            <th>TOTAL</th>
          </tr>
        </thead>
        <tbody>
          {rowKeys.map((key, i) => (
            <tr key={key}>
              <td>{key}</td>
              {months.map(month => (
                <td key={month}>{formatCurrency(rows[key][month] || 0)}</td>
              ))}
              <td><strong>{formatCurrency(rowTotals[i])}</strong></td>
            </tr>
          ))}
        </tbody>
        <tfoot>
          <tr>
            <td><strong>TOTAL</strong></td>
            {colTotals.map((total, i) => (
              <td key={i}><strong>{formatCurrency(total)}</strong></td>
            ))}
            <td><strong>{formatCurrency(grandTotal)}</strong></td>
          </tr>
        </tfoot>
      </table>
    );
  };

  return (
    <div className="report-container">
      <h1>Reporte de Ingresos</h1>

      <form onSubmit={handleSubmit} className="date-form">
        <div className="input-group">
          <div className="date-input-container">
            <label>Fecha Inicio:</label>
            <input
              type="date"
              className="date-input"
              value={dates.start}
              onChange={(e) => setDates({ ...dates, start: e.target.value })}
            />
          </div>

          <div className="date-input-container">
            <label>Fecha Fin:</label>
            <input
              type="date"
              className="date-input"
              value={dates.end}
              onChange={(e) => setDates({ ...dates, end: e.target.value })}
            />
          </div>

          <button type="submit" className="load-button" disabled={loading}>
            {loading ? 'Cargando...' : 'Generar Reporte'}
          </button>
        </div>
      </form>

      {error && <div className="error-message">{error}</div>}

      {report && (
        <div className="report-results">
          <h2>Resultados:</h2>
          <div className="date-display">
            <p>Fecha Inicio: {displayDates.start}</p>
            <p>Fecha Fin: {displayDates.end}</p>
          </div>

          <div className="report-sections">
            {report.rows && (
              <div className="report-section">
                <h3>Ingresos por categoría y mes</h3>
                {renderTable(report.rows)}
              </div>
            )}

            {report.TOTAL !== undefined && (
              <div className="total-section">
                <h3>TOTAL GENERAL</h3>
                <p className="total-amount">{formatCurrency(report.TOTAL)}</p>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default ReportByLaps;
