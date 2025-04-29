import { useEffect, useState } from 'react';
import { format, startOfWeek, addDays, parseISO, eachHourOfInterval } from 'date-fns';
import { es } from 'date-fns/locale';
import { getWeeklySchedule } from '../services/schedule.service';
import './WeeklySchedule.css';

const WeeklySchedule = () => {
  const [currentDate, setCurrentDate] = useState(new Date());
  const [reservations, setReservations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Obtener semana actual
  const weekStart = startOfWeek(currentDate, { weekStartsOn: 1 }); // Lunes como primer día
  const weekEnd = addDays(weekStart, 6);

  // Obtener reservas
  useEffect(() => {
    const fetchData = async () => {
      try {
        const data = await getWeeklySchedule(
          format(weekStart, 'yyyy-MM-dd'),
          format(weekEnd, 'yyyy-MM-dd')
        );
        
        setReservations(data);
        setLoading(false);
      } catch (err) {
        setError(err.message);
        setLoading(false);
      }
    };

    fetchData();
  }, [currentDate]);

  // Generar días de la semana
  const weekDays = Array.from({ length: 7 }).map((_, i) => ({
    date: addDays(weekStart, i),
    label: format(addDays(weekStart, i), 'EEE dd/MM', { locale: es })
  }));

  // Generar horas del día (8:00 - 22:00)
  const timeSlots = eachHourOfInterval({
    start: new Date().setHours(8, 0),
    end: new Date().setHours(22, 0)
  });

  // Organizar reservas por día y hora
  const groupedReservations = reservations.reduce((acc, reservation) => {
    const day = format(parseISO(reservation.start), 'yyyy-MM-dd');
    const hour = format(parseISO(reservation.start), 'HH:mm');
    
    if (!acc[day]) acc[day] = {};
    if (!acc[day][hour]) acc[day][hour] = [];
    
    acc[day][hour].push(reservation);
    return acc;
  }, {});

  if (loading) return <div className="loading">Cargando calendario...</div>;
  if (error) return <div className="error">Error: {error}</div>;

  return (
    <div className="weekly-schedule-container">
      <div className="schedule-header">
        <button 
          onClick={() => setCurrentDate(prev => addDays(prev, -7))}
          className="nav-button"
        >
          &lt; Semana Anterior
        </button>
        
        <h2>
          {format(weekStart, 'dd MMM yyyy', { locale: es })} - {format(weekEnd, 'dd MMM yyyy', { locale: es })}
        </h2>
        
        <button 
          onClick={() => setCurrentDate(prev => addDays(prev, 7))}
          className="nav-button"
        >
          Próxima Semana &gt;
        </button>
      </div>

      <div className="schedule-grid">
        {/* Columna de horas */}
        <div className="time-column">
          {/* Celda vacía para alinear con encabezado de días */}
          <div className="day-header empty-header"></div>
          {timeSlots.map(time => (
            <div key={time} className="time-slot">
              {format(time, 'HH:mm')}
            </div>
          ))}
        </div>

        {/* Grid de días */}
        {weekDays.map(day => (
          <div key={day.date} className="day-column">
            <div className="day-header">{day.label}</div>
            {timeSlots.map(time => {
              const formattedDate = format(day.date, 'yyyy-MM-dd');
              const formattedTime = format(time, 'HH:mm');
              const slotReservations = groupedReservations[formattedDate]?.[formattedTime] || [];

              return (
                <div 
                  key={`${formattedDate}-${formattedTime}`} 
                  className="time-cell"
                >
                  {slotReservations.map(reservation => (
                    <div 
                      key={reservation.reservationCode}
                      className="reservation-block"
                    >
                      {reservation.reservationCode}
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
