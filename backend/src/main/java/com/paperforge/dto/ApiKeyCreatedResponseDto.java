package com.paperforge.dto;

public class ApiKeyCreatedResponseDto {

    private ApiKeyResponseDto apiKey;
    private String rawSecret;

    public ApiKeyCreatedResponseDto() {}

    public ApiKeyCreatedResponseDto(ApiKeyResponseDto apiKey, String rawSecret) {
        this.apiKey = apiKey;
        this.rawSecret = rawSecret;
    }

    public ApiKeyResponseDto getApiKey() { return apiKey; }
    public void setApiKey(ApiKeyResponseDto apiKey) { this.apiKey = apiKey; }

    public String getRawSecret() { return rawSecret; }
    public void setRawSecret(String rawSecret) { this.rawSecret = rawSecret; }
}
