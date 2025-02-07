package com.example.chat.dialogs;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.example.chat.constants.CentralizedConstants;
import com.example.chat.model.menus.MenuOption;
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

            this::handleMenuSelection,

        };
        addDialog(new ChoicePrompt(CentralizedConstants.MENU_PROMPT));
        addDialog(new FaturaDialog(CentralizedConstants.FATURA_DIALOG_ID));
        addDialog(new WaterfallDialog(CentralizedConstants.MENU_WATERFALL_DIALOG, Arrays.asList(waterfallSteps)));
        setInitialDialogId(CentralizedConstants.MENU_WATERFALL_DIALOG);
    }



    private CompletableFuture<DialogTurnResult> showMenuStep(WaterfallStepContext stepContext) {
        System.out.println("ShowMenuStep - Başladı");
        return DialogUtils.showDynamicMenu(stepContext,"Lütfen bir işlem seciniz", MenuOption.class,CentralizedConstants.MENU_PROMPT,ListStyle.SUGGESTED_ACTION).thenApply(dialogTurnResult -> {
            DialogTurnResult result = dialogTurnResult;
            System.out.println("result==>"+result);
            return result;
        });
    }



    private CompletableFuture<DialogTurnResult> handleMenuSelection(WaterfallStepContext stepContext) {
        FoundChoice choice = (FoundChoice) stepContext.getResult();
        MenuOption selected = MenuMatcher.fromDisplayText(choice.getValue(), MenuOption.class);
        return stepContext.replaceDialog(selected.getDialogId());
    }


} 