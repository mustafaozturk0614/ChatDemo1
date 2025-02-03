package com.example.chat.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.microsoft.bot.builder.MessageFactory;
import com.microsoft.bot.dialogs.DialogContext;
import com.microsoft.bot.dialogs.DialogTurnResult;
import com.microsoft.bot.dialogs.DialogTurnStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.chat.entity.Bill;
import com.example.chat.enums.BillStatus;
import com.example.chat.repository.BillRepository;

import static com.example.chat.EchoBot.MENU_DIALOG_ID;

@Service
public class BillService {

    @Autowired
    private BillRepository billRepository;

    /**
     * Faturayı kaydeder.
     * @param bill Kaydedilecek fatura
     * @return Bill Kaydedilen fatura
     */
    public Bill saveBill(Bill bill) {
        return billRepository.save(bill);
    }

    /**
     * Faturayı siler.
     * @param id Silinecek fatura ID'si
     */
    public void deleteBill(Long id) {
        billRepository.deleteById(id);
    }


    public CompletableFuture<DialogTurnResult> handleLastUnpaidBill(Long userId, DialogContext dialogContext) {
        return CompletableFuture.supplyAsync(() -> billRepository.findByUserId(userId))
                .thenCompose(bills -> {
                    if (!bills.isEmpty()) {
                        Bill lastBill = bills.get(0);
                        return dialogContext.getContext().sendActivity(MessageFactory.text("Son ödenmemiş faturanız: " + lastBill.getBillNumber() + " - " + lastBill.getAmount() + " TL"))
                                .thenCompose(result -> {
                                    // Yeni bir DialogTurnResult oluştur ve dön
                                    DialogTurnResult dialogResult = new DialogTurnResult(DialogTurnStatus.COMPLETE);
                                    dialogResult.setResult("Son ödenmemiş faturanız: " + lastBill.getBillNumber() + " - " + lastBill.getAmount() + " TL");
                                    return CompletableFuture.completedFuture(dialogResult);
                                });
                    } else {
                        return dialogContext.getContext().sendActivity(MessageFactory.text("Ödenmemiş faturanız bulunmamaktadır."))
                                .thenCompose(result -> dialogContext.replaceDialog(MENU_DIALOG_ID));
                    }
                });
    }
    public CompletableFuture<DialogTurnResult> handleAllUnpaidBills(Long userId, DialogContext dialogContext) {
        return CompletableFuture.supplyAsync(() -> billRepository.findByUserIdAndStatus(userId, BillStatus.UNPAID))
                .thenCompose(bills -> {
                    StringBuilder response = new StringBuilder("Ödenmemiş faturalarınız:\n");
                    if (!bills.isEmpty()) {
                        for (Bill bill : bills) {
                            response.append(bill.getBillNumber()).append(" - ").append(bill.getAmount()).append(" TL\n");
                        }
                    } else {
                        response.append("Ödenmemiş faturanız bulunmamaktadır.");
                    }
                    return dialogContext.getContext().sendActivity(MessageFactory.text(response.toString()))
                            .thenCompose(result -> dialogContext.replaceDialog(MENU_DIALOG_ID));
                });
    }

} 