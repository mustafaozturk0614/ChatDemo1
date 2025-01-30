package com.example.chat.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.microsoft.bot.builder.MessageFactory;
import com.microsoft.bot.dialogs.DialogContext;
import com.microsoft.bot.dialogs.DialogTurnResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.chat.entity.Payment;
import com.example.chat.repository.PaymentRepository;

import static com.example.chat.EchoBot.MENU_DIALOG_ID;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    public List<Payment> getPaymentsByBillId(Long billId) {
        return paymentRepository.findByBillId(billId);
    }


    public Double getTotalPaymentsByUserId(Long userId) {
        // Kullanıcının toplam ödemelerini hesaplamak için gerekli metod
        return paymentRepository.findTotalPaymentsByUserId(userId);
    }

    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }

    public CompletableFuture<DialogTurnResult> handlePaidBills(Long userId, DialogContext dialogContext) {
        return CompletableFuture.supplyAsync(() -> paymentRepository.findByUserId(userId))
                .thenCompose(payments -> {
                    StringBuilder response = new StringBuilder("Ödenmiş faturalarınız:\n");
                    if (!payments.isEmpty()) {
                        for (Payment payment : payments) {
                            response.append("Fatura No: ").append(payment.getBillId()).append(" - ").append(payment.getAmountPaid()).append(" TL\n");
                        }
                    } else {
                        response.append("Ödenmiş faturanız bulunmamaktadır.");
                    }
                    return dialogContext.getContext().sendActivity(MessageFactory.text(response.toString()))
                            .thenCompose(result -> dialogContext.replaceDialog(MENU_DIALOG_ID));
                });
    }
} 