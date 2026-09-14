package com.paperforge.controller;

import com.paperforge.dto.ApiKeyResponseDto;
import com.paperforge.model.User;
import com.paperforge.repository.UserRepository;
import com.paperforge.service.ApiKeyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/api-keys")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin API Keys", description = "Administrator API key governance and monitoring endpoints")
public class AdminApiKeyController {

    private final ApiKeyService apiKeyService;
    private final UserRepository userRepository;

    public AdminApiKeyController(ApiKeyService apiKeyService, UserRepository userRepository) {
        this.apiKeyService = apiKeyService;
        this.userRepository = userRepository;
    }

    @GetMapping
    @Operation(summary = "List All API Keys", description = "Lists all generated API keys across all users in the system.")
    public ResponseEntity<List<ApiKeyResponseDto>> getAllApiKeys() {
        List<ApiKeyResponseDto> keys = apiKeyService.getAllApiKeys();
        return ResponseEntity.ok(keys);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Admin Revoke API Key", description = "Admin force-revocation of any user's API key.")
    public ResponseEntity<Void> revokeApiKeyAdmin(@PathVariable Long id, Authentication authentication) {
        User adminUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Admin user not found: " + authentication.getName()));
        apiKeyService.revokeApiKey(id, adminUser, true);
        return ResponseEntity.noContent().build();
    }
}
