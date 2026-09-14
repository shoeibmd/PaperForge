package com.paperforge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paperforge.dto.ApiKeyCreateRequestDto;
import com.paperforge.dto.ApiKeyCreatedResponseDto;
import com.paperforge.dto.ApiKeyResponseDto;
import com.paperforge.model.User;
import com.paperforge.repository.UserRepository;
import com.paperforge.service.ApiKeyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiKeyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ApiKeyService apiKeyService;

    @MockBean
    private UserRepository userRepository;

    @Test
    @WithMockUser(username = "user1")
    void createApiKey_Success() throws Exception {
        User user = new User("user1", "user1@example.com", "pass");
        user.setId(1L);

        ApiKeyResponseDto dto = new ApiKeyResponseDto(1L, "My Key", "pf_live_12345678", "user1", false,
                Set.of("pdf:read"), LocalDateTime.now(), null, null, 0);
        ApiKeyCreatedResponseDto createdDto = new ApiKeyCreatedResponseDto(dto, "pf_live_12345678901234567890");

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(apiKeyService.createApiKey(any(User.class), any(ApiKeyCreateRequestDto.class))).thenReturn(createdDto);

        ApiKeyCreateRequestDto request = new ApiKeyCreateRequestDto("My Key", Set.of("pdf:read"), null);

        mockMvc.perform(post("/api/v1/api-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rawSecret").value("pf_live_12345678901234567890"))
                .andExpect(jsonPath("$.apiKey.name").value("My Key"));
    }

    @Test
    @WithMockUser(username = "user1")
    void getUserApiKeys_Success() throws Exception {
        User user = new User("user1", "user1@example.com", "pass");
        user.setId(1L);

        ApiKeyResponseDto dto = new ApiKeyResponseDto(1L, "My Key", "pf_live_12345678", "user1", false,
                Set.of("pdf:read"), LocalDateTime.now(), null, null, 0);

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(apiKeyService.getUserApiKeys(any(User.class))).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/api-keys"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("My Key"));
    }
}
