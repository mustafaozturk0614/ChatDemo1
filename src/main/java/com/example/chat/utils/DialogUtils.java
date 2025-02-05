package com.example.chat.utils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import com.microsoft.bot.builder.ConversationState;
import com.microsoft.bot.builder.MessageFactory;
import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.builder.UserState;
import com.microsoft.bot.dialogs.DialogContext;
import com.microsoft.bot.dialogs.DialogTurnResult;
import com.microsoft.bot.dialogs.DialogTurnStatus;
import com.microsoft.bot.dialogs.WaterfallStepContext;
import com.microsoft.bot.dialogs.choices.FoundChoice;
import com.microsoft.bot.schema.Activity;

public class DialogUtils {

    public static CompletionStage<DialogTurnResult> sendMessageAndReturn(DialogContext dialogContext, String message, String menuDialogId) {
        return dialogContext.getContext().sendActivity(MessageFactory.text(message))
                .thenCompose(result -> dialogContext.replaceDialog(menuDialogId));
    }

    public static CompletionStage<DialogTurnResult> startDialog(DialogContext dialogContext, String dialogId) {
        return dialogContext.beginDialog(dialogId);
    }

    public static CompletionStage<DialogTurnResult> sendErrorMessageAndReturn(DialogContext dialogContext, String errorMessage, String menuDialogId) {
        return dialogContext.getContext().sendActivity(MessageFactory.text("Hata: " + errorMessage))
                .thenCompose(res -> dialogContext.replaceDialog(menuDialogId));
    }

    public static CompletionStage<DialogTurnResult> sendCardAndReturn(DialogContext dialogContext, Activity cardActivity, String menuDialogId) {
        return dialogContext.getContext().sendActivity(cardActivity)
                .thenCompose(res -> dialogContext.replaceDialog(menuDialogId));
    }

    public static CompletionStage<DialogTurnResult> withTimeout(CompletionStage<DialogTurnResult> future, long timeout, TimeUnit unit) {
        return future.toCompletableFuture()
                .completeOnTimeout(new DialogTurnResult(DialogTurnStatus.CANCELLED), timeout, unit);
    }

    public static CompletionStage<Void> saveContext(CompletableFuture<?> voidCompletableFuture, UserState userState, ConversationState conversationState, TurnContext turnContext) {
            return voidCompletableFuture.thenCompose(result -> conversationState.saveChanges(turnContext))
                    .thenCompose(result -> userState.saveChanges(turnContext));
    }
    protected <T extends Enum<T>> CompletableFuture<DialogTurnResult> processSelectionStep(WaterfallStepContext stepContext, Class<T> enumClass) {
        FoundChoice choice = (FoundChoice) stepContext.getResult();
        T selectedOption = Enum.valueOf(enumClass, choice.getValue());
        return stepContext.getContext().sendActivity(
                MessageFactory.text(selectedOption.toString())
        ).thenCompose(res -> stepContext.endDialog(selectedOption));
    }
}