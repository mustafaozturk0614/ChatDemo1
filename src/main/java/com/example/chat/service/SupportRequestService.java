package com.example.chat.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.chat.entity.SupportRequest;
import com.example.chat.repository.SupportRequestRepository;

@Service
public class SupportRequestService {

    @Autowired
    private SupportRequestRepository supportRequestRepository;

    public SupportRequest saveSupportRequest(SupportRequest supportRequest) {
        return supportRequestRepository.save(supportRequest);
    }

    public List<SupportRequest> getSupportRequestsByUserId(Long userId) {
        return supportRequestRepository.findByUserId(userId);
    }

    public void deleteSupportRequest(Long id) {
        supportRequestRepository.deleteById(id);
    }
} 