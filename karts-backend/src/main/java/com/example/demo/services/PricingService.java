// ======= PRICING SERVICE =======
package com.example.demo.services;

import com.example.demo.entities.PricingEntity;
import com.example.demo.repositories.PricingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class PricingService {
    @Autowired
    private PricingRepository pricingRepository;

    public ArrayList<PricingEntity> getPricing() {
        return (ArrayList<PricingEntity>) pricingRepository.findAll();
    }

    public PricingEntity getPricingByLaps(int laps) {
        return (PricingEntity) pricingRepository.findByLaps(laps).orElse(null);
    }

    public Integer getTotalDurationByLaps(int laps) {
        return pricingRepository.getTotalDurationByLaps(laps);
    }

}
