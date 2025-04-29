// src/components/AddEditReservation.jsx
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
  TextField, Button, Container, Typography,
  Box, Chip, Divider, Grid, CircularProgress
} from "@mui/material";
import reservationService from "../services/reservation.service";

const AddEditReservation = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [reservation, setReservation] = useState({ 
    startDateTime: "", 
    laps: 10, 
    numberOfPeople: 1, 
    clientRuts: [], 
    kartCodes: []
  });
  const [currentRut, setCurrentRut] = useState("");
  const [currentKart, setCurrentKart] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (id) {
      setLoading(true);
      reservationService.getById(id)
        .then(data => setReservation({
          ...data,
          startDateTime: data.startDateTime.split('T')[0] + 'T' + data.startDateTime.split('T')[1].slice(0,5)
        }))
        .catch(err => { 
          console.error("Error cargando reserva:", err); 
          alert("No se pudo cargar la reserva"); 
          navigate("/reservations"); 
        })
        .finally(() => setLoading(false));
    }
  }, [id, navigate]);

  const handleAddRut = () => { 
    if (currentRut && !reservation.clientRuts.includes(currentRut)) { 
      setReservation(prev => ({ ...prev, clientRuts: [...prev.clientRuts, currentRut] })); 
      setCurrentRut(""); 
    }
  };

  const handleRemoveRut = rut => setReservation(prev => ({ ...prev, clientRuts: prev.clientRuts.filter(r => r !== rut) }));
  const handleAddKart = () => { 
    if (currentKart && !reservation.kartCodes.includes(currentKart)) { 
      setReservation(prev => ({ ...prev, kartCodes: [...prev.kartCodes, currentKart] })); 
      setCurrentKart(""); 
    }
  };
  const handleRemoveKart = k => setReservation(prev => ({ ...prev, kartCodes: prev.kartCodes.filter(x => x !== k) }));

  const handleSubmit = async e => {
    e.preventDefault();
    setLoading(true);
    const dto = {
      startDateTime: reservation.startDateTime,
      laps: reservation.laps,
      numberOfPeople: reservation.numberOfPeople,
      clientRuts: reservation.clientRuts,
      kartCodes: reservation.kartCodes
    };
    try {
      if (id) await reservationService.update(id, dto);
      else    await reservationService.create(dto);
      alert(id ? "Reserva actualizada" : "Reserva creada");
      navigate("/reservations");
    } catch (err) {
      console.error("Error guardando reserva:", err);
      alert("Error al guardar los cambios");
    } finally { setLoading(false); }
  };

  if (loading) return (<Container sx={{ display:'flex', justifyContent:'center', mt:4 }}><CircularProgress/></Container>);

  return (
    <Container maxWidth="md" sx={{ mt:4, py:4 }}>
      <Typography variant="h4" gutterBottom>{id ? "Editar Reserva" : "Nueva Reserva"}</Typography>
      <form onSubmit={handleSubmit}>
        <Grid container spacing={3}>
          <Grid item xs={12} md={6}>
            <TextField
              fullWidth
              label="Fecha y Hora de Inicio"
              type="datetime-local"
              value={reservation.startDateTime}
              onChange={e => setReservation(prev => ({ ...prev, startDateTime: e.target.value }))}
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
              onChange={e => setReservation(prev => ({ ...prev, laps: parseInt(e.target.value)||0 }))}
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
              onChange={e => setReservation(prev => ({ ...prev, numberOfPeople: parseInt(e.target.value)||1 }))}
              required
              inputProps={{ min: 1, max: 15 }}
            />
          </Grid>

          <Grid item xs={12}>
            <Divider sx={{ my:2 }} />
            <Typography variant="h6">Clientes</Typography>
            <Box sx={{ display:'flex', gap:2, my:1 }}>
              <TextField fullWidth label="Agregar RUT" value={currentRut} onChange={e=>setCurrentRut(e.target.value)} />
              <Button variant="contained" onClick={handleAddRut}>Agregar</Button>
            </Box>
            <Box sx={{ display:'flex', gap:1, flexWrap:'wrap' }}>
              {reservation.clientRuts.map(r=> <Chip key={r} label={r} onDelete={()=>handleRemoveRut(r)} />)}
            </Box>
          </Grid>

          <Grid item xs={12}>
            <Divider sx={{ my:2 }} />
            <Typography variant="h6">Karts</Typography>
            <Box sx={{ display:'flex', gap:2, my:1 }}>
              <TextField fullWidth label="Código de Kart" value={currentKart} onChange={e=>setCurrentKart(e.target.value)} />
              <Button variant="contained" onClick={handleAddKart}>Agregar</Button>
            </Box>
            <Box sx={{ display:'flex', gap:1, flexWrap:'wrap' }}>
              {reservation.kartCodes.map(k=> <Chip key={k} label={k} color="primary" onDelete={()=>handleRemoveKart(k)} />)}
            </Box>
          </Grid>

          <Grid item xs={12}>
            <Box sx={{ display:'flex', justifyContent:'flex-end', gap:2 }}>
              <Button type="submit" variant="contained" disabled={loading}>{id?"Actualizar":"Crear"}</Button>
              <Button variant="contained" color="secondary" onClick={()=>navigate("/reservations")}>Cancelar</Button>
            </Box>
          </Grid>
        </Grid>
      </form>
    </Container>
  );
};

export default AddEditReservation;