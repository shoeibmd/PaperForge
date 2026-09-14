package com.paperforge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paperforge.model.User;
import com.paperforge.repository.UserRepository;
import com.paperforge.registry.ToolRegistry;
import com.paperforge.service.PipelineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PipelineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PipelineService pipelineService;

    @MockBean
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        User user = new User("user", "user@example.com", "pass");
        user.setId(1L);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testGetToolsAndPipelinesSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/pipelines/tools"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/pipelines"))
                .andExpect(status().isOk());
    }
}
