package com.example.chat.model;

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

    public static BillingOption fromDisplayText(String text) {
        for (BillingOption option : values()) {
            if (option.getDisplayText().equals(text)) {
                return option;
            }
        }
        throw new IllegalArgumentException("Invalid display text: " + text);
    }

    @Override
    public String getDialogId() {
        return "";
    }
}
