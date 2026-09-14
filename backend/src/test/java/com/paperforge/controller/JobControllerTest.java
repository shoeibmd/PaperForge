package com.paperforge.controller;

import com.paperforge.dto.JobResponseDto;
import com.paperforge.model.JobStatus;
import com.paperforge.model.User;
import com.paperforge.repository.UserRepository;
import com.paperforge.service.JobService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class JobControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JobService jobService;

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
    void testGetJobStatusSuccess() throws Exception {
        JobResponseDto dto = new JobResponseDto(
                "job-999", 1L, "CONVERT", JobStatus.PROCESSING, 50,
                null, "input.docx", null, LocalDateTime.now(), null, LocalDateTime.now().plusDays(1)
        );

        when(jobService.getJobStatus(eq("job-999"), anyLong(), anyBoolean())).thenReturn(dto);

        mockMvc.perform(get("/api/v1/jobs/job-999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("job-999")))
                .andExpect(jsonPath("$.status", is("PROCESSING")))
                .andExpect(jsonPath("$.progressPercent", is(50)));
    }
}
