package com.paperforge.dto;

import java.time.LocalDateTime;
import java.util.Set;

public class ApiKeyResponseDto {

    private Long id;
    private String name;
    private String keyPrefix;
    private String username;
    private boolean revoked;
    private Set<String> scopes;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime lastUsedAt;
    private long usageCount;

    public ApiKeyResponseDto() {}

    public ApiKeyResponseDto(Long id, String name, String keyPrefix, String username, boolean revoked,
                             Set<String> scopes, LocalDateTime createdAt, LocalDateTime expiresAt,
                             LocalDateTime lastUsedAt, long usageCount) {
        this.id = id;
        this.name = name;
        this.keyPrefix = keyPrefix;
        this.username = username;
        this.revoked = revoked;
        this.scopes = scopes;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.lastUsedAt = lastUsedAt;
        this.usageCount = usageCount;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getKeyPrefix() { return keyPrefix; }
    public void setKeyPrefix(String keyPrefix) { this.keyPrefix = keyPrefix; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public boolean isRevoked() { return revoked; }
    public void setRevoked(boolean revoked) { this.revoked = revoked; }

    public Set<String> getScopes() { return scopes; }
    public void setScopes(Set<String> scopes) { this.scopes = scopes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public LocalDateTime getLastUsedAt() { return lastUsedAt; }
    public void setLastUsedAt(LocalDateTime lastUsedAt) { this.lastUsedAt = lastUsedAt; }

    public long getUsageCount() { return usageCount; }
    public void setUsageCount(long usageCount) { this.usageCount = usageCount; }
}
