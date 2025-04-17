package com.example.demo.repositories;

import com.example.demo.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface PricingRepository extends JpaRepository<PricingEntity, Long> { }
