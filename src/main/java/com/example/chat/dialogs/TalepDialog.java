package com.example.chat.dialogs;

import com.example.chat.model.menus.TalepTipi;
import com.microsoft.bot.dialogs.*;
import com.microsoft.bot.dialogs.prompts.ChoicePrompt;
import com.microsoft.bot.dialogs.prompts.PromptOptions;
import com.microsoft.bot.builder.MessageFactory;
import com.microsoft.bot.dialogs.choices.Choice;
import com.microsoft.bot.dialogs.choices.FoundChoice;
import com.microsoft.bot.dialogs.prompts.TextPrompt;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class TalepDialog extends ComponentDialog {
    private static final String TALIP_PROMPT = "talepPrompt";
    private static final String DETAIL_PROMPT = "detayPrompt";
    private static final String CONFIRM_PROMPT = "confirmPrompt";

    public TalepDialog(String dialogId) {
        super(dialogId);

        // Waterfall adımlarını tanımla
        WaterfallStep[] waterfallSteps = new WaterfallStep[] {
            this::showTalepTipiStep,
            this::handleTalepTipiStep,
            this::getTalepDetayStep,
            this::handleTalepDetayStep,
            this::confirmTalepStep,
            this::processTalepStep
        };

        // WaterfallDialog'u ekle
        addDialog(new WaterfallDialog(dialogId, Arrays.asList(waterfallSteps)));
        addDialog(new ChoicePrompt(TALIP_PROMPT));
        addDialog(new TextPrompt(DETAIL_PROMPT));
        addDialog(new ChoicePrompt(CONFIRM_PROMPT)); // Confirm prompt'u ekle
        setInitialDialogId(dialogId);
    }

    private CompletableFuture<DialogTurnResult> showTalepTipiStep(WaterfallStepContext stepContext) {
        List<Choice> choices = Arrays.stream(TalepTipi.values())
            .map(option -> new Choice(option.getDisplayText()))
            .collect(Collectors.toList());

        PromptOptions promptOptions = new PromptOptions();
        promptOptions.setPrompt(MessageFactory.text("Lütfen talep tipini seçin:"));
        promptOptions.setChoices(choices);

        return stepContext.prompt(TALIP_PROMPT, promptOptions);
    }

    private CompletableFuture<DialogTurnResult> handleTalepTipiStep(WaterfallStepContext stepContext) {
        String selectedOption = ((FoundChoice) stepContext.getResult()).getValue();
        TalepTipi talepOption = TalepTipi.fromDisplayText(selectedOption);
        stepContext.getValues().put("talepTipi", talepOption);
        return stepContext.next(null);
    }

    private CompletableFuture<DialogTurnResult> getTalepDetayStep(WaterfallStepContext stepContext) {
        PromptOptions promptOptions = new PromptOptions();
        promptOptions.setPrompt(MessageFactory.text("Lütfen talep detayını girin:"));
        return stepContext.prompt(DETAIL_PROMPT, promptOptions);
    }

    private CompletableFuture<DialogTurnResult> handleTalepDetayStep(WaterfallStepContext stepContext) {
        String detay = (String) stepContext.getResult();
        stepContext.getValues().put("talepDetay", detay);
        return stepContext.next(null);
    }

    private CompletableFuture<DialogTurnResult> confirmTalepStep(WaterfallStepContext stepContext) {
        TalepTipi talepTipi = (TalepTipi) stepContext.getValues().get("talepTipi");
        String talepDetay = (String) stepContext.getValues().get("talepDetay");

        // Evet ve Hayır seçeneklerini ekle
        List<Choice> choices = Arrays.asList(new Choice("Evet"), new Choice("Hayır"));

        PromptOptions promptOptions = new PromptOptions();
        promptOptions.setPrompt(MessageFactory.text("Talep türü: " + talepTipi.getDisplayText() + "\nDetay: " + talepDetay + "\nOnaylıyor musunuz?"));
        promptOptions.setChoices(choices); // Seçenekleri ekle

        return stepContext.prompt(CONFIRM_PROMPT, promptOptions);
    }

    private CompletableFuture<DialogTurnResult> processTalepStep(WaterfallStepContext stepContext) {
        FoundChoice choice = (FoundChoice) stepContext.getResult(); // FoundChoice olarak al
        boolean onaylandi = choice.getValue().equalsIgnoreCase("Evet"); // Evet ise true, değilse false

        if (onaylandi) {
            return stepContext.getContext().sendActivity(MessageFactory.text("Talebiniz alınmıştır."))
                    .thenCompose(result -> stepContext.endDialog());
        } else {
            return stepContext.getContext().sendActivity(MessageFactory.text("Talep iptal edildi."))
                    .thenCompose(result -> stepContext.endDialog());
        }
    }
} 