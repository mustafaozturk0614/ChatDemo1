package com.example.chat.repository;

import com.example.chat.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByBillId(Long billId);
    List<Payment> findByUserId(Long userId);
    
    // Kullanıcının toplam ödemelerini hesaplamak için
    @Query("SELECT SUM(p.amountPaid) FROM Payment p WHERE p.userId = :userId")
    Double findTotalPaymentsByUserId(@Param("userId") Long userId);
} 