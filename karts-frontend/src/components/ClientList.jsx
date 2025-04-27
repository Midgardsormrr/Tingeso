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
  const [clients, setClients] = useState([]);
  const navigate = useNavigate();

  const init = () => {
    clientService.getAll()
      .then(data => setClients(Array.isArray(data) ? data : []))
      .catch(error => console.error("Error cargando clientes.", error));
  };

  useEffect(() => { init(); }, []);

  const handleDelete = (rut) => {
    if (window.confirm("¿Está seguro que desea eliminar este cliente?")) {
      clientService.remove(rut)
        .then(() => init())
        .catch(error => console.error("Error al eliminar cliente.", error));
    }
  };

  return (
    <TableContainer 
      component={Paper}
      sx={{
        width: "100%",
        height: "calc(100vh - 64px)",
        display: "flex",
        flexDirection: "column",
        overflow: "auto",
        border: "none",
        marginTop:"0px"
      }}
    >
      <Button
        variant="contained"
        component={Link}
        to="/client/add"
        sx={{ 
          m: 2,
          width: "fit-content",
          alignSelf: "flex-start"
        }}
        startIcon={<PersonAddIcon />}
      >
        Añadir Cliente
      </Button>

      
        <Table sx={{ 
          minWidth: "100%",
          tableLayout: "fixed",
          "& .MuiTableCell-root": {
            padding: "12px 16px",
            verticalAlign: "top"
          }
        }}>
          <TableHead>
            <TableRow>
              <TableCell sx={{ width: "15%", fontWeight: "600" }}>RUT</TableCell>
              <TableCell sx={{ width: "20%", fontWeight: "600" }}>Nombre</TableCell>
              <TableCell sx={{ width: "25%", fontWeight: "600" }}>Email</TableCell>
              <TableCell sx={{ width: "15%", fontWeight: "600" }} align="center">Nacimiento</TableCell>
              <TableCell sx={{ width: "15%", fontWeight: "600" }} align="center">Visitas/Mes</TableCell>
              <TableCell sx={{ width: "10%", fontWeight: "600" }} align="center">Operaciones</TableCell>
            </TableRow>
          </TableHead>
          
          <TableBody>
            {clients.map((client) => (
              <TableRow key={client.rut} hover>
                <TableCell>{client.rut}</TableCell>
                <TableCell>{client.name}</TableCell>
                <TableCell>{client.email}</TableCell>
                <TableCell align="center">{client.birthDate}</TableCell>
                <TableCell align="center">{client.monthlyVisitCount}</TableCell>
                <TableCell align="center" sx={{ "& button": { mx: 0.5 } }}>
                  <Button
                    variant="contained"
                    color="info"
                    size="small"
                    onClick={() => navigate(`/client/edit/${client.rut}`)}
                    startIcon={<EditIcon />}
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
            ))}
          </TableBody>
        </Table>
      
    </TableContainer>
  );
  
};

export default ClientList;