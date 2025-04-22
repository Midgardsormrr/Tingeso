package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "clients")
public class ClientEntity {
    @Id
    @Column(name = "rut", length = 12, nullable = false, unique = true)
    private String rut;
    private String name;
    private String email;
    private LocalDate birthDate;
    private int monthlyVisitCount;
}