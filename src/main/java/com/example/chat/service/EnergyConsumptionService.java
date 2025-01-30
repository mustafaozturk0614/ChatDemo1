package com.example.chat.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.microsoft.bot.builder.MessageFactory;
import com.microsoft.bot.dialogs.DialogContext;
import com.microsoft.bot.dialogs.DialogTurnResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.chat.entity.EnergyConsumption;
import com.example.chat.repository.EnergyConsumptionRepository;

import static com.example.chat.EchoBot.MENU_DIALOG_ID;

@Service
public class EnergyConsumptionService {

    @Autowired
    private EnergyConsumptionRepository energyConsumptionRepository;

    public CompletableFuture<DialogTurnResult> handleConsumptionAnalysis(Long userId, DialogContext dialogContext) {
        return CompletableFuture.supplyAsync(() -> energyConsumptionRepository.findByUserId(userId))
                .thenCompose(consumptions -> {
                    StringBuilder response = new StringBuilder("Enerji tüketim analiziniz:\n");
                    if (!consumptions.isEmpty()) {
                        for (EnergyConsumption consumption : consumptions) {
                            response.append("Dönem: ").append(consumption.getPeriod()).append(" - Tüketim: ").append(consumption.getConsumptionAmount()).append(" kWh\n");
                        }
                    } else {
                        response.append("Enerji tüketim veriniz bulunmamaktadır.");
                    }
                    return dialogContext.getContext().sendActivity(MessageFactory.text(response.toString()))
                            .thenCompose(result -> dialogContext.replaceDialog(MENU_DIALOG_ID));
                });
    }

    public EnergyConsumption saveEnergyConsumption(EnergyConsumption energyConsumption) {
        return energyConsumptionRepository.save(energyConsumption);
    }



    public void deleteEnergyConsumption(Long id) {
        energyConsumptionRepository.deleteById(id);
    }
} 