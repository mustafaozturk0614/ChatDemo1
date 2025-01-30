package com.example.chat.dialogs;

import com.example.chat.model.menus.FaturaSorgulamaOption;
import com.microsoft.bot.dialogs.*;
import com.microsoft.bot.dialogs.prompts.ChoicePrompt;
import com.microsoft.bot.dialogs.prompts.PromptOptions;
import com.microsoft.bot.builder.MessageFactory;
import com.microsoft.bot.dialogs.choices.Choice;
import com.microsoft.bot.dialogs.choices.FoundChoice;
import com.microsoft.bot.schema.ActionTypes;
import com.microsoft.bot.schema.Activity;
import com.microsoft.bot.schema.CardAction;
import com.microsoft.bot.schema.SuggestedActions;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class FaturaSorgulamaDialog extends ComponentDialog {
    private static final String FATURA_SORGULAMA_PROMPT = "faturaSorgulamaPrompt";

    public FaturaSorgulamaDialog(String dialogId) {
        super(dialogId);

        // Waterfall adımlarını tanımla
        WaterfallStep[] waterfallSteps = new WaterfallStep[] {
            this::handleFaturaSorgulamaStep
        };

        // WaterfallDialog'u ekle
        addDialog(new WaterfallDialog(dialogId, Arrays.asList(waterfallSteps)));
        addDialog(new ChoicePrompt(FATURA_SORGULAMA_PROMPT));
        setInitialDialogId(dialogId);
    }

    private CompletableFuture<DialogTurnResult> handleFaturaSorgulamaStep(WaterfallStepContext stepContext) {
        List<Choice> choices = Arrays.stream(FaturaSorgulamaOption.values())
                .map(option -> new Choice(option.getDisplayText()))
                .collect(Collectors.toList());

        Activity faturaSorgulamaMessage = MessageFactory.text("Fatura sorgulama işlemleriniz için hangi seçeneği tercih edersiniz?");
        faturaSorgulamaMessage.setSuggestedActions(new SuggestedActions() {{
            setActions(choices.stream()
                    .map(choice -> new CardAction() {{
                        setTitle(choice.getValue());
                        setValue(choice.getValue());
                        setType(ActionTypes.POST_BACK);
                    }})
                    .collect(Collectors.toList()));
        }});

        PromptOptions promptOptions = new PromptOptions();
        promptOptions.setPrompt(faturaSorgulamaMessage);
        promptOptions.setChoices(choices);

        return stepContext.prompt("faturaSorgulamaPrompt", promptOptions);
} }