package com.example.chat.dialogs;

import com.example.chat.model.menus.FaturaOption;
import com.microsoft.bot.dialogs.*;
import com.microsoft.bot.dialogs.prompts.ChoicePrompt;
import com.microsoft.bot.dialogs.prompts.PromptOptions;
import com.microsoft.bot.builder.MessageFactory;
import com.microsoft.bot.dialogs.choices.Choice;
import com.microsoft.bot.dialogs.choices.FoundChoice;
import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.schema.Activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;



public class FaturaDialog extends ComponentDialog {
    private static final String FATURA_PROMPT = "faturaPrompt";
    private static final String WATERFALL_DIALOG = "faturaWaterfall";
    private static final String FATURA_SORGULAMA_DIALOG_ID ="faturaSorgulamaDialog";
    private static final String CONFIRM_PROMPT = "confirmPrompt";
    ;

    public FaturaDialog(String dialogId) {
        super(dialogId);

        WaterfallStep[] waterfallSteps = new WaterfallStep[] {
            this::showFaturaOptionsStep,
            this::handleFaturaSelectionStep,
            this::confirmFaturaOdemeStep
        };

        addDialog(new WaterfallDialog(WATERFALL_DIALOG, Arrays.asList(waterfallSteps)));
        addDialog(new ChoicePrompt(FATURA_PROMPT));
        addDialog(new ChoicePrompt(CONFIRM_PROMPT));
        setInitialDialogId(WATERFALL_DIALOG);
    }

    private CompletableFuture<DialogTurnResult> showFaturaOptionsStep(WaterfallStepContext stepContext) {

        List<Choice> choices = Arrays.stream(FaturaOption.values())
        .map(option -> new Choice(option.getDisplayText()))
        .collect(Collectors.toList());

        PromptOptions promptOptions = new PromptOptions();
        promptOptions.setPrompt(MessageFactory.text("Fatura işlemleriniz için hangi seçeneği tercih edersiniz?"));
        promptOptions.setChoices(choices);

        return stepContext.prompt("faturaPrompt", promptOptions);
    }

    private CompletableFuture<DialogTurnResult> handleFaturaSelectionStep(WaterfallStepContext stepContext) {
        FoundChoice choice = (FoundChoice) stepContext.getResult();
        String selection = choice.getValue();

        if (selection.equals("Fatura Sorgula")) {
            return stepContext.replaceDialog(FATURA_SORGULAMA_DIALOG_ID);
        } else if (selection.equals("Fatura Öde")) {
            String faturaDetay = "Fatura Detayları:\nDönem: Mart 2024\nTutar: 856,75 TL\nSon Ödeme: 25.03.2024\nDurum: Ödenmemiş\n\nÖdemek ister misiniz?";
            PromptOptions promptOptions = new PromptOptions();
            promptOptions.setPrompt(MessageFactory.text(faturaDetay));
            promptOptions.setChoices(Arrays.asList(new Choice("Evet"), new Choice("Hayır"))); // Evet/Hayır seçenekleri

            return stepContext.prompt(CONFIRM_PROMPT, promptOptions);
        } else if (selection.equals("Geri")) {
            return stepContext.endDialog();
        }

        return stepContext.next(null);
    }

    private CompletableFuture<DialogTurnResult> confirmFaturaOdemeStep(WaterfallStepContext stepContext) {
        FoundChoice choice = (FoundChoice) stepContext.getResult(); // FoundChoice olarak al
        boolean odemeOnaylandi = choice.getValue().equalsIgnoreCase("Evet"); // Evet ise true, değilse false

        if (odemeOnaylandi) {
            String odemeUrl = "https://odeme.example.com/fatura/12345";
            String message = String.format("Ödeme sayfasına yönlendiriliyorsunuz...\n%s", odemeUrl);
            return stepContext.getContext().sendActivity(MessageFactory.text(message))
                    .thenCompose(result -> stepContext.endDialog());
        } else {
            return stepContext.getContext().sendActivity(MessageFactory.text("İşlem iptal edildi. Ana menüye dönülüyor..."))
                    .thenCompose(result -> stepContext.endDialog());
        }
    }
}