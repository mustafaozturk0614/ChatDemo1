package com.example.chat.model;

public enum FaturaSorgulamaOption implements IntentMenuOption {
    SON_ODENMEMIS_FATURA("Son Ödenmemiş Fatura 📄", "LastUnpaidBillIntent", DialogType.INTENT_DIALOG),
    TUM_ODENMEMIS_FATURALAR("Tüm Ödenmemiş Faturalar 📑", "AllUnpaidBillsIntent", DialogType.INTENT_DIALOG),
    ODENMIS_FATURALAR("Ödenmiş Faturalar ✅", "PaidBillsIntent", DialogType.INTENT_DIALOG),
    GERI_DON("Geri Dön 🔙", "None", DialogType.MENU_DIALOG);


    private final String displayText;
    private final String intentName;
    private final DialogType dialogType;

    FaturaSorgulamaOption(String displayText, String intentName, DialogType dialogType) {
        this.displayText = displayText;
        this.intentName = intentName;
        this.dialogType = dialogType;
    }

    public String getDisplayText() {
        return displayText;
    }

    public String getIntentName() {
        return intentName;
    }

    public DialogType getDialogType() {
        return dialogType;
    }

    public static FaturaSorgulamaOption fromIntent(String intent) {
        for (FaturaSorgulamaOption option : values()) {
            if (option.getIntentName().equals(intent)) {
                return option;
            }
        }
        return GERI_DON; // Eğer intent bulunamazsa geri dön
    }
}
