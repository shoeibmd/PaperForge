package com.paperforge.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paperforge.model.AuditEventType;
import com.paperforge.model.AuditLog;
import com.paperforge.repository.AuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    private ObjectMapper objectMapper;
    private AuditLogService auditLogService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        auditLogService = new AuditLogService(auditLogRepository, objectMapper);
    }

    @Test
    void logEvent_SanitizesSensitiveFields() {
        Map<String, String> sensitiveData = Map.of(
                "username", "john_doe",
                "password", "SuperSecret123!",
                "token", "bearer_jwt_token_xyz",
                "apikey", "pf_live_1234567890"
        );

        auditLogService.logEvent(AuditEventType.LOGIN, "john_doe", "127.0.0.1", "Mozilla/5.0", null, sensitiveData, true);

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());

        AuditLog savedLog = captor.getValue();
        assertNotNull(savedLog);
        assertEquals(AuditEventType.LOGIN, savedLog.getEventType());
        assertEquals("john_doe", savedLog.getUsername());

        String json = savedLog.getDetailsJson();
        assertTrue(json.contains("\"username\":\"john_doe\""));
        assertTrue(json.contains("\"password\":\"[REDACTED]\""));
        assertTrue(json.contains("\"token\":\"[REDACTED]\""));
        assertTrue(json.contains("\"apikey\":\"[REDACTED]\""));
        assertFalse(json.contains("SuperSecret123!"));
    }

    @Test
    void cleanupExpiredAuditLogs_DeletesOlderRecords() {
        when(auditLogRepository.deleteByTimestampBefore(any())).thenReturn(42);

        int deleted = auditLogService.cleanupExpiredAuditLogs(90);

        assertEquals(42, deleted);
        verify(auditLogRepository, times(1)).deleteByTimestampBefore(any());
    }
}
