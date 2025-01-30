// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.example.chat;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.example.chat.dialogs.*;
import com.example.chat.entity.Bill;
import com.example.chat.entity.Payment;
import com.example.chat.entity.SupportRequest;
import com.example.chat.model.menus.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.chat.service.BillService;
import com.example.chat.service.EnergyConsumptionService;
import com.example.chat.entity.EnergyConsumption;
import com.example.chat.service.IntentService;
import com.example.chat.service.PaymentService;
import com.example.chat.service.SubscriptionService;
import com.example.chat.service.SupportRequestService;
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
import com.microsoft.bot.dialogs.WaterfallDialog;
import com.microsoft.bot.dialogs.WaterfallStep;
import com.microsoft.bot.dialogs.WaterfallStepContext;
import com.microsoft.bot.dialogs.choices.Choice;
import com.microsoft.bot.dialogs.choices.FoundChoice;
import com.microsoft.bot.dialogs.prompts.ChoicePrompt;
import com.microsoft.bot.dialogs.prompts.ConfirmPrompt;
import com.microsoft.bot.dialogs.prompts.PromptOptions;
import com.microsoft.bot.dialogs.prompts.TextPrompt;
import com.microsoft.bot.schema.ActionTypes;
import com.microsoft.bot.schema.Activity;
import com.microsoft.bot.schema.Attachment;
import com.microsoft.bot.schema.CardAction;
import com.microsoft.bot.schema.ChannelAccount;
import com.microsoft.bot.schema.HeroCard;
import com.microsoft.bot.schema.SuggestedActions;
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
    private final EnergyDialog energyDialog;
    private final SupportDialog supportDialog;

    // Dialog ID sabitleri
    private static final String MENU_DIALOG_ID = "menuDialog";
    private static final String TALEP_DIALOG_ID = "talepDialog";
    private static final String FATURA_DIALOG_ID = "faturaDialog";
    private static final String FATURA_SORGULAMA_DIALOG_ID = "faturaSorgulamaDialog";

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

//    private void addMenuDialog() {
//        WaterfallStep[] menuSteps = createMenuSteps();
//        dialogs.add(new WaterfallDialog(MENU_DIALOG_ID, Arrays.asList(menuSteps)));
//        dialogs.add(new ChoicePrompt("menuPrompt"));
//    }
//
//    private void addTalepDialog() {
//        WaterfallStep[] talepSteps = createTalepSteps();
//        dialogs.add(new WaterfallDialog(TALEP_DIALOG_ID, Arrays.asList(talepSteps)));
//        dialogs.add(new ChoicePrompt("talepPrompt"));
//        dialogs.add(new TextPrompt("detayPrompt"));
//        dialogs.add(new ConfirmPrompt("confirmPrompt"));
//    }
//
//    private void addFaturaDialog() {
//        WaterfallStep[] faturaSteps = createFaturaSteps();
//        dialogs.add(new WaterfallDialog(FATURA_DIALOG_ID, Arrays.asList(faturaSteps)));
//        dialogs.add(new ChoicePrompt("faturaPrompt"));
//    }
//
//    private void addFaturaSorgulamaDialog() {
//        WaterfallStep[] faturaSorgulamaSteps = createFaturaSorgulamaSteps();
//        dialogs.add(new WaterfallDialog(FATURA_SORGULAMA_DIALOG_ID, Arrays.asList(faturaSorgulamaSteps)));
//        dialogs.add(new ChoicePrompt("faturaSorgulamaPrompt"));
//    }
//
//    private WaterfallStep[] createMenuSteps() {
//        return new WaterfallStep[] {
//                this::showMenuStep,
//                this::finalStep
//        };
//    }
//
//    private WaterfallStep[] createTalepSteps() {
//        return new WaterfallStep[] {
//                this::showTalepTipiStep,
//                this::handleTalepTipiStep,
//                this::getTalepDetayStep,
//                this::handleTalepDetayStep,
//                this::confirmTalepStep,
//                this::processTalepStep
//        };
//    }
//
//    private WaterfallStep[] createFaturaSteps() {
//        return new WaterfallStep[] {
//                this::showFaturaOptionsStep,
//                this::handleFaturaSelectionStep,
//                this::confirmFaturaOdemeStep,
//                this::finalStep
//        };
//    }
//
//    private WaterfallStep[] createFaturaSorgulamaSteps() {
//        return new WaterfallStep[] {
//                this::handleFaturaSorgulamaStep,
//                this::finalStep
//        };
//    }

 

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

    // private CompletableFuture<DialogTurnResult> showTalepTipiStep(WaterfallStepContext stepContext) {
    //     List<Choice> choices = Arrays.stream(TalepTipi.values())
    //     .map(option -> new Choice(option.getDisplayText()))
    //     .collect(Collectors.toList());


    //     Activity menuMessage = MessageFactory.text("Lütfen talep tipini seçin:");
    //     menuMessage.setSuggestedActions(new SuggestedActions() {{
    //         setActions(choices.stream()
    //                 .map(choice -> new CardAction() {{
    //                     setTitle(choice.getValue());
    //                     setValue(choice.getValue());
    //                     setType(ActionTypes.POST_BACK);
    //                 }})
    //                 .collect(Collectors.toList()));
    //     }});

    //     PromptOptions promptOptions = new PromptOptions();
    //     promptOptions.setPrompt(menuMessage);
    //     promptOptions.setChoices(choices);

    //     return stepContext.prompt("talepPrompt", promptOptions);
    // }

    // private CompletableFuture<DialogTurnResult> handleTalepTipiStep(WaterfallStepContext stepContext) {
    //     FoundChoice choice = (FoundChoice) stepContext.getResult();
    //     String selection = choice.getValue();

    //     if (selection.equals(TalepTipi.GERI.getDisplayText())) {
    //         return stepContext.endDialog();
    //     }

    //     stepContext.getValues().put("talepTipi", selection);
    //     return stepContext.next(selection);
    // }

    // private CompletableFuture<DialogTurnResult> getTalepDetayStep(WaterfallStepContext stepContext) {
    //     String talepTipi = (String) stepContext.getValues().get("talepTipi");
    //     String promptText = "";

    //     if (talepTipi.equals(TalepTipi.ARIZA.getDisplayText())) {
    //         promptText = "Lütfen arıza ile ilgili detaylı bilgi verin (konum, sorun türü vb.):";
    //     } else if (talepTipi.equals(TalepTipi.BAGLANTI.getDisplayText())) {
    //         promptText = "Lütfen yeni bağlantı için adres ve iletişim bilgilerinizi girin:";
    //     } else if (talepTipi.equals(TalepTipi.SAYAC.getDisplayText())) {
    //         promptText = "Lütfen sayaç işleminizi detaylandırın:";
    //     }

    //     PromptOptions promptOptions = new PromptOptions();
    //     promptOptions.setPrompt(MessageFactory.text(promptText));
    //     return stepContext.prompt("detayPrompt", promptOptions);
    // }

    // private CompletableFuture<DialogTurnResult> handleTalepDetayStep(WaterfallStepContext stepContext) {
    //     String detay = (String) stepContext.getResult();
    //     stepContext.getValues().put("talepDetay", detay);

    //     String talepTipi = (String) stepContext.getValues().get("talepTipi");
    //     String onayMesaji = String.format(
    //             "Talebinizi onaylıyor musunuz?\n\nTalep Tipi: %s\nDetay: %s",
    //             talepTipi,
    //             detay
    //     );

    //     PromptOptions promptOptions = new PromptOptions();
    //     promptOptions.setPrompt(MessageFactory.text(onayMesaji));
    //     return stepContext.prompt("confirmPrompt", promptOptions);
    // }

    // private CompletableFuture<DialogTurnResult> confirmTalepStep(WaterfallStepContext stepContext) {
    //     boolean onaylandi = (boolean) stepContext.getResult();

    //     if (onaylandi) {
    //         String talepNo = String.format("T%d", (int)(Math.random() * 100000));
    //         String successMessage = String.format("Talebiniz başarıyla oluşturuldu!\nTakip Numaranız: %s", talepNo);
    //         return stepContext.getContext().sendActivity(MessageFactory.text(successMessage))
    //                 .thenCompose(result -> stepContext.next(talepNo));
    //     } else {
    //         return stepContext.getContext().sendActivity(MessageFactory.text("Talep iptal edildi."))
    //                 .thenCompose(result -> stepContext.endDialog());
    //     }
    // }

    // private CompletableFuture<DialogTurnResult> processTalepStep(WaterfallStepContext stepContext) {
    //     try {
    //         String talepNo = null;
    //         Object stepResult = stepContext.getResult();

    //         if (stepResult instanceof String) {
    //             talepNo = (String) stepResult;
    //         } else {
    //             talepNo = (String) stepContext.getValues().get("talepNo");
    //         }

    //         if (talepNo == null) {
    //             // Talep iptal edilmiş veya hata oluşmuş
    //             return stepContext.endDialog();
    //         }

    //         Activity successMessage = MessageFactory.text(String.format(
    //                 "✅ Talebiniz başarıyla oluşturuldu!\n\n🔢 Takip Numaranız: %s\n\n📱 Talebinizi web sitemizden veya mobil uygulamamızdan takip edebilirsiniz.", talepNo));

    //         return stepContext.getContext().sendActivity(successMessage)
    //                 .thenCompose(sendResult -> stepContext.endDialog());

    //     } catch (Exception ex) {
    //         return stepContext.getContext().sendActivity(MessageFactory.text("İşleminiz tamamlanamadı. Lütfen tekrar deneyin."))
    //                 .thenCompose(sendResult -> stepContext.endDialog());
    //     }
    // }

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
            System.out.println("userMessage: " + userMessage);
            return dialogContext.continueDialog()
                    .thenCompose(dialogTurnResult -> {
                        if (dialogTurnResult.getStatus() == DialogTurnStatus.EMPTY ||
                                dialogTurnResult.getStatus() == DialogTurnStatus.COMPLETE) {
                            return handleUserMessage(turnContext, userMessage);
                          //  return dialogContext.beginDialog("menuDialog");
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
            turnContext.sendActivity(MessageFactory.text("Bir hata oluştu. Lütfen tekrar deneyin.")).join();
            return CompletableFuture.completedFuture(null);
        }
    }

    private CompletableFuture<DialogTurnResult> handleUserMessage(TurnContext turnContext, String userMessage) {
        try {
            DialogContext dialogContext = dialogs.createContext(turnContext).join();

            // Kullanıcı mesajına göre seçenek bulma
            MenuOptionInterface selectedOption = findMenuOption(userMessage);

            if (selectedOption != null) {
                return handleSelectedOption(dialogContext, selectedOption);
            }

            // Intent tespiti
            String detectedIntent = intentService.detectIntent(userMessage);
            return handleDetectedIntent(dialogContext, detectedIntent);

        } catch (Exception ex) {
            logger.error("Kullanıcı mesajı işlenirken hata oluştu", ex);
            return turnContext.sendActivity(MessageFactory.text("Bir hata oluştu. Lütfen tekrar deneyin."))
                    .thenCompose(result -> CompletableFuture.completedFuture(null));
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
            return dialogContext.beginDialog(MENU_DIALOG_ID);
        }

        switch (intent) {
            case "LastUnpaidBillIntent":
                return handleLastUnpaidBill(userId, dialogContext);
            case "AllUnpaidBillsIntent":
                return handleAllUnpaidBills(userId, dialogContext);
            case "PaidBillsIntent":
                return handlePaidBills(userId, dialogContext);
            case "EnergySavingTipsIntent":
                return showEnergySavingTips(dialogContext);
            case "ConsumptionAnalysisIntent":
                return handleConsumptionAnalysis(userId, dialogContext);
            case "SupportRequestIntent":
                return handleSupportRequest(userId, dialogContext);
            default:
                return dialogContext.getContext().sendActivity(MessageFactory.text("Anlayamadım, lütfen tekrar deneyin."))
                        .thenCompose(result -> dialogContext.beginDialog(MENU_DIALOG_ID));
        }
    }

    private CompletableFuture<DialogTurnResult> handleLastUnpaidBill(Long userId, DialogContext dialogContext) {
        return billService.getUnpaidBillsByUserId(userId)
                .thenCompose(bills -> {
                    if (!bills.isEmpty()) {
                        Bill lastBill = bills.get(0);
                        return dialogContext.getContext().sendActivity(MessageFactory.text("Son ödenmemiş faturanız: " + lastBill.getBillNumber() + " - " + lastBill.getAmount() + " TL"))
                                .thenCompose(result -> dialogContext.beginDialog(MENU_DIALOG_ID));
                    } else {
                        return dialogContext.getContext().sendActivity(MessageFactory.text("Ödenmemiş faturanız bulunmamaktadır."))
                                .thenCompose(result -> dialogContext.beginDialog(MENU_DIALOG_ID));
                    }
                });
    }

    private CompletableFuture<DialogTurnResult> handleAllUnpaidBills(Long userId, DialogContext dialogContext) {
        return billService.getUnpaidBillsByUserId(userId)
                .thenCompose(bills -> {
                    StringBuilder response = new StringBuilder("Ödenmemiş faturalarınız:\n");
                    if (!bills.isEmpty()) {
                        for (Bill bill : bills) {
                            response.append(bill.getBillNumber()).append(" - ").append(bill.getAmount()).append(" TL\n");
                        }
                    } else {
                        response.append("Ödenmemiş faturanız bulunmamaktadır.");
                    }
                    return dialogContext.getContext().sendActivity(MessageFactory.text(response.toString()))
                            .thenCompose(result -> dialogContext.beginDialog(MENU_DIALOG_ID));
                });
    }

    private CompletableFuture<DialogTurnResult> handlePaidBills(Long userId, DialogContext dialogContext) {
        return paymentService.getAllPaymentsByUserId(userId)
                .thenCompose(payments -> {
                    StringBuilder response = new StringBuilder("Ödenmiş faturalarınız:\n");
                    if (!payments.isEmpty()) {
                        for (Payment payment : payments) {
                            response.append("Fatura No: ").append(payment.getBillId()).append(" - ").append(payment.getAmountPaid()).append(" TL\n");
                        }
                    } else {
                        response.append("Ödenmiş faturanız bulunmamaktadır.");
                    }
                    return dialogContext.getContext().sendActivity(MessageFactory.text(response.toString()))
                            .thenCompose(result -> dialogContext.beginDialog(MENU_DIALOG_ID));
                });
    }

    private CompletableFuture<DialogTurnResult> showEnergySavingTips(DialogContext dialogContext) {
        String tips = "Enerji tasarrufu ipuçları:\n\n" +
                      "1. Aydınlatmada LED ampuller kullanın.\n" +
                      "2. Elektrikli cihazları bekleme modunda bırakmayın.\n" +
                      "3. Klimaları 24-26°C arasında kullanın.\n" +
                      "4. Buzdolabınızı güneş almayan bir yere yerleştirin.\n" +
                      "5. Çamaşır makinesini tam dolu çalıştırın.";
        return dialogContext.getContext().sendActivity(MessageFactory.text(tips))
                .thenCompose(result -> dialogContext.beginDialog(MENU_DIALOG_ID));
    }

    private CompletableFuture<DialogTurnResult> handleConsumptionAnalysis(Long userId, DialogContext dialogContext) {
        return energyConsumptionService.getEnergyConsumptionsByUserId(userId)
                .thenCompose(consumptions -> {
                    StringBuilder response = new StringBuilder("Enerji tüketim analiziniz:\n");
                    if (!consumptions.isEmpty()) {
                        for (EnergyConsumption consumption : consumptions) {
                            response.append("Dönem: ").append(consumption.getPeriod()).append(" - Tüketim: ").append(consumption.getConsumptionAmount()).append(" kWh\n");
                        }
                    } else {
                        response.append("Enerji tüketim veriniz bulunmamaktadır.");
                    }
                    return dialogContext.getContext().sendActivity(MessageFactory.text(response.toString()))
                            .thenCompose(result -> dialogContext.beginDialog(MENU_DIALOG_ID));
                });
    }

    private CompletableFuture<DialogTurnResult> handleSupportRequest(Long userId, DialogContext dialogContext) {
        SupportRequest supportRequest = new SupportRequest();
        supportRequest.setUserId(userId);
        supportRequest.setRequestType("General Inquiry");
        supportRequest.setDescription("Destek talebi alındı.");
        supportRequestService.saveSupportRequest(supportRequest);
        return dialogContext.getContext().sendActivity(MessageFactory.text("Destek talebiniz alındı. En kısa sürede dönüş yapılacaktır."))
                .thenCompose(result -> dialogContext.beginDialog(MENU_DIALOG_ID));
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








    private CompletableFuture<DialogTurnResult> showFaturaOptionsStep(WaterfallStepContext stepContext) {
        List<Choice> choices = Arrays.stream(FaturaOption.values())
        .map(option -> new Choice(option.getDisplayText()))
        .collect(Collectors.toList());

        PromptOptions promptOptions = new PromptOptions();
        promptOptions.setPrompt(MessageFactory.text("Fatura işlemleriniz için hangi seçeneği tercih edersiniz?"));
        promptOptions.setChoices(choices);

        return stepContext.prompt("faturaPrompt", promptOptions);
    }

    private CompletableFuture<DialogTurnResult> handleFaturaSelectionStep(WaterfallStepContext stepContext) {
        try {
            FoundChoice choice = (FoundChoice) stepContext.getResult();
            String selection = choice.getValue();

            if (selection.equals(FaturaOption.FATURA_SORGULA.getDisplayText())) {
                return stepContext.replaceDialog(FATURA_SORGULAMA_DIALOG_ID);
            } else if (selection.equals(FaturaOption.FATURA_ODE.getDisplayText())) {
                String faturaDetay = String.format(
                        "Fatura Detayları:\nDönem: Mart 2024\nTutar: 856,75 TL\nSon Ödeme: 25.03.2024\nDurum: Ödenmemiş\n\nÖdemek ister misiniz?"
                );

                PromptOptions promptOptions = new PromptOptions();
                promptOptions.setPrompt(MessageFactory.text(faturaDetay));
                return stepContext.prompt("confirmPrompt", promptOptions);
            } else if (selection.equals(FaturaOption.GERI.getDisplayText())) {
                return stepContext.endDialog();
            }

            return stepContext.next(null);

        } catch (Exception ex) {
            logger.error("Error in handleFaturaSelectionStep", ex);
            return stepContext.endDialog();
        }
    }

    private CompletableFuture<DialogTurnResult> confirmFaturaOdemeStep(WaterfallStepContext stepContext) {
        boolean odemeOnaylandi = (boolean) stepContext.getResult();

        if (odemeOnaylandi) {
            String odemeUrl = "https://odeme.example.com/fatura/12345";
            String message = String.format("Ödeme sayfasına yönlendiriliyorsunuz...\n%s", odemeUrl);
            return stepContext.getContext().sendActivity(MessageFactory.text(message))
                    .thenCompose(result -> stepContext.endDialog());
        } else {
            return stepContext.getContext().sendActivity(MessageFactory.text("İşlem iptal edildi. Ana menüye dönülüyor..."))
                    .thenCompose(result -> stepContext.endDialog());
        }
    }



    // Modify handleFaturaSorgulamaStep to include suggested actions

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
    }
}