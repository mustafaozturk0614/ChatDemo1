package com.example.chat.dialogs;

import com.example.chat.constants.CentralizedConstants;
import com.example.chat.entity.Bill;
import com.example.chat.model.menus.EnergyIntentOption;
import com.example.chat.model.menus.FaturaSorgulamaOption;
import com.example.chat.service.IntentService;
import com.example.chat.utils.DialogUtils;
import com.microsoft.bot.dialogs.*;
import com.microsoft.bot.dialogs.choices.ListStyle;
import com.microsoft.bot.dialogs.prompts.ChoicePrompt;
import com.microsoft.bot.dialogs.prompts.PromptOptions;
import com.microsoft.bot.builder.MessageFactory;
import com.microsoft.bot.dialogs.choices.Choice;
import com.microsoft.bot.dialogs.choices.FoundChoice;
import com.microsoft.bot.schema.ActionTypes;
import com.microsoft.bot.schema.Activity;
import com.microsoft.bot.schema.CardAction;
import com.microsoft.bot.schema.SuggestedActions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.example.chat.constants.CentralizedConstants.FATURA_SORGULAMA_PROMPT;

public class FaturaSorgulamaDialog extends ComponentDialog {

   
    public FaturaSorgulamaDialog(String dialogId) {
        super(dialogId);

        // Waterfall adımlarını tanımla
        WaterfallStep[] waterfallSteps = new WaterfallStep[] {
            this::handleFaturaSorgulamaStep
            , this::processSelectionStep
        };

        // WaterfallDialog'u ekle
        addDialog(new WaterfallDialog(dialogId, Arrays.asList(waterfallSteps)));
        addDialog(new ChoicePrompt(FATURA_SORGULAMA_PROMPT));
        setInitialDialogId(dialogId);
    }

    private CompletableFuture<DialogTurnResult> handleFaturaSorgulamaStep(WaterfallStepContext stepContext) {

        return   DialogUtils.showDynamicMenu(stepContext, "Enerji yönetimi menüsüne hoş geldiniz. Lütfen bir seçenek belirleyin.", FaturaSorgulamaOption.class, CentralizedConstants.FATURA_PROMPT, ListStyle.SUGGESTED_ACTION);

}

    private CompletableFuture<DialogTurnResult> processSelectionStep(WaterfallStepContext stepContext) {
      return    DialogUtils.processSelectionStep(stepContext, FaturaSorgulamaOption.class);
//        FoundChoice choice = (FoundChoice) stepContext.getResult();
//        FaturaSorgulamaOption selectedOption = FaturaSorgulamaOption.fromDisplayText(choice.getValue());
//        return stepContext.getContext().sendActivity(
//                MessageFactory.text(selectedOption.getIntentName())
//        ).thenCompose(res ->stepContext.endDialog(selectedOption));
    }
}