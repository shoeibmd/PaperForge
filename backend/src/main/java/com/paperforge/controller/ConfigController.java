package com.paperforge.controller;

import com.paperforge.dto.ConfigResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/config")
public class ConfigController {

    @GetMapping
    @Cacheable("systemConfig")
    public ResponseEntity<ConfigResponse> getConfig() {
        ConfigResponse config = new ConfigResponse(
                104_857_600L, // 100 MB default upload limit
                Map.of(
                        "pdfCore", true,
                        "conversion", true,
                        "ocr", true,
                        "security", true
                ),
                "light",
                "support@paperforge.org"
        );
        return ResponseEntity.ok(config);
    }
}
