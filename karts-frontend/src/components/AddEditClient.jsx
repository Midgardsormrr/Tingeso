import { useState, useEffect } from "react";
import { Link, useParams, useNavigate } from "react-router-dom";
import clientService from "../services/client.service";
import Box from "@mui/material/Box";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import FormControl from "@mui/material/FormControl";
import SaveIcon from "@mui/icons-material/Save";

const AddEditClient = () => {
  const [rut, setRut] = useState("");
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [birthDate, setBirthDate] = useState("");
  const [monthlyVisitCount, setMonthlyVisitCount] = useState(0);
  const { id } = useParams();
  const [titleClientForm, setTitleClientForm] = useState("");
  const navigate = useNavigate();

  const saveClient = (e) => {
    e.preventDefault();
    const client = { id, rut, name, email, birthDate, monthlyVisitCount };
    if (id) {
      clientService.update(client)
        .then((response) => {
          console.log("Cliente ha sido actualizado.", response.data);
          navigate("/client/list");
        })
        .catch((error) => {
          console.log("Error al actualizar cliente.", error);
        });
    } else {
      clientService.create(client)
        .then((response) => {
          console.log("Cliente ha sido añadido.", response.data);
          navigate("/client/list");
        })
        .catch((error) => {
          console.log("Error al crear cliente.", error);
        });
    }
  };

  useEffect(() => {
    if (id) {
      setTitleClientForm("Editar Cliente");
      clientService.get(id)
        .then((res) => {
          const c = res.data;
          setRut(c.rut);
          setName(c.name);
          setEmail(c.email);
          setBirthDate(c.birthDate);
          setMonthlyVisitCount(c.monthlyVisitCount);
        })
        .catch((error) => {
          console.log("Error al obtener cliente.", error);
        });
    } else {
      setTitleClientForm("Nuevo Cliente");
    }
  }, [id]);

  return (
    <Box display="flex" flexDirection="column" alignItems="center" justifyContent="center" component="form">
      <h3>{titleClientForm}</h3>
      <hr />
      <form>
        <FormControl fullWidth>
          <TextField
            id="rut"
            label="RUT"
            value={rut}
            variant="standard"
            onChange={(e) => setRut(e.target.value)}
            helperText="Ej. 12.345.678-9"
          />
        </FormControl>

        <FormControl fullWidth>
          <TextField
            id="name"
            label="Nombre"
            value={name}
            variant="standard"
            onChange={(e) => setName(e.target.value)}
          />
        </FormControl>

        <FormControl fullWidth>
          <TextField
            id="email"
            label="Email"
            type="email"
            value={email}
            variant="standard"
            onChange={(e) => setEmail(e.target.value)}
          />
        </FormControl>

        <FormControl fullWidth>
          <TextField
            id="birthDate"
            label="Fecha de Nacimiento"
            type="date"
            value={birthDate}
            variant="standard"
            onChange={(e) => setBirthDate(e.target.value)}
            InputLabelProps={{ shrink: true }}
          />
        </FormControl>

        <FormControl fullWidth>
          <TextField
            id="monthlyVisitCount"
            label="Visitas Mensuales"
            type="number"
            value={monthlyVisitCount}
            variant="standard"
            onChange={(e) => setMonthlyVisitCount(parseInt(e.target.value, 10))}
          />
        </FormControl>

        <FormControl>
          <br />
          <Button variant="contained" color="primary" onClick={saveClient} startIcon={<SaveIcon />}>
            Guardar
          </Button>
        </FormControl>
      </form>
      <hr />
      <Link to="/client/list">Volver a la lista</Link>
    </Box>
  );
};

export default AddEditClient;