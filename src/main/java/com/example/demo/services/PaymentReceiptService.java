// ======= PAYMENT RECEIPT SERVICE =======
package com.example.demo.services;

import com.example.demo.entities.PaymentReceiptEntity;
import com.example.demo.repositories.PaymentReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentReceiptService {
    @Autowired
    private PaymentReceiptRepository receiptRepository;

    public ArrayList<PaymentReceiptEntity> getReceipts() {
        return (ArrayList<PaymentReceiptEntity>) receiptRepository.findAll();
    }

    public PaymentReceiptEntity saveReceipt(PaymentReceiptEntity receipt) {
        return receiptRepository.save(receipt);
    }

    public PaymentReceiptEntity getReceiptById(Long Id) {
        return receiptRepository.findById(Id).orElse(null);
    }

    public PaymentReceiptEntity updateReceipt(PaymentReceiptEntity receipt) {
        return receiptRepository.save(receipt);
    }

    public boolean deleteReceipt(Long id) throws Exception {
        try {
            receiptRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
