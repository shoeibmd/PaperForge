package com.paperforge.controller;

import com.paperforge.dto.HealthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/health")
@Tag(name = "System", description = "System health and runtime configuration endpoints")
public class HealthController {

    @GetMapping
    @Operation(summary = "Get system health status", description = "Returns system operational status and timestamp")
    @ApiResponse(responseCode = "200", description = "System is operational")
    public ResponseEntity<HealthResponse> getHealth() {
        return ResponseEntity.ok(HealthResponse.up());
    }
}
