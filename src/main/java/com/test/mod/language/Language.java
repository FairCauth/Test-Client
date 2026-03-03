package com.test.mod.language;


public enum Language {
    English,
    Chinese;

    public static Language getLanguage() {
        return English;
    }

    public static Language getDefaultLanguage() {
        return English;
    }

    public static String getLabel(Text[] texts, Language language) {
        for (Text text : texts) {
            if (text.language().equals(language))
                return text.label();
        }
        return "";
    }
}
