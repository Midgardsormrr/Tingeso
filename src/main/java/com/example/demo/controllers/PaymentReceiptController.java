// ======= PAYMENT RECEIPT CONTROLLER =======
package com.example.demo.controllers;

import com.example.demo.entities.PaymentReceiptEntity;
import com.example.demo.services.PaymentReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/receipts")
public class PaymentReceiptController {
    @Autowired
    private PaymentReceiptService receiptService;

    @GetMapping
    public ArrayList<PaymentReceiptEntity> getAllReceipts() {
        return receiptService.getReceipts();
    }

    @PostMapping
    public PaymentReceiptEntity createReceipt(@RequestBody PaymentReceiptEntity receipt) {
        return receiptService.saveReceipt(receipt);
    }

    @GetMapping("/{code}")
    public PaymentReceiptEntity getByCode(@PathVariable Long Id) {
        return receiptService.getReceiptById(Id);
    }

    @PutMapping
    public PaymentReceiptEntity updateReceipt(@RequestBody PaymentReceiptEntity receipt) {
        return receiptService.updateReceipt(receipt);
    }

    @DeleteMapping("/{id}")
    public boolean deleteReceipt(@PathVariable Long id) throws Exception {
        return receiptService.deleteReceipt(id);
    }
}
