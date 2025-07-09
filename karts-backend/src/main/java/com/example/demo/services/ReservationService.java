// ======= RESERVATION SERVICE =======
package com.example.demo.services;

import com.example.demo.entities.*;
import com.example.demo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.beans.Transient;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private KartRepository kartRepository;
    @Autowired
    private PricingRepository pricingRepository;
    @Autowired
    private PaymentReceiptRepository paymentReceiptRepository;
    @Autowired
    private PaymentReceiptService paymentReceiptService;
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

    @Transient
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

        // Validar cantidad de RUTs
        if (reservation.getClientRuts() == null || reservation.getClientRuts().isEmpty()) {
            throw new RuntimeException("No ingresaste ningún RUT");
        }

        if (reservation.getClientRuts().size() != reservation.getNumberOfPeople()) {
            throw new RuntimeException("La cantidad de RUTs debe coincidir con el número de personas");
        }

        // Validar existencia de los RUTs (clientes)
        List<String> clientRuts = reservation.getClientRuts();
        List<ClientEntity> clientsInDb = clientRepository.findByRutIn(clientRuts);
        if (clientsInDb.size() != clientRuts.size()) {
            throw new RuntimeException("Uno o más RUTs no están registrados en el sistema");
        }

        // Validar que haya karts asociados
        if (reservation.getKartCodes() == null || reservation.getKartCodes().isEmpty()) {
            throw new RuntimeException("No ingresaste ningún código de kart");
        }

        // Validar cantidad de karts igual a personas
        if (reservation.getKartCodes().size() != reservation.getNumberOfPeople()) {
            throw new RuntimeException("La cantidad de karts debe coincidir con el número de personas");
        }

        // Validar disponibilidad de los karts
        List<String> kartCodes = reservation.getKartCodes();
        List<KartEntity> availableKarts = kartRepository.findByCodeInAndStatus(kartCodes, "AVAILABLE");
        if (availableKarts.size() != kartCodes.size()) {
            throw new RuntimeException("Uno o más karts no están disponibles");
        }

        // Calcular duración de la reserva
        Integer totalDuration = pricingRepository.getTotalDurationByLaps(reservation.getLaps());
        if (totalDuration == null) {
            throw new RuntimeException("No hay configuración de precio para esa cantidad de vueltas");
        }

        LocalDateTime start = reservation.getStartDateTime();
        LocalDateTime end = start.plusMinutes(totalDuration);

        // Validar horario permitido
        DayOfWeek day = start.getDayOfWeek();
        LocalTime startTime = start.toLocalTime();
        LocalTime openingTime = (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY)
                ? LocalTime.of(10, 0)
                : LocalTime.of(14, 0);
        LocalTime closingTime = LocalTime.of(22, 0);

        if (startTime.isBefore(openingTime) || end.toLocalTime().isAfter(closingTime)) {
            throw new RuntimeException("Reserva fuera del horario permitido");
        }

        // Validar colisión de reservas
        List<ReservationEntity> conflictingReservations =
                reservationRepository.findByStartDateTimeLessThanAndEndDateTimeGreaterThan(end, start);

        if (!conflictingReservations.isEmpty()) {
            throw new RuntimeException("Horario no disponible. Hay otra reserva que se superpone.");
        }

        // Crear reserva
        ReservationEntity new_reservation = new ReservationEntity();
        new_reservation.setStartDateTime(start);
        new_reservation.setEndDateTime(end);
        new_reservation.setLaps(reservation.getLaps());
        new_reservation.setNumberOfPeople(reservation.getNumberOfPeople());
        new_reservation.setStatus("CONFIRMED");
        new_reservation.setClientRuts(reservation.getClientRuts());
        new_reservation.setKartCodes(reservation.getKartCodes());
        new_reservation.setReservationCode("RES-" + LocalDateTime.now().toLocalDate() + "-K" + (int)(Math.random() * 1000));

        // Guardar reserva y generar recibo
        reservationRepository.save(new_reservation);
        PaymentReceiptEntity receipt = paymentReceiptService.generateReceipt(new_reservation);
        paymentReceiptRepository.save(receipt);

        return new_reservation;
    }


    public Map<String, Map<YearMonth, Long>> generateRevenueReport(LocalDate startDate, LocalDate endDate) {
        List<ReservationEntity> reservations = reservationRepository
                .findByStartDateTimeBetweenAndStatus(
                        startDate.atStartOfDay(),
                        endDate.atTime(23, 59, 59),
                        "CONFIRMED"
                );

        Map<String, Map<YearMonth, Long>> report = new LinkedHashMap<>();

        // Inicializar estructura para todas las categorías
        List<String> categories = List.of("10 vueltas o máx 10 min",
                "15 vueltas o máx 15 min",
                "20 vueltas o máx 20 min");
        categories.forEach(cat -> report.put(cat, new TreeMap<>()));

        for (ReservationEntity reservation : reservations) {
            PaymentReceiptEntity receipt = paymentReceiptRepository
                    .findByReservationCode(reservation.getReservationCode());

            if (receipt == null) continue;

            YearMonth month = YearMonth.from(reservation.getStartDateTime());
            String category = getCategoryByLaps(reservation.getLaps());

            // Calcular monto total del recibo
            double total = receipt.getPaymentDetails().stream()
                    .mapToDouble(PaymentDetailEntity::getAmount)
                    .sum();

            // Actualizar reporte
            report.get(category).merge(month, (long) total, Long::sum);
        }

        // Calcular totales mensuales y general
        Map<YearMonth, Long> monthlyTotals = new TreeMap<>();
        report.forEach((category, months) ->
                months.forEach((month, amount) ->
                        monthlyTotals.merge(month, amount, Long::sum)
                )
        );

        report.put("TOTAL", monthlyTotals);
        return report;
    }

    private String getCategoryByLaps(int laps) {
        return switch (laps) {
            case 10 -> "10 vueltas o máx 10 min";
            case 15 -> "15 vueltas o máx 15 min";
            case 20 -> "20 vueltas o máx 20 min";
            default -> throw new IllegalArgumentException("Categoría no válida");
        };
    }

    public Map<String, Map<YearMonth, Long>> generatePeopleReport(LocalDate startDate, LocalDate endDate) {
        List<ReservationEntity> reservations = reservationRepository
                .findByStartDateTimeBetweenAndStatus(
                        startDate.atStartOfDay(),
                        endDate.atTime(23, 59, 59),
                        "CONFIRMED"
                );

        Map<String, Map<YearMonth, Long>> report = new LinkedHashMap<>();

        // Categorías basadas en los rangos de descuento grupal
        List<String> categories = List.of(
                "1-2 personas",
                "3-5 personas",
                "6-10 personas",
                "11-15 personas"
        );

        categories.forEach(cat -> report.put(cat, new TreeMap<>()));

        for (ReservationEntity reservation : reservations) {
            PaymentReceiptEntity receipt = paymentReceiptRepository
                    .findByReservationCode(reservation.getReservationCode());

            if (receipt == null) continue;

            YearMonth month = YearMonth.from(reservation.getStartDateTime());
            String category = getCategoryByPeople(reservation.getNumberOfPeople());

            double total = receipt.getPaymentDetails().stream()
                    .mapToDouble(PaymentDetailEntity::getAmount)
                    .sum();

            report.get(category).merge(month, (long) total, Long::sum);
        }

        // Cálculo de totales
        Map<YearMonth, Long> monthlyTotals = new TreeMap<>();
        report.forEach((category, months) ->
                months.forEach((month, amount) ->
                        monthlyTotals.merge(month, amount, Long::sum)
                )
        );

        report.put("TOTAL", monthlyTotals);
        return report;
    }

    private String getCategoryByPeople(int numberOfPeople) {
        return switch (numberOfPeople) {
            case 1, 2 -> "1-2 personas";
            case 3, 4, 5 -> "3-5 personas";
            case 6, 7, 8, 9, 10 -> "6-10 personas";
            case 11, 12, 13, 14, 15 -> "11-15 personas";
            default -> throw new IllegalArgumentException("Número de personas no válido: " + numberOfPeople);
        };
    }

    public List<Map<String, Object>> getWeeklySchedule(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        return reservationRepository
                .findByStartDateTimeBetweenAndStatus(start, end, "CONFIRMED")
                .stream()
                .map(res -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("reservationCode", res.getReservationCode());
                    response.put("start", res.getStartDateTime());
                    response.put("end", res.getEndDateTime());
                    return response;
                }).collect(Collectors.toList());
    }

}