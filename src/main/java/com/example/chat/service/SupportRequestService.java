package com.example.chat.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.microsoft.bot.builder.MessageFactory;
import com.microsoft.bot.dialogs.DialogContext;
import com.microsoft.bot.dialogs.DialogTurnResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.chat.entity.SupportRequest;
import com.example.chat.repository.SupportRequestRepository;

import static com.example.chat.EchoBot.MENU_DIALOG_ID;

@Service
public class SupportRequestService {

    @Autowired
    private SupportRequestRepository supportRequestRepository;

    public CompletableFuture<DialogTurnResult> handleSupportRequest(Long userId, DialogContext dialogContext) {
        SupportRequest supportRequest = new SupportRequest();
        supportRequest.setUserId(userId);
        supportRequest.setRequestType("General Inquiry");
        supportRequest.setDescription("Destek talebi alındı.");
        saveSupportRequest(supportRequest);

        return dialogContext.getContext().sendActivity(MessageFactory.text("Destek talebiniz alındı. En kısa sürede dönüş yapılacaktır."))
                .thenCompose(result -> dialogContext.replaceDialog(MENU_DIALOG_ID));
    }

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