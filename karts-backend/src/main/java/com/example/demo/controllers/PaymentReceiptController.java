package com.example.demo.controllers;

import com.example.demo.entities.*;
import com.example.demo.services.PaymentReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/receipts")
@CrossOrigin("*")
public class PaymentReceiptController {

    @Autowired
    private PaymentReceiptService paymentReceiptService;

    @GetMapping("/{reservationCode}")
    public ResponseEntity<PaymentReceiptEntity> getReceiptByReservationCode(
            @PathVariable String reservationCode) {
        PaymentReceiptEntity receipt = paymentReceiptService.getReceiptByReservationCode(reservationCode);
        return ResponseEntity.ok(receipt);
    }


    @GetMapping
    public ResponseEntity<List<PaymentReceiptEntity>> getAllReceipts() {
        List<PaymentReceiptEntity> receipts = paymentReceiptService.getAllReceipts();
        return ResponseEntity.ok(receipts);
    }

    @PostMapping
    public ResponseEntity<PaymentReceiptEntity> generateReceipt(
            @RequestBody ReservationEntity reservation) {
        PaymentReceiptEntity receipt = paymentReceiptService.generateReceipt(reservation);
        return ResponseEntity.ok(receipt);
    }
}
