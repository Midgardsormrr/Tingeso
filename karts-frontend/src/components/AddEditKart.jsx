// src/components/AddEditKart.jsx
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Container, Typography, TextField, Button, MenuItem, Grid, CircularProgress, Box } from "@mui/material";
import kartService from "../services/kart.service";

const AddEditKart = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [kart, setKart] = useState({ code: "", status: "AVAILABLE" });
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (id) {
      setLoading(true);
      kartService.getById(id)
        .then(data => setKart({ code: data.code, status: data.status }))
        .catch(err => { console.error(err); navigate("/karts"); })
        .finally(() => setLoading(false));
    }
  }, [id, navigate]);

  const handleSubmit = async e => {
    e.preventDefault();
    setLoading(true);
    try {
      if (id) await kartService.updateStatus(id, kart.status);
      else    await kartService.create(kart);
      navigate("/karts");
    } catch (err) {
      console.error(err);
      alert("Error guardando kart");
    } finally { setLoading(false); }
  };

  if (loading) return <Box sx={{ display:'flex',justifyContent:'center',mt:4 }}><CircularProgress/></Box>;

  return (
    <Container maxWidth="sm" sx={{ mt:4 }}>
      <Typography variant="h5" gutterBottom>{id ? "Editar Kart" : "Nuevo Kart"}</Typography>
      <form onSubmit={handleSubmit}>
        <Grid container spacing={2}>
          {!id && (
            <Grid item xs={12}>
              <TextField fullWidth label="Código" value={kart.code} onChange={e=>setKart(prev=>({...prev,code:e.target.value}))} required />
            </Grid>
          )}
          <Grid item xs={12}>
            <TextField select fullWidth label="Status" value={kart.status} onChange={e=>setKart(prev=>({...prev,status:e.target.value}))}>
              <MenuItem value="AVAILABLE">AVAILABLE</MenuItem>
              <MenuItem value="NOT AVAILABLE">NOT AVAILABLE</MenuItem>
            </TextField>
          </Grid>
          <Grid item xs={12}>
            <Button type="submit" variant="contained">{id?"Actualizar":"Crear"}</Button>
            <Button variant="outlined" sx={{ ml:2 }} onClick={()=>navigate("/karts")}>Cancelar</Button>
          </Grid>
        </Grid>
      </form>
    </Container>
  );
};
export default AddEditKart;