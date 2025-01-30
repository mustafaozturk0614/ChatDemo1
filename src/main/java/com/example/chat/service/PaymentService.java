package com.example.chat.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.chat.entity.Payment;
import com.example.chat.repository.PaymentRepository;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    public List<Payment> getPaymentsByBillId(Long billId) {
        return paymentRepository.findByBillId(billId);
    }

    public CompletableFuture<List<Payment>> getAllPaymentsByUserId(Long userId) {
        return CompletableFuture.supplyAsync(() -> paymentRepository.findByUserId(userId));
    }

    public Double getTotalPaymentsByUserId(Long userId) {
        // Kullanıcının toplam ödemelerini hesaplamak için gerekli metod
        return paymentRepository.findTotalPaymentsByUserId(userId);
    }

    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }
} 