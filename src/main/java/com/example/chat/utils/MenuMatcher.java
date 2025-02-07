package com.example.chat.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.example.chat.constants.CentralizedConstants;
import com.example.chat.model.menus.EnergyIntentOption;
import com.example.chat.model.menus.FaturaOption;
import com.example.chat.model.menus.FaturaSorgulamaOption;
import com.example.chat.model.menus.IntentMenuOption;
import com.example.chat.model.menus.MenuOption;
import com.example.chat.model.menus.MenuOptionInterface;
import com.example.chat.model.menus.SupportOption;
import com.example.chat.model.menus.TalepTipi;
import com.microsoft.bot.dialogs.DialogContext;

public class MenuMatcher {
    private static final Map<String, Map<String, MenuOptionInterface>> DIALOG_OPTIONS = new HashMap<>();
    private static final Map<String, String> PROMPTMAP = new HashMap<>();

    static {
        // Her diyalog için menü seçeneklerini yükle
        loadDialogOptions("menuDialog", MenuOption.values());
        loadDialogOptions("faturaDialog", FaturaOption.values());
        loadDialogOptions("faturaSorgulamaDialog", FaturaSorgulamaOption.values());
        loadDialogOptions("energyDialog", EnergyIntentOption.values());
        loadDialogOptions("supportDialog", SupportOption.values());
        loadDialogOptions("talepDialog", TalepTipi.values());

        PROMPTMAP.put(CentralizedConstants.MENU_DIALOG_ID,CentralizedConstants.MENU_PROMPT);
        PROMPTMAP.put(CentralizedConstants.SUPPORT_DIALOG_ID,CentralizedConstants.SUPPORT_PROMPT);
        PROMPTMAP.put(CentralizedConstants.TALEP_DIALOG_ID,CentralizedConstants.TALEP_PROMPT);
        PROMPTMAP.put(CentralizedConstants.FATURA_DIALOG_ID,CentralizedConstants.FATURA_PROMPT);
        PROMPTMAP.put(CentralizedConstants.FATURA_SORGULAMA_DIALOG_ID,CentralizedConstants.FATURA_SORGULAMA_PROMPT);
        PROMPTMAP.put(CentralizedConstants.ENERGY_DIALOG_ID,CentralizedConstants.ENERGY_PROMPT);
    }

    private static void loadDialogOptions(String dialogId, MenuOptionInterface[] options) {
        Map<String, MenuOptionInterface> optionMap = new HashMap<>();
        for (MenuOptionInterface option : options) {
            String key = cleanText(option.getDisplayText());
            optionMap.put(key, option);
        }
        DIALOG_OPTIONS.put(dialogId, optionMap);
    }

    public static MenuOptionInterface findOption(String userInput, DialogContext context) {
        if (userInput == null || userInput.isEmpty()) return null;
String currentDialogId = context.getActiveDialog()==null?"menuDialog":context.getActiveDialog().getId();
        String cleanInput = cleanText(userInput);

        // 1. Önce mevcut diyalogun seçeneklerinde ara
        Map<String, MenuOptionInterface> currentOptions = DIALOG_OPTIONS.get(currentDialogId);
        if (currentOptions != null) {
            MenuOptionInterface option = currentOptions.get(cleanInput);
            if (option != null) return option;
        }

        // 2. Ana menüdeyse tüm seçeneklerde ara
        if ("menuDialog".equals(currentDialogId)) {
            return DIALOG_OPTIONS.values().stream()
                    .flatMap(map -> map.entrySet().stream())
                    .filter(entry -> entry.getKey().equals(cleanInput))
                    .map(Map.Entry::getValue)
                    .findFirst()
                    .orElse(null);
        }

        return null;
    }

    public  static String  getPrompts(String dialogId){
        return PROMPTMAP.get(dialogId);

    }

    private static String cleanText(String text) {
        return text.replaceAll("[^a-zA-ZçğıöşüÇĞİÖŞÜ\\s]", "")
                .replaceAll("\\s+", " ")
                .toLowerCase(Locale.forLanguageTag("tr"))
                .trim();
    }

    public static <T extends Enum<T> & MenuOptionInterface> T fromDisplayText(String displayText, Class<T> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(option -> option.getDisplayText().equalsIgnoreCase(displayText))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Geçersiz seçim: " + displayText));
    }

    public static <T extends Enum<T> & IntentMenuOption> T fromIntent(String intent,Class<T> enumClass) {
        return   Arrays.stream(enumClass.getEnumConstants()).filter(option -> option.getIntentName().equalsIgnoreCase(intent)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Geçersiz seçim: " + intent));
    }
}