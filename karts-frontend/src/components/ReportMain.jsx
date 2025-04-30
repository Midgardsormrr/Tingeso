import React, { useState } from "react";
import DatePicker from "react-datepicker";
import { useNavigate } from "react-router-dom";
import "react-datepicker/dist/react-datepicker.css";

const ReportMain = () => {
  const navigate = useNavigate();
  const [startDate, setStartDate] = useState(new Date("2024-01-01"));
  const [endDate, setEndDate] = useState(new Date("2024-03-31"));

  const handleNavigate = (type) => {
    // Puedes pasar las fechas por estado global o query params
    if (type === "person") {
      navigate("/reservations/report/by-people", { state: { startDate, endDate } });
    } else {
      navigate("reservations/report/by-laps", { state: { startDate, endDate } });
    }
  };

  return (
    <div className="report-container" style={{ padding: "2rem" }}>
      <h2>Generar Reporte</h2>

      <div style={{ marginBottom: "1rem" }}>
        <label>Selecciona rango de fechas:</label>
        <div style={{ display: "flex", gap: "1rem", marginTop: "0.5rem" }}>
          <DatePicker
            selected={startDate}
            onChange={(date) => setStartDate(date)}
            selectsStart
            startDate={startDate}
            endDate={endDate}
            dateFormat="dd/MM/yyyy"
          />
          <DatePicker
            selected={endDate}
            onChange={(date) => setEndDate(date)}
            selectsEnd
            startDate={startDate}
            endDate={endDate}
            dateFormat="dd/MM/yyyy"
          />
        </div>
      </div>

      <div style={{ display: "flex", gap: "1rem" }}>
        <button onClick={() => handleNavigate("people")}>
          Reporte por Personas
        </button>
        <button onClick={() => handleNavigate("laps")}>
          Reporte por Vueltas
        </button>
      </div>
    </div>
  );
};

export default ReportMain;
