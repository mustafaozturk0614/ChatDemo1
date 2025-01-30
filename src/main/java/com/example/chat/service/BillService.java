package com.example.chat.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.chat.entity.Bill;
import com.example.chat.enums.BillStatus;
import com.example.chat.repository.BillRepository;

@Service
public class BillService {

    @Autowired
    private BillRepository billRepository;

    public Bill saveBill(Bill bill) {
        return billRepository.save(bill);
    }

    public List<Bill> getBillsByUserId(Long userId) {
        return billRepository.findByUserId(userId);
    }

    public List<Bill> getUnpaidBillsByUserId(Long userId) {
        return billRepository.findByUserIdAndStatus(userId, BillStatus.UNPAID);
    }

    public List<Bill> getPaidBillsByUserId(Long userId) {
        return billRepository.findByUserIdAndStatus(userId, BillStatus.PAID);
    }

    public List<Bill> getAllBillsByUserId(Long userId) {
        return billRepository.findByUserId(userId);
    }

    public Double getTotalAnnualPayments(Long userId) {
        // Ödeme toplamını hesaplamak için gerekli metod
        // Bu metod, yıllık ödemeleri toplamak için kullanılabilir
        return billRepository.findTotalAnnualPaymentsByUserId(userId);
    }

    public Double getTotalAnnualEnergyConsumption(Long userId) {
        // Enerji tüketim toplamını hesaplamak için gerekli metod
        // Bu metod, yıllık enerji tüketimini toplamak için kullanılabilir
        return billRepository.findTotalAnnualEnergyConsumptionByUserId(userId);
    }

    public void deleteBill(Long id) {
        billRepository.deleteById(id);
    }
} 