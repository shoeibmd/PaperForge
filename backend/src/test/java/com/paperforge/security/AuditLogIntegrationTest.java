package com.paperforge.security;

import com.paperforge.dto.AuthRequestDto;
import com.paperforge.dto.RegisterRequestDto;
import com.paperforge.model.AuditEventType;
import com.paperforge.model.AuditLog;
import com.paperforge.repository.AuditLogRepository;
import com.paperforge.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class AuditLogIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Test
    void authOperations_TriggerAuditLogs() {
        String uniqueUsername = "audit_user_" + System.currentTimeMillis();
        RegisterRequestDto reg = new RegisterRequestDto(uniqueUsername, uniqueUsername + "@example.com", "Password123!");
        authService.register(reg, "192.168.1.100");

        AuthRequestDto login = new AuthRequestDto(uniqueUsername, "Password123!");
        authService.login(login, "192.168.1.100");

        List<AuditLog> userLogs = auditLogRepository.findAll().stream()
                .filter(a -> uniqueUsername.equals(a.getUsername()))
                .toList();

        assertFalse(userLogs.isEmpty());
        assertTrue(userLogs.stream().anyMatch(l -> l.getEventType() == AuditEventType.USER_CREATED));
        assertTrue(userLogs.stream().anyMatch(l -> l.getEventType() == AuditEventType.LOGIN));
    }
}
