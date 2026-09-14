package com.paperforge.dto;

import com.paperforge.model.JobStatus;
import java.time.LocalDateTime;

public class JobResponseDto {

    private String id;
    private Long userId;
    private String jobType;
    private JobStatus status;
    private int progressPercent;
    private String errorMessage;
    private String inputFilename;
    private String outputFilename;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private LocalDateTime expiresAt;

    public JobResponseDto() {}

    public JobResponseDto(String id, Long userId, String jobType, JobStatus status, int progressPercent,
                          String errorMessage, String inputFilename, String outputFilename,
                          LocalDateTime createdAt, LocalDateTime completedAt, LocalDateTime expiresAt) {
        this.id = id;
        this.userId = userId;
        this.jobType = jobType;
        this.status = status;
        this.progressPercent = progressPercent;
        this.errorMessage = errorMessage;
        this.inputFilename = inputFilename;
        this.outputFilename = outputFilename;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
        this.expiresAt = expiresAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }

    public JobStatus getStatus() { return status; }
    public void setStatus(JobStatus status) { this.status = status; }

    public int getProgressPercent() { return progressPercent; }
    public void setProgressPercent(int progressPercent) { this.progressPercent = progressPercent; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public String getInputFilename() { return inputFilename; }
    public void setInputFilename(String inputFilename) { this.inputFilename = inputFilename; }

    public String getOutputFilename() { return outputFilename; }
    public void setOutputFilename(String outputFilename) { this.outputFilename = outputFilename; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
}
