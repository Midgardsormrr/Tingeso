import React, { useState } from 'react';
import { parseISO, format, isValid } from 'date-fns';
import { es } from 'date-fns/locale';
import { getRevenueReport } from '../services/report.service';
import './ReportByLaps.css';

const ReportByLaps = () => {
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [report, setReport] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const validateDates = () => {
    if (!startDate || !endDate) {
      throw new Error('Ambas fechas son requeridas');
    }
    if (new Date(startDate) > new Date(endDate)) {
      throw new Error('La fecha inicial no puede ser mayor a la final');
    }
  };

  const loadReport = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    
    try {
      validateDates();
      
      const data = await getRevenueReport(startDate, endDate);
      
      if (!data || typeof data !== 'object' || Object.values(data).some(entry => typeof entry !== 'object')) {
        throw new Error('Formato de datos inválido del servidor');
      }

      setReport(data);
    } catch (e) {
      console.error('❌ Error:', e);
      setError(e.message || 'Error al cargar el reporte');
      setReport(null);
    } finally {
      setLoading(false);
    }
  };

  const hasData = report && typeof report === 'object' && Object.keys(report).length > 0;

  const getMonthsData = () => {
    const monthSet = new Set();
    
    Object.values(report).forEach(mMap => {
      if (typeof mMap === 'object') {
        Object.keys(mMap).forEach(m => {
          if (/^\d{4}-\d{2}$/.test(m)) {
            monthSet.add(m);
          }
        });
      }
    });

    return Array.from(monthSet)
      .sort()
      .map(m => {
        try {
          const date = parseISO(`${m}-01`);
          return {
            key: m,
            label: isValid(date) 
              ? format(date, 'LLLL yyyy', { locale: es }) 
              : `${m} (inválido)`
          };
        } catch (e) {
          return { key: m, label: `${m} (error)` };
        }
      });
  };

  const months = hasData ? getMonthsData() : [];

  return (
    <div className="report-container">
      <h2 className="report-header">Reporte por Vueltas</h2>

      <form onSubmit={loadReport} className="input-group">
        <input
          type="date"
          value={startDate}
          onChange={e => setStartDate(e.target.value)}
          className="date-input"
          required
        />
        <input
          type="date"
          value={endDate}
          onChange={e => setEndDate(e.target.value)}
          className="date-input"
          required
          min={startDate}
        />
        <button 
          type="submit" 
          className="load-button"
          disabled={loading}
        >
          {loading ? 'Cargando...' : 'Generar Reporte'}
        </button>
      </form>

      {error && (
        <div className="error-message">
          ⚠️ Error: {error}
        </div>
      )}

      {hasData ? (
        <>
          <table className="report-table">
            <thead>
              <tr>
                <th>Categoría</th>
                {months.map(m => (
                  <th key={m.key} title={m.key}>{m.label}</th>
                ))}
                <th>TOTAL</th>
              </tr>
            </thead>
            <tbody>
              {Object.entries(report).map(([category, monthData]) => {
                const total = months.reduce(
                  (sum, month) => sum + (Number(monthData[month.key]) || 0),
                  0
                );

                return (
                  <tr 
                    key={category} 
                    className={category === 'TOTAL' ? 'total-row' : ''}
                  >
                    <td className="category-cell">{category}</td>
                    {months.map(month => (
                      <td key={month.key}>
                        {(Number(monthData[month.key]) || 0).toLocaleString('es-CL')}
                      </td>
                    ))}
                    <td className="total-cell">
                      {total.toLocaleString('es-CL')}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>

          <div className="data-debug">
            <h3>Datos en crudo (debug):</h3>
            <pre>{JSON.stringify(report, null, 2)}</pre>
          </div>
        </>
      ) : (
        !loading && <p className="no-data">🎈 No hay datos para mostrar</p>
      )}
    </div>
  );
};

export default ReportByLaps;