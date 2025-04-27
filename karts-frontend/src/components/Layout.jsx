// src/components/Layout.jsx
import { Link } from "react-router-dom";

const Layout = ({ children }) => {
  return (
    <div className="flex flex-col min-h-screen">
      
      {/* Header */}
      <header className="bg-blue-600 text-white p-4">
        <nav className="flex justify-between">
          <h1 className="font-bold text-lg">Mi Aplicación</h1>
          <div className="space-x-4">
            <Link to="/" className="hover:underline">Inicio</Link>
            <Link to="/clients" className="hover:underline">Clientes</Link>
            {/* Puedes agregar más links aquí */}
          </div>
        </nav>
      </header>

      {/* Contenido principal */}
      <main className="flex-1 p-8 bg-gray-100">
        {children}
      </main>

      {/* Footer */}
      <footer className="bg-gray-800 text-white text-center p-4">
        © {new Date().getFullYear()} Mi Aplicación. Todos los derechos reservados.
      </footer>

    </div>
  );
};

export default Layout;
