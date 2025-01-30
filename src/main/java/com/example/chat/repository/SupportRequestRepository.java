package com.example.chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.chat.entity.SupportRequest;

@Repository
public interface SupportRequestRepository extends JpaRepository<SupportRequest, Long> {
    // Kullanıcıya ait destek taleplerini bulmak için
    List<SupportRequest> findByUserId(Long userId);
} 