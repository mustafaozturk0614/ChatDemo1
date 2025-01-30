package com.example.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.chat.entity.EnergySavingTip;

@Repository
public interface EnergySavingTipRepository extends JpaRepository<EnergySavingTip, Long> {
    // Enerji tasarrufu ipuçlarını bulmak için
} 