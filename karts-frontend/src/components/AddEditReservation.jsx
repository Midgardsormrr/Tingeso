import { useEffect, useState } from "react";
import { useNavigate, useParams, useLocation } from "react-router-dom";
import { 
  TextField, Button, Container, Typography, 
  MenuItem, Box, Chip, Divider, Grid,
  CircularProgress
} from "@mui/material";
import reservationService from "../services/reservation.service";

const AddEditReservation = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const location = useLocation();
  const [reservation, setReservation] = useState({
    startDateTime: new Date().toISOString().slice(0, 16),
    laps: 10,
    numberOfPeople: 1,
    clientRuts: [],
    kartCodes: [],
    status: "CONFIRMED"
  });
  const [currentRut, setCurrentRut] = useState("");
  const [currentKart, setCurrentKart] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (id) {
      setLoading(true);
      // Primero intenta usar los datos de location.state
      if (location.state?.reservation) {
        const { reservation } = location.state;
        setReservation({
          ...reservation,
          startDateTime: new Date(reservation.startDateTime).toISOString().slice(0, 16)
        });
        setLoading(false);
      } else {
        // Si no hay state, hace fetch a la API
        const fetchReservation = async () => {
          try {
            const data = await reservationService.getById(id);
            setReservation({
              ...data,
              startDateTime: new Date(data.startDateTime).toISOString().slice(0, 16)
            });
          } catch (error) {
            console.error("Error cargando reserva:", error);
            navigate("/reservations", { replace: true });
          } finally {
            setLoading(false);
          }
        };
        fetchReservation();
      }
    }
  }, [id, navigate, location.state]);

  const handleAddRut = () => {
    if (currentRut && !reservation.clientRuts.includes(currentRut)) {
      setReservation({
        ...reservation,
        clientRuts: [...reservation.clientRuts, currentRut]
      });
      setCurrentRut("");
    }
  };

  const handleRemoveRut = (rutToRemove) => {
    setReservation({
      ...reservation,
      clientRuts: reservation.clientRuts.filter(rut => rut !== rutToRemove)
    });
  };

  const handleAddKart = () => {
    if (currentKart && !reservation.kartCodes.includes(currentKart)) {
      setReservation({
        ...reservation,
        kartCodes: [...reservation.kartCodes, currentKart]
      });
      setCurrentKart("");
    }
  };

  const handleRemoveKart = (kartToRemove) => {
    setReservation({
      ...reservation,
      kartCodes: reservation.kartCodes.filter(kart => kart !== kartToRemove)
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    // Validaciones básicas
    if (reservation.clientRuts.length === 0) {
      setError("Debe agregar al menos un cliente");
      setLoading(false);
      return;
    }

    if (reservation.kartCodes.length === 0) {
      setError("Debe agregar al menos un kart");
      setLoading(false);
      return;
    }

    try {
      const reservationData = {
        ...reservation,
        startDateTime: new Date(reservation.startDateTime).toISOString()
      };

      if (id) {
        await reservationService.update(reservationData);
        alert("Reserva actualizada exitosamente");
      } else {
        await reservationService.create(reservationData);
        alert("Reserva creada exitosamente");
      }
      navigate("/reservations");
    } catch (error) {
      console.error("Error guardando reserva:", error);
      setError(error.response?.data?.message || "Error al guardar los cambios");
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <Container maxWidth="md" sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
        <CircularProgress />
      </Container>
    );
  }

  return (
    <Container maxWidth="md" sx={{ mt: 4, py: 4 }}>
      <Typography variant="h4" gutterBottom>
        {id ? "Editar Reserva" : "Nueva Reserva"}
      </Typography>
      
      {error && (
        <Typography color="error" sx={{ mb: 2 }}>
          {error}
        </Typography>
      )}

      <form onSubmit={handleSubmit}>
        <Grid container spacing={3}>
          <Grid item xs={12} md={6}>
            <TextField
              fullWidth
              label="Fecha y Hora de Inicio"
              type="datetime-local"
              value={reservation.startDateTime}
              onChange={(e) => setReservation({
                ...reservation,
                startDateTime: e.target.value
              })}
              margin="normal"
              required
              InputLabelProps={{ shrink: true }}
            />
          </Grid>

          <Grid item xs={12} md={6}>
            <TextField
              fullWidth
              label="Número de Vueltas"
              type="number"
              value={reservation.laps}
              onChange={(e) => setReservation({
                ...reservation,
                laps: parseInt(e.target.value) || 0
              })}
              margin="normal"
              required
              inputProps={{ min: 1 }}
            />
          </Grid>

          <Grid item xs={12} md={6}>
            <TextField
              fullWidth
              label="Número de Personas"
              type="number"
              value={reservation.numberOfPeople}
              onChange={(e) => setReservation({
                ...reservation,
                numberOfPeople: parseInt(e.target.value) || 1
              })}
              margin="normal"
              required
              inputProps={{ min: 1, max: 15 }}
            />
          </Grid>

          <Grid item xs={12} md={6}>
            <TextField
              select
              fullWidth
              label="Estado"
              value={reservation.status}
              onChange={(e) => setReservation({
                ...reservation,
                status: e.target.value
              })}
              margin="normal"
              required
            >
              <MenuItem value="CONFIRMED">Confirmada</MenuItem>
              <MenuItem value="CANCELLED">Cancelada</MenuItem>
            </TextField>
          </Grid>

          <Grid item xs={12}>
            <Divider sx={{ my: 2 }} />
            <Typography variant="h6" gutterBottom>
              Clientes
            </Typography>
            <Box sx={{ display: 'flex', gap: 2, mb: 2 }}>
              <TextField
                fullWidth
                label="Agregar RUT de Cliente"
                value={currentRut}
                onChange={(e) => setCurrentRut(e.target.value)}
                margin="none"
              />
              <Button
                variant="contained"
                onClick={handleAddRut}
                sx={{ minWidth: 120 }}
              >
                Agregar
              </Button>
            </Box>
            <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
              {reservation.clientRuts.map(rut => (
                <Chip
                  key={rut}
                  label={rut}
                  onDelete={() => handleRemoveRut(rut)}
                />
              ))}
            </Box>
          </Grid>

          <Grid item xs={12}>
            <Divider sx={{ my: 2 }} />
            <Typography variant="h6" gutterBottom>
              Karts
            </Typography>
            <Box sx={{ display: 'flex', gap: 2, mb: 2 }}>
              <TextField
                fullWidth
                label="Agregar Código de Kart"
                value={currentKart}
                onChange={(e) => setCurrentKart(e.target.value)}
                margin="none"
              />
              <Button
                variant="contained"
                onClick={handleAddKart}
                sx={{ minWidth: 120 }}
              >
                Agregar
              </Button>
            </Box>
            <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
              {reservation.kartCodes.map(kart => (
                <Chip
                  key={kart}
                  label={kart}
                  color="primary"
                  onDelete={() => handleRemoveKart(kart)}
                />
              ))}
            </Box>
          </Grid>

          <Grid item xs={12}>
            <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 2 }}>
              <Button
                variant="contained"
                color="primary"
                type="submit"
                disabled={loading}
                sx={{ mt: 3 }}
              >
                {loading ? <CircularProgress size={24} /> : (id ? "Actualizar" : "Crear")}
              </Button>
              <Button
                variant="contained"
                color="secondary"
                sx={{ mt: 3 }}
                onClick={() => navigate("/reservations")}
                disabled={loading}
              >
                Cancelar
              </Button>
            </Box>
          </Grid>
        </Grid>
      </form>
    </Container>
  );
};

export default AddEditReservation;