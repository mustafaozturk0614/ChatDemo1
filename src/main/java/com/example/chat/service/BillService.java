package com.example.chat.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.example.chat.utils.DialogUtils;
import com.microsoft.bot.dialogs.Dialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.chat.EchoBot.MENU_DIALOG_ID;
import com.example.chat.entity.Bill;
import com.example.chat.enums.BillStatus;
import com.example.chat.repository.BillRepository;
import com.microsoft.bot.builder.MessageFactory;
import com.microsoft.bot.dialogs.DialogContext;
import com.microsoft.bot.dialogs.DialogTurnResult;
import com.microsoft.bot.dialogs.DialogTurnStatus;

@Service
public class BillService {

    private static final Logger logger = LoggerFactory.getLogger(BillService.class);

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
        return CompletableFuture.supplyAsync(() -> billRepository.findFirstByUserIdAndStatusOrderByDueDateDesc(userId,BillStatus.UNPAID))
                .thenCompose(bills -> {
                    if (bills.isEmpty()) {
                        return DialogUtils.sendMessageAndReturn(dialogContext, "Ödenmemiş faturanız bulunmamaktadır.",MENU_DIALOG_ID);
                    }

                    Bill lastBill = bills.get();
                    String message = String.format("Son ödenmemiş faturanız: %s - %s TL", lastBill.getBillNumber(), lastBill.getAmount());
                    return DialogUtils.sendMessageAndReturn(dialogContext, message,MENU_DIALOG_ID);
                })
                .handle((result, ex) -> {
                    if (ex != null) {
                        logger.error("Fatura alınırken hata oluştu: ", ex);
                        return DialogUtils.sendErrorMessageAndReturn(dialogContext, "Fatura alınırken hata olştu. Lütfen tekrar deneyin.",MENU_DIALOG_ID);
                    }
                    return CompletableFuture.completedFuture(result);
                })
                .thenCompose(future -> future);
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
                    return DialogUtils.sendMessageAndReturn(dialogContext, response.toString(),MENU_DIALOG_ID);
                });
    }}