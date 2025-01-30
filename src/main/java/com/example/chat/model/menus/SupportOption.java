package com.example.chat.model.menus;

public enum SupportOption implements DialogMenuOption {
    GENERAL_INQUIRY("📝 Genel Bilgi", "generalInquiryDialog", DialogType.MENU_DIALOG),
    TECHNICAL_SUPPORT("🔧 Teknik Destek", "technicalSupportDialog", DialogType.MENU_DIALOG),
    COMPLAINT("⚠️ Şikayet", "complaintDialog", DialogType.MENU_DIALOG),
    GERI("🔙 Ana Menü", "menuDialog", DialogType.MENU_DIALOG);

    private final String displayText;
    private final String dialogId;
    private final DialogType dialogType;

    SupportOption(String displayText, String dialogId, DialogType dialogType) {
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