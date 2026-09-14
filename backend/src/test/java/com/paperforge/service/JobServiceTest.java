package com.paperforge.service;

import com.paperforge.dto.JobResponseDto;
import com.paperforge.model.Job;
import com.paperforge.model.JobStatus;
import com.paperforge.repository.JobRepository;
import com.paperforge.storage.StorageCategory;
import com.paperforge.storage.StorageProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JobServiceTest {

    private JobRepository jobRepository;
    private StorageProvider storageProvider;
    private AuditLogService auditLogService;
    private JobService jobService;

    @BeforeEach
    void setUp() {
        jobRepository = mock(JobRepository.class);
        storageProvider = mock(StorageProvider.class);
        auditLogService = mock(AuditLogService.class);

        jobService = new JobService(jobRepository, storageProvider, auditLogService);
    }

    @Test
    void testSubmitJobSuccess() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "doc.pdf", "application/pdf", "data".getBytes());

        JobResponseDto dto = jobService.submitJob(1L, "CONVERT", file, "127.0.0.1");

        assertNotNull(dto.getId());
        assertEquals(JobStatus.QUEUED, dto.getStatus());
        assertEquals("CONVERT", dto.getJobType());
        verify(jobRepository, times(1)).save(any(Job.class));
        verify(storageProvider, times(1)).saveFile(eq(StorageCategory.JOBS), anyString(), any());
    }

    @Test
    void testUserIsolationUnauthorizedAccess() {
        Job job = new Job("job-123", 1L, "CONVERT", "doc.pdf");
        when(jobRepository.findById("job-123")).thenReturn(Optional.of(job));

        assertThrows(SecurityException.class, () -> {
            jobService.getJobStatus("job-123", 2L, false); // User 2 trying to access User 1's job
        });
    }

    @Test
    void testCancelJobSuccess() {
        Job job = new Job("job-123", 1L, "CONVERT", "doc.pdf");
        job.setStatus(JobStatus.QUEUED);
        when(jobRepository.findById("job-123")).thenReturn(Optional.of(job));

        jobService.cancelJob("job-123", 1L, false, "127.0.0.1");

        assertEquals(JobStatus.CANCELLED, job.getStatus());
        verify(jobRepository, times(1)).save(job);
    }
}
