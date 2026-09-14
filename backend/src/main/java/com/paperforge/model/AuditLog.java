package com.paperforge.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "audit_logs",
        indexes = {
                @Index(name = "idx_audit_logs_timestamp", columnList = "timestamp"),
                @Index(name = "idx_audit_logs_event_type", columnList = "event_type"),
                @Index(name = "idx_audit_logs_username", columnList = "username"),
                @Index(name = "idx_audit_logs_client_ip", columnList = "client_ip"),
                @Index(name = "idx_audit_logs_success", columnList = "success")
        }
)
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 50)
    private AuditEventType eventType;

    @Column(length = 50)
    private String username;

    @Column(name = "client_ip", length = 45)
    private String clientIp;

    @Column(name = "user_agent", length = 255)
    private String userAgent;

    @Column(name = "resource_id", length = 100)
    private String resourceId;

    @Column(name = "details_json", columnDefinition = "TEXT")
    private String detailsJson;

    private boolean success = true;

    public AuditLog() {}

    public AuditLog(AuditEventType eventType, String username, String clientIp, String userAgent,
                    String resourceId, String detailsJson, boolean success) {
        this.timestamp = LocalDateTime.now();
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
