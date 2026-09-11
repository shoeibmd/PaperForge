package com.paperforge.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OcrValidationUtils {

    private static final Set<String> ALLOWED_LANGUAGES;

    static {
        Set<String> langs = new HashSet<>();
        langs.add("eng");
        langs.add("spa");
        langs.add("fra");
        langs.add("deu");
        langs.add("ita");
        langs.add("por");
        langs.add("nld");
        langs.add("rus");
        langs.add("chi_sim");
        langs.add("chi_tra");
        langs.add("jpn");
        langs.add("kor");
        langs.add("ara");
        langs.add("hin");
        ALLOWED_LANGUAGES = Collections.unmodifiableSet(langs);
    }

    public static void validateLanguages(List<String> languages) {
        if (languages == null || languages.isEmpty()) {
            return;
        }

        for (String lang : languages) {
            if (lang == null || !ALLOWED_LANGUAGES.contains(lang.trim().toLowerCase())) {
                throw new IllegalArgumentException("Unsupported or invalid OCR language code: " + lang);
            }
        }
    }

    public static String buildLanguageParam(List<String> languages) {
        if (languages == null || languages.isEmpty()) {
            return "eng";
        }
        validateLanguages(languages);
        return String.join("+", languages.stream().map(String::trim).map(String::toLowerCase).toList());
    }
}
