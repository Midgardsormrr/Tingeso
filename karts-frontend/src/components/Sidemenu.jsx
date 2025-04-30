import { Box, Drawer, List, ListItemButton, ListItemIcon, ListItemText } from "@mui/material";
import { useNavigate } from "react-router-dom";
import { Home, Person } from "@mui/icons-material"; // <-- Nombres correctos

export default function Sidemenu({ open, toggleDrawer }) {
  const navigate = useNavigate();

  const listOptions = () => (
    <Box sx={{ width: 250 }} role="presentation">
      <List>
        {/* Home */}
        <ListItemButton 
          onClick={() => {
            navigate("/home");
            toggleDrawer(false);
          }}
        >
          <ListItemIcon><Home /></ListItemIcon>
          <ListItemText primary="Inicio" />
        </ListItemButton>

        {/* Karts */}
        <ListItemButton 
          onClick={() => {
            navigate("/karts");
            toggleDrawer(false);
          }}
        >
          <ListItemIcon><Person /></ListItemIcon>
          <ListItemText primary="Karts" />
        </ListItemButton>

        {/* Clientes */}
        <ListItemButton 
          onClick={() => {
            navigate("/clients");
            toggleDrawer(false);
          }}
        >
          <ListItemIcon><Person /></ListItemIcon>
          <ListItemText primary="Clientes" />
        </ListItemButton>

        {/* Reservas */}
        <ListItemButton 
          onClick={() => {
            navigate("/reservations");
            toggleDrawer(false);
          }}
        >
          <ListItemIcon><Person /></ListItemIcon>
          <ListItemText primary="Reservas" />
        </ListItemButton>

        {/* Schedule */}
        <ListItemButton 
          onClick={() => {
            navigate("/reservations/weekly-schedule");
            toggleDrawer(false);
          }}
        >
          <ListItemIcon><Person /></ListItemIcon>
          <ListItemText primary="Calendario" />
        </ListItemButton>

        {/* Report */}
        <ListItemButton 
          onClick={() => {
            navigate("/reservations/report");
            toggleDrawer(false);
          }}
        >
          <ListItemIcon><Person /></ListItemIcon>
          <ListItemText primary="Generar Reporte" />
        </ListItemButton>

      </List>
    </Box>
  );

  return (
    <Drawer
      anchor="left"
      open={open}
      onClose={toggleDrawer(false)}
      sx={{
        "& .MuiDrawer-paper": {
          width: 250,
          boxSizing: "border-box",
          zIndex: (theme) => theme.zIndex.drawer
        }
      }}
    >
      {listOptions()}
    </Drawer>
  );
}