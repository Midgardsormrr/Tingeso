package com.example.demo.services;

import com.example.demo.entities.*;
import com.example.demo.repositories.ClientRepository;
import com.example.demo.repositories.PaymentReceiptRepository;
import com.example.demo.repositories.PricingRepository;
import com.example.demo.repositories.SpecialDayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PaymentReceiptService {

    @Autowired
    private PricingRepository pricingRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private PaymentReceiptRepository paymentReceiptRepository;

    @Autowired
    private SpecialDayRepository specialDayRepository;
    public PaymentReceiptEntity generateReceipt(ReservationEntity reservation) {
        int laps = reservation.getLaps();
        int people = reservation.getNumberOfPeople();
        LocalDate reservationDate = reservation.getStartDateTime().toLocalDate();

        // 1) Precio base por persona
        Float totalPrice = pricingRepository.getTotalPriceByLaps(laps);
        if (totalPrice == null) {
            throw new RuntimeException("No hay precio configurado para esta cantidad de vueltas");
        }
        float basePricePerPerson = totalPrice / people;
        System.out.println("Precio base por persona: " + basePricePerPerson);

        // 2) Multiplicador por día especial (fin de semana o feriado)
        DayOfWeek day = reservationDate.getDayOfWeek();
        float priceMultiplier = 1.0f;
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            SpecialDayEntity weekend = specialDayRepository.findByType("WEEKEND");
            if (weekend != null) {
                priceMultiplier = (float) weekend.getPriceMultiplier();
                System.out.println("Aplicado multiplicador de fin de semana: " + priceMultiplier);
            }
        } else {
            SpecialDayEntity holiday = specialDayRepository.findByDate(reservationDate);
            if (holiday != null) {
                priceMultiplier = (float) holiday.getPriceMultiplier();
                System.out.println("Aplicado multiplicador de feriado: " + priceMultiplier);
            }
        }
        float priceAfterDayMultiplier = basePricePerPerson * priceMultiplier;
        System.out.println("Precio después de multiplicador de día especial: " + priceAfterDayMultiplier);

        // 3) % descuento grupal
        float groupDiscount;
        if (people >= 3 && people <= 5) groupDiscount = 10f;
        else if (people >= 6 && people <= 10) groupDiscount = 20f;
        else if (people >= 11 && people <= 15) groupDiscount = 30f;
        else groupDiscount = 0f;

        System.out.println("Descuento grupal: " + groupDiscount + "%");

        // 4) Obtener todos los clientes
        List<ClientEntity> clients = clientRepository.findByRutIn(reservation.getClientRuts());
        if (clients.isEmpty()) {
            throw new RuntimeException("No se encontraron clientes para esta reserva");
        }

        // 5) Lógica cumpleañeros: cuántos pueden recibir el multiplicador "BIRTHDAY"
        SpecialDayEntity birthdaySpecial = specialDayRepository.findByType("BIRTHDAY");
        if (birthdaySpecial == null) {
            System.out.println("No se encontró un especial de cumpleaños en la base de datos.");
        } else {
            System.out.println("Multiplicador de cumpleaños: " + birthdaySpecial.getPriceMultiplier());
        }

        int maxBirthday = 0;
        if (people >= 3 && people <= 5) maxBirthday = 1;
        else if (people >= 6 && people <= 10) maxBirthday = 2;

        Set<String> birthdayNames = clients.stream()
                .filter(c -> c.getBirthDate() != null
                        && c.getBirthDate().getMonth() == reservationDate.getMonth()
                        && c.getBirthDate().getDayOfMonth() == reservationDate.getDayOfMonth())
                .limit(maxBirthday)
                .map(ClientEntity::getName)
                .collect(Collectors.toSet());

        System.out.println("Cumpleañeros: " + birthdayNames);

        // 6) Construir detalles de pago
        List<PaymentDetailEntity> details = clients.stream().map(client -> {
            PaymentDetailEntity d = new PaymentDetailEntity();
            d.setClientName(client.getName());

            // 6.1 aplica descuento grupal
            float p1 = priceAfterDayMultiplier * (1 - groupDiscount / 100f);
            System.out.println("Precio después de descuento grupal: " + p1);

            // 6.2 aplica descuento frecuencia
            int visits = client.getMonthlyVisitCount();
            float freqDisc = visits >= 7 ? 30f : visits >= 5 ? 20f : visits >= 2 ? 10f : 0f;
            float p2 = p1 * (1 - freqDisc / 100f);
            System.out.println("Precio después de descuento de frecuencia: " + p2);

            // 6.3 aplica multiplicador cumpleaños si corresponde
            float p3 = p2;
            if (birthdayNames.contains(client.getName()) && birthdaySpecial != null) {
                System.out.println(client.getName() + " tiene descuento de cumpleaños!");
                p3 = p2 * (float) birthdaySpecial.getPriceMultiplier();
            }

            d.setAmount(p3);
            return d;
        }).collect(Collectors.toList());

        // 7) Armar y guardar recibo
        PaymentReceiptEntity receipt = new PaymentReceiptEntity();
        receipt.setReservationCode(reservation.getReservationCode());
        receipt.setReservationDateTime(reservation.getStartDateTime());
        receipt.setLaps(laps);
        receipt.setNumberOfPeople(people);
        receipt.setReservedBy(clients.get(0).getName());
        receipt.setPaymentDetails(details);

        return paymentReceiptRepository.save(receipt);
    }










    public PaymentReceiptEntity getReceiptByReservationCode(String reservationCode) {
        return paymentReceiptRepository.findByReservationCode(reservationCode);
    }

    // Nuevo método para obtener todos los recibos
    public List<PaymentReceiptEntity> getAllReceipts() {
        return paymentReceiptRepository.findAll();
    }
}
