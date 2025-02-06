package com.example.chat.dialogs;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.example.chat.constants.CentralizedConstants;
import com.example.chat.model.menus.MenuOption;
import com.example.chat.utils.DialogUtils;
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
import com.microsoft.bot.schema.ActionTypes;
import com.microsoft.bot.schema.Activity;
import com.microsoft.bot.schema.CardAction;
import com.microsoft.bot.schema.SuggestedActions;



public class MenuDialog extends ComponentDialog {

    public MenuDialog(String dialogId) {
        super(dialogId);

        // Waterfall adımlarını tanımla
        WaterfallStep[] waterfallSteps = new WaterfallStep[] {
            this::showMenuStep,
            this::handleMenuSelection
        };
        ChoicePrompt choicePrompt = new ChoicePrompt(CentralizedConstants.MENU_PROMPT);
        // Exact match zorunluluğunu kaldır
        choicePrompt.setStyle(ListStyle.SUGGESTED_ACTION);
        // WaterfallDialog'u ekle
        addDialog(new WaterfallDialog(CentralizedConstants.MENU_WATERFALL_DIALOG, Arrays.asList(waterfallSteps)));
        addDialog(choicePrompt);
        setInitialDialogId(CentralizedConstants.MENU_WATERFALL_DIALOG);
    }

    private CompletableFuture<DialogTurnResult> showMenuStep(WaterfallStepContext stepContext) {
        System.out.println("ShowMenuStep - Başladı");
        return DialogUtils.showDynamicMenu(stepContext,"Lütfen bir işlem seciniz", MenuOption.class,CentralizedConstants.MENU_PROMPT,ListStyle.SUGGESTED_ACTION);
    }



    private CompletableFuture<DialogTurnResult> handleMenuSelection(WaterfallStepContext stepContext) {
//        System.out.println("🟢 handleMenuSelection ÇALIŞTI! Kullanıcının seçimi alındı.");
//        Object result = stepContext.getResult();
//
//        FoundChoice choice = (FoundChoice) result;
//        String selectedOption = choice.getValue();
//        System.out.println("🟢 Kullanıcının seçimi: " + selectedOption);
//
//        MenuOption menuOption = MenuOption.fromDisplayText(selectedOption);
//        return stepContext.replaceDialog(menuOption.getDialogId());
        return DialogUtils.handleSelection
                (stepContext, MenuOption.class, "Lütfen bir işlem seciniz", CentralizedConstants.MENU_WATERFALL_DIALOG);

    }


} 