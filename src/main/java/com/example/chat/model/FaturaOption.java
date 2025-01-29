package com.example.chat.model;

public enum FaturaOption implements  DialogMenuOption {
    FATURA_SORGULA("Fatura Sorgula","faturaSorgulamaDialog",DialogType.MENU_DIALOG),
    FATURA_ODE("Fatura Öde","faturaOdemeDialog",DialogType.MENU_DIALOG),
    GERI("Ana Menü","menuDialog",DialogType.MENU_DIALOG);


    private final String displayText;
    private final String dialogId;
    private final DialogType dialogType;

    FaturaOption(String displayText, String dialogId, DialogType dialogType) {
        this.displayText = displayText;
        this.dialogId = dialogId;
        this.dialogType = dialogType;
    }


    public String getDisplayText() {
        return displayText;
    }

    public String getDialogId() {
        return dialogId;
    }

    public DialogType getDialogType() {
        return dialogType;
    }
}
