// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.example.chat;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import com.example.chat.dialogs.*;
import com.example.chat.model.menus.*;
import com.example.chat.service.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.microsoft.bot.dialogs.WaterfallStepContext;
import com.microsoft.bot.schema.ActionTypes;
import com.microsoft.bot.schema.Attachment;
import com.microsoft.bot.schema.CardAction;
import com.microsoft.bot.schema.ChannelAccount;
import com.microsoft.bot.schema.HeroCard;

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

    private final DialogSet dialogs;
    private final ConversationState conversationState;
    private final UserState userState;
    private static final String DEFAULT_USER = "Değerli Müşterimiz";
    @Autowired
    private  IntentService intentService;
    @Autowired
    private BillService billService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private EnergyConsumptionService energyConsumptionService;
    @Autowired
    private SupportRequestService supportRequestService;
    @Autowired
    private  EnergySavingTipService energySavingTipService;
    private final EnergyDialog energyDialog;
    private final SupportDialog supportDialog;

    // Dialog ID sabitleri
    public static final String MENU_DIALOG_ID = "menuDialog";
    public static final String TALEP_DIALOG_ID = "talepDialog";
    public static final String FATURA_DIALOG_ID = "faturaDialog";
    public static final String FATURA_SORGULAMA_DIALOG_ID = "faturaSorgulamaDialog";

    public EchoBot(ConversationState conversationState, UserState userState,
                   EnergyDialog energyDialog, SupportDialog supportDialog) {
        this.conversationState = conversationState;
        this.userState = userState;
        this.energyDialog = energyDialog;
        this.supportDialog = supportDialog;

        StatePropertyAccessor<DialogState> dialogStateAccessor =
                conversationState.createProperty("DialogState");

        dialogs = new DialogSet(dialogStateAccessor);

        // Dialogları ekle
        dialogs.add(new MenuDialog(MENU_DIALOG_ID));
        dialogs.add(new TalepDialog(TALEP_DIALOG_ID));
        dialogs.add(new FaturaDialog(FATURA_DIALOG_ID));
        dialogs.add(new FaturaSorgulamaDialog(FATURA_SORGULAMA_DIALOG_ID));
        dialogs.add(energyDialog);
        dialogs.add(supportDialog);
    }



    private CompletableFuture<DialogTurnResult> handleDetectedIntent(
        WaterfallStepContext stepContext,
        String intent
    ) {
        System.out.println("Detected intent: " + intent);
        switch (intent) {
            case "LastUnpaidBillIntent":
                return stepContext.getContext()
                    .sendActivity(MessageFactory.text("Showing your last unpaid bill..."))
                    .thenCompose(result -> stepContext.next(null));

            case "AllUnpaidBillsIntent":
                return stepContext.getContext()
                    .sendActivity(MessageFactory.text("Showing all your unpaid bills..."))
                    .thenCompose(result -> stepContext.next(null));

            default:
                return stepContext.getContext()
                    .sendActivity(MessageFactory.text("I'm not sure what you want. Please try again."))
                    .thenCompose(result -> stepContext.endDialog());
        }
    }


    private CompletableFuture<DialogTurnResult> finalStep(WaterfallStepContext stepContext) {
        return stepContext.endDialog();
    }

    private Attachment createFaturaCard(Integer faturaNo) {
        HeroCard card = new HeroCard();
        card.setTitle("📄 Fatura Detayları");

        String cardText = String.format(
                "Dönem: Mart 2024\nTutar: 856,75 TL\nSon Ödeme: 25.03.2024\nDurum: Ödenmemiş"
        );

        card.setText(cardText);
        card.setButtons(Arrays.asList(
                new CardAction() {{
                    setTitle("Öde");
                    setValue("Öde");
                    setType(ActionTypes.POST_BACK);
                }},
                new CardAction() {{
                    setTitle("Ana Menü");
                    setValue("Ana Menü");
                    setType(ActionTypes.POST_BACK);
                }}
        ));

        return card.toAttachment();
    }

    private Attachment createTalepCard(String talepTipi, String detay) {
        HeroCard card = new HeroCard();
        card.setTitle("📋 Talep Özeti");

        String cardText = String.format(
                "Talep Tipi: %s\nTarih: %s\n\nDetay:\n%s\n\nBu bilgiler doğru mu?",
                talepTipi,
                new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date()),
                detay
        );

        card.setText(cardText);
        card.setButtons(Arrays.asList(
                new CardAction() {{
                    setTitle("Onayla");
                    setValue("Onayla");
                    setType(ActionTypes.POST_BACK);
                }},
                new CardAction() {{
                    setTitle("İptal");
                    setValue("İptal");
                    setType(ActionTypes.POST_BACK);
                }}
        ));

        return card.toAttachment();
    }

    private Attachment createSonOdemeCard(Integer faturaNo) {
        HeroCard card = new HeroCard();
        card.setTitle("📅 Son Ödeme Bilgisi");
        card.setSubtitle(String.format("Fatura No: #%d", faturaNo));

        List<String> detaylar = Arrays.asList(
                "📆 Son Ödeme Tarihi: 25.03.2024",
                "💰 Ödenecek Tutar: 856,75 TL",
                "⚠️ Kalan Gün: 15",
                "\n⚠️ Önemli Bilgi:",
                "Son ödeme tarihini geçirmemenizi öneririz.",
                "Geç ödemelerde faiz uygulanır."
        );

        card.setText(String.join("\n\n", detaylar));
        card.setButtons(Arrays.asList(
                new CardAction() {{
                    setTitle("Şimdi Öde 💳");
                    setValue("Fatura Öde");
                    setType(ActionTypes.POST_BACK);
                }},
                new CardAction() {{
                    setTitle("Hatırlatıcı Kur ⏰");
                    setValue("Hatırlatıcı");
                    setType(ActionTypes.POST_BACK);
                }},
                new CardAction() {{
                    setTitle("Ana Menüye Dön 🔙");
                    setValue("Ana Menü");
                    setType(ActionTypes.POST_BACK);
                }}
        ));

        return card.toAttachment();
    }

    @Override
    protected CompletableFuture<Void> onMembersAdded(List<ChannelAccount> membersAdded, TurnContext turnContext) {
        return membersAdded.stream()
                .filter(member -> !StringUtils.equals(member.getId(), turnContext.getActivity().getRecipient().getId()))
                .map(channel -> turnContext.sendActivity(MessageFactory.text(
                        String.format("Hoş geldiniz %s! Size nasıl yardımcı olabilirim?", DEFAULT_USER))))
                .findFirst()
                .orElse(CompletableFuture.completedFuture(null))
                .thenCompose(result -> {
                    DialogContext dialogContext = dialogs.createContext(turnContext).join();
                    return dialogContext.beginDialog(MENU_DIALOG_ID)
                            .thenApply(dialogResult -> null);
                });
    }

    @Override
    protected CompletableFuture<Void> onMessageActivity(TurnContext turnContext) {
        try {
            DialogContext dialogContext = dialogs.createContext(turnContext).join();
            String userMessage = turnContext.getActivity().getText();

            // Dil algılama ve çeviri işlemi
            String detectedLanguage = intentService.detectLanguage(userMessage);


            return dialogContext.continueDialog()
                    .thenCompose(dialogTurnResult -> {
                        String processedMessage=userMessage;
                        MenuOptionInterface selectedOption = findMenuOption(userMessage);
                        if(selectedOption==null) {
                            dialogTurnResult.setStatus(DialogTurnStatus.EMPTY);
                            if (!"en".equals(detectedLanguage)) {
                                processedMessage=(intentService.translateToEnglish(userMessage, "en"));
                            }
                        }
                        System.out.println("userMessage: " + processedMessage);
                        if (dialogTurnResult.getStatus() == DialogTurnStatus.EMPTY ||
                                dialogTurnResult.getStatus() == DialogTurnStatus.COMPLETE) {
                            return handleUserMessage(turnContext, selectedOption, processedMessage);
                        }
                        return CompletableFuture.completedFuture(dialogTurnResult);
                    })
                    .thenCompose(result -> conversationState.saveChanges(turnContext))
                    .thenCompose(result -> userState.saveChanges(turnContext))
                    .exceptionally(ex -> {
                        turnContext.sendActivity(MessageFactory.text("Bir hata oluştu. Ana menüye yönlendiriliyorsunuz..."+ex)).join();
                        dialogContext.beginDialog(MENU_DIALOG_ID).join();
                        return null;
                    });

        } catch (Exception ex) {
            ex.printStackTrace();
            turnContext.sendActivity(MessageFactory.text("Bir hata oluştu. Lütfen tekrar deneyin.")).join();
            return CompletableFuture.completedFuture(null);
        }
    }

    private CompletableFuture<DialogTurnResult> handleUserMessage(TurnContext turnContext, MenuOptionInterface selectedOption, String userMessage) {
        try {
            DialogContext dialogContext = dialogs.createContext(turnContext).join();
            if (selectedOption == null) {
                // Intent tespiti yap
                String detectedIntent = intentService.detectIntent(userMessage);
                CompletableFuture<DialogTurnResult> res = handleDetectedIntent(dialogContext, detectedIntent);

                return res.thenCompose(response -> {
                    if (response == null || response.getResult() == null) {
                        // Eğer bir sonuç yoksa veya hata olduysa, ana menüye yönlendir
                        return turnContext.sendActivity(MessageFactory.text("Anlayamadım, lütfen tekrar deneyin. Ana menüye yönlendiriliyorsunuz."))
                                .thenCompose(result -> dialogContext.replaceDialog(MENU_DIALOG_ID));
                    }
                    String responseText = response.getResult().toString();

                    // Yanıtı Türkçeye çevir (eğer gerekliyse)
                    if (!"tr".equals(intentService.detectLanguage(responseText))) {
                        responseText = intentService.translateToEnglish(responseText, "tr");
                    }
                    if (response.getStatus() == DialogTurnStatus.COMPLETE) {
                        return CompletableFuture.completedFuture(response);
                    }
                    // Kullanıcıya dönecek cevabı al


                    // Mesajı gönder ve diyalogu tamamla
                    return turnContext.sendActivity(MessageFactory.text(responseText))
                            .thenApply(resultActivity -> response);
                });
            }

            // Seçili menü opsiyonunu işle
            return handleSelectedOption(dialogContext, selectedOption);
        } catch (Exception ex) {
            logger.error("Kullanıcı mesajı işlenirken hata oluştu", ex);
            return CompletableFuture.completedFuture(null);
        }
    }


    private MenuOptionInterface findMenuOption(String userMessage) {
        return Stream.of(
                Stream.of(MenuOption.values()),
                Stream.of(FaturaOption.values()),
                Stream.of(FaturaSorgulamaOption.values()),
                Stream.of(EnergyIntentOption.values()),
                Stream.of(SupportOption.values()),
                Stream.of(EnergyOption.values())
            )
            .flatMap(s -> s)
            .filter(option -> option.getDisplayText().equals(userMessage))
            .findFirst()
            .orElse(null);
    }

    private CompletableFuture<DialogTurnResult> handleSelectedOption(DialogContext dialogContext, MenuOptionInterface selectedOption) {
        if (selectedOption.getDialogType() == DialogType.MENU_DIALOG) {
            return dialogContext.beginDialog(((DialogMenuOption) selectedOption).getDialogId());
        } else if (selectedOption.getDialogType() == DialogType.INTENT_DIALOG) {
            return handleDetectedIntent(dialogContext, ((IntentMenuOption) selectedOption).getIntentName());
        }
        return CompletableFuture.completedFuture(null);
    }

    private CompletableFuture<DialogTurnResult> handleDetectedIntent(DialogContext dialogContext, String intent) {
        Long userId = 1L; // Kullanıcı ID'sini dinamik olarak alın


        if (intent == null || intent.equals("None")) {
            return dialogContext.getContext().sendActivity(MessageFactory.text("Ne yapmak istediğinizi anlayamadım. Ana menüye yönlendiriliyorsunuz."))
                    .thenCompose(result -> dialogContext.replaceDialog(MENU_DIALOG_ID));
        }

        switch (intent) {
            case "LastUnpaidBillIntent":
                return billService.handleLastUnpaidBill(userId, dialogContext);
            case "AllUnpaidBillsIntent":
                return billService.handleAllUnpaidBills(userId, dialogContext);
            case "PaidBillsIntent":
                return paymentService.handlePaidBills(userId, dialogContext);
            case "EnergySavingTipsIntent":
                return energySavingTipService.showEnergySavingTips(dialogContext);
            case "ConsumptionAnalysisIntent":
                return energyConsumptionService.handleConsumptionAnalysis(userId, dialogContext);
            case "SupportRequestIntent":
                return supportRequestService.handleSupportRequest(userId, dialogContext);
            default:
                return dialogContext.getContext().sendActivity(MessageFactory.text("Bu isteği anlayamadım. Ana menüye yönlendiriliyorsunuz."))
                        .thenCompose(result -> dialogContext.replaceDialog(MENU_DIALOG_ID));
        }

    }



    private Attachment createGecmisFaturalarCard() {
        HeroCard card = new HeroCard();
        card.setTitle("📋 Geçmiş Faturalar");
        card.setSubtitle("Son 3 Aylık Fatura Özeti");

        List<String> faturalar = Arrays.asList(
                "Şubat 2024\n💰 Tutar: 789,50 TL\n⚡ Tüketim: 220 kWh\n✅ Durum: Ödendi",
                "Ocak 2024\n💰 Tutar: 712,25 TL\n⚡ Tüketim: 195 kWh\n✅ Durum: Ödendi",
                "Aralık 2023\n💰 Tutar: 678,00 TL\n⚡ Tüketim: 180 kWh\n✅ Durum: Ödendi"
        );

        card.setText(String.join("\n\n", faturalar));
        card.setButtons(Arrays.asList(
                new CardAction() {{
                    setTitle("Tüm Geçmiş");
                    setValue("Tüm Geçmiş Faturalar");
                    setType(ActionTypes.POST_BACK);
                }},
                new CardAction() {{
                    setTitle("Ana Menüye Dön");
                    setValue("Ana Menü");
                    setType(ActionTypes.POST_BACK);
                }}
        ));

        return card.toAttachment();
    }
}