package com.paperforge.model;

public enum ApiKeyScope {
    PDF_READ("pdf:read"),
    PDF_WRITE("pdf:write"),
    OCR("ocr"),
    CONVERSION("conversion"),
    ADMIN("admin");

    private final String value;

    ApiKeyScope(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ApiKeyScope fromValue(String value) {
        for (ApiKeyScope scope : ApiKeyScope.values()) {
            if (scope.value.equalsIgnoreCase(value) || scope.name().equalsIgnoreCase(value)) {
                return scope;
            }
        }
        throw new IllegalArgumentException("Unknown API Key Scope: " + value);
    }
}
