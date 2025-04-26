package com.example.demo.entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Entity
@Table(name = "discounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    private String type;                // GROUP, FREQUENT, BIRTHDAY
    private Integer minPeople;
    private Integer maxPeople;
    private Integer minVisits;
    private Integer maxVisits;
    private double percentage;
}
