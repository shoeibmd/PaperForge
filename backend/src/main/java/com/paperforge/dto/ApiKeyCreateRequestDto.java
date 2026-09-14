package com.paperforge.dto;

import java.time.LocalDateTime;
import java.util.Set;

public class ApiKeyCreateRequestDto {

    private String name;
    private Set<String> scopes;
    private LocalDateTime expiresAt;

    public ApiKeyCreateRequestDto() {}

    public ApiKeyCreateRequestDto(String name, Set<String> scopes, LocalDateTime expiresAt) {
        this.name = name;
        this.scopes = scopes;
        this.expiresAt = expiresAt;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Set<String> getScopes() { return scopes; }
    public void setScopes(Set<String> scopes) { this.scopes = scopes; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
}
