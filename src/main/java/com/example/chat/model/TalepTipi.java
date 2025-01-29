package com.example.chat.model;

public enum TalepTipi implements DialogMenuOption {
    ARIZA("Arıza Bildirimi"),
    BAGLANTI("Yeni Bağlantı"),
    SAYAC("Sayaç İşlemleri"),
    GERI("Ana Menü");


    private final String displayText;

    TalepTipi(String displayText) {
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
