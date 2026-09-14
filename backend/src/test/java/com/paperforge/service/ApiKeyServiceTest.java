package com.paperforge.service;

import com.paperforge.dto.ApiKeyCreateRequestDto;
import com.paperforge.dto.ApiKeyCreatedResponseDto;
import com.paperforge.dto.ApiKeyResponseDto;
import com.paperforge.model.ApiKey;
import com.paperforge.model.ApiKeyScope;
import com.paperforge.model.User;
import com.paperforge.repository.ApiKeyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApiKeyServiceTest {

    @Mock
    private ApiKeyRepository apiKeyRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private ApiKeyService apiKeyService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("testuser", "test@example.com", "encodedPassword");
        user.setId(1L);
    }

    @Test
    void createApiKey_Success() {
        ApiKeyCreateRequestDto dto = new ApiKeyCreateRequestDto("Test Key", Set.of("pdf:read", "ocr"), null);

        when(passwordEncoder.encode(anyString())).thenReturn("$2a$12$mockedHashValue");
        when(apiKeyRepository.save(any(ApiKey.class))).thenAnswer(invocation -> {
            ApiKey k = invocation.getArgument(0);
            k.setId(10L);
            return k;
        });

        ApiKeyCreatedResponseDto response = apiKeyService.createApiKey(user, dto);

        assertNotNull(response);
        assertNotNull(response.getRawSecret());
        assertTrue(response.getRawSecret().startsWith("pf_live_"));
        assertEquals("Test Key", response.getApiKey().getName());
        assertEquals("testuser", response.getApiKey().getUsername());
        assertTrue(response.getApiKey().getScopes().contains("pdf:read"));
        assertTrue(response.getApiKey().getScopes().contains("ocr"));

        verify(auditLogService, times(1)).logSecurityEvent(eq("API_KEY_CREATED"), eq("testuser"), anyString());
    }

    @Test
    void validateAndAuthenticateApiKey_Success() {
        String rawKey = "pf_live_12345678901234567890123456789012";
        String prefix = rawKey.substring(0, 16);

        ApiKey apiKey = new ApiKey("Test Key", prefix, "$2a$12$mockedHashValue", user, Set.of(ApiKeyScope.PDF_READ), null);
        apiKey.setId(5L);

        when(apiKeyRepository.findByKeyPrefixAndRevokedFalse(prefix)).thenReturn(List.of(apiKey));
        when(passwordEncoder.matches(rawKey, apiKey.getKeyHash())).thenReturn(true);
        when(apiKeyRepository.save(any(ApiKey.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<ApiKey> authenticated = apiKeyService.validateAndAuthenticateApiKey(rawKey);

        assertTrue(authenticated.isPresent());
        assertEquals(1, authenticated.get().getUsageCount());
        assertNotNull(authenticated.get().getLastUsedAt());
        verify(auditLogService, times(1)).logSecurityEvent(eq("API_KEY_AUTH_SUCCESS"), eq("testuser"), anyString());
    }

    @Test
    void validateAndAuthenticateApiKey_Expired() {
        String rawKey = "pf_live_12345678901234567890123456789012";
        String prefix = rawKey.substring(0, 16);

        ApiKey apiKey = new ApiKey("Test Key", prefix, "$2a$12$mockedHashValue", user, Set.of(ApiKeyScope.PDF_READ), LocalDateTime.now().minusDays(1));
        apiKey.setId(5L);

        when(apiKeyRepository.findByKeyPrefixAndRevokedFalse(prefix)).thenReturn(List.of(apiKey));
        when(passwordEncoder.matches(rawKey, apiKey.getKeyHash())).thenReturn(true);

        Optional<ApiKey> authenticated = apiKeyService.validateAndAuthenticateApiKey(rawKey);

        assertTrue(authenticated.isEmpty());
        verify(auditLogService, times(1)).logSecurityEvent(eq("API_KEY_EXPIRED"), eq("testuser"), anyString());
    }
}
