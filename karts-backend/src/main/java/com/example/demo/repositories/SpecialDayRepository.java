package com.example.demo.repositories;

import com.example.demo.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface SpecialDayRepository extends JpaRepository<SpecialDayEntity, Long> {

    boolean existsByDate(LocalDate date);

    SpecialDayEntity findByDate(LocalDate localDate);

    SpecialDayEntity findByType(String weekend);

}