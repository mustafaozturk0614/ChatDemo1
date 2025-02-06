package com.example.chat.model.menus;

public interface IntentMenuOption<F> extends MenuOptionInterface {
    String getIntentName();

    String getDialogId();
}