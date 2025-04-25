package com.example.demo.entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Entity
@Table(name = "payment_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long receiptId;              // FK a PaymentReceiptEntity.id
    private String clientName;
    private double basePrice;
    private double groupDiscount;
    private double frequentDiscount;
    private double birthdayDiscount;
    private double finalAmount;
    private double tax;
    private double totalAmount;
}
