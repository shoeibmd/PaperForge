package com.paperforge.controller;

import com.paperforge.service.StorageService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/storage")
public class StorageController {

    private final StorageService storageService;

    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/usage")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getUserStorageUsage(Authentication auth) throws IOException {
        Long userId = 1L; // Mock or extracted user ID
        long usageBytes = storageService.getUserStorageUsage(userId);
        return ResponseEntity.ok(Map.of(
                "userId", userId,
                "usageBytes", usageBytes,
                "usageFormatted", (usageBytes / (1024 * 1024)) + " MB"
        ));
    }

    @GetMapping("/download/{filename}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> downloadUserFile(@PathVariable String filename, Authentication auth) throws IOException {
        Long userId = 1L;
        byte[] fileBytes = storageService.getUserFile(userId, filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileBytes);
    }
}
