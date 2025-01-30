package com.example.chat.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.chat.entity.Bill;
import com.example.chat.enums.BillStatus;
import com.example.chat.repository.BillRepository;

@Service
public class BillService {

    @Autowired
    private BillRepository billRepository;

    /**
     * Kullanıcının ödenmemiş faturalarını getirir.
     * @param userId Kullanıcının ID'si
     * @return CompletableFuture<List<Bill>> Ödenmemiş faturaların listesi
     */
    public CompletableFuture<List<Bill>> getUnpaidBillsByUserId(Long userId) {
        return CompletableFuture.supplyAsync(() -> billRepository.findByUserIdAndStatus(userId, BillStatus.UNPAID));
    }

    /**
     * Kullanıcının tüm faturalarını getirir.
     * @param userId Kullanıcının ID'si
     * @return CompletableFuture<List<Bill>> Kullanıcının tüm faturalarının listesi
     */
    public CompletableFuture<List<Bill>> getAllBillsByUserId(Long userId) {
        return CompletableFuture.supplyAsync(() -> billRepository.findByUserId(userId));
    }

    /**
     * Kullanıcının son ödenmemiş faturasını getirir.
     * @param userId Kullanıcının ID'si
     * @return CompletableFuture<Bill> Son ödenmemiş fatura
     */
    public CompletableFuture<Bill> getLastUnpaidBill(Long userId) {
        return getUnpaidBillsByUserId(userId)
                .thenApply(bills -> bills.isEmpty() ? null : bills.get(0)); // İlk ödenmemiş faturayı döndür
    }

    /**
     * Faturayı kaydeder.
     * @param bill Kaydedilecek fatura
     * @return Bill Kaydedilen fatura
     */
    public Bill saveBill(Bill bill) {
        return billRepository.save(bill);
    }

    /**
     * Faturayı siler.
     * @param id Silinecek fatura ID'si
     */
    public void deleteBill(Long id) {
        billRepository.deleteById(id);
    }
} 