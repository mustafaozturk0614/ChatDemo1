package com.example.chat.dialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.example.chat.constants.CentralizedConstants;
import com.example.chat.model.menus.FaturaOption;
import com.example.chat.model.menus.MenuOption;
import com.example.chat.utils.DialogUtils;
import com.example.chat.utils.MenuMatcher;
import com.microsoft.bot.builder.MessageFactory;
import com.microsoft.bot.dialogs.*;
import com.microsoft.bot.dialogs.choices.Choice;
import com.microsoft.bot.dialogs.choices.FoundChoice;
import com.microsoft.bot.dialogs.choices.ListStyle;
import com.microsoft.bot.dialogs.prompts.ChoicePrompt;
import com.microsoft.bot.dialogs.prompts.PromptOptions;
import com.microsoft.bot.schema.Activity;

public class FaturaDialog extends ComponentDialog {

    public FaturaDialog(String dialogId) {
        super(dialogId);

        WaterfallStep[] waterfallSteps = new WaterfallStep[] {
            this::showFaturaOptionsStep,
            this::handleFaturaSelectionStep,
            this::confirmFaturaOdemeStep
        };

        addDialog(new WaterfallDialog(CentralizedConstants.FATURA_WATERFALL_DIALOG, Arrays.asList(waterfallSteps)));
        addDialog(new ChoicePrompt(CentralizedConstants.FATURA_PROMPT));
        addDialog(new ChoicePrompt(CentralizedConstants.CONFIRM_PROMPT));
        setInitialDialogId(CentralizedConstants.FATURA_WATERFALL_DIALOG);
    }



    private CompletableFuture<DialogTurnResult> showFaturaOptionsStep(WaterfallStepContext stepContext) {
        return DialogUtils.showDynamicMenu(stepContext,"Lütfen bir işlem seciniz", FaturaOption.class,CentralizedConstants.FATURA_PROMPT, ListStyle.SUGGESTED_ACTION);
    }

    private CompletableFuture<DialogTurnResult> handleFaturaSelectionStep(WaterfallStepContext stepContext) {
        FoundChoice choice = (FoundChoice) stepContext.getResult();
        FaturaOption selected = MenuMatcher.fromDisplayText(choice.getValue(),FaturaOption.class) ;
        switch(selected) {
            case FATURA_SORGULA:
                return DialogUtils.sendMessageAndReturn(
                        stepContext,
                        "Fatura sorgulama başlatılıyor...",
                        CentralizedConstants.FATURA_SORGULAMA_DIALOG_ID
                );

            case FATURA_ODE:
                return getFaturaDetayAsync()
                        .thenCompose(detay ->
                                DialogUtils.showChoicePrompt(
                                        stepContext,
                                        createFaturaCard(detay),
                                        Arrays.asList(new Choice("Evet"), new Choice("Hayır")),
                                        CentralizedConstants.CONFIRM_PROMPT
                                )
                        );

            default:
                return DialogUtils.sendMessageAndReturn(
                        stepContext,
                        "Geçersiz seçim, ana menüye dönülüyor",
                        CentralizedConstants.MENU_DIALOG_ID
                );
        }


    }

    private CompletableFuture<DialogTurnResult> processFaturaOdeme(WaterfallStepContext stepContext) {
        return getFaturaDetayAsync()
                .thenCompose(detay -> {
                    // 1. Aktivite oluşturma
                    Activity faturaActivity = MessageFactory.text(createFaturaCard(detay));

                    // 2. Seçenekleri doğru şekilde ekleme
                    List<Choice> choices = Arrays.asList(
                            new Choice("Evet"),
                            new Choice("Hayır")
                    );

                    // 3. Gelişmiş prompt options
                    PromptOptions promptOptions = new PromptOptions();
                    promptOptions.setPrompt(faturaActivity);
                    promptOptions.setChoices(choices);
                    promptOptions.setStyle(ListStyle.SUGGESTED_ACTION); // Önemli!

                    return stepContext.prompt(CentralizedConstants.CONFIRM_PROMPT, promptOptions);
                });
    }

    private CompletableFuture<String> getFaturaDetayAsync() {
        // Gerçek veri kaynağından async çekim
        return CompletableFuture.supplyAsync(() -> 
            "Fatura Detayları:\nDönem: Mart 2024\nTutar: 856,75 TL\nSon Ödeme: 25.03.2024\nDurum: Ödenmemiş"
        );
    }

    private String createFaturaCard(String detay) {
       return detay + "\n\nÖdemek ister misiniz?";
    }

    private CompletableFuture<DialogTurnResult> confirmFaturaOdemeStep(WaterfallStepContext stepContext) {
        FoundChoice choice = (FoundChoice) stepContext.getResult();
        if (choice.getValue().equals("Evet")) {
            String odemeUrl = "https://odeme.example.com/fatura/12345";
            String message = String.format("Ödeme sayfasına yönlendiriliyorsunuz...\n%s", odemeUrl);
            return stepContext.getContext().sendActivity(MessageFactory.text(message))
                    .thenCompose(result -> stepContext.endDialog());
        }else{
            return stepContext.getContext().sendActivity(MessageFactory.text("Fatura odemesi iptal edildi"))
                    .thenCompose(result -> stepContext.endDialog());
        }
}}