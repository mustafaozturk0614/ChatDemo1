package com.example.chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.chat.entity.Subscription;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    // Kullanıcıya ait abonelikleri bulmak için
    List<Subscription> findByUserId(Long userId);
} 