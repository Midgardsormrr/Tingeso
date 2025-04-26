// ======= PAYMENT DETAIL CONTROLLER =======
package com.example.demo.controllers;

import com.example.demo.entities.PaymentDetailEntity;
import com.example.demo.services.PaymentDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/payment-details")
public class PaymentDetailController {
    @Autowired
    private PaymentDetailService detailService;

    @GetMapping
    public ArrayList<PaymentDetailEntity> getAllDetails() {
        return detailService.getPaymentDetails();
    }

    @PostMapping
    public PaymentDetailEntity createDetail(@RequestBody PaymentDetailEntity detail) {
        return detailService.savePaymentDetail(detail);
    }


    @PutMapping
    public PaymentDetailEntity updateDetail(@RequestBody PaymentDetailEntity detail) {
        return detailService.updatePaymentDetail(detail);
    }

    @DeleteMapping("/{id}")
    public boolean deleteDetail(@PathVariable Long id) throws Exception {
        return detailService.deletePaymentDetail(id);
    }
}