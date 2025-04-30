// services/report.service.js
export const getRevenueReport = async (startDate, endDate) => {
    const response = await fetch(`tu_endpoint?start=${startDate}&end=${endDate}`);
    
    if (!response.ok) {
      const error = await response.text();
      throw new Error(error.slice(0, 100)); // Mostrar parte del error
    }
    
    const contentType = response.headers.get('content-type');
    if (!contentType?.includes('application/json')) {
      throw new Error('Respuesta no es JSON');
    }
  
    return response.json();
  };