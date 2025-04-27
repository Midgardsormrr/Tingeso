// src/components/ClientList.jsx
import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import clientService from "../services/client.service";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Paper from "@mui/material/Paper";
import Button from "@mui/material/Button";
import PersonAddIcon from "@mui/icons-material/PersonAdd";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";

const ClientList = () => {
  const [clients, setClients] = useState([]);  // Inicializa clients como un arreglo vacío
  const navigate = useNavigate();

  const init = () => {
    clientService.getAll()
  .then(data => { // Ahora data es el array directamente
    console.log("Clientes cargados.", data);
    setClients(Array.isArray(data) ? data : []);
  })
  .catch(error => {
    console.error("Error cargando clientes.", error);
    setClients([]);
  });
  };

  useEffect(() => {
    init();
  }, []);

  const handleDelete = (rut) => {
    const confirmDelete = window.confirm("¿Está seguro que desea eliminar este cliente?");
    if (confirmDelete) {
      clientService.remove(rut)
        .then(response => {
          console.log("Cliente eliminado.", response.data);
          init();  // Vuelve a cargar la lista de clientes
        })
        .catch(error => {
          console.error("Error al eliminar cliente.", error);
        });
    }
  };

  const handleEdit = (rut) => {
    navigate(`/client/edit/${rut}`);
  };

  return (
    <TableContainer component={Paper}>
      <br />
      <Link
        to="/client/add"
        style={{ textDecoration: "none", marginBottom: "1rem" }}
      >
        <Button
          variant="contained"
          color="primary"
          startIcon={<PersonAddIcon />}
        >
          Añadir Cliente
        </Button>
      </Link>
      <br /><br />
      <Table sx={{ minWidth: 650 }} size="small" aria-label="tabla de clientes">
        <TableHead>
          <TableRow>
            <TableCell align="left" sx={{ fontWeight: "bold" }}>RUT</TableCell>
            <TableCell align="left" sx={{ fontWeight: "bold" }}>Nombre</TableCell>
            <TableCell align="left" sx={{ fontWeight: "bold" }}>Email</TableCell>
            <TableCell align="center" sx={{ fontWeight: "bold" }}>Nacimiento</TableCell>
            <TableCell align="center" sx={{ fontWeight: "bold" }}>Visitas/Mes</TableCell>
            <TableCell align="center" sx={{ fontWeight: "bold" }}>Operaciones</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {clients.length > 0 ? (
            clients.map((client) => (
              <TableRow key={client.rut}>
                <TableCell align="left">{client.rut}</TableCell>
                <TableCell align="left">{client.name}</TableCell>
                <TableCell align="left">{client.email}</TableCell>
                <TableCell align="center">{client.birthDate}</TableCell>
                <TableCell align="center">{client.monthlyVisitCount}</TableCell>
                <TableCell align="center">
                  <Button
                    variant="contained"
                    color="info"
                    size="small"
                    onClick={() => handleEdit(client.rut)}
                    startIcon={<EditIcon />}
                    style={{ marginRight: "0.5rem" }}
                  >
                    Editar
                  </Button>
                  <Button
                    variant="contained"
                    color="error"
                    size="small"
                    onClick={() => handleDelete(client.rut)}
                    startIcon={<DeleteIcon />}
                  >
                    Eliminar
                  </Button>
                </TableCell>
              </TableRow>
            ))
          ) : (
            <TableRow>
              <TableCell colSpan={6} align="center">No hay clientes disponibles.</TableCell>
            </TableRow>
          )}
        </TableBody>
      </Table>
    </TableContainer>
  );
};

export default ClientList;
