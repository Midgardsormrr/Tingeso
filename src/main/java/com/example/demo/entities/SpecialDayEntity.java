package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "special_days")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpecialDayEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    private LocalDate date;
    private String type;                // HOLIDAY, WEEKEND
    private double priceMultiplier;
}
