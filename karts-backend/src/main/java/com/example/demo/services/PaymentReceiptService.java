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

        Float totalPrice = pricingRepository.getTotalPriceByLaps(laps);
        if (totalPrice == null) {
            throw new RuntimeException("No hay precio configurado para esta cantidad de vueltas");
        }
        float basePricePerPerson = totalPrice / people;

        // >>>> AÑADIMOS LÓGICA PARA APLICAR PRECIO POR FIN DE SEMANA O FERIADO <<<<
        DayOfWeek day = reservationDate.getDayOfWeek();
        float priceMultiplier = 1.0f; // Valor por defecto

        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            SpecialDayEntity weekendSpecial = specialDayRepository.findByType("WEEKEND");
            if (weekendSpecial != null) {
                priceMultiplier = (float) weekendSpecial.getPriceMultiplier();
            }
        } else {
            SpecialDayEntity holidaySpecial = specialDayRepository.findByDate(reservationDate);
            if (holidaySpecial != null) {
                priceMultiplier = (float) holidaySpecial.getPriceMultiplier();
            }
        }

        float finalPricePerPerson = basePricePerPerson * priceMultiplier;
        // >>>> FIN DE LÓGICA PRECIO POR DIA ESPECIAL<<<<

        // Determinar el porcentaje de descuento según la cantidad de personas
        float groupDiscountPercentage;
        if (people >= 3 && people <= 5) {
            groupDiscountPercentage = 10.0f;
        } else if (people >= 6 && people <= 10) {
            groupDiscountPercentage = 20.0f;
        } else if (people >= 11 && people <= 15) {
            groupDiscountPercentage = 30.0f;
        } else {
            groupDiscountPercentage = 0.0f;
        }

        List<ClientEntity> clients = clientRepository.findByRutIn(reservation.getClientRuts());
        if (clients.isEmpty()) {
            throw new RuntimeException("No se encontraron clientes para esta reserva");
        }

        PaymentReceiptEntity paymentReceipt = new PaymentReceiptEntity();
        paymentReceipt.setReservationCode(reservation.getReservationCode());
        paymentReceipt.setReservationDateTime(reservation.getStartDateTime());
        paymentReceipt.setLaps(laps);
        paymentReceipt.setNumberOfPeople(people);
        paymentReceipt.setReservedBy(clients.get(0).getName());

        List<PaymentDetailEntity> paymentDetails = clients.stream().map(client -> {
            PaymentDetailEntity detail = new PaymentDetailEntity();
            detail.setClientName(client.getName());

            // Aplicar descuento por grupo
            float priceAfterGroupDiscount = finalPricePerPerson * (1 - groupDiscountPercentage / 100.0f);

            // Determinar el porcentaje de descuento por cliente frecuente
            float frequencyDiscountPercentage = 0.0f;
            int visits = client.getMonthlyVisitCount();
            if (visits >= 7) {
                frequencyDiscountPercentage = 30.0f;
            } else if (visits >= 5) {
                frequencyDiscountPercentage = 20.0f;
            } else if (visits >= 2) {
                frequencyDiscountPercentage = 10.0f;
            }

            // Aplicar descuento por cliente frecuente
            float finalPrice = priceAfterGroupDiscount * (1 - frequencyDiscountPercentage / 100.0f);

            detail.setAmount(finalPrice);

            return detail;
        }).collect(Collectors.toList());

        paymentReceipt.setPaymentDetails(paymentDetails);

        // al salvar el receipt, cascada guardará también los detalles
        return paymentReceiptRepository.save(paymentReceipt);
    }







    public PaymentReceiptEntity getReceiptByReservationCode(String reservationCode) {
        return paymentReceiptRepository.findByReservationCode(reservationCode);
    }

    // Nuevo método para obtener todos los recibos
    public List<PaymentReceiptEntity> getAllReceipts() {
        return paymentReceiptRepository.findAll();
    }
}
