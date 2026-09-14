package com.paperforge.service;

import com.paperforge.dto.ApiKeyCreateRequestDto;
import com.paperforge.dto.ApiKeyCreatedResponseDto;
import com.paperforge.dto.ApiKeyResponseDto;
import com.paperforge.model.ApiKey;
import com.paperforge.model.ApiKeyScope;
import com.paperforge.model.User;
import com.paperforge.repository.ApiKeyRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ApiKeyService {

    private static final String KEY_PREFIX_HEADER = "pf_live_";

    private final ApiKeyRepository apiKeyRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    public ApiKeyService(ApiKeyRepository apiKeyRepository,
                         PasswordEncoder passwordEncoder,
                         AuditLogService auditLogService) {
        this.apiKeyRepository = apiKeyRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public ApiKeyCreatedResponseDto createApiKey(User user, ApiKeyCreateRequestDto dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("API Key name cannot be empty");
        }

        String rawSecret = KEY_PREFIX_HEADER + UUID.randomUUID().toString().replace("-", "")
                + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        String prefix = rawSecret.substring(0, 16);
        String hash = passwordEncoder.encode(rawSecret);

        Set<ApiKeyScope> scopes = new HashSet<>();
        if (dto.getScopes() != null && !dto.getScopes().isEmpty()) {
            for (String scopeStr : dto.getScopes()) {
                scopes.add(ApiKeyScope.fromValue(scopeStr));
            }
        } else {
            scopes.add(ApiKeyScope.PDF_READ);
            scopes.add(ApiKeyScope.PDF_WRITE);
            scopes.add(ApiKeyScope.OCR);
            scopes.add(ApiKeyScope.CONVERSION);
        }

        ApiKey apiKey = new ApiKey(dto.getName().trim(), prefix, hash, user, scopes, dto.getExpiresAt());
        ApiKey saved = apiKeyRepository.save(apiKey);

        auditLogService.logSecurityEvent("API_KEY_CREATED", user.getUsername(),
                "Created API Key ID=" + saved.getId() + ", prefix=" + saved.getKeyPrefix());

        return new ApiKeyCreatedResponseDto(mapToResponseDto(saved), rawSecret);
    }

    @Transactional(readOnly = true)
    public List<ApiKeyResponseDto> getUserApiKeys(User user) {
        return apiKeyRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ApiKeyResponseDto> getAllApiKeys() {
        return apiKeyRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void revokeApiKey(Long keyId, User requester, boolean isAdmin) {
        ApiKey apiKey = apiKeyRepository.findById(keyId)
                .orElseThrow(() -> new IllegalArgumentException("API Key not found: " + keyId));

        if (!isAdmin && !apiKey.getUser().getId().equals(requester.getId())) {
            throw new AccessDeniedException("You do not have permission to revoke this API key");
        }

        apiKey.setRevoked(true);
        apiKeyRepository.save(apiKey);

        auditLogService.logSecurityEvent("API_KEY_REVOKED", requester.getUsername(),
                "Revoked API Key ID=" + apiKey.getId() + ", prefix=" + apiKey.getKeyPrefix());
    }

    @Transactional
    public Optional<ApiKey> validateAndAuthenticateApiKey(String rawKey) {
        if (rawKey == null || !rawKey.startsWith(KEY_PREFIX_HEADER) || rawKey.length() < 16) {
            return Optional.empty();
        }

        String prefix = rawKey.substring(0, 16);
        List<ApiKey> candidates = apiKeyRepository.findByKeyPrefixAndRevokedFalse(prefix);

        for (ApiKey candidate : candidates) {
            if (passwordEncoder.matches(rawKey, candidate.getKeyHash())) {
                if (candidate.isExpired()) {
                    auditLogService.logSecurityEvent("API_KEY_EXPIRED", candidate.getUser().getUsername(),
                            "Attempted use of expired API Key ID=" + candidate.getId());
                    return Optional.empty();
                }

                candidate.setLastUsedAt(LocalDateTime.now());
                candidate.setUsageCount(candidate.getUsageCount() + 1);
                apiKeyRepository.save(candidate);

                auditLogService.logSecurityEvent("API_KEY_AUTH_SUCCESS", candidate.getUser().getUsername(),
                        "API Key ID=" + candidate.getId() + " authenticated");
                return Optional.of(candidate);
            }
        }

        auditLogService.logSecurityEvent("API_KEY_AUTH_FAILED", "SYSTEM", "Invalid API key attempt for prefix: " + prefix);
        return Optional.empty();
    }

    public ApiKeyResponseDto mapToResponseDto(ApiKey apiKey) {
        Set<String> scopeStrings = apiKey.getScopes().stream()
                .map(ApiKeyScope::getValue)
                .collect(Collectors.toSet());

        return new ApiKeyResponseDto(
                apiKey.getId(),
                apiKey.getName(),
                apiKey.getKeyPrefix(),
                apiKey.getUser().getUsername(),
                apiKey.isRevoked(),
                scopeStrings,
                apiKey.getCreatedAt(),
                apiKey.getExpiresAt(),
                apiKey.getLastUsedAt(),
                apiKey.getUsageCount()
        );
    }
}
