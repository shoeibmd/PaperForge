package com.paperforge.scheduler;

import com.paperforge.service.AuditLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class AuditLogCleanupScheduler {

    private static final Logger logger = LoggerFactory.getLogger(AuditLogCleanupScheduler.class);

    private final AuditLogService auditLogService;
    private final int retentionDays;

    public AuditLogCleanupScheduler(AuditLogService auditLogService,
                                   @Value("${PAPERFORGE_AUDIT_LOG_RETENTION_DAYS:90}") int retentionDays) {
        this.auditLogService = auditLogService;
        this.retentionDays = retentionDays;
    }

    @Scheduled(cron = "${PAPERFORGE_AUDIT_CLEANUP_CRON:0 0 3 * * ?}")
    public void cleanupExpiredAuditLogs() {
        logger.info("Executing scheduled audit log retention cleanup (Retention = {} days)...", retentionDays);
        try {
            int deletedCount = auditLogService.cleanupExpiredAuditLogs(retentionDays);
            logger.info("Scheduled audit log cleanup complete. Deleted {} records.", deletedCount);
        } catch (Exception e) {
            logger.error("Failed to execute scheduled audit log cleanup: {}", e.getMessage(), e);
        }
    }
}
