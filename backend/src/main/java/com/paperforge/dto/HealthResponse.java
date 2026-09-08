package com.paperforge.dto;

import java.time.Instant;

public class HealthResponse {
    private String status;
    private String timestamp;

    public HealthResponse() {
    }

    public HealthResponse(String status, String timestamp) {
        this.status = status;
        this.timestamp = timestamp;
    }

    public static HealthResponse up() {
        return new HealthResponse("UP", Instant.now().toString());
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
