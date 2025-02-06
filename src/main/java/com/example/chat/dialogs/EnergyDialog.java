package com.example.chat.dialogs;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import com.example.chat.constants.CentralizedConstants;
import com.example.chat.model.menus.EnergyIntentOption;
import com.example.chat.utils.DialogUtils;
import com.microsoft.bot.dialogs.ComponentDialog;
import com.microsoft.bot.dialogs.DialogTurnResult;
import com.microsoft.bot.dialogs.DialogTurnStatus;
import com.microsoft.bot.dialogs.WaterfallDialog;
import com.microsoft.bot.dialogs.WaterfallStep;
import com.microsoft.bot.dialogs.WaterfallStepContext;
import com.microsoft.bot.dialogs.choices.ListStyle;
import com.microsoft.bot.dialogs.prompts.ChoicePrompt;

public class EnergyDialog extends ComponentDialog {

    public EnergyDialog(String dialogId) {
        super(dialogId);
        WaterfallStep[] waterfallSteps = {
                this::showEnergyOptionsStep,
                this::handleEnergySelection,
                this::processSelectionStep // Yeni eklenen handle step
        };
        addDialog(new WaterfallDialog(CentralizedConstants.ENERGY_WATERFALL_DIALOG, Arrays.asList(waterfallSteps)));
        addDialog(new ChoicePrompt(CentralizedConstants.ENERGY_PROMPT));
        setInitialDialogId(CentralizedConstants.ENERGY_WATERFALL_DIALOG);
    }   

    private CompletableFuture<DialogTurnResult> handleEnergySelection(WaterfallStepContext waterfallStepContext) {
        return CompletableFuture.completedFuture(new DialogTurnResult(DialogTurnStatus.WAITING)).thenCompose(res->waterfallStepContext.next(waterfallStepContext.getResult()));
    }

    private CompletableFuture<DialogTurnResult> showEnergyOptionsStep(WaterfallStepContext stepContext) {


      return   DialogUtils.showDynamicMenu(stepContext, "Enerji yönetimi menüsüne hoş geldiniz. Lütfen bir seçenek belirleyin.", EnergyIntentOption.class, CentralizedConstants.ENERGY_PROMPT, ListStyle.SUGGESTED_ACTION);

    }


    private CompletableFuture<DialogTurnResult> processSelectionStep(WaterfallStepContext stepContext) {
        System.out.println("step context getresult==>"+stepContext.getResult());
     return    DialogUtils.processSelectionStep(stepContext,EnergyIntentOption.class);

    }
} 