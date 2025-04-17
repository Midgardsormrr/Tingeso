package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
@Entity
@Table(name = "reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    private String reservationCode;      // Ej: "RES-20231025-K001"
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private int laps;
    private int maxTime;                 // en minutos
    private int numberOfPeople;
    private String status;               // CONFIRMED, CANCELLED

    // Lista de objetos Cliente
    @Transient
    private List<ClientEntity> clients;

    // Objeto Kart reservado
    @Transient
    private List<KartEntity> karts;

    // Objeto PaymentReceipt asociado
    @Transient
    private PaymentReceiptEntity paymentReceipt;
}
