package com.paperforge.dto;

import com.paperforge.model.AuditEventType;

import java.time.LocalDateTime;

public class AuditLogResponseDto {

    private Long id;
    private LocalDateTime timestamp;
    private AuditEventType eventType;
    private String username;
    private String clientIp;
    private String userAgent;
    private String resourceId;
    private String detailsJson;
    private boolean success;

    public AuditLogResponseDto() {}

    public AuditLogResponseDto(Long id, LocalDateTime timestamp, AuditEventType eventType, String username,
                              String clientIp, String userAgent, String resourceId, String detailsJson, boolean success) {
        this.id = id;
        this.timestamp = timestamp;
        this.eventType = eventType;
        this.username = username;
        this.clientIp = clientIp;
        this.userAgent = userAgent;
        this.resourceId = resourceId;
        this.detailsJson = detailsJson;
        this.success = success;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public AuditEventType getEventType() { return eventType; }
    public void setEventType(AuditEventType eventType) { this.eventType = eventType; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getClientIp() { return clientIp; }
    public void setClientIp(String clientIp) { this.clientIp = clientIp; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public String getResourceId() { return resourceId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }

    public String getDetailsJson() { return detailsJson; }
    public void setDetailsJson(String detailsJson) { this.detailsJson = detailsJson; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
}
