package com.example.chat.dialogs;

import com.example.chat.constants.CentralizedConstants;
import com.example.chat.model.menus.FaturaOption;
import com.example.chat.model.menus.MenuOption;
import com.example.chat.model.menus.SupportOption;
import com.example.chat.service.SupportRequestService;
import com.example.chat.utils.DialogUtils;
import com.example.chat.utils.MenuMatcher;
import com.microsoft.bot.dialogs.*;

import com.microsoft.bot.builder.MessageFactory;
import com.microsoft.bot.dialogs.choices.Choice;
import com.microsoft.bot.dialogs.choices.FoundChoice;
import com.microsoft.bot.dialogs.choices.ListStyle;
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
            this::handleSupport,
            this::handleSupportOptionStep,
            this::finalStep
        )));
        addDialog(new ChoicePrompt("supportPrompt"));
        setInitialDialogId("supportWaterfall");
    }

    private CompletableFuture<DialogTurnResult> showSupportOptionsStep(WaterfallStepContext stepContext) {
        return DialogUtils.showDynamicMenu(stepContext,"Destek talep etmek için lütfen bir seçenek belirleyin:", SupportOption.class, "supportPrompt", ListStyle.SUGGESTED_ACTION);
    }
    private CompletableFuture<DialogTurnResult> handleSupport(WaterfallStepContext waterfallStepContext) {
        return CompletableFuture.completedFuture(new DialogTurnResult(DialogTurnStatus.WAITING)).thenCompose(res->waterfallStepContext.next(waterfallStepContext.getResult()));
    }
    private CompletableFuture<DialogTurnResult> handleSupportOptionStep(WaterfallStepContext stepContext) {

        return CompletableFuture.completedFuture(new DialogTurnResult(DialogTurnStatus.WAITING))
                .thenCompose(result -> {
                    FoundChoice choice = (FoundChoice) stepContext.getResult();
                    SupportOption selectedOption = MenuMatcher.fromDisplayText(choice.getValue(), SupportOption.class);
                    return switch (selectedOption) {
                        case GENERAL_INQUIRY -> handleGeneralInquiry(stepContext);
                        case TECHNICAL_SUPPORT -> handleTechnicalSupport(stepContext);
                        case COMPLAINT -> handleComplaint(stepContext);
                        case GERI -> stepContext.replaceDialog(CentralizedConstants.MENU_DIALOG_ID);
                        default -> stepContext.endDialog();
                    };
                });
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