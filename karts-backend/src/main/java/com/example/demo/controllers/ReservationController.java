// ======= RESERVATION CONTROLLER =======
package com.example.demo.controllers;

import com.example.demo.entities.ReservationEntity;
import com.example.demo.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reservations")
@CrossOrigin("*")
public class ReservationController {
    @Autowired
    private ReservationService reservationService;

    @GetMapping
    public ArrayList<ReservationEntity> getAllReservations() {
        return reservationService.getReservations();
    }

    @PostMapping("/create")
    public ReservationEntity createReservation(@RequestBody ReservationEntity reservation) {
        return reservationService.saveReservation(reservation);
    }

    @GetMapping("/{code}")
    public ReservationEntity getByCode(@PathVariable Long Id) {
        return reservationService.getReservationById(Id);
    }

    @GetMapping("/date/{date}")
    public List<ReservationEntity> getByDate(@PathVariable String date) {
        return reservationService.getReservationsByDate(LocalDate.parse(date));
    }

    @PutMapping
    public ReservationEntity updateReservation(@RequestBody ReservationEntity reservation) {
        return reservationService.updateReservation(reservation);
    }

    @GetMapping("/{id}")
    public ReservationEntity getById(@PathVariable Long id) {
        ReservationEntity r = reservationService.getReservationById(id);
        return r;
    }

    @PutMapping("/{id}")  // coincide con service.update(id,...)
    public ReservationEntity updateReservation(@PathVariable Long id, @RequestBody ReservationEntity reservation) {
        reservation.setId(id);
        return reservationService.updateReservation(reservation);
    }
    @DeleteMapping("/{id}")
    public boolean deleteReservation(@PathVariable Long id) throws Exception {
        return reservationService.deleteReservation(id);
    }

    @GetMapping("/report")
    public ResponseEntity<Map<String, Map<YearMonth, Long>>> generateReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        return ResponseEntity.ok(reservationService.generateRevenueReport(start, end));
    }

    @GetMapping("/report/people")
    public ResponseEntity<Map<String, Map<YearMonth, Long>>> generatePeopleReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        return ResponseEntity.ok(reservationService.generatePeopleReport(start, end));
    }

    @GetMapping("/weekly-schedule")
    public ResponseEntity<List<Map<String, Object>>> getWeeklySchedule(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        List<Map<String, Object>> schedule = reservationService.getWeeklySchedule(start, end);
        return ResponseEntity.ok(schedule);
    }

}