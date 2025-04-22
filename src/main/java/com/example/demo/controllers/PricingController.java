// ======= PRICING CONTROLLER =======
package com.example.demo.controllers;

import com.example.demo.entities.PricingEntity;
import com.example.demo.services.PricingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/pricing")
public class PricingController {
    @Autowired
    private PricingService pricingService;

    @GetMapping
    public ArrayList<PricingEntity> getAllPricing() {
        return pricingService.getPricing();
    }

    @GetMapping("/laps/{laps}")
    public PricingEntity getByLaps(@PathVariable int laps) {
        return pricingService.getPricingByLaps(laps);
    }
}