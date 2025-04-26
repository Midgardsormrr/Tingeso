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

    public void generateReceipt(ReservationEntity reservation) {
        int laps = reservation.getLaps();
        int people = reservation.getNumberOfPeople();

        // Obtener el precio total por las vueltas
        Float totalPrice = pricingRepository.getTotalPriceByLaps(laps);
        if (totalPrice == null) {
            throw new RuntimeException("No hay precio configurado para esta cantidad de vueltas");
        }

        // Calcular precio por persona
        float pricePerPerson = totalPrice / people;

        // Obtener los clientes por RUT
        List<ClientEntity> clients = clientRepository.findByRutIn(reservation.getClientRuts());
        if (clients.isEmpty()) {
            throw new RuntimeException("No se encontraron clientes para esta reserva");
        }

        // Crear la entidad del recibo de pago sin detalles aún
        PaymentReceiptEntity paymentReceipt = new PaymentReceiptEntity();
        paymentReceipt.setReservationCode(reservation.getReservationCode());
        paymentReceipt.setReservationDateTime(reservation.getStartDateTime());
        paymentReceipt.setLaps(laps);
        paymentReceipt.setNumberOfPeople(people);
        paymentReceipt.setReservedBy(clients.get(0).getName());

        // Crear detalles de pago y asignarlos al recibo
        List<PaymentDetailEntity> paymentDetails = clients.stream().map(client -> {
            PaymentDetailEntity detail = new PaymentDetailEntity();
            detail.setClientName(client.getName());
            detail.setAmount(pricePerPerson);

            // Elimina la asignación de paymentReceipt aquí
            // No necesitamos asignar paymentReceipt a cada PaymentDetailEntity

            return detail;
        }).collect(Collectors.toList());

        // Asignar los detalles al recibo
        paymentReceipt.setPaymentDetails(paymentDetails);

        // Guardar en base de datos (cascade = ALL guarda también los detalles)
        paymentReceiptRepository.save(paymentReceipt);
    }

    public PaymentReceiptEntity getReceiptByReservationCode(String reservationCode) {
        // Buscar el recibo de pago por código de reserva
        return paymentReceiptRepository.findByReservationCode(reservationCode);
    }
}
