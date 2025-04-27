import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import reservationService from "../services/reservation.service";
import { 
  Table, TableBody, TableCell, TableContainer, 
  TableHead, TableRow, Paper, Button, 
  Box, Typography, Chip, CircularProgress
} from "@mui/material";
import { Add, Edit, Delete } from "@mui/icons-material";

const ReservationList = () => {
  const [reservations, setReservations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

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

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Typography color="error" sx={{ mt: 4, textAlign: 'center' }}>
        {error}
      </Typography>
    );
  }

  return (
    <TableContainer component={Paper}>
      <Button
        variant="contained"
        component={Link}
        to="/reservations/create"
        sx={{ m: 2 }}
        startIcon={<Add />}
      >
        Nueva Reserva
      </Button>

      <Table>
        <TableHead>
          <TableRow>
            <TableCell>ID</TableCell>
            <TableCell>Código</TableCell>
            <TableCell>Clientes</TableCell>
            <TableCell>Fecha/Hora Inicio</TableCell>
            <TableCell>Fecha/Hora Término</TableCell>
            <TableCell>Personas</TableCell>
            <TableCell>Vueltas</TableCell>
            <TableCell>Karts</TableCell>
            <TableCell>Estado</TableCell>
            <TableCell>Acciones</TableCell>
          </TableRow>
        </TableHead>
        
        <TableBody>
          {reservations.map((reservation) => (
            <TableRow key={reservation.id}>
              <TableCell>{reservation.id}</TableCell>
              <TableCell>{reservation.reservationCode}</TableCell>
              <TableCell>
                <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5 }}>
                  {reservation.clientRuts?.map(rut => (
                    <Chip key={rut} label={rut} size="small" />
                  ))}
                </Box>
              </TableCell>
              <TableCell>{formatDateTime(reservation.startDateTime)}</TableCell>
              <TableCell>{formatDateTime(reservation.endDateTime)}</TableCell>
              <TableCell>{reservation.numberOfPeople}</TableCell>
              <TableCell>{reservation.laps}</TableCell>
              <TableCell>
                <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5 }}>
                  {reservation.kartCodes?.map(kart => (
                    <Chip key={kart} label={kart} size="small" color="primary" />
                  ))}
                </Box>
              </TableCell>
              <TableCell>
                <Chip 
                  label={reservation.status} 
                  color={
                    reservation.status === "CONFIRMED" ? "success" : 
                    reservation.status === "CANCELLED" ? "error" : "default"
                  } 
                />
              </TableCell>
              <TableCell>
                <Box sx={{ display: 'flex', gap: 1 }}>
                  <Button
                    variant="contained"
                    color="info"
                    size="small"
                    onClick={() => navigate(`/reservations/edit/${reservation.id}`, { 
                      state: { reservation } 
                    })}
                    startIcon={<Edit fontSize="small" />}
                  >
                    Editar
                  </Button>
                  <Button
                    variant="contained"
                    color="error"
                    size="small"
                    onClick={() => handleDelete(reservation.id)}
                    startIcon={<Delete fontSize="small" />}
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