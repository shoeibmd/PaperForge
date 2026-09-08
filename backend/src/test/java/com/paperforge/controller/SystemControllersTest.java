package com.paperforge.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SystemControllersTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void healthEndpointReturnsUp() throws Exception {
        mockMvc.perform(get("/api/v1/health")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("UP")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    void infoEndpointReturnsPaperForgeMetadata() throws Exception {
        mockMvc.perform(get("/api/v1/info")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("PaperForge")))
                .andExpect(jsonPath("$.tagline", is("Forge your documents.")))
                .andExpect(jsonPath("$.version", is("0.0.1-SNAPSHOT")))
                .andExpect(jsonPath("$.environment", is("development")));
    }

    @Test
    void configEndpointReturnsSystemConfiguration() throws Exception {
        mockMvc.perform(get("/api/v1/config")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maxUploadSizeBytes", is(104857600)))
                .andExpect(jsonPath("$.defaultTheme", is("light")))
                .andExpect(jsonPath("$.supportEmail", is("support@paperforge.org")))
                .andExpect(jsonPath("$.featureFlags.pdfCore", is(true)));
    }
}
