import { useEffect, useState } from 'react';
import { format, startOfWeek, addDays, parseISO, eachHourOfInterval, set } from 'date-fns';
import { es } from 'date-fns/locale';
import { getWeeklySchedule } from '../services/schedule.service';
import './WeeklySchedule.css';

const WeeklySchedule = () => {
  const [currentDate, setCurrentDate] = useState(new Date());
  const [reservations, setReservations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const weekStart = startOfWeek(currentDate, { weekStartsOn: 1 });
    const weekEnd = addDays(weekStart, 6);

    const fetchData = async () => {
      try {
        const data = await getWeeklySchedule(
          format(weekStart, 'yyyy-MM-dd'),
          format(weekEnd, 'yyyy-MM-dd')
        );
        console.log("weekly-schedule response:", data);
        setReservations(data);
        setLoading(false);
      } catch (err) {
        setError(err.message);
        setLoading(false);
      }
    };

    fetchData();
  }, [currentDate]);

  const weekStart = startOfWeek(currentDate, { weekStartsOn: 1 });
  const weekEnd = addDays(weekStart, 6);

  const timeSlots = eachHourOfInterval({
    start: set(weekStart, { hours: 8, minutes: 0 }),
    end: set(weekStart, { hours: 22, minutes: 0 })
  });

  const weekDays = Array.from({ length: 7 }).map((_, i) => {
    const date = addDays(weekStart, i);
    return {
      date,
      label: format(date, 'EEE dd/MM', { locale: es })
    };
  });

  // Agrupamos las reservas correctamente con fecha y hora
  const grouped = reservations.reduce((acc, r) => {
    if (!r.start) return acc;
    
    const reservationDate = new Date(r.start);
    const dayKey = format(reservationDate, 'yyyy-MM-dd');
    const hourKey = format(reservationDate, 'HH:00'); // Redondea a la hora completa
    
    acc[dayKey] = acc[dayKey] || {};
    acc[dayKey][hourKey] = acc[dayKey][hourKey] || [];
    acc[dayKey][hourKey].push(r);
    
    return acc;
  }, {});

  if (loading) return <div className="loading">Cargando calendario…</div>;
  if (error) return <div className="error">Error: {error}</div>;

  return (
    <div className="weekly-schedule-container">
      <div className="schedule-header">
        <button onClick={() => setCurrentDate(d => addDays(d, -7))} className="nav-button">
          &lt; Semana Anterior
        </button>
        <h2>
          {format(weekStart, 'dd MMM yyyy', { locale: es })} – {format(weekEnd, 'dd MMM yyyy', { locale: es })}
        </h2>
        <button onClick={() => setCurrentDate(d => addDays(d, 7))} className="nav-button">
          Próxima Semana &gt;
        </button>
      </div>

      <div className="schedule-grid">
        <div className="time-column">
          <div className="day-header empty-header"></div>
          {timeSlots.map(t => (
            <div key={t.toString()} className="time-slot">
              {format(t, 'HH:mm')}
            </div>
          ))}
        </div>

        {weekDays.map(day => (
          <div key={day.date.toString()} className="day-column">
            <div className="day-header">{day.label}</div>
            {timeSlots.map(t => {
              const d = format(day.date, 'yyyy-MM-dd'); // 'yyyy-MM-dd' para el día
              const h = format(t, 'HH:mm'); // 'HH:mm' para la hora
              const slot = grouped[d]?.[h] || []; // Obtenemos las reservas agrupadas para el día y hora
              return (
                <div key={`${d}-${h}`} className="time-cell">
                  {slot.length > 0 ? (
                    slot.map(r => (
                      <div key={r.reservationCode} className="reservation-block">
                        {r.reservationCode}
                      </div>
                    ))
                  ) : (
                    <div className="no-reservation">Sin reservas</div>
                  )}
                </div>
              );
            })}
          </div>
        ))}
      </div>
    </div>
  );
};

export default WeeklySchedule;
