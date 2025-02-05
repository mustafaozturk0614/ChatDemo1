package com.example.chat.dialogs;

import com.example.chat.model.menus.FaturaOption;
import com.example.chat.model.menus.SupportOption;
import com.example.chat.service.SupportRequestService;
import com.microsoft.bot.dialogs.ComponentDialog;
import com.microsoft.bot.dialogs.WaterfallDialog;
import com.microsoft.bot.dialogs.WaterfallStep;

import com.microsoft.bot.dialogs.DialogTurnResult;
import com.microsoft.bot.dialogs.WaterfallStepContext;
import com.microsoft.bot.builder.MessageFactory;
import com.microsoft.bot.dialogs.choices.Choice;
import com.microsoft.bot.dialogs.choices.FoundChoice;
import com.microsoft.bot.dialogs.prompts.ChoicePrompt;
import com.microsoft.bot.dialogs.prompts.PromptOptions;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class SupportDialog extends ComponentDialog {


    public SupportDialog(String dialogId) {
        super(dialogId);
        addDialog(new WaterfallDialog("supportWaterfall", Arrays.asList(
            this::showSupportOptionsStep,
            this::handleSupportOptionStep,
            this::finalStep
        )));
        addDialog(new ChoicePrompt("supportPrompt"));
        setInitialDialogId("supportWaterfall");
    }

    private CompletableFuture<DialogTurnResult> showSupportOptionsStep(WaterfallStepContext stepContext) {
        String promptMessage = "Destek talep etmek için lütfen bir seçenek belirleyin:";
        PromptOptions promptOptions = new PromptOptions();
        promptOptions.setPrompt(MessageFactory.text(promptMessage));
        promptOptions.setChoices( Arrays.stream(SupportOption.values())
                .map(option -> new Choice(option.getDisplayText()))
                .collect(Collectors.toList()));

        return stepContext.prompt("supportPrompt", promptOptions);
    }

    private CompletableFuture<DialogTurnResult> handleSupportOptionStep(WaterfallStepContext stepContext) {
        String selectedOption = ((FoundChoice) stepContext.getResult()).getValue();

        switch (selectedOption) {
            case "Genel Bilgi":
                return handleGeneralInquiry(stepContext);
            case "Teknik Destek":
                return handleTechnicalSupport(stepContext);
            case "Şikayet":
                return handleComplaint(stepContext);
            case "Geri Dön":
                return stepContext.endDialog();
            default:
                return stepContext.endDialog();
        }
    }

    private CompletableFuture<DialogTurnResult> handleGeneralInquiry(WaterfallStepContext stepContext) {
        // Genel bilgi talebi işlemleri
        return stepContext.getContext().sendActivity(MessageFactory.text("Genel bilgi talebiniz alınmıştır."))
                .thenCompose(result -> stepContext.endDialog());
    }

    private CompletableFuture<DialogTurnResult> handleTechnicalSupport(WaterfallStepContext stepContext) {
        // Teknik destek talebi işlemleri
        return stepContext.getContext().sendActivity(MessageFactory.text("Teknik destek talebiniz alınmıştır."))
                .thenCompose(result -> stepContext.endDialog());
    }

    private CompletableFuture<DialogTurnResult> handleComplaint(WaterfallStepContext stepContext) {
        // Şikayet talebi işlemleri
        return stepContext.getContext().sendActivity(MessageFactory.text("Şikayet talebiniz alınmıştır."))
                .thenCompose(result -> stepContext.endDialog());
    }

    private CompletableFuture<DialogTurnResult> finalStep(WaterfallStepContext stepContext) {
        return stepContext.endDialog();
    }
} 