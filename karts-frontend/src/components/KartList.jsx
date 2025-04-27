// src/components/KartList.jsx
import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import kartService from "../services/kart.service";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Paper from "@mui/material/Paper";
import Button from "@mui/material/Button";
import AddIcon from "@mui/icons-material/Add";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import Box from "@mui/material/Box";

const KartList = () => {
  const [karts, setKarts] = useState([]);
  const navigate = useNavigate();

  const init = () => {
    kartService.getAll()
      .then(data => setKarts(Array.isArray(data) ? data : []))
      .catch(error => console.error("Error cargando karts.", error));
  };

  useEffect(() => { init(); }, []);

  const handleDelete = (id) => {
    if (window.confirm("¿Está seguro que desea eliminar este kart?")) {
      kartService.remove(id)
        .then(() => init())
        .catch(error => console.error("Error al eliminar kart.", error));
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
        to="/karts/create"
        sx={{ 
          m: 2,
          width: "fit-content",
          alignSelf: "flex-start"
        }}
        startIcon={<AddIcon />}
      >
        Nuevo Kart
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
            <TableCell sx={{ fontWeight: 600 }}>ID</TableCell>
            <TableCell sx={{ fontWeight: 600 }}>Código</TableCell>
            <TableCell align="center" sx={{ fontWeight: 600 }}>Status</TableCell>
            <TableCell sx={{ fontWeight: 600 }} align="center">Operaciones</TableCell>
          </TableRow>
        </TableHead>
        
        <TableBody>
          {karts.map((kart) => (
            <TableRow key={kart.id} hover>
              <TableCell>{kart.id}</TableCell>
              <TableCell>{kart.code}</TableCell>
              <TableCell align="center">{kart.status}</TableCell>
              <TableCell align="center">
                <Box sx={{ 
                  display: "flex",
                  gap: 1,
                  justifyContent: "center",
                  "& .MuiButton-root": {
                    minWidth: 100,
                    fontSize: "0.75rem",
                    padding: "6px 12px",
                    flex: 1
                  }
                }}>
                  <Button
                    variant="contained"
                    color="info"
                    size="small"
                    onClick={() => navigate(`/karts/edit/${kart.id}`)}
                    startIcon={<EditIcon fontSize="small" />}
                  >
                    Editar
                  </Button>
                  <Button
                    variant="contained"
                    color="error"
                    size="small"
                    onClick={() => handleDelete(kart.id)}
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

export default KartList;
