import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { TextField, Button, Container, Typography } from "@mui/material";
import clientService from "../services/client.service";

const AddEditClient = () => {
  const { rut } = useParams();
  const navigate = useNavigate();
  const [client, setClient] = useState({
    rut: "",
    name: "",
    email: "",
    birthDate: "",
    monthlyVisitCount: 0
  });

  // Cargar datos del cliente al montar el componente
  useEffect(() => {
    const loadClientData = async () => {
      if (rut) {
        try {
          const response = await clientService.getByRut(rut);
          const clientData = response.data;
          
          // Formatear fecha si es necesario (eliminar tiempo si existe)
          if (clientData.birthDate) {
            clientData.birthDate = clientData.birthDate.split('T')[0];
          }
          
          setClient(clientData);
        } catch (error) {
          console.error("Error cargando cliente:", error);
          alert("Error al cargar los datos del cliente");
          navigate("/clients");
        }
      }
    };

    loadClientData();
  }, [rut, navigate]);

  const handleInputChange = (e) => {
    setClient({
      ...client,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (rut) {
        await clientService.update(client);
        alert("Cliente actualizado exitosamente");
      } else {
        await clientService.create(client);
        alert("Cliente creado exitosamente");
      }
      navigate("/clients");
    } catch (error) {
      console.error("Error guardando cliente:", error);
      alert("Error al guardar los cambios");
    }
  };

  return (
    <Container maxWidth="md" sx={{ mt: 4, 
      minHeight: '100vh',        // Ocupa toda la altura vertical
      display: 'auto',           // Activa flexbox
      flexDirection: 'column',   // Apila elementos verticalmente
      justifyContent: 'center',  // Centra verticalmente
      py: 4                      // Padding vertical
    }}>
      <Typography variant="h4" gutterBottom>
        {rut ? "Editar Cliente" : "Nuevo Cliente"}
      </Typography>
      
      <form onSubmit={handleSubmit}>
        <TextField
          fullWidth
          label="RUT"
          name="rut"
          value={client.rut}
          onChange={handleInputChange}
          margin="normal"
          required
          disabled={!!rut} // Deshabilitar RUT en edición
        />

        <TextField
          fullWidth
          label="Nombre"
          name="name"
          value={client.name}
          onChange={handleInputChange}
          margin="normal"
          required
        />

        <TextField
          fullWidth
          label="Email"
          name="email"
          type="email"
          value={client.email}
          onChange={handleInputChange}
          margin="normal"
          required
        />

        <TextField
          fullWidth
          label="Fecha de Nacimiento"
          name="birthDate"
          type="date"
          value={client.birthDate}
          onChange={handleInputChange}
          margin="normal"
          InputLabelProps={{ shrink: true }}
          required
        />

        <TextField
          fullWidth
          label="Visitas por Mes"
          name="monthlyVisitCount"
          type="number"
          value={client.monthlyVisitCount}
          onChange={handleInputChange}
          margin="normal"
          inputProps={{ min: 0 }}
          required
        />

        <Button
          type="submit"
          variant="contained"
          color="primary"
          sx={{ mt: 3, mr: 2 }}
        >
          {rut ? "Actualizar" : "Crear"}
        </Button>

        <Button
          variant="contained"
          color="secondary"
          sx={{ mt: 3 }}
          onClick={() => navigate("/clients")}
        >
          Cancelar
        </Button>
      </form>
    </Container>
  );
};

export default AddEditClient;