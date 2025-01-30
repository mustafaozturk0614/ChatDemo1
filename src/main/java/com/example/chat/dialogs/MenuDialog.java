package com.example.chat.dialogs;

import com.example.chat.model.menus.MenuOption;
import com.microsoft.bot.dialogs.*;
import com.microsoft.bot.dialogs.choices.Choice;
import com.microsoft.bot.dialogs.prompts.PromptOptions;
import com.microsoft.bot.builder.MessageFactory;
import com.microsoft.bot.schema.Activity;
import com.microsoft.bot.schema.SuggestedActions;
import com.microsoft.bot.schema.CardAction;
import com.microsoft.bot.schema.ActionTypes;
import com.microsoft.bot.dialogs.prompts.ChoicePrompt;
import com.microsoft.bot.dialogs.choices.FoundChoice;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class MenuDialog extends ComponentDialog {
    private static final String MENU_PROMPT = "menuPrompt";

    public MenuDialog(String dialogId) {
        super(dialogId);

        // Waterfall adımlarını tanımla
        WaterfallStep[] waterfallSteps = new WaterfallStep[] {
            this::showMenuStep,
            this::handleMenuSelection
        };

        // WaterfallDialog'u ekle
        addDialog(new WaterfallDialog(dialogId, Arrays.asList(waterfallSteps)));
        addDialog(new ChoicePrompt(MENU_PROMPT));
        setInitialDialogId(dialogId);
    }

    private CompletableFuture<DialogTurnResult> showMenuStep(WaterfallStepContext stepContext) {
        List<Choice> choices = Arrays.stream(MenuOption.values())
            .map(option -> new Choice(option.getDisplayText()))
            .collect(Collectors.toList());

        Activity welcomeMessage = MessageFactory.text("Merhaba! Size nasıl yardımcı olabilirim?");
        welcomeMessage.setSuggestedActions(new SuggestedActions() {{
            setActions(choices.stream()
                .map(choice -> new CardAction() {{
                    setTitle(choice.getValue());
                    setValue(choice.getValue());
                    setType(ActionTypes.POST_BACK);
                }})
                .collect(Collectors.toList()));
        }});

        PromptOptions promptOptions = new PromptOptions();
        promptOptions.setPrompt(welcomeMessage);
        promptOptions.setChoices(choices);

        return stepContext.prompt(MENU_PROMPT, promptOptions);
    }

    private CompletableFuture<DialogTurnResult> handleMenuSelection(WaterfallStepContext stepContext) {
        String selectedOption = ((FoundChoice) stepContext.getResult()).getValue();
        MenuOption menuOption = MenuOption.fromDisplayText(selectedOption);

        return switch (menuOption) {
            case FATURA_ISLEMLERI -> stepContext.replaceDialog("faturaDialog");
            case TALEP_SIKAYET -> stepContext.replaceDialog("talepDialog");
            default -> stepContext.endDialog();
        };
    }
} 