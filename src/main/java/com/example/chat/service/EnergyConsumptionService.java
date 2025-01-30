package com.example.chat.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.chat.entity.EnergyConsumption;
import com.example.chat.repository.EnergyConsumptionRepository;

@Service
public class EnergyConsumptionService {

    @Autowired
    private EnergyConsumptionRepository energyConsumptionRepository;

    public EnergyConsumption saveEnergyConsumption(EnergyConsumption energyConsumption) {
        return energyConsumptionRepository.save(energyConsumption);
    }

    public CompletableFuture<List<EnergyConsumption>> getEnergyConsumptionsByUserId(Long userId) {
        return CompletableFuture.supplyAsync(() -> energyConsumptionRepository.findByUserId(userId));
    }

    public void deleteEnergyConsumption(Long id) {
        energyConsumptionRepository.deleteById(id);
    }
} 