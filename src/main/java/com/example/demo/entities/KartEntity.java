package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "karts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KartEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;
    private String code;                 // K001, K002, …
    private String status;               // AVAILABLE, UNDER_MAINTENANCE
}
