// src/App.js
import './App.css';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Navbar from './components/Navbar';
import Home from './components/Home';
import ClientList from './components/ClientList';
import AddEditClient from './components/AddEditClient';
import NotFound from './components/NotFound';
import ReservationList from './components/ReservationList';
import AddEditReservation from './components/AddEditReservation';
import KartList from './components/KartList';
import AddEditKart from './components/AddEditKart';
import WeeklySchedule from './components/WeeklySchedule';

function App() {
  return (
    <Router>
      <div className="app-container">
        <Navbar />
        <Routes>
          <Route path="/home" element={<Home />} />
          <Route path="/clients" element={<ClientList />} />
          <Route path="/client/add" element={<AddEditClient />} />
          <Route path="/client/edit/:rut" element={<AddEditClient />} />
          <Route path="/karts" element={<KartList />} />
          <Route path="/karts/create" element={<AddEditKart />} />
          <Route path="/karts/edit/:id" element={<AddEditKart />} />
          <Route path="/reservations" element={<ReservationList />} />
          <Route path="/reservations/create" element={<AddEditReservation />} />
          <Route path="/reservations/edit/:id" element={<AddEditReservation />} />
          <Route path="/reservations/weekly-schedule" element={<WeeklySchedule />} />
          <Route path="*" element={<NotFound />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
