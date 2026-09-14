package com.paperforge.controller;

import com.paperforge.dto.ApiKeyCreateRequestDto;
import com.paperforge.dto.ApiKeyCreatedResponseDto;
import com.paperforge.dto.ApiKeyResponseDto;
import com.paperforge.model.User;
import com.paperforge.repository.UserRepository;
import com.paperforge.service.ApiKeyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/api-keys")
@Tag(name = "API Keys", description = "User API key management endpoints")
public class ApiKeyController {

    private final ApiKeyService apiKeyService;
    private final UserRepository userRepository;

    public ApiKeyController(ApiKeyService apiKeyService, UserRepository userRepository) {
        this.apiKeyService = apiKeyService;
        this.userRepository = userRepository;
    }

    @PostMapping
    @Operation(summary = "Create API Key", description = "Generates a new API key with scoped permissions. The secret is returned only once.")
    public ResponseEntity<ApiKeyCreatedResponseDto> createApiKey(@RequestBody ApiKeyCreateRequestDto dto,
                                                                 Authentication authentication) {
        User user = getUserFromAuth(authentication);
        ApiKeyCreatedResponseDto result = apiKeyService.createApiKey(user, dto);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    @Operation(summary = "List User API Keys", description = "Retrieves all active and revoked API keys belonging to the authenticated user.")
    public ResponseEntity<List<ApiKeyResponseDto>> getUserApiKeys(Authentication authentication) {
        User user = getUserFromAuth(authentication);
        List<ApiKeyResponseDto> keys = apiKeyService.getUserApiKeys(user);
        return ResponseEntity.ok(keys);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Revoke API Key", description = "Revokes an API key belonging to the authenticated user.")
    public ResponseEntity<Void> revokeApiKey(@PathVariable Long id, Authentication authentication) {
        User user = getUserFromAuth(authentication);
        apiKeyService.revokeApiKey(id, user, false);
        return ResponseEntity.noContent().build();
    }

    private User getUserFromAuth(Authentication authentication) {
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found: " + authentication.getName()));
    }
}
