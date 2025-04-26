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
@AllArgsConstructor
@NoArgsConstructor
public class PaymentReceiptEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String reservationCode;
    private LocalDateTime reservationDateTime;
    private int laps;
    private int numberOfPeople;
    private String reservedBy; // nombre del cliente principal

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "payment_receipt_id")   // <-- aquí
    private List<PaymentDetailEntity> paymentDetails;


}
