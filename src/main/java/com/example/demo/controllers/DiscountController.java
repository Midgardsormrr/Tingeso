// ======= DISCOUNT CONTROLLER =======
package com.example.demo.controllers;

import com.example.demo.entities.DiscountEntity;
import com.example.demo.services.DiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/discounts")
public class DiscountController {
    @Autowired
    private DiscountService discountService;

    @GetMapping
    public ArrayList<DiscountEntity> getAllDiscounts() {
        return discountService.getDiscounts();
    }

    @GetMapping("/group/{size}")
    public List<DiscountEntity> getGroupDiscount(@PathVariable("size") int size) {
        return discountService.getGroupDiscount(size);
    }

    @GetMapping("/frequent/{visits}")
    public List<DiscountEntity> getFrequentDiscount(@PathVariable int visits) {
        return discountService.getFrequentDiscount(visits);
    }

    @GetMapping("/birthday/{size}")
    public List<DiscountEntity> getBirthdayDiscount(@PathVariable("size") int size) {
        return discountService.getBirthdayDiscount(size);
    }
}
