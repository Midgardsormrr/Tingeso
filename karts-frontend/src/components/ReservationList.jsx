// src/components/ReservationList.jsx
import { useEffect, useState } from "react";
import { Link as RouterLink } from "react-router-dom";
import reservationService from "../services/reservation.service";
import {
  Table, TableBody, TableCell, TableContainer,
  TableHead, TableRow, Paper, Button,
  Box, Typography, Chip, CircularProgress
} from "@mui/material";
import { Add as AddIcon, Edit as EditIcon, Delete as DeleteIcon } from "@mui/icons-material";

const ReservationList = () => {
  const [reservations, setReservations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const loadReservations = async () => {
    try {
      const data = await reservationService.getAll();
      setReservations(Array.isArray(data) ? data : []);
    } catch (err) {
      console.error("Error cargando reservas:", err);
      setError("Error al cargar las reservas");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadReservations();
  }, []);

  const handleDelete = async (id) => {
    if (window.confirm("¿Está seguro que desea eliminar esta reserva?")) {
      try {
        await reservationService.remove(id);
        await loadReservations();
        alert("Reserva eliminada exitosamente");
      } catch (err) {
        console.error("Error eliminando reserva:", err);
        alert("Error al eliminar la reserva");
      }
    }
  };

  const formatDateTime = (dateTimeString) => {
    if (!dateTimeString) return "-";
    const date = new Date(dateTimeString);
    return date.toLocaleString('es-CL');
  };

  if (loading) return <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}><CircularProgress /></Box>;
  if (error)   return <Typography color="error" sx={{ mt: 4, textAlign: 'center' }}>{error}</Typography>;

  return (
    <TableContainer component={Paper}
      sx={{ width: "100%", height: "calc(100vh - 64px)", display: "flex", flexDirection: "column", overflow: "auto", border: "none", marginTop: 0 }}>
      <Button
        component={RouterLink}
        to="/reservations/create"
        variant="contained"
        sx={{ m: 2, width: "fit-content", alignSelf: "flex-start" }}
        startIcon={<AddIcon />}
      >
        Nueva Reserva
      </Button>

      <Table sx={{ minWidth: "100%", tableLayout: "fixed", "& .MuiTableCell-root": { padding: "12px 16px", verticalAlign: "middle" } }}>
        <TableHead>
          <TableRow>
            <TableCell sx={{ fontWeight: 600 }}>Código</TableCell>
            <TableCell sx={{ fontWeight: 600 }}>Clientes</TableCell>
            <TableCell sx={{ fontWeight: 600 }}>Inicio</TableCell>
            <TableCell sx={{ fontWeight: 600 }}>Término</TableCell>
            <TableCell sx={{ fontWeight: 600 }}>Personas</TableCell>
            <TableCell sx={{ fontWeight: 600 }}>Vueltas</TableCell>
            <TableCell sx={{ fontWeight: 600 }}>Karts</TableCell>
            <TableCell sx={{ fontWeight: 600 }}>Estado</TableCell>
            <TableCell sx={{ fontWeight: 600 }} align="center">Acciones</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {reservations.map(r => (
            <TableRow key={r.id} hover>
              <TableCell>{r.reservationCode || "-"}</TableCell>
              <TableCell>
                <Box sx={{ display: 'flex', gap: .5, flexWrap: 'wrap' }}>
                  {r.clientRuts?.map(rut => <Chip key={rut} label={rut} size="small" />)}
                </Box>
              </TableCell>
              <TableCell>{formatDateTime(r.startDateTime)}</TableCell>
              <TableCell>{formatDateTime(r.endDateTime)}</TableCell>
              <TableCell align="center">{r.numberOfPeople}</TableCell>
              <TableCell align="center">{r.laps}</TableCell>
              <TableCell>
                <Box sx={{ display: 'flex', gap: .5, flexWrap: 'wrap' }}>
                  {r.kartCodes?.map(k => <Chip key={k} label={k} size="small" color="primary" />)}
                </Box>
              </TableCell>
              <TableCell>
                <Chip
                  label={r.status}
                  color={r.status === 'CONFIRMED' ? 'success' : r.status === 'CANCELLED' ? 'error' : 'default'}
                />
              </TableCell>
              <TableCell align="center">
                <Box sx={{ display: 'flex', gap: 1, justifyContent: 'center', '& .MuiButton-root': { minWidth: 100, fontSize: '.75rem', padding: '6px 12px', flex: 1 } }}>
                  <Button
                    component={RouterLink}
                    to={`/reservations/edit/${r.id}`}
                    variant="contained"
                    color="info"
                    size="small"
                    startIcon={<EditIcon fontSize="small" />}
                  >
                    Editar
                  </Button>
                  <Button
                    variant="contained"
                    color="error"
                    size="small"
                    onClick={() => handleDelete(r.id)}
                    startIcon={<DeleteIcon fontSize="small" />}
                  >
                    Eliminar
                  </Button>
                </Box>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
};

export default ReservationList;