// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.example.chat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

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
    private DialogSet dialogs;
    private ConversationState conversationState;
    private UserState userState;
    private static final String DEFAULT_USER = "Değerli Müşterimiz";
    
    public EchoBot(ConversationState withConversationState, UserState withUserState) {
        conversationState = withConversationState;
        userState = withUserState;
        
        StatePropertyAccessor<DialogState> dialogStateAccessor = 
            conversationState.createProperty("DialogState");
        
        dialogs = new DialogSet(dialogStateAccessor);
        
        WaterfallStep[] menuSteps = new WaterfallStep[] {
            this::showMenuStep,
            this::handleMenuSelectionStep,
            this::finalStep
        };
        
        WaterfallStep[] talepSteps = new WaterfallStep[] {
            this::showTalepTipiStep,
            this::handleTalepTipiStep,
            this::getTalepDetayStep,
            this::handleTalepDetayStep,
            this::confirmTalepStep,
            this::processTalepStep
        };
        
        WaterfallStep[] faturaSteps = new WaterfallStep[] {
            this::showFaturaOptionsStep,
            this::handleFaturaSelectionStep,
            this::confirmFaturaOdemeStep,
            this::finalStep
        };
        
        dialogs.add(new WaterfallDialog("menuDialog", Arrays.asList(menuSteps)));
        dialogs.add(new WaterfallDialog("talepDialog", Arrays.asList(talepSteps)));
        dialogs.add(new WaterfallDialog("faturaDialog", Arrays.asList(faturaSteps)));
        dialogs.add(new ChoicePrompt("menuPrompt"));
        dialogs.add(new ChoicePrompt("talepPrompt"));
        dialogs.add(new ChoicePrompt("faturaPrompt"));
        dialogs.add(new TextPrompt("detayPrompt"));
        dialogs.add(new ConfirmPrompt("confirmPrompt"));
    }

    private CompletableFuture<DialogTurnResult> showMenuStep(WaterfallStepContext stepContext) {
        List<Choice> choices = Arrays.asList(
            new Choice(MenuOption.FATURA_ISLEMLERI.getDisplayText()),
            new Choice(MenuOption.TALEP_SIKAYET.getDisplayText())
        );
        
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
        
        return stepContext.prompt("menuPrompt", promptOptions);
    }

    private CompletableFuture<DialogTurnResult> handleMenuSelectionStep(WaterfallStepContext stepContext) {
        FoundChoice choice = (FoundChoice) stepContext.getResult();
        String selection = choice.getValue();
        
        if (selection.equals(MenuOption.FATURA_ISLEMLERI.getDisplayText())) {
            return stepContext.beginDialog("faturaDialog");
        } else if (selection.equals(MenuOption.TALEP_SIKAYET.getDisplayText())) {
            return stepContext.beginDialog("talepDialog");
        }
        
        return stepContext.endDialog();
    }

    private CompletableFuture<DialogTurnResult> showTalepTipiStep(WaterfallStepContext stepContext) {
        List<Choice> choices = new ArrayList<>();
        for (TalepTipi tip : TalepTipi.values()) {
            choices.add(new Choice(tip.getDisplayText()));
        }
        
        Activity menuMessage = MessageFactory.text("Lütfen talep tipini seçin:");
        menuMessage.setSuggestedActions(new SuggestedActions() {{
            setActions(choices.stream()
                .map(choice -> new CardAction() {{
                    setTitle(choice.getValue());
                    setValue(choice.getValue());
                    setType(ActionTypes.POST_BACK);
                }})
                .collect(Collectors.toList()));
        }});
        
        PromptOptions promptOptions = new PromptOptions();
        promptOptions.setPrompt(menuMessage);
        promptOptions.setChoices(choices);
        
        return stepContext.prompt("talepPrompt", promptOptions);
    }

    private CompletableFuture<DialogTurnResult> handleTalepTipiStep(WaterfallStepContext stepContext) {
        FoundChoice choice = (FoundChoice) stepContext.getResult();
        String selection = choice.getValue();
        
        if (selection.equals(TalepTipi.GERI.getDisplayText())) {
            return stepContext.endDialog();
        }
        
        stepContext.getValues().put("talepTipi", selection);
        return stepContext.next(selection);
    }

    private CompletableFuture<DialogTurnResult> getTalepDetayStep(WaterfallStepContext stepContext) {
        String talepTipi = (String) stepContext.getValues().get("talepTipi");
        String promptText = "";
        
        if (talepTipi.equals(TalepTipi.ARIZA.getDisplayText())) {
            promptText = "Lütfen arıza ile ilgili detaylı bilgi verin (konum, sorun türü vb.):";
        } else if (talepTipi.equals(TalepTipi.BAGLANTI.getDisplayText())) {
            promptText = "Lütfen yeni bağlantı için adres ve iletişim bilgilerinizi girin:";
        } else if (talepTipi.equals(TalepTipi.SAYAC.getDisplayText())) {
            promptText = "Lütfen sayaç işleminizi detaylandırın:";
        }
        
        PromptOptions promptOptions = new PromptOptions();
        promptOptions.setPrompt(MessageFactory.text(promptText));
        return stepContext.prompt("detayPrompt", promptOptions);
    }

    private CompletableFuture<DialogTurnResult> handleTalepDetayStep(WaterfallStepContext stepContext) {
        String detay = (String) stepContext.getResult();
        stepContext.getValues().put("talepDetay", detay);
        
        String talepTipi = (String) stepContext.getValues().get("talepTipi");
        String onayMesaji = String.format(
            "Talebinizi onaylıyor musunuz?\n\nTalep Tipi: %s\nDetay: %s",
            talepTipi,
            detay
        );
        
        PromptOptions promptOptions = new PromptOptions();
        promptOptions.setPrompt(MessageFactory.text(onayMesaji));
        return stepContext.prompt("confirmPrompt", promptOptions);
    }

    private CompletableFuture<DialogTurnResult> confirmTalepStep(WaterfallStepContext stepContext) {
        boolean onaylandi = (boolean) stepContext.getResult();
        
        if (onaylandi) {
            String talepNo = String.format("T%d", (int)(Math.random() * 100000));
            String successMessage = String.format("Talebiniz başarıyla oluşturuldu!\nTakip Numaranız: %s", talepNo);
            return stepContext.getContext().sendActivity(MessageFactory.text(successMessage))
                .thenCompose(result -> stepContext.next(talepNo));
        } else {
            return stepContext.getContext().sendActivity(MessageFactory.text("Talep iptal edildi."))
                .thenCompose(result -> stepContext.endDialog());
        }
    }

    private CompletableFuture<DialogTurnResult> processTalepStep(WaterfallStepContext stepContext) {
        try {
            String talepNo = null;
            Object stepResult = stepContext.getResult();
            
            if (stepResult instanceof String) {
                talepNo = (String) stepResult;
            } else {
                talepNo = (String) stepContext.getValues().get("talepNo");
            }
            
            if (talepNo == null) {
                // Talep iptal edilmiş veya hata oluşmuş
                return stepContext.endDialog();
            }
            
            Activity successMessage = MessageFactory.text(String.format(
                "✅ Talebiniz başarıyla oluşturuldu!\n\n🔢 Takip Numaranız: %s\n\n📱 Talebinizi web sitemizden veya mobil uygulamamızdan takip edebilirsiniz.", talepNo));
            
            return stepContext.getContext().sendActivity(successMessage)
                .thenCompose(sendResult -> stepContext.endDialog());
                
        } catch (Exception ex) {
            return stepContext.getContext().sendActivity(MessageFactory.text("İşleminiz tamamlanamadı. Lütfen tekrar deneyin."))
                .thenCompose(sendResult -> stepContext.endDialog());
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
                return dialogContext.beginDialog("menuDialog")
                    .thenApply(dialogResult -> null);
            });
    }

    @Override
    protected CompletableFuture<Void> onMessageActivity(TurnContext turnContext) {
        try {
            DialogContext dialogContext = dialogs.createContext(turnContext).join();
            
            return dialogContext.continueDialog()
                .thenCompose(dialogTurnResult -> {
                    if (dialogTurnResult.getStatus() == DialogTurnStatus.EMPTY ||
                        dialogTurnResult.getStatus() == DialogTurnStatus.COMPLETE) {
                        return dialogContext.beginDialog("menuDialog");
                    }
                    return CompletableFuture.completedFuture(dialogTurnResult);
                })
                .thenCompose(result -> conversationState.saveChanges(turnContext))
                .thenCompose(result -> userState.saveChanges(turnContext))
                .exceptionally(ex -> {
                    turnContext.sendActivity(MessageFactory.text("Bir hata oluştu. Ana menüye yönlendiriliyorsunuz...")).join();
                    dialogContext.beginDialog("menuDialog").join();
                    return null;
                });
                
        } catch (Exception ex) {
            turnContext.sendActivity(MessageFactory.text("Bir hata oluştu. Lütfen tekrar deneyin.")).join();
            return CompletableFuture.completedFuture(null);
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
                setValue("Ana Menüye Dön 🔙");
                setType(ActionTypes.POST_BACK);
            }}
        ));
        
        return card.toAttachment();
    }

    // Menü seçenekleri için enum
    private enum MenuOption {
        FATURA_ISLEMLERI("Fatura İşlemleri 💰"),
        TALEP_SIKAYET("Talep/Şikayet 📨");

        private final String displayText;

        MenuOption(String displayText) {
            this.displayText = displayText;
        }

        public String getDisplayText() {
            return displayText;
        }
    }

    // Alt menü seçenekleri için enum
    private enum FaturaOption {
        FATURA_SORGULA("Fatura Sorgula"),
        FATURA_ODE("Fatura Öde"),
        GERI("Ana Menü");

        private final String displayText;

        FaturaOption(String displayText) {
            this.displayText = displayText;
        }

        public String getDisplayText() {
            return displayText;
        }
    }

    private enum TalepTipi {
        ARIZA("Arıza Bildirimi"),
        BAGLANTI("Yeni Bağlantı"),
        SAYAC("Sayaç İşlemleri"),
        GERI("Ana Menü");

        private final String displayText;

        TalepTipi(String displayText) {
            this.displayText = displayText;
        }

        public String getDisplayText() {
            return displayText;
        }
    }

    private enum BillingOption {
        SON_FATURA("📄 Son Fatura"),
        SON_ODEME("💰 Son Ödeme"),
        GECMIS_FATURALAR("📋 Geçmiş Faturalar");

        private final String displayText;

        BillingOption(String displayText) {
            this.displayText = displayText;
        }

        public String getDisplayText() {
            return displayText;
        }

        public static BillingOption fromDisplayText(String text) {
            for (BillingOption option : values()) {
                if (option.getDisplayText().equals(text)) {
                    return option;
                }
            }
            throw new IllegalArgumentException("Invalid display text: " + text);
        }
    }

    private CompletableFuture<DialogTurnResult> showFaturaOptionsStep(WaterfallStepContext stepContext) {
        List<Choice> choices = Arrays.asList(
            new Choice(FaturaOption.FATURA_SORGULA.getDisplayText()),
            new Choice(FaturaOption.FATURA_ODE.getDisplayText()),
            new Choice(FaturaOption.GERI.getDisplayText())
        );
        
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
                String faturaDetay = "Son Ödenmemiş Fatura:\nDönem: Mart 2024\nTutar: 856,75 TL\nSon Ödeme: 25.03.2024\nDurum: Ödenmemiş";
                return stepContext.getContext().sendActivity(MessageFactory.text(faturaDetay))
                    .thenCompose(result -> stepContext.endDialog());
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
}

