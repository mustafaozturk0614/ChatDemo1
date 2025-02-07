package com.example.chat.model.menus;

public enum BillingOption  implements DialogMenuOption{
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

    @Override
    public DialogType getDialogType() {
        return null;
    }



    @Override
    public String getDialogId() {
        return "";
    }
}
