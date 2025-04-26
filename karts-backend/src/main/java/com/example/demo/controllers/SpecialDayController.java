// ======= SPECIAL DAY CONTROLLER =======
package com.example.demo.controllers;

import com.example.demo.entities.SpecialDayEntity;
import com.example.demo.services.SpecialDayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;

@RestController
@RequestMapping("/special-days")
public class SpecialDayController {
    @Autowired
    private SpecialDayService specialDayService;

    @GetMapping
    public ArrayList<SpecialDayEntity> getAllSpecialDays() {
        return specialDayService.getSpecialDays();
    }

    @GetMapping("/date/{date}")
    public SpecialDayEntity getByDate(@PathVariable String date) {
        return specialDayService.getSpecialDayByDate(LocalDate.parse(date));
    }

    @GetMapping("/is-special/{date}")
    public boolean isSpecial(@PathVariable String date) {
        return specialDayService.isSpecialDay(LocalDate.parse(date));
    }
}
