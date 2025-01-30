package com.example.chat.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.chat.entity.EnergySavingTip;
import com.example.chat.repository.EnergySavingTipRepository;

@Service
public class EnergySavingTipService {

    @Autowired
    private EnergySavingTipRepository energySavingTipRepository;

    public EnergySavingTip saveEnergySavingTip(EnergySavingTip energySavingTip) {
        return energySavingTipRepository.save(energySavingTip);
    }

    public List<EnergySavingTip> getAllEnergySavingTips() {
        return energySavingTipRepository.findAll();
    }

    public void deleteEnergySavingTip(Long id) {
        energySavingTipRepository.deleteById(id);
    }
} 