package com.example.chat.utils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.chat.model.menus.DialogMenuOption;
import com.example.chat.model.menus.MenuOptionInterface;
import com.microsoft.bot.builder.ConversationState;
import com.microsoft.bot.builder.MessageFactory;
import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.builder.UserState;
import com.microsoft.bot.dialogs.DialogContext;
import com.microsoft.bot.dialogs.DialogTurnResult;
import com.microsoft.bot.dialogs.DialogTurnStatus;
import com.microsoft.bot.dialogs.WaterfallStepContext;
import com.microsoft.bot.dialogs.choices.Choice;
import com.microsoft.bot.dialogs.choices.FoundChoice;
import com.microsoft.bot.dialogs.choices.ListStyle;
import com.microsoft.bot.dialogs.prompts.PromptOptions;
import com.microsoft.bot.schema.ActionTypes;
import com.microsoft.bot.schema.Activity;
import com.microsoft.bot.schema.CardAction;
import com.microsoft.bot.schema.SuggestedActions;

public class DialogUtils {
    private static final Logger logger = LoggerFactory.getLogger(DialogUtils.class);


    public static CompletionStage<DialogTurnResult> startDialog(DialogContext dialogContext, String dialogId) {
        return dialogContext.beginDialog(dialogId);
    }



    public static CompletionStage<DialogTurnResult> withTimeout(CompletionStage<DialogTurnResult> future, long timeout, TimeUnit unit) {
        return future.toCompletableFuture()
                .completeOnTimeout(new DialogTurnResult(DialogTurnStatus.CANCELLED), timeout, unit);
    }

    public static CompletionStage<Void> saveContext(CompletableFuture<?> voidCompletableFuture, UserState userState, ConversationState conversationState, TurnContext turnContext) {
            return voidCompletableFuture.thenCompose(result -> conversationState.saveChanges(turnContext))
                    .thenCompose(result -> userState.saveChanges(turnContext));
    }
    public static <T extends Enum<T> & MenuOptionInterface>  CompletableFuture<DialogTurnResult> processSelectionStep(WaterfallStepContext stepContext, Class<T> enumClass) {
        FoundChoice choice = (FoundChoice) stepContext.getResult();
        T selectedOption = MenuMatcher.fromDisplayText(choice.getValue(), enumClass);
        CompletableFuture<DialogTurnResult> dialogTurnResultCompletableFuture = stepContext.getParent() != null ? stepContext.getParent().endDialog(selectedOption) : null;
        return stepContext.endDialog(selectedOption);
    }

    public static CompletableFuture<DialogTurnResult> showChoicePrompt(
            WaterfallStepContext stepContext,
            String promptText,
            List<Choice> choices,
            String promptId) {


        PromptOptions promptOptions = new PromptOptions();
        promptOptions.setPrompt(MessageFactory.text(promptText));
        promptOptions.setChoices(choices);

        return stepContext.prompt(promptId, promptOptions);
    }


      // 1. Genelleştirilmiş Menü Gösterimi
    /**
     * Enum tabanlı dinamik menü gösterimi
     * @param enumClass Menü seçeneklerini içeren enum sınıfı
     * @param listStyle Seçeneklerin gösterim stili (SUGGESTED_ACTION, HERO_CARD vb.)
     */
    public static <T extends Enum<T> & MenuOptionInterface> CompletableFuture<DialogTurnResult> showDynamicMenu(
            WaterfallStepContext stepContext,
            String promptText,
            Class<T> enumClass,
            String promptId,
            ListStyle listStyle) {
        
        List<Choice> choices = Arrays.stream(enumClass.getEnumConstants())
                .map(option -> new Choice(option.getDisplayText()))
                .collect(Collectors.toList());

        return createPrompt(stepContext, promptText, choices, promptId, listStyle, ActionTypes.POST_BACK);
    }

    // 2. Çoklu Kullanım Senaryoları için Overload'lar
    public static CompletableFuture<DialogTurnResult> showDynamicMenu(
            WaterfallStepContext stepContext,
            String promptText,
            List<Choice> customChoices,
            String promptId) {
        
        return createPrompt(stepContext, promptText, customChoices, promptId, ListStyle.SUGGESTED_ACTION, ActionTypes.POST_BACK);
    }

    // 3. Merkezi Prompt Oluşturucu
    private static CompletableFuture<DialogTurnResult> createPrompt(
            WaterfallStepContext stepContext,
            String promptText,
            List<Choice> choices,
            String promptId,
            ListStyle listStyle,
            ActionTypes actionType) {
        
        Activity promptActivity = buildPromptActivity(promptText, choices, actionType);
        
        PromptOptions options = new PromptOptions();
        options.setPrompt(promptActivity);
        options.setChoices(choices);
        options.setStyle(listStyle);
       // options.setValidations(new PromptValidationContext(stepContext.getContext()));

        return stepContext.prompt(promptId, options);
    }

    // 4. Aktivite Builder
    private static Activity buildPromptActivity(String text, List<Choice> choices, ActionTypes actionType) {
        List<CardAction> actions = choices.stream()
                .map(choice -> new CardAction() {{
                    setType(actionType);
                    setTitle(choice.getValue());
                    setValue(choice.getValue());
                    setText(choice.getValue());
                  //  setDisplayText(choice.getAction());
                }})
                .collect(Collectors.toList());

        Activity activity = MessageFactory.text(text);
        activity.setSuggestedActions(new SuggestedActions() {{
            setActions(actions);
        }});
        //   activity.setInputHint(InputHint.EXPECTING_INPUT);
        return activity;
    }

    // 5. Akıllı Seçim İşleyici
    /**
     * Seçim sonuçlarını otomatik olarak enum'a dönüştürür
     * @param errorDialogId Hata durumunda dönülecek dialog ID
     */
    public static <T extends Enum<T> & MenuOptionInterface> CompletableFuture<DialogTurnResult> handleSelection(
            WaterfallStepContext stepContext,
            Class<T> enumClass,
            String successMessage,
            String errorDialogId) {
        try {
            FoundChoice choice = (FoundChoice) stepContext.getResult();
            T selected = findEnumValue(enumClass, choice.getValue());
            
            if (selected instanceof DialogMenuOption) {
                // Eğer seçim bir dialog başlatacaksa
                return stepContext.beginDialog(((DialogMenuOption) selected).getDialogId());
            } else {
                // Normal seçim işlemi
                return stepContext.next(selected);
            }
        } catch (Exception ex) {
            logger.error("Seçim işleme hatası: {}", ex.getMessage());
            return sendErrorMessageAndReturn(stepContext, "Geçersiz seçim", errorDialogId)
                .toCompletableFuture();
        }
    }

    // 6. Gelişmiş Hata Yönetimi
    public static CompletableFuture<DialogTurnResult> handleError(
            DialogContext dialogContext,
            Throwable error,
            String errorMessage,
            String fallbackDialogId) {
        
        logger.error("Dialog Hatası [{}]: {}",
            dialogContext.getActiveDialog().getId(),
            error.getMessage(),
            error
        );
        
        return sendErrorMessageAndReturn(
            dialogContext,
            errorMessage,
            fallbackDialogId
        ).toCompletableFuture();
    }

    // 7. Otomatik State Yöneticisi
    public static CompletableFuture<Void> autoSaveStates(
            TurnContext context,
            UserState userState,
            ConversationState conversationState) {
        
        return conversationState.saveChanges(context, false)
            .thenCompose(__ -> userState.saveChanges(context, false))
            .exceptionally(ex -> {
                logger.error("State kaydetme hatası: {}", ex.getMessage());
                return null;
            });
    }

    // Yardımcı Metodlar
    private static <T extends Enum<T> & MenuOptionInterface> T findEnumValue(Class<T> enumClass, String value) {
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(e -> e.getDisplayText().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Geçersiz seçim: " + value));
    }

    public static CompletionStage<DialogTurnResult> sendErrorMessageAndReturn(
            DialogContext dialogContext,
            String errorMessage,
            String returnDialogId) {
        
        Activity errorActivity = MessageFactory.text("⛔ Hata: " + errorMessage);
        errorActivity.setSpeak("Hata oluştu. Lütfen tekrar deneyin.");
        
        return dialogContext.getContext().sendActivity(errorActivity)
                .thenCompose(res -> dialogContext.replaceDialog(returnDialogId));
    }

    public static CompletableFuture<DialogTurnResult> sendMessageAndReturn(
            DialogContext dialogContext,
            String message,
            String returnDialogId) {
        return dialogContext.getContext().sendActivity(MessageFactory.text(message))
            .thenCompose(res -> dialogContext.replaceDialog(returnDialogId))
            .exceptionally(ex -> {
                ex.printStackTrace();
                logger.error("Mesaj gönderilirken hata: {}", ex.getMessage());
                return new DialogTurnResult(DialogTurnStatus.CANCELLED);
            });
    }
}