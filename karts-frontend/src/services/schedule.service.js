import httpClient from "../http-common";

export const getWeeklySchedule = async (startDate, endDate) => {
  try {
    const response = await httpClient.get("/reservations/weekly-schedule", {
      params: { start: startDate, end: endDate }
    });
    
    // Verificamos la estructura de la respuesta
    if (Array.isArray(response.data)) {
      return response.data;
    } else if (Array.isArray(response.data.content)) {
      return response.data.content;
    } else {
      console.warn("Formato de respuesta inesperado:", response.data);
      return [];
    }
  } catch (error) {
    console.error("Error al obtener el calendario:", error);
    throw error;
  }
};