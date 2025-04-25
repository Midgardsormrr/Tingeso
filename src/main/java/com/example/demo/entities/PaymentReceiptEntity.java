package com.example.demo.entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "payment_receipts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentReceiptEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String receiptCode;         // Ej: "REC-20231025-001"
    private LocalDateTime issuedAt;

    // Resumen de la Reserva
    private String reservationCode;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private int laps;
    private int maxTime;
    private int numberOfPeople;
    private String reservingClientName;

    // Detalles de pago (no persistidos directamente)
    @Transient
    private List<PaymentDetailEntity> paymentDetails;

    // Totales generales
    private double subTotal;
    private double tax;
    private double totalAmount;
}