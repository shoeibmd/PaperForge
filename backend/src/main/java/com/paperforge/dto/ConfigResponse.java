package com.paperforge.dto;

import java.util.Map;

public class ConfigResponse {
    private long maxUploadSizeBytes;
    private Map<String, Boolean> featureFlags;
    private String defaultTheme;
    private String supportEmail;

    public ConfigResponse() {
    }

    public ConfigResponse(long maxUploadSizeBytes, Map<String, Boolean> featureFlags, String defaultTheme, String supportEmail) {
        this.maxUploadSizeBytes = maxUploadSizeBytes;
        this.featureFlags = featureFlags;
        this.defaultTheme = defaultTheme;
        this.supportEmail = supportEmail;
    }

    public long getMaxUploadSizeBytes() {
        return maxUploadSizeBytes;
    }

    public void setMaxUploadSizeBytes(long maxUploadSizeBytes) {
        this.maxUploadSizeBytes = maxUploadSizeBytes;
    }

    public Map<String, Boolean> getFeatureFlags() {
        return featureFlags;
    }

    public void setFeatureFlags(Map<String, Boolean> featureFlags) {
        this.featureFlags = featureFlags;
    }

    public String getDefaultTheme() {
        return defaultTheme;
    }

    public void setDefaultTheme(String defaultTheme) {
        this.defaultTheme = defaultTheme;
    }

    public String getSupportEmail() {
        return supportEmail;
    }

    public void setSupportEmail(String supportEmail) {
        this.supportEmail = supportEmail;
    }
}
