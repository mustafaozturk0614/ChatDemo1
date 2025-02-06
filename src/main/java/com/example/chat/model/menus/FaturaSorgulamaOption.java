package com.example.chat.model.menus;

public enum FaturaSorgulamaOption implements IntentMenuOption<FaturaSorgulamaOption> {
    SON_ODENMEMIS_FATURA("Son Ödenmemiş Fatura 📄", "LastUnpaidBillIntent", DialogType.INTENT_DIALOG),
    TUM_ODENMEMIS_FATURALAR("Tüm Ödenmemiş Faturalar 📑", "AllUnpaidBillsIntent", DialogType.INTENT_DIALOG),
    ODENMIS_FATURALAR("Ödenmiş Faturalar ✅", "PaidBillsIntent", DialogType.INTENT_DIALOG),
    GERI_DON("Geri Dön 🔙", "None", DialogType.INTENT_DIALOG);


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

    @Override
    public String getDialogId() {
        return "";
    }


}
