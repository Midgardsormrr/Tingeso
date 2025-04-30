import { useEffect, useState } from 'react';
import { format, startOfWeek, addDays, eachHourOfInterval, set } from 'date-fns';
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
        console.log("Datos de reservas recibidos:", data);
        setReservations(data);
        setLoading(false);
      } catch (err) {
        console.error("Error al obtener reservas:", err);
        setError(err.message);
        setLoading(false);
      }
    };

    fetchData();
  }, [currentDate]);

  const calculateDurationHeight = (start, end) => {
    const startDate = new Date(start);
    const endDate = new Date(end);
    const durationMinutes = (endDate - startDate) / (1000 * 60);
    return (durationMinutes / 60) * 60; // 60px por hora
  };

  const calculatePositionOffset = (start) => {
    const startDate = new Date(start);
    const minutes = startDate.getMinutes();
    return (minutes / 60) * 60; // 60px por hora
  };

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

  // Agrupamos las reservas por día y hora
  const groupedReservations = reservations.reduce((acc, reservation) => {
    if (!reservation.start) return acc;
    
    const reservationDate = new Date(reservation.start);
    const dayKey = format(reservationDate, 'yyyy-MM-dd');
    const hourKey = format(reservationDate, 'HH:00'); // Agrupamos por hora redondeada
    
    acc[dayKey] = acc[dayKey] || {};
    acc[dayKey][hourKey] = acc[dayKey][hourKey] || [];
    acc[dayKey][hourKey].push(reservation);
    
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
              const dayKey = format(day.date, 'yyyy-MM-dd');
              const hourKey = format(t, 'HH:00');
              const slotReservations = groupedReservations[dayKey]?.[hourKey] || [];
              
              return (
                <div key={`${dayKey}-${hourKey}`} className="time-cell">
                  {slotReservations.map(r => (
                    <div 
                      key={r.reservationCode}
                      className="reservation-block"
                      style={{
                        height: `${calculateDurationHeight(r.start, r.end)}px`,
                        top: `${calculatePositionOffset(r.start)}px`
                      }}
                    >
                      <div className="reservation-content">
                        <span className="reservation-code">{r.reservationCode}</span>
                        <span className="reservation-time">
                          {format(new Date(r.start), 'HH:mm')} - {format(new Date(r.end), 'HH:mm')}
                        </span>
                      </div>
                    </div>
                  ))}
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