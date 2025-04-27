import { useState } from "react";
import { AppBar, Toolbar, Typography, Button, IconButton, Box } from "@mui/material";
import MenuIcon from "@mui/icons-material/Menu";
import { Link } from "react-router-dom";
import SideMenu from "./SideMenu"; // Asegúrate que la ruta sea correcta

export default function Navbar() {
  const [open, setOpen] = useState(false);

  // Función para abrir/cerrar el menú
  const toggleDrawer = (open) => (event) => {
    if (event.type === 'keydown' && (event.key === 'Tab' || event.key === 'Shift')) return;
    setOpen(open);
  };

  return (
    <Box sx={{ flexGrow: 1 }}>
      <AppBar position="fixed" sx={{ height: 64, bgcolor: "primary.main", zIndex: 1000 }}>
        <Toolbar>
          <IconButton
            size="large"
            edge="start"
            color="inherit"
            sx={{ mr: 2 }}
            onClick={toggleDrawer(true)} // ¡Aquí estaba el error!
          >
            <MenuIcon />
          </IconButton>

          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            <Link to="/home" style={{ color: "white", textDecoration: "none" }}>
              Karting System
            </Link>
          </Typography>

          <Button color="inherit" component={Link} to="/login">
            Login
          </Button>
        </Toolbar>
      </AppBar>

      {/* Pasa el estado y la función al SideMenu */}
      <SideMenu open={open} toggleDrawer={toggleDrawer} />
    </Box>
  );
}