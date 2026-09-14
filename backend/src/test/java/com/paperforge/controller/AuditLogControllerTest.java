package com.paperforge.controller;

import com.paperforge.dto.AuditLogResponseDto;
import com.paperforge.model.AuditEventType;
import com.paperforge.service.AuditLogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuditLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuditLogService auditLogService;

    @Test
    @WithMockUser(username = "adminUser", roles = {"ADMIN"})
    void getAuditLogs_AdminUser_Success() throws Exception {
        AuditLogResponseDto dto = new AuditLogResponseDto(1L, LocalDateTime.now(), AuditEventType.LOGIN, "adminUser",
                "127.0.0.1", "Mozilla/5.0", null, "{}", true);

        when(auditLogService.searchAuditLogs(any(), any())).thenReturn(new PageImpl<>(List.of(dto)));

        mockMvc.perform(get("/api/v1/admin/audit-logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].eventType").value("LOGIN"));
    }

    @Test
    @WithMockUser(username = "regularUser", roles = {"USER"})
    void getAuditLogs_RegularUser_Forbidden() throws Exception {
        mockMvc.perform(get("/api/v1/admin/audit-logs"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "adminUser", roles = {"ADMIN"})
    void exportAuditLogsCsv_Success() throws Exception {
        AuditLogResponseDto dto = new AuditLogResponseDto(1L, LocalDateTime.now(), AuditEventType.LOGIN, "adminUser",
                "127.0.0.1", "Mozilla/5.0", null, "{}", true);

        when(auditLogService.getFilteredAuditLogsForExport(any())).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/admin/audit-logs/export"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"paperforge_audit_logs.csv\""))
                .andExpect(content().contentType("text/csv"));
    }
}
