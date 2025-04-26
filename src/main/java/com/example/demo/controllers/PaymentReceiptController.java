package com.example.demo.controllers;

import com.example.demo.entities.PaymentReceiptEntity;
import com.example.demo.services.PaymentReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/receipts")
public class PaymentReceiptController {

    @Autowired
    private PaymentReceiptService paymentReceiptService;

    @GetMapping("/{reservationCode}")
    public PaymentReceiptEntity getReceipt(@PathVariable String reservationCode) {
        return paymentReceiptService.getReceiptByReservationCode(reservationCode);
    }
}
