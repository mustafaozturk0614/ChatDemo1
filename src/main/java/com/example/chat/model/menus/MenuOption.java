package com.example.chat.model.menus;

import com.example.chat.constants.CentralizedConstants;

public enum MenuOption implements DialogMenuOption {
    FATURA_ISLEMLERI("Fatura İşlemleri 💰", CentralizedConstants.FATURA_DIALOG_ID, DialogType.MENU_DIALOG),
    ENERJI_YONETIMI("Enerji Yönetimi ⚡", CentralizedConstants.ENERGY_DIALOG_ID, DialogType.MENU_DIALOG),
    TALEP_SIKAYET("Talep/Şikayet 📨", CentralizedConstants.TALEP_DIALOG_ID, DialogType.MENU_DIALOG),
    DESTEK("Destek 🤝", CentralizedConstants.SUPPORT_DIALOG_ID, DialogType.MENU_DIALOG);

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