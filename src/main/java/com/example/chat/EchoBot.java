// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.example.chat;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.chat.constants.CentralizedConstants;
import com.example.chat.dialogs.EnergyDialog;
import com.example.chat.dialogs.FaturaDialog;
import com.example.chat.dialogs.FaturaSorgulamaDialog;
import com.example.chat.dialogs.MenuDialog;
import com.example.chat.dialogs.SupportDialog;
import com.example.chat.dialogs.TalepDialog;
import com.example.chat.model.menus.DialogMenuOption;
import com.example.chat.model.menus.IntentMenuOption;
import com.example.chat.model.menus.MenuOptionInterface;
import com.example.chat.service.IntentService;
import com.example.chat.utils.DialogUtils;
import com.example.chat.utils.MenuMatcher;
import com.microsoft.bot.builder.ActivityHandler;
import com.microsoft.bot.builder.ConversationState;
import com.microsoft.bot.builder.MessageFactory;
import com.microsoft.bot.builder.StatePropertyAccessor;
import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.builder.UserState;
import com.microsoft.bot.dialogs.DialogContext;
import com.microsoft.bot.dialogs.DialogSet;
import com.microsoft.bot.dialogs.DialogState;
import com.microsoft.bot.dialogs.DialogTurnResult;
import com.microsoft.bot.dialogs.DialogTurnStatus;
import com.microsoft.bot.schema.Activity;
import com.microsoft.bot.schema.ChannelAccount;

/**
 * This class implements the functionality of the Bot.
 *
 * <p>
 * This is where application specific logic for interacting with the users would be added. For this
 * sample, the {@link #onMessageActivity(TurnContext)} echos the text back to the user. The {@link
 * #onMembersAdded(List, TurnContext)} will send a greeting to new conversation participants.
 * </p>
 */
public class EchoBot extends ActivityHandler {
    private static final Logger logger = LoggerFactory.getLogger(EchoBot.class);
    @Autowired
    private  IntentService intentService;
    private final DialogSet dialogs;
    private final ConversationState conversationState;
    private final UserState userState;
    private static final String DEFAULT_USER = "Değerli Müşterimiz";



    // Dialog ID sabitleri
    public static final String MENU_DIALOG_ID = CentralizedConstants.MENU_DIALOG_ID;
    public static final String TALEP_DIALOG_ID = CentralizedConstants.TALEP_DIALOG_ID;
    public static final String FATURA_DIALOG_ID = CentralizedConstants.FATURA_DIALOG_ID;
    public static final String FATURA_SORGULAMA_DIALOG_ID = CentralizedConstants.FATURA_SORGULAMA_DIALOG_ID;
    public static final String ENERGY_DIALOG_ID = CentralizedConstants.ENERGY_DIALOG_ID;
    public static final String SUPPORT_DIALOG_ID = CentralizedConstants.SUPPORT_DIALOG_ID;



    private MenuOptionInterface findMenuOption(String userMessage, DialogContext dialogContext) {
        return MenuMatcher.findOption(userMessage,dialogContext);
    }
    public EchoBot(ConversationState conversationState, UserState userState) {
        this.conversationState = conversationState;
        this.userState = userState;


        StatePropertyAccessor<DialogState> dialogStateAccessor =
                conversationState.createProperty("DialogState");

        dialogs = new DialogSet(dialogStateAccessor);

        // Dialogları ekle
        dialogs.add(new MenuDialog(MENU_DIALOG_ID));
        dialogs.add(new TalepDialog(TALEP_DIALOG_ID));
        dialogs.add(new FaturaDialog(FATURA_DIALOG_ID));
        dialogs.add(new FaturaSorgulamaDialog(FATURA_SORGULAMA_DIALOG_ID));
        dialogs.add(new EnergyDialog(ENERGY_DIALOG_ID));
        dialogs.add(new SupportDialog(SUPPORT_DIALOG_ID));

    }


    @Override
    protected CompletableFuture<Void> onMembersAdded(List<ChannelAccount> membersAdded, TurnContext turnContext) {
        // Hoş geldiniz mesajını ve menüyü tek bir activity içinde gönder
        Activity welcomeMessage = MessageFactory.text("Hoş geldiniz! Size nasıl yardımcı olabilirim?");
        return turnContext.sendActivity(welcomeMessage)
                .thenCompose(res -> dialogs.createContext(turnContext))
                .thenCompose(dialogContext -> {
                    System.out.println("🟢 onMembersAdded - Ana Menü başlatılıyor...");
                    // Direkt olarak menü dialog'unu başlat
                    return dialogContext.beginDialog(MENU_DIALOG_ID);
                })
                .thenCompose(result -> {
                    System.out.println("Dialog sonucu: " + result.getStatus());
                    return conversationState.saveChanges(turnContext);
                })
                .exceptionally(ex -> {
                    System.err.println("❌ Hata: " + ex.getMessage());
                    return null;
                });
    }


        @Override
    protected CompletableFuture<Void> onMessageActivity(TurnContext turnContext) {
        try {
        return   dialogs.createContext(turnContext).thenCompose(dialogContext->dialogContext.continueDialog().thenCompose(dialogTurnResult -> {
            String userMessage = turnContext.getActivity().getText().trim().toLowerCase(Locale.forLanguageTag("tr"));
            System.out.println("dialogTurnResult.getStatus(): " + dialogTurnResult.getStatus());
                if (dialogContext.getChild()!=null&&dialogContext.getChild().getActiveDialog().getId().equals("menuPrompt")) {
                    logger.warn("Dialog Child ID: " + dialogContext.getChild().getActiveDialog().getId());
                    return DialogUtils.saveContext(processUserInput(dialogContext,userMessage), userState, conversationState, turnContext);
                }
            if (dialogTurnResult.getStatus() == DialogTurnStatus.COMPLETE) {
                Object result = dialogTurnResult.getResult();
                CompletableFuture<DialogTurnResult> lastResult = null;
                if(result==null){
                    lastResult = dialogContext.replaceDialog(MENU_DIALOG_ID);
                }
                else  if (result instanceof MenuOptionInterface selectedOption) {
                    lastResult= processMenuOrIntent(dialogContext, selectedOption);
                }

                return  DialogUtils.saveContext(lastResult, userState, conversationState, turnContext) ;
            }   System.out.println(dialogContext.getActiveDialog().getId());
            return DialogUtils.saveContext(CompletableFuture.completedFuture(null), userState, conversationState, turnContext);

        }));
        } catch (Exception ex) {
            logger.error("Hata oluştu: ", ex);
            return turnContext.sendActivity(MessageFactory.text("Beklenmeyen bir hata oluştu. Lütfen tekrar deneyin."))
                .thenApply(result -> null).thenCompose(result -> conversationState.saveChanges(turnContext))
                    .thenCompose(result -> userState.saveChanges(turnContext));
        }
    }



    private CompletableFuture<Void> processUserInput(DialogContext dialogContext, String userMessage) {
        String processedMessage = processMessageLanguage(userMessage);
        return intentService.processIntent(dialogContext, processedMessage).thenApply(result -> null);
    }

    /**
     * Kullanıcının mesajını dil açısından işler.
     */
    private String processMessageLanguage(String userMessage) {
        String detectedLanguage = intentService.detectLanguage(userMessage);
    String processedMessage=!"en".equals(detectedLanguage)?intentService.translateToEnglish(userMessage, "en"):userMessage;
      return intentService.detectIntent(processedMessage);

    }
    private CompletableFuture<DialogTurnResult> processMenuOrIntent(DialogContext dialogContext, MenuOptionInterface selectedOption) {
        System.out.println("porsess menu calıstııııııı");
        System.out.println(selectedOption);return switch (selectedOption.getDialogType()) {
            case MENU_DIALOG -> dialogContext.beginDialog(((DialogMenuOption) selectedOption).getDialogId());
            case INTENT_DIALOG -> intentService.processIntent(dialogContext, ((IntentMenuOption) selectedOption).getIntentName());
            case REQUEST_DIALOG -> dialogContext.continueDialog().thenCompose(CompletableFuture::completedFuture);
            default -> CompletableFuture.completedFuture(null);
        };
    }

}