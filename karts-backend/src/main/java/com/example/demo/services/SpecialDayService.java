// ======= SPECIAL DAY SERVICE =======
package com.example.demo.services;

import com.example.demo.entities.SpecialDayEntity;
import com.example.demo.repositories.SpecialDayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;

@Service
public class SpecialDayService {
    @Autowired
    private SpecialDayRepository specialDayRepository;

    public ArrayList<SpecialDayEntity> getSpecialDays() {
        return (ArrayList<SpecialDayEntity>) specialDayRepository.findAll();
    }

    public SpecialDayEntity getSpecialDayByDate(LocalDate date) {
        return (SpecialDayEntity) specialDayRepository.findByDate(date);
    }

    public boolean isSpecialDay(LocalDate date) {
        return specialDayRepository.existsByDate(date);
    }
}