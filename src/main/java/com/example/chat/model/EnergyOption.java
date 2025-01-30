package com.example.chat.model;

public enum EnergyOption implements DialogMenuOption {
    CONSUMPTION_ANALYSIS("📊 Tüketim Analizi", "consumptionAnalysisDialog", DialogType.MENU_DIALOG),
    SAVING_TIPS("💡 Tasarruf İpuçları", "savingTipsDialog", DialogType.MENU_DIALOG),
    USAGE_COMPARISON("📈 Kullanım Karşılaştırma", "usageComparisonDialog", DialogType.MENU_DIALOG),
    GERI("🔙 Ana Menü", "menuDialog", DialogType.MENU_DIALOG);

    private final String displayText;
    private final String dialogId;
    private final DialogType dialogType;

    EnergyOption(String displayText, String dialogId, DialogType dialogType) {
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