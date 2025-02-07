package com.example.chat.dialogs;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.example.chat.constants.CentralizedConstants;
import com.example.chat.model.menus.FaturaOption;
import com.example.chat.model.menus.TalepTipi;
import com.example.chat.utils.DialogUtils;
import com.example.chat.utils.MenuMatcher;
import com.microsoft.bot.builder.MessageFactory;
import com.microsoft.bot.dialogs.ComponentDialog;
import com.microsoft.bot.dialogs.DialogTurnResult;
import com.microsoft.bot.dialogs.WaterfallDialog;
import com.microsoft.bot.dialogs.WaterfallStep;
import com.microsoft.bot.dialogs.WaterfallStepContext;
import com.microsoft.bot.dialogs.choices.Choice;
import com.microsoft.bot.dialogs.choices.FoundChoice;
import com.microsoft.bot.dialogs.choices.ListStyle;
import com.microsoft.bot.dialogs.prompts.ChoicePrompt;
import com.microsoft.bot.dialogs.prompts.PromptOptions;
import com.microsoft.bot.dialogs.prompts.TextPrompt;

public class TalepDialog extends ComponentDialog {

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
        addDialog(new ChoicePrompt(CentralizedConstants.TALEP_PROMPT));
        addDialog(new TextPrompt(CentralizedConstants.DETAIL_PROMPT));
        addDialog(new ChoicePrompt(CentralizedConstants.CONFIRM_PROMPT)); // Confirm prompt'u ekle
        setInitialDialogId(dialogId);
    }

    private CompletableFuture<DialogTurnResult> showTalepTipiStep(WaterfallStepContext stepContext) {
        return DialogUtils.showDynamicMenu(stepContext,"Lütfen bir işlem seciniz", TalepTipi.class,CentralizedConstants.TALEP_PROMPT, ListStyle.SUGGESTED_ACTION);
    }

    private CompletableFuture<DialogTurnResult> handleTalepTipiStep(WaterfallStepContext stepContext) {
        String selectedOption = ((FoundChoice) stepContext.getResult()).getValue();
        TalepTipi talepOption = MenuMatcher.fromDisplayText(selectedOption, TalepTipi.class);
        stepContext.getValues().put("talepTipi", talepOption);
        return stepContext.next(null);
    }

    private CompletableFuture<DialogTurnResult> getTalepDetayStep(WaterfallStepContext stepContext) {
        PromptOptions promptOptions = new PromptOptions();
        promptOptions.setPrompt(MessageFactory.text("Lütfen talep detayını girin:"));
        return stepContext.prompt(CentralizedConstants.DETAIL_PROMPT, promptOptions);
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

        return stepContext.prompt(CentralizedConstants.CONFIRM_PROMPT, promptOptions);
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