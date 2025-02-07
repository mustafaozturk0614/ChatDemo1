package com.example.chat.dialogs;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import static com.example.chat.constants.CentralizedConstants.FATURA_SORGULAMA_PROMPT;
import static com.example.chat.constants.CentralizedConstants.FATURA_SORGULAMA_WATERFALL_DIALOG;
import com.example.chat.model.menus.FaturaSorgulamaOption;
import com.example.chat.utils.DialogUtils;
import com.microsoft.bot.dialogs.ComponentDialog;
import com.microsoft.bot.dialogs.DialogTurnResult;
import com.microsoft.bot.dialogs.WaterfallDialog;
import com.microsoft.bot.dialogs.WaterfallStep;
import com.microsoft.bot.dialogs.WaterfallStepContext;
import com.microsoft.bot.dialogs.choices.ListStyle;
import com.microsoft.bot.dialogs.prompts.ChoicePrompt;

public class FaturaSorgulamaDialog extends ComponentDialog {

   
    public FaturaSorgulamaDialog(String dialogId) {
        super(dialogId);

        // Waterfall adımlarını tanımla
        WaterfallStep[] waterfallSteps = new WaterfallStep[] {
            this::handleFaturaSorgulamaStep
            , this::processSelectionStep
        };

        // WaterfallDialog'u ekle
        addDialog(new WaterfallDialog(FATURA_SORGULAMA_WATERFALL_DIALOG, Arrays.asList(waterfallSteps)));
        addDialog(new ChoicePrompt(FATURA_SORGULAMA_PROMPT));
        setInitialDialogId(FATURA_SORGULAMA_WATERFALL_DIALOG);
    }

    private CompletableFuture<DialogTurnResult> handleFaturaSorgulamaStep(WaterfallStepContext stepContext) {
        if (stepContext.getContext().getTurnState().get("DialogState") != null) {
            System.out.println("Active Dialog ID: " + stepContext.getParent().getActiveDialog().getId());
        }
        return   DialogUtils.showDynamicMenu(stepContext, "Fatura Sorgulam Ekranın Hoşgeldiniz !!", FaturaSorgulamaOption.class, FATURA_SORGULAMA_PROMPT, ListStyle.SUGGESTED_ACTION);

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