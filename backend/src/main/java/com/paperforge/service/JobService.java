package com.paperforge.service;

import com.paperforge.dto.JobResponseDto;
import com.paperforge.model.Job;
import com.paperforge.model.JobStatus;
import com.paperforge.repository.JobRepository;
import com.paperforge.security.FilenameSanitizer;
import com.paperforge.storage.StorageCategory;
import com.paperforge.storage.StorageProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final StorageProvider storageProvider;
    private final AuditLogService auditLogService;
    private JobWorker jobWorker;

    public JobService(JobRepository jobRepository, StorageProvider storageProvider, AuditLogService auditLogService) {
        this.jobRepository = jobRepository;
        this.storageProvider = storageProvider;
        this.auditLogService = auditLogService;
    }

    @org.springframework.beans.factory.annotation.Autowired
    public void setJobWorker(@org.springframework.context.annotation.Lazy JobWorker jobWorker) {
        this.jobWorker = jobWorker;
    }

    @Transactional
    public JobResponseDto submitJob(Long userId, String jobType, MultipartFile file, String clientIp) throws IOException {
        String jobId = UUID.randomUUID().toString();
        String sanitizedFilename = FilenameSanitizer.sanitizeFilename(file.getOriginalFilename());

        Job job = new Job(jobId, userId, jobType, sanitizedFilename);
        jobRepository.save(job);

        String jobStoragePath = jobId + "/input/" + sanitizedFilename;
        try (InputStream is = file.getInputStream()) {
            storageProvider.saveFile(StorageCategory.JOBS, jobStoragePath, is);
        }

        auditLogService.logSecurityEvent("JOB_SUBMIT", clientIp, "jobId=" + jobId + ", jobType=" + jobType + ", userId=" + userId);

        if (jobWorker != null) {
            jobWorker.processJobAsync(jobId);
        }

        return convertToDto(job);
    }

    public JobResponseDto getJobStatus(String jobId, Long requestingUserId, boolean isAdmin) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found: " + jobId));

        if (!isAdmin && !job.getUserId().equals(requestingUserId)) {
            throw new SecurityException("Unauthorized access to job " + jobId);
        }

        return convertToDto(job);
    }

    public List<JobResponseDto> getUserJobs(Long userId) {
        return jobRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::convertToDto)
                .toList();
    }

    @Transactional
    public void cancelJob(String jobId, Long requestingUserId, boolean isAdmin, String clientIp) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found: " + jobId));

        if (!isAdmin && !job.getUserId().equals(requestingUserId)) {
            throw new SecurityException("Unauthorized access to job " + jobId);
        }

        if (job.getStatus() == JobStatus.QUEUED || job.getStatus() == JobStatus.PROCESSING) {
            job.setStatus(JobStatus.CANCELLED);
            job.setCompletedAt(LocalDateTime.now());
            jobRepository.save(job);
            auditLogService.logSecurityEvent("JOB_CANCEL", clientIp, "jobId=" + jobId + ", userId=" + requestingUserId);
        }
    }

    public byte[] downloadJobOutput(String jobId, Long requestingUserId, boolean isAdmin, String clientIp) throws IOException {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found: " + jobId));

        if (!isAdmin && !job.getUserId().equals(requestingUserId)) {
            throw new SecurityException("Unauthorized access to job " + jobId);
        }

        if (job.getStatus() != JobStatus.COMPLETED || job.getOutputFilename() == null) {
            throw new IllegalStateException("Job output is not ready or failed");
        }

        String outputStoragePath = jobId + "/output/" + job.getOutputFilename();
        try (InputStream is = storageProvider.getFile(StorageCategory.JOBS, outputStoragePath)) {
            byte[] bytes = is.readAllBytes();
            auditLogService.logSecurityEvent("JOB_DOWNLOAD", clientIp, "jobId=" + jobId + ", userId=" + requestingUserId);
            return bytes;
        }
    }

    @Transactional
    public void updateJobProgress(String jobId, JobStatus status, int progress, String outputFilename, String errorMessage) {
        jobRepository.findById(jobId).ifPresent(job -> {
            job.setStatus(status);
            job.setProgressPercent(progress);
            if (outputFilename != null) job.setOutputFilename(outputFilename);
            if (errorMessage != null) job.setErrorMessage(errorMessage);
            if (status == JobStatus.COMPLETED || status == JobStatus.FAILED || status == JobStatus.CANCELLED) {
                job.setCompletedAt(LocalDateTime.now());
            }
            jobRepository.save(job);
        });
    }

    public JobResponseDto convertToDto(Job job) {
        return new JobResponseDto(
                job.getId(),
                job.getUserId(),
                job.getJobType(),
                job.getStatus(),
                job.getProgressPercent(),
                job.getErrorMessage(),
                job.getInputFilename(),
                job.getOutputFilename(),
                job.getCreatedAt(),
                job.getCompletedAt(),
                job.getExpiresAt()
        );
    }
}
