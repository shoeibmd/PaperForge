package com.paperforge.dto;

import java.time.LocalDateTime;
import java.util.Set;

public class UserManagementDto {

    private Long id;
    private String username;
    private String email;
    private boolean enabled;
    private Long storageQuotaBytes;
    private LocalDateTime createdAt;
    private Set<String> roles;

    public UserManagementDto() {}

    public UserManagementDto(Long id, String username, String email, boolean enabled, Long storageQuotaBytes, LocalDateTime createdAt, Set<String> roles) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.enabled = enabled;
        this.storageQuotaBytes = storageQuotaBytes;
        this.createdAt = createdAt;
        this.roles = roles;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public Long getStorageQuotaBytes() { return storageQuotaBytes; }
    public void setStorageQuotaBytes(Long storageQuotaBytes) { this.storageQuotaBytes = storageQuotaBytes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
}
