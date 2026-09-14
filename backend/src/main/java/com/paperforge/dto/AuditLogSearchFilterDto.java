package com.paperforge.dto;

import com.paperforge.model.AuditEventType;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class AuditLogSearchFilterDto {

    private AuditEventType eventType;
    private String username;
    private String clientIp;
    private Boolean success;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDate;

    private String query;

    public AuditLogSearchFilterDto() {}

    public AuditEventType getEventType() { return eventType; }
    public void setEventType(AuditEventType eventType) { this.eventType = eventType; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getClientIp() { return clientIp; }
    public void setClientIp(String clientIp) { this.clientIp = clientIp; }

    public Boolean getSuccess() { return success; }
    public void setSuccess(Boolean success) { this.success = success; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
}
