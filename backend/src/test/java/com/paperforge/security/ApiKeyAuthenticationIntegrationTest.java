package com.paperforge.security;

import com.paperforge.dto.ApiKeyCreateRequestDto;
import com.paperforge.dto.ApiKeyCreatedResponseDto;
import com.paperforge.model.User;
import com.paperforge.repository.UserRepository;
import com.paperforge.service.ApiKeyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiKeyAuthenticationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApiKeyService apiKeyService;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private String rawSecretKey;

    @BeforeEach
    void setUp() {
        user = userRepository.findByUsername("apikeyuser").orElseGet(() -> {
            User u = new User("apikeyuser", "apikeyuser@example.com", "$2a$12$dummyPasswordHash");
            return userRepository.save(u);
        });

        ApiKeyCreateRequestDto request = new ApiKeyCreateRequestDto("Integration Test Key", Set.of("pdf:read", "ocr"), null);
        ApiKeyCreatedResponseDto created = apiKeyService.createApiKey(user, request);
        rawSecretKey = created.getRawSecret();
    }

    @Test
    void authenticateWithApiKeyHeader_Success() throws Exception {
        mockMvc.perform(get("/api/v1/health")
                        .header("X-API-Key", rawSecretKey))
                .andExpect(status().isOk());
    }

    @Test
    void authenticateWithBearerApiKeyHeader_Success() throws Exception {
        mockMvc.perform(get("/api/v1/health")
                        .header("Authorization", "Bearer " + rawSecretKey))
                .andExpect(status().isOk());
    }

    @Test
    void authenticateWithInvalidApiKeyHeader_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/pipelines")
                        .header("X-API-Key", "pf_live_invalidkey1234567890123456"))
                .andExpect(status().isUnauthorized());
    }
}
