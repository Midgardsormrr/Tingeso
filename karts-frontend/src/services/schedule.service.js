export const getWeeklySchedule = async (startDate, endDate) => {
    try {
      const response = await fetch(
        `http://localhost:8090/reservations/weekly-schedule?start=${startDate}&end=${endDate}`
      );
      
      if (!response.ok) {
        throw new Error('Error al obtener el calendario');
      }
      
      return await response.json();
    } catch (error) {
      console.error('Error:', error);
      throw error;
    }
  };