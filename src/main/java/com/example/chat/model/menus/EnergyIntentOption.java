package com.example.chat.model.menus;

public enum EnergyIntentOption implements IntentMenuOption {
    CONSUMPTION_ANALYSIS("📊 Tüketim Analizi", "ConsumptionAnalysisIntent", DialogType.INTENT_DIALOG),
    SAVING_TIPS("💡 Tasarruf İpuçları", "EnergySavingTipsIntent", DialogType.INTENT_DIALOG),
    USAGE_COMPARISON("📈 Kullanım Karşılaştırma", "UsageComparisonIntent", DialogType.INTENT_DIALOG),
    GERI("🔙 Geri", "None", DialogType.INTENT_DIALOG);

    private final String displayText;
    private final String intentName;
    private final DialogType dialogType;

    EnergyIntentOption(String displayText, String intentName, DialogType dialogType) {
        this.displayText = displayText;
        this.intentName = intentName;
        this.dialogType = dialogType;
    }

    @Override
    public String getDisplayText() {
        return displayText;
    }

    @Override
    public String getIntentName() {
        return intentName;
    }

    @Override
    public String getDialogId() {
        return "";
    }

    @Override
    public DialogType getDialogType() {
        return dialogType;
    }


} 