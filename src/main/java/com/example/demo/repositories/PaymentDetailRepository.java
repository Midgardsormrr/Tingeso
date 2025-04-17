package com.example.demo.repositories;

import com.example.demo.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface PaymentDetailRepository extends JpaRepository<PaymentDetailEntity, Long> { }
