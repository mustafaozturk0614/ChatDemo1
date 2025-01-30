package com.example.chat.dialogs;

import com.example.chat.entity.EnergyConsumption;
import com.example.chat.model.EnergyIntentOption;
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
    private final EnergyConsumptionService energyConsumptionService;

    public EnergyDialog(EnergyConsumptionService energyConsumptionService) {
        super("energyDialog");
        this.energyConsumptionService = energyConsumptionService;

        addDialog(new WaterfallDialog("energyWaterfall", Arrays.asList(
            this::showEnergyOptionsStep,
            this::handleEnergyOptionStep,
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

    private CompletableFuture<DialogTurnResult> handleEnergyOptionStep(WaterfallStepContext stepContext) {
        String selectedOption = ((FoundChoice) stepContext.getResult()).getValue();

        switch (selectedOption) {
            case "Tüketim Analizi":
                return handleConsumptionAnalysis(stepContext);
            case "Enerji Tasarrufu İpuçları":
                return showEnergySavingTips(stepContext);
            case "Geri Dön":
                return stepContext.endDialog();
            default:
                return stepContext.endDialog();
        }
    }

    private CompletableFuture<DialogTurnResult> handleConsumptionAnalysis(WaterfallStepContext stepContext) {
        Long userId = 1L; // Kullanıcı ID'sini dinamik olarak alın
        return energyConsumptionService.getEnergyConsumptionsByUserId(userId)
            .thenCompose(consumptions -> {
                StringBuilder response = new StringBuilder("Enerji tüketim analiziniz:\n");
                if (!consumptions.isEmpty()) {
                    for (EnergyConsumption consumption : consumptions) {
                        response.append("Dönem: ").append(consumption.getPeriod())
                                .append(" - Tüketim: ").append(consumption.getConsumptionAmount()).append(" kWh\n");
                    }
                } else {
                    response.append("Enerji tüketim veriniz bulunmamaktadır.");
                }
                return stepContext.getContext().sendActivity(MessageFactory.text(response.toString()))
                        .thenCompose(result -> stepContext.endDialog());
            });
    }

    private CompletableFuture<DialogTurnResult> showEnergySavingTips(WaterfallStepContext stepContext) {
        String tips = "Enerji tasarrufu ipuçları:\n\n" +
                      "1. Aydınlatmada LED ampuller kullanın.\n" +
                      "2. Elektrikli cihazları bekleme modunda bırakmayın.\n" +
                      "3. Klimaları 24-26°C arasında kullanın.\n" +
                      "4. Buzdolabınızı güneş almayan bir yere yerleştirin.\n" +
                      "5. Çamaşır makinesini tam dolu çalıştırın.";
        return stepContext.getContext().sendActivity(MessageFactory.text(tips))
                .thenCompose(result -> stepContext.endDialog());
    }

    private CompletableFuture<DialogTurnResult> finalStep(WaterfallStepContext stepContext) {
        return stepContext.endDialog();
    }
} 