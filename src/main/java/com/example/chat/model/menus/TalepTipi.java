package com.example.chat.model.menus;

import lombok.Getter;

@Getter
public enum TalepTipi implements MenuOptionInterface {
    ARIZA("Arıza Bildirimi", DialogType.REQUEST_DIALOG),
    BAGLANTI("Yeni Bağlantı", DialogType.REQUEST_DIALOG),
    SAYAC("Sayaç İşlemleri", DialogType.REQUEST_DIALOG),
    GERI("Ana Menü", DialogType.REQUEST_DIALOG);

    private final String displayText;
    private final DialogType dialogType;

    TalepTipi(String displayText, DialogType dialogType) {
        this.displayText = displayText;
        this.dialogType = dialogType;
    }

    public static TalepTipi fromDisplayText(String displayText) {
        for (TalepTipi talepTipi : TalepTipi.values()) {
            if (talepTipi.getDisplayText().equalsIgnoreCase(displayText)) {
                return talepTipi;
            }
        }
        throw new IllegalArgumentException("Geçersiz talep tipi: " + displayText);
    }


}
