package com.example.chat.dialogs;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.example.chat.model.menus.MenuOption;
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
    private static final String MENU_PROMPT = "menuPrompt";
    private static final String WATERFALL_DIALOG = "menuWaterFall";

    public MenuDialog(String dialogId) {
        super(dialogId);

        // Waterfall adımlarını tanımla
        WaterfallStep[] waterfallSteps = new WaterfallStep[] {
            this::showMenuStep,
            this::handleMenuSelection
        };
        ChoicePrompt choicePrompt = new ChoicePrompt(MENU_PROMPT);
        // Exact match zorunluluğunu kaldır
        choicePrompt.setStyle(ListStyle.SUGGESTED_ACTION);
        // WaterfallDialog'u ekle
        addDialog(new WaterfallDialog(WATERFALL_DIALOG, Arrays.asList(waterfallSteps)));
        addDialog(choicePrompt);
        setInitialDialogId(WATERFALL_DIALOG);
    }

    private CompletableFuture<DialogTurnResult> showMenuStep(WaterfallStepContext stepContext) {
        System.out.println("ShowMenuStep - Başladı");

        // Menüyü gösterelim
        List<Choice> choices = Arrays.stream(MenuOption.values())
                .map(option -> new Choice(option.getDisplayText()))
                .collect(Collectors.toList());

        List<CardAction> cardActions = choices.stream()
                .map(choice -> new CardAction() {{
                    setType(ActionTypes.POST_BACK); // POST_BACK kullanarak yanıt gönder
                    setTitle(choice.getValue());
                    setValue(choice.getValue());

                    // Kullanıcıdan gelen yanıt burada kullanılacak
                }})
                .collect(Collectors.toList());

        // Prompt Activity oluştur
        Activity promptActivity = MessageFactory.text("Lütfen bir işlem seçin:");
        promptActivity.setSuggestedActions(new SuggestedActions() {{
            setActions(cardActions);
        }});

        // Prompt ayarları
        PromptOptions promptOptions = new PromptOptions();
        promptOptions.setPrompt(promptActivity);
        promptOptions.setChoices(choices);
        return stepContext.prompt(MENU_PROMPT, promptOptions)
                .thenApply(result -> {
                    System.out.println("🟢 Kullanıcı bir seçim yaptı:");
                    // Burada sonucu görebilirsin
                    return result;
                });
    }



    private CompletableFuture<DialogTurnResult> handleMenuSelection(WaterfallStepContext stepContext) {
        System.out.println("🟢 handleMenuSelection ÇALIŞTI! Kullanıcının seçimi alındı.");
        Object result = stepContext.getResult();
        if (!(result instanceof FoundChoice)) {
            System.out.println("⚠️ Hata! Kullanıcıdan gelen yanıt `FoundChoice` değil!");
            return stepContext.endDialog();
        }

        FoundChoice choice = (FoundChoice) result;
        String selectedOption = choice.getValue();
        System.out.println("🟢 Kullanıcının seçimi: " + selectedOption);

        MenuOption menuOption = MenuOption.fromDisplayText(selectedOption);
        return stepContext.replaceDialog(menuOption.getDialogId());
    }


} 