// ======= DISCOUNT SERVICE =======
package com.example.demo.services;

import com.example.demo.entities.DiscountEntity;
import com.example.demo.repositories.DiscountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DiscountService {
    @Autowired
    private DiscountRepository discountRepository;

    public ArrayList<DiscountEntity> getDiscounts() {
        return (ArrayList<DiscountEntity>) discountRepository.findAll();
    }

    public List<DiscountEntity> getGroupDiscount(int groupSize) {
        return discountRepository.findByTypeAndMinPeopleLessThanEqualAndMaxPeopleGreaterThanEqual(
                "GROUP", groupSize, groupSize);
    }

    public List<DiscountEntity> getFrequentDiscount(int visits) {
        return discountRepository.findByTypeAndMinVisitsLessThanEqualAndMaxVisitsGreaterThanEqual(
                "FREQUENT", visits, visits);
    }

    public List<DiscountEntity> getBirthdayDiscount(int groupSize) {
        return discountRepository.findByTypeAndMinPeopleLessThanEqualAndMaxPeopleGreaterThanEqual(
                "BIRTHDAY", groupSize, groupSize);
    }
}