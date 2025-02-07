package com.example.chat.model.menus;

import com.example.chat.constants.CentralizedConstants;
import lombok.Getter;

@Getter
public enum TalepTipi implements DialogMenuOption {
    ARIZA("Arıza Bildirimi", CentralizedConstants.TALEP_PROMPT, DialogType.REQUEST_DIALOG),
    BAGLANTI("Yeni Bağlantı", CentralizedConstants.TALEP_PROMPT, DialogType.REQUEST_DIALOG),
    SAYAC("Sayaç İşlemleri", CentralizedConstants.TALEP_PROMPT, DialogType.REQUEST_DIALOG),
    GERI("Ana Menü", CentralizedConstants.MENU_DIALOG_ID, DialogType.REQUEST_DIALOG);

    private final String displayText;
    private final String dialogId;
    private final DialogType dialogType;

    TalepTipi(String displayText, String dialogId, DialogType dialogType) {
        this.dialogId = dialogId;
        this.displayText = displayText;
        this.dialogType = dialogType;
    }
}
