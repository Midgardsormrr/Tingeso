import React, { useState } from 'react';
import { parseISO, format } from 'date-fns';
import { es } from 'date-fns/locale';
import { getRevenueReport } from '../services/report.service';
import './ReportByLaps.css';

const ReportByLaps = () => {
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate]     = useState('');
  const [report, setReport]       = useState(null);
  const [loading, setLoading]     = useState(false);
  const [error, setError]         = useState(null);

  const loadReport = async (e) => {
    e.preventDefault();
    console.log('⏳ loadReport start', { startDate, endDate });
    setLoading(true);
    setError(null);
    try {
      const data = await getRevenueReport(startDate, endDate);
      console.log('✅ servicio devolvió:', data);
      setReport(data);
    } catch (e) {
      console.error('❌ error al cargar reporte:', e);
      setError('Error al cargar el reporte');
      setReport(null);
    } finally {
      setLoading(false);
    }
  };

  // ¿ya tenemos datos?
  const hasData = report && Object.keys(report).length > 0;

  // extraer meses de todas las categorías
  let months = [];
  if (hasData) {
    const monthSet = new Set();
    Object.values(report).forEach(mMap => {
      Object.keys(mMap).forEach(m => monthSet.add(m));
    });
    months = Array.from(monthSet)
      .sort()
      .map(m => ({ key: m, label: format(parseISO(m + '-01'), 'LLLL yyyy', { locale: es }) }));
  }

  return (
    <div className="report-container">
      <h2 className="report-header">Reporte por Vueltas</h2>

      <form onSubmit={loadReport} className="input-group">
        <input
          type="date"
          value={startDate}
          onChange={e => setStartDate(e.target.value)}
          className="date-input"
        />
        <input
          type="date"
          value={endDate}
          onChange={e => setEndDate(e.target.value)}
          className="date-input"
        />
        <button type="submit" className="load-button">
          {loading ? 'Cargando...' : 'Cargar Reporte'}
        </button>
      </form>

      {error && <p className="error">{error}</p>}

      {/* siempre mostramos el JSON para debug */}
      <pre style={{ background: '#f7f7f7', padding: '0.5rem' }}>
        {JSON.stringify(report, null, 2)}
      </pre>

      {hasData && (
        <table className="report-table">
          <thead>
            <tr>
              <th>Categoría</th>
              {months.map(m => <th key={m.key}>{m.label}</th>)}
              <th>TOTAL</th>
            </tr>
          </thead>
          <tbody>
            {Object.entries(report).map(([cat, mMap]) => {
              const rowTotal = months.reduce((sum, m) => sum + (mMap[m.key]||0), 0);
              return (
                <tr key={cat} className={cat === 'TOTAL' ? 'total-row' : ''}>
                  <td className="category-cell">{cat}</td>
                  {months.map(m => (
                    <td key={m.key}>{(mMap[m.key]||0).toLocaleString('es-CL')}</td>
                  ))}
                  <td>{rowTotal.toLocaleString('es-CL')}</td>
                </tr>
              );
            })}
          </tbody>
        </table>
      )}

      {!hasData && !loading && <p className="no-data">No hay datos para mostrar</p>}
    </div>
  );
};

export default ReportByLaps;
