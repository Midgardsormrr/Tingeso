// ======= RESERVATION SERVICE =======
package com.example.demo.services;

import com.example.demo.entities.ClientEntity;
import com.example.demo.entities.KartEntity;
import com.example.demo.entities.ReservationEntity;
import com.example.demo.repositories.ClientRepository;
import com.example.demo.repositories.KartRepository;
import com.example.demo.repositories.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private KartRepository kartRepository;

    public ArrayList<ReservationEntity> getReservations() {
        return (ArrayList<ReservationEntity>) reservationRepository.findAll();
    }


    public ReservationEntity getReservationById(Long Id) {
        return reservationRepository.findById(Id).orElse(null);
    }

    public List<ReservationEntity> getReservationsByDate(LocalDate date) {
        List<ReservationEntity> list = new ArrayList<>();
        for (ReservationEntity r : reservationRepository.findAll()) {
            if (r.getStartDateTime().toLocalDate().equals(date)) {
                list.add(r);
            }
        }
        return list;
    }

    public ReservationEntity updateReservation(ReservationEntity reservation) {
        return reservationRepository.save(reservation);
    }

    public boolean deleteReservation(Long id) throws Exception {
        try {
            reservationRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public ReservationEntity saveReservation(@RequestBody ReservationEntity reservation) {

        // Validar número de personas
        if (reservation.getNumberOfPeople() < 1 || reservation.getNumberOfPeople() > 15) {
            throw new RuntimeException("Número de personas inválido (1 a 15 permitido)");
        }

        // Validar colisión de horarios
        List<ReservationEntity> conflictingReservations =
                reservationRepository.findByStartDateTimeLessThanAndEndDateTimeGreaterThan(
                        reservation.getEndDateTime(), reservation.getStartDateTime());

        if (!conflictingReservations.isEmpty()) {
            throw new RuntimeException("Horario no disponible. Hay otra reserva que se superpone.");
        }

        // Validar existencia de los RUTs (clientes)
        if ((reservation.getClientRuts() == null) || reservation.getClientRuts().isEmpty()) {
            throw new RuntimeException("No ingresaste ningun rut");
        }

        List<String> clientRuts = reservation.getClientRuts(); // Cambiar a getClientRuts, ya que es una lista de Strings

        // Buscar los clientes en la base de datos usando los RUTs
        List<ClientEntity> clientsInDb = clientRepository.findByRutIn(clientRuts);
        if (clientsInDb.size() != clientRuts.size()) {
            return null; // uno o más RUTs no existen
        }

        // Validar estado de los karts
        if (reservation.getKartCodes() == null || reservation.getKartCodes().isEmpty()) {
            throw new RuntimeException("No ingresaste ningún kart");
        }

        List<String> kartCodes = reservation.getKartCodes();

        List<KartEntity> availableKarts = kartRepository.findByCodeInAndStatus(kartCodes, "AVAILABLE");
        if (availableKarts.size() != kartCodes.size()) {
            throw new RuntimeException("Uno o más karts no están disponibles");
        }

        // Crear y guardar reserva
        ReservationEntity new_reservation = new ReservationEntity();
        new_reservation.setStartDateTime(reservation.getStartDateTime());
        new_reservation.setEndDateTime(reservation.getEndDateTime());
        new_reservation.setLaps(reservation.getLaps());
        new_reservation.setMaxTime(reservation.getMaxTime());
        new_reservation.setNumberOfPeople(reservation.getNumberOfPeople());
        new_reservation.setStatus("CONFIRMED");
        new_reservation.setClientRuts(reservation.getClientRuts());
        new_reservation.setKartCodes(reservation.getKartCodes());
        new_reservation.setReservationCode("RES-" + LocalDateTime.now().toLocalDate() + "-K" + (int)(Math.random() * 1000));


        reservationRepository.save(new_reservation);


        return new_reservation;
    }
}