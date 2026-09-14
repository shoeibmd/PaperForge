package com.paperforge.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paperforge.dto.AuditLogResponseDto;
import com.paperforge.dto.AuditLogSearchFilterDto;
import com.paperforge.model.AuditEventType;
import com.paperforge.model.AuditLog;
import com.paperforge.repository.AuditLogRepository;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class AuditLogService {

    private static final Logger logger = LoggerFactory.getLogger(AuditLogService.class);
    private static final Pattern SENSITIVE_KEY_PATTERN = Pattern.compile(
            "(?i)\"(password|secret|rawsecret|token|jwt|apikey|bearer|authorization|creditcard|ssn)\"\\s*:\\s*\"[^\"]*\""
    );

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    public AuditLogService(AuditLogRepository auditLogRepository, ObjectMapper objectMapper) {
        this.auditLogRepository = auditLogRepository;
        this.objectMapper = objectMapper;
    }

    public void logSecurityEvent(String eventType, String clientIp, String details) {
        AuditEventType type = AuditEventType.fromStringOrDefault(eventType);
        logEvent(type, null, clientIp, null, null, details, true);
    }

    @Async("auditTaskExecutor")
    public void logEventAsync(AuditEventType eventType, String username, String clientIp, String userAgent,
                              String resourceId, Object detailsObject, boolean success) {
        logEvent(eventType, username, clientIp, userAgent, resourceId, detailsObject, success);
    }

    @Transactional
    public void logEvent(AuditEventType eventType, String username, String clientIp, String userAgent,
                         String resourceId, Object detailsObject, boolean success) {
        String detailsJson = sanitizeAndToJson(detailsObject);

        AuditLog auditLog = new AuditLog(
                eventType,
                username,
                clientIp != null ? clientIp : "UNKNOWN",
                userAgent,
                resourceId,
                detailsJson,
                success
        );

        auditLogRepository.save(auditLog);

        String logSummary = String.format("[SECURITY AUDIT] timestamp=%s eventType=%s username=%s clientIp=%s details=%s",
                Instant.now(), eventType, username != null ? username : "ANONYMOUS", clientIp, detailsJson);
        logger.info(logSummary);
    }

    @Transactional(readOnly = true)
    public Page<AuditLogResponseDto> searchAuditLogs(AuditLogSearchFilterDto filter, Pageable pageable) {
        Specification<AuditLog> spec = createSpecification(filter);
        return auditLogRepository.findAll(spec, pageable).map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public List<AuditLogResponseDto> getFilteredAuditLogsForExport(AuditLogSearchFilterDto filter) {
        Specification<AuditLog> spec = createSpecification(filter);
        return auditLogRepository.findAll(spec).stream().map(this::mapToDto).toList();
    }

    @Transactional
    public int cleanupExpiredAuditLogs(int retentionDays) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(retentionDays);
        int deleted = auditLogRepository.deleteByTimestampBefore(cutoff);
        logger.info("Cleaned up {} expired audit log entries older than {} days (cutoff={})", deleted, retentionDays, cutoff);
        return deleted;
    }

    public String sanitizeAndToJson(Object detailsObject) {
        if (detailsObject == null) return "{}";
        try {
            String json;
            if (detailsObject instanceof String str) {
                json = str.trim().startsWith("{") || str.trim().startsWith("[") ? str : "{\"details\":\"" + escapeJson(str) + "\"}";
            } else {
                json = objectMapper.writeValueAsString(detailsObject);
            }
            return sanitizeJsonString(json);
        } catch (Exception e) {
            return "{\"details\":\"[SANITY_FORMAT_ERROR]\"}";
        }
    }

    public String sanitizeJsonString(String input) {
        if (input == null) return "{}";
        String redacted = SENSITIVE_KEY_PATTERN.matcher(input).replaceAll("\"$1\":\"[REDACTED]\"");
        return redacted;
    }

    private String escapeJson(String raw) {
        return raw.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ").replace("\r", "");
    }

    private Specification<AuditLog> createSpecification(AuditLogSearchFilterDto filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getEventType() != null) {
                predicates.add(cb.equal(root.get("eventType"), filter.getEventType()));
            }
            if (filter.getUsername() != null && !filter.getUsername().trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("username")), "%" + filter.getUsername().trim().toLowerCase() + "%"));
            }
            if (filter.getClientIp() != null && !filter.getClientIp().trim().isEmpty()) {
                predicates.add(cb.equal(root.get("clientIp"), filter.getClientIp().trim()));
            }
            if (filter.getSuccess() != null) {
                predicates.add(cb.equal(root.get("success"), filter.getSuccess()));
            }
            if (filter.getStartDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("timestamp"), filter.getStartDate()));
            }
            if (filter.getEndDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("timestamp"), filter.getEndDate()));
            }
            if (filter.getQuery() != null && !filter.getQuery().trim().isEmpty()) {
                String q = "%" + filter.getQuery().trim().toLowerCase() + "%";
                Predicate qUser = cb.like(cb.lower(root.get("username")), q);
                Predicate qDetails = cb.like(cb.lower(root.get("detailsJson")), q);
                Predicate qRes = cb.like(cb.lower(root.get("resourceId")), q);
                predicates.add(cb.or(qUser, qDetails, qRes));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private AuditLogResponseDto mapToDto(AuditLog auditLog) {
        return new AuditLogResponseDto(
                auditLog.getId(),
                auditLog.getTimestamp(),
                auditLog.getEventType(),
                auditLog.getUsername(),
                auditLog.getClientIp(),
                auditLog.getUserAgent(),
                auditLog.getResourceId(),
                auditLog.getDetailsJson(),
                auditLog.isSuccess()
        );
    }
}
