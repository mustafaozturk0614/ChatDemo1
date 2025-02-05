package com.example.chat.dialogs;

import com.example.chat.entity.EnergyConsumption;
import com.example.chat.model.menus.EnergyIntentOption;
import com.example.chat.service.EnergyConsumptionService;
import com.microsoft.bot.dialogs.ComponentDialog;
import com.microsoft.bot.dialogs.WaterfallDialog;

import com.microsoft.bot.dialogs.DialogTurnResult;
import com.microsoft.bot.dialogs.WaterfallStepContext;
import com.microsoft.bot.builder.MessageFactory;
import com.microsoft.bot.dialogs.choices.Choice;
import com.microsoft.bot.dialogs.choices.FoundChoice;
import com.microsoft.bot.dialogs.prompts.ChoicePrompt;
import com.microsoft.bot.dialogs.prompts.PromptOptions;
import com.microsoft.bot.schema.ActionTypes;
import com.microsoft.bot.schema.Activity;
import com.microsoft.bot.schema.CardAction;
import com.microsoft.bot.schema.SuggestedActions;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class EnergyDialog extends ComponentDialog {

    public EnergyDialog(String dialogId) {
        super(dialogId);
        addDialog(new WaterfallDialog("energyWaterfall", Arrays.asList(
            this::showEnergyOptionsStep,
            this::finalStep
        )));
        addDialog(new ChoicePrompt("energyPrompt"));
        setInitialDialogId("energyWaterfall");
    }

    private CompletableFuture<DialogTurnResult> showEnergyOptionsStep(WaterfallStepContext stepContext) {
        // Enerji yönetimi seçeneklerini tanımlama
        List<Choice> choices = Arrays.stream(EnergyIntentOption.values())
                .map(option -> new Choice(option.getDisplayText()))
                .collect(Collectors.toList());
        // Kullanıcıya gösterilecek mesaj
        Activity energyOptionsMessage = MessageFactory.text("Enerji yönetimi menüsüne hoş geldiniz. Lütfen bir seçenek belirleyin:");
        
        // Önerilen eylemleri ayarlama
        energyOptionsMessage.setSuggestedActions(new SuggestedActions() {{
            setActions(choices.stream()
                    .map(choice -> new CardAction() {{
                        setTitle(choice.getValue());
                        setValue(choice.getValue());
                        setType(ActionTypes.POST_BACK);
                    }})
                    .collect(Collectors.toList()));
        }});
        // Prompt ayarları
        PromptOptions promptOptions = new PromptOptions();
        promptOptions.setPrompt(energyOptionsMessage);
        promptOptions.setChoices(choices);

        return stepContext.prompt("energyPrompt", promptOptions);
    }


    private CompletableFuture<DialogTurnResult> finalStep(WaterfallStepContext stepContext) {
        return stepContext.endDialog();
    }
} 