// ======= RESERVATION SERVICE =======
package com.example.demo.services;

import com.example.demo.entities.ReservationEntity;
import com.example.demo.repositories.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;

    public ArrayList<ReservationEntity> getReservations() {
        return (ArrayList<ReservationEntity>) reservationRepository.findAll();
    }

    public ReservationEntity saveReservation(ReservationEntity reservation) {
        return reservationRepository.save(reservation);
    }

    public ReservationEntity getReservationById(Long Id) {
        return reservationRepository.findById(Id).orElse(null);
    }

    public List<ReservationEntity> getReservationsByDate(LocalDate date) {
        return reservationRepository.findAll().stream()
                .filter(r -> r.getStartDateTime().toLocalDate().equals(date))
                .collect(Collectors.toList());
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
}