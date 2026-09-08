package com.paperforge.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuditLogService {

    private static final Logger logger = LoggerFactory.getLogger(AuditLogService.class);

    public void logSecurityEvent(String eventType, String clientIp, String details) {
        String logEntry = String.format("[SECURITY AUDIT] timestamp=%s eventType=%s clientIp=%s details=%s",
                Instant.now(), eventType, clientIp != null ? clientIp : "UNKNOWN", details);
        logger.info(logEntry);
    }
}
