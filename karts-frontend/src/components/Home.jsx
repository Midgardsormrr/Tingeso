import { Container, Typography, Grid, Card, CardContent, useTheme } from '@mui/material';
import { Code, School, Build, People, DirectionsCar } from '@mui/icons-material';

const Home = () => {
  const theme = useTheme();

  return (
    <Container maxWidth="lg" sx={{ 
      minHeight: 'calc(100vh - 64px)',
      marginTop: '64px',
      display: 'flex',
      flexDirection: 'column',
      justifyContent: 'space-between',
      py: 8,
      textAlign: 'center'
    }}>
      <div>
        {/* Encabezado principal */}
        <Typography variant="h2" component="h1" gutterBottom sx={{
          fontWeight: 700,
          color: theme.palette.primary.main,
          mb: 4,
          textTransform: 'uppercase'
        }}>
          <DirectionsCar sx={{ fontSize: '3rem', verticalAlign: 'middle', mr: 2 }} />
          Gestión de Karting
        </Typography>

        {/* Subtítulo */}
        <Typography variant="h5" component="h2" gutterBottom sx={{
          color: theme.palette.text.secondary,
          mb: 6,
          maxWidth: '800px',
          mx: 'auto',
          lineHeight: 1.6
        }}>
          Sistema desarrollado para la asignatura de Métodos/Técnicas de Ingeniería de Software
          <br />
          <School sx={{ fontSize: '1.5rem', verticalAlign: 'middle', mx: 1, color: theme.palette.secondary.main }} />
          Universidad de Santiago de Chile
        </Typography>

        {/* Tarjetas de tecnologías - Contenido centrado */}
        <Grid container spacing={4} sx={{ mb: 8, justifyContent: 'center' }}>
          <Grid item xs={12} md={4}>
            <Card sx={{ 
              height: '100%',
              transition: 'transform 0.3s',
              '&:hover': { transform: 'translateY(-5px)' }
            }}>
              <CardContent sx={{ textAlign: 'center' }}>
                <Code sx={{ fontSize: 50, color: theme.palette.primary.main, mb: 2 }} />
                <Typography variant="h5" gutterBottom align="center">
                  Frontend
                </Typography>
                <Typography variant="body1" color="text.secondary" sx={{ textAlign: 'center' }}>
                  Desarrollado con React.js
                  <br />
                  Material-UI para el diseño
                  <br />
                  React Router para navegación
                </Typography>
              </CardContent>
            </Card>
          </Grid>

          <Grid item xs={12} md={4}>
            <Card sx={{ 
              height: '100%',
              transition: 'transform 0.3s',
              '&:hover': { transform: 'translateY(-5px)' }
            }}>
              <CardContent sx={{ textAlign: 'center' }}>
                <Build sx={{ fontSize: 50, color: theme.palette.primary.main, mb: 2 }} />
                <Typography variant="h5" gutterBottom align="center">
                  Backend
                </Typography>
                <Typography variant="body1" color="text.secondary" sx={{ textAlign: 'center' }}>
                  Construido con Spring Boot
                  <br />
                  Maven como gestor de dependencias
                  <br />
                  JPA para persistencia de datos
                </Typography>
              </CardContent>
            </Card>
          </Grid>

          <Grid item xs={12} md={4}>
            <Card sx={{ 
              height: '100%',
              transition: 'transform 0.3s',
              '&:hover': { transform: 'translateY(-5px)' }
            }}>
              <CardContent sx={{ textAlign: 'center' }}>
                <People sx={{ fontSize: 50, color: theme.palette.primary.main, mb: 2 }} />
                <Typography variant="h5" gutterBottom align="center">
                  Funcionalidades
                </Typography>
                <Typography variant="body1" color="text.secondary" sx={{ textAlign: 'center' }}>
                  Gestión de clientes
                  <br />
                  Registro de visitas
                  <br />
                  Control de operaciones
                </Typography>
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      </div>

      {/* Footer */}
      <Typography variant="body2" color="text.secondary" sx={{ 
        mt: 'auto',
        pt: 4,
        textAlign: 'center'
      }}>
        © {new Date().getFullYear()} Sistema de Gestión de Karting - USACH
        <br />
        Desarrollo con ❤️ usando Spring Boot y React
      </Typography>
    </Container>
  );
};

export default Home;