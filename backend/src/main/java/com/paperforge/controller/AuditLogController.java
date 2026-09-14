package com.paperforge.controller;

import com.paperforge.dto.AuditLogResponseDto;
import com.paperforge.dto.AuditLogSearchFilterDto;
import com.paperforge.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/audit-logs")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Audit Logs", description = "Administrator security audit log monitoring and export endpoints")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    @Operation(summary = "Search & Filter Audit Logs", description = "Retrieves paginated audit log events with optional filters.")
    public ResponseEntity<Page<AuditLogResponseDto>> getAuditLogs(
            @ModelAttribute AuditLogSearchFilterDto filter,
            @PageableDefault(size = 20, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<AuditLogResponseDto> results = auditLogService.searchAuditLogs(filter, pageable);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/export")
    @Operation(summary = "Export Audit Logs CSV", description = "Downloads a CSV report of audit logs matching the specified search filters.")
    public ResponseEntity<byte[]> exportAuditLogsCsv(@ModelAttribute AuditLogSearchFilterDto filter) {
        List<AuditLogResponseDto> logs = auditLogService.getFilteredAuditLogsForExport(filter);

        StringBuilder csv = new StringBuilder();
        csv.append("ID,Timestamp,Event Type,Username,Client IP,User Agent,Resource ID,Success,Details\n");

        for (AuditLogResponseDto log : logs) {
            csv.append(log.getId()).append(",");
            csv.append(escapeCsvField(log.getTimestamp() != null ? log.getTimestamp().toString() : "")).append(",");
            csv.append(escapeCsvField(log.getEventType() != null ? log.getEventType().name() : "")).append(",");
            csv.append(escapeCsvField(log.getUsername())).append(",");
            csv.append(escapeCsvField(log.getClientIp())).append(",");
            csv.append(escapeCsvField(log.getUserAgent())).append(",");
            csv.append(escapeCsvField(log.getResourceId())).append(",");
            csv.append(log.isSuccess()).append(",");
            csv.append(escapeCsvField(log.getDetailsJson())).append("\n");
        }

        byte[] csvBytes = csv.toString().getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"paperforge_audit_logs.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvBytes);
    }

    private String escapeCsvField(String field) {
        if (field == null) return "\"\"";
        String value = field.trim();
        // Prevent CSV Formula Injection
        if (value.startsWith("=") || value.startsWith("+") || value.startsWith("-") || value.startsWith("@") || value.startsWith("\t")) {
            value = "'" + value;
        }
        // Escape quotes
        value = value.replace("\"", "\"\"");
        return "\"" + value + "\"";
    }
}
