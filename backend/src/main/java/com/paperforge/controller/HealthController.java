package com.paperforge.controller;

import com.paperforge.engine.LibreOfficeProcessManager;
import com.paperforge.manager.QpdfProcessManager;
import com.paperforge.manager.TesseractProcessManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
@Tag(name = "System", description = "System health and runtime configuration endpoints")
public class HealthController {

    private final DataSource dataSource;
    private final LibreOfficeProcessManager libreOfficeProcessManager;
    private final TesseractProcessManager tesseractProcessManager;
    private final QpdfProcessManager qpdfProcessManager;

    public HealthController(DataSource dataSource,
                            LibreOfficeProcessManager libreOfficeProcessManager,
                            TesseractProcessManager tesseractProcessManager,
                            QpdfProcessManager qpdfProcessManager) {
        this.dataSource = dataSource;
        this.libreOfficeProcessManager = libreOfficeProcessManager;
        this.tesseractProcessManager = tesseractProcessManager;
        this.qpdfProcessManager = qpdfProcessManager;
    }

    @GetMapping
    @Operation(summary = "Get system health status", description = "Returns operational component status")
    @ApiResponse(responseCode = "200", description = "System operational check completed")
    public ResponseEntity<Map<String, Object>> getHealth() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", Instant.now().toString());

        Map<String, String> components = new HashMap<>();
        components.put("database", isDatabaseUp() ? "UP" : "DOWN");
        components.put("libreoffice", libreOfficeProcessManager.isLibreOfficeAvailable() ? "UP" : "NOT_INSTALLED");
        components.put("tesseract", tesseractProcessManager.isTesseractAvailable() ? "UP" : "NOT_INSTALLED");
        components.put("qpdf", qpdfProcessManager.isQpdfAvailable() ? "UP" : "NOT_INSTALLED");

        response.put("components", components);

        return ResponseEntity.ok(response);
    }

    private boolean isDatabaseUp() {
        try (Connection conn = dataSource.getConnection()) {
            return conn.isValid(2);
        } catch (Exception e) {
            return false;
        }
    }
}
