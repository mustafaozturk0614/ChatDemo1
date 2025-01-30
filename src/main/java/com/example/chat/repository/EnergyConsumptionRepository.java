package com.example.chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.chat.entity.EnergyConsumption;

@Repository
public interface EnergyConsumptionRepository extends JpaRepository<EnergyConsumption, Long> {
    // Kullanıcıya ait enerji tüketimlerini bulmak için
    List<EnergyConsumption> findByUserId(Long userId);
} 