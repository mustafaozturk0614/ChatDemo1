package com.example.chat.model;

public enum MenuOption implements DialogMenuOption {
    FATURA_ISLEMLERI("Fatura İşlemleri 💰", "faturaDialog", DialogType.MENU_DIALOG),
    TALEP_SIKAYET("Talep/Şikayet 📨", "talepDialog", DialogType.MENU_DIALOG);

    private final String displayText;
    private final String dialogId;
    private final DialogType dialogType;

    MenuOption(String displayText, String dialogId, DialogType dialogType) {
        this.displayText = displayText;
        this.dialogId = dialogId;
        this.dialogType = dialogType;
    }

    @Override
    public String getDisplayText() {
        return displayText;
    }

    @Override
    public String getDialogId() {
        return dialogId;
    }

    @Override
    public DialogType getDialogType() {
        return dialogType;
    }
} 