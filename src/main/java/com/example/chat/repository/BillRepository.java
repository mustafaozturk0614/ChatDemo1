package com.example.chat.repository;

import com.example.chat.entity.Bill;
import com.example.chat.enums.BillStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    List<Bill> findByUserId(Long userId);
    List<Bill> findByUserIdAndStatus(Long userId, BillStatus status);
    Optional<Bill> findFirstByUserIdAndStatusOrderByDueDateDesc(Long userId, BillStatus status);
    // Yıllık ödemeleri toplamak için
    @Query("SELECT SUM(b.amount) FROM Bill b WHERE b.userId = :userId AND YEAR(b.dueDate) = YEAR(CURRENT_DATE)")
    Double findTotalAnnualPaymentsByUserId(@Param("userId") Long userId);
    
    // Yıllık enerji tüketimini toplamak için
    @Query("SELECT SUM(e.consumptionAmount) FROM EnergyConsumption e WHERE e.userId = :userId AND YEAR(e.createdAt) = YEAR(CURRENT_DATE)")
    Double findTotalAnnualEnergyConsumptionByUserId(@Param("userId") Long userId);
} 