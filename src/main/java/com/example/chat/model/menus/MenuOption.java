package com.example.chat.model.menus;

public enum MenuOption implements DialogMenuOption {
    FATURA_ISLEMLERI("Fatura İşlemleri 💰", "faturaDialog", DialogType.MENU_DIALOG),
    ENERJI_YONETIMI("Enerji Yönetimi ⚡", "energyDialog", DialogType.MENU_DIALOG),
    TALEP_SIKAYET("Talep/Şikayet 📨", "talepDialog", DialogType.MENU_DIALOG),
    DESTEK("Destek 🤝", "supportDialog", DialogType.MENU_DIALOG);

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
    public static MenuOption fromDisplayText(String displayText) {
        for (MenuOption menuOption : MenuOption.values()) {
            if (menuOption.getDisplayText().equalsIgnoreCase(displayText)) {
                return menuOption;
            }
        }
        throw new IllegalArgumentException("Geçersiz menü seçeneği: " + displayText);
    }
} 