// ======= PAYMENT DETAIL SERVICE =======
package com.example.demo.services;

import com.example.demo.entities.PaymentDetailEntity;
import com.example.demo.repositories.PaymentDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentDetailService {
    @Autowired
    private PaymentDetailRepository detailRepository;

    public ArrayList<PaymentDetailEntity> getPaymentDetails() {
        return (ArrayList<PaymentDetailEntity>) detailRepository.findAll();
    }

    public PaymentDetailEntity savePaymentDetail(PaymentDetailEntity detail) {
        return detailRepository.save(detail);
    }

    public PaymentDetailEntity getPaymentDetailById(Long id) {
        return detailRepository.findById(id).orElse(null);
    }

    public List<PaymentDetailEntity> getPaymentDetailsByReceiptId(Long receiptId) {
        return detailRepository.findByReceiptId(receiptId);
    }

    public PaymentDetailEntity updatePaymentDetail(PaymentDetailEntity detail) {
        return detailRepository.save(detail);
    }

    public boolean deletePaymentDetail(Long id) throws Exception {
        try {
            detailRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}