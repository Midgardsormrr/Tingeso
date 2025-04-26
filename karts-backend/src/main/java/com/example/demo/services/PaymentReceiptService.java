package com.example.demo.services;

import com.example.demo.entities.*;
import com.example.demo.repositories.ClientRepository;
import com.example.demo.repositories.PaymentReceiptRepository;
import com.example.demo.repositories.PricingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public PaymentReceiptEntity generateReceipt(ReservationEntity reservation) {
        int laps = reservation.getLaps();
        int people = reservation.getNumberOfPeople();

        Float totalPrice = pricingRepository.getTotalPriceByLaps(laps);
        if (totalPrice == null) {
            throw new RuntimeException("No hay precio configurado para esta cantidad de vueltas");
        }
        float pricePerPerson = totalPrice / people;

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
            detail.setAmount(pricePerPerson);
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
