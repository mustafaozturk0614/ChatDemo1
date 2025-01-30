package com.example.chat.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.microsoft.bot.builder.MessageFactory;
import com.microsoft.bot.dialogs.DialogContext;
import com.microsoft.bot.dialogs.DialogTurnResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.chat.entity.EnergySavingTip;
import com.example.chat.repository.EnergySavingTipRepository;

import static com.example.chat.EchoBot.MENU_DIALOG_ID;

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

    public CompletableFuture<DialogTurnResult> showEnergySavingTips(DialogContext dialogContext) {
        String tips = "Enerji tasarrufu ipuçları:\n\n" +
                "1. Aydınlatmada LED ampuller kullanın.\n" +
                "2. Elektrikli cihazları bekleme modunda bırakmayın.\n" +
                "3. Klimaları 24-26°C arasında kullanın.\n" +
                "4. Buzdolabınızı güneş almayan bir yere yerleştirin.\n" +
                "5. Çamaşır makinesini tam dolu çalıştırın.";
        return dialogContext.getContext().sendActivity(MessageFactory.text(tips))
                .thenCompose(result -> dialogContext.replaceDialog(MENU_DIALOG_ID));
    }
} 