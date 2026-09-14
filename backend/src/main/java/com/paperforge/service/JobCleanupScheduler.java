package com.paperforge.service;

import com.paperforge.model.Job;
import com.paperforge.model.JobStatus;
import com.paperforge.repository.JobRepository;
import com.paperforge.storage.StorageCategory;
import com.paperforge.storage.StorageProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class JobCleanupScheduler {

    private static final Logger logger = LoggerFactory.getLogger(JobCleanupScheduler.class);

    private final JobRepository jobRepository;
    private final StorageProvider storageProvider;

    public JobCleanupScheduler(JobRepository jobRepository, StorageProvider storageProvider) {
        this.jobRepository = jobRepository;
        this.storageProvider = storageProvider;
    }

    @Scheduled(cron = "0 0 * * * *") // Run hourly
    @Transactional
    public void cleanupExpiredJobs() {
        LocalDateTime now = LocalDateTime.now();
        List<Job> expiredJobs = jobRepository.findByStatusAndExpiresAtBefore(JobStatus.COMPLETED, now);

        for (Job job : expiredJobs) {
            try {
                if (job.getOutputFilename() != null) {
                    String outputStoragePath = job.getId() + "/output/" + job.getOutputFilename();
                    storageProvider.deleteFile(StorageCategory.JOBS, outputStoragePath);
                }
                job.setStatus(JobStatus.EXPIRED);
                jobRepository.save(job);
                logger.info("Successfully cleaned up expired job: {}", job.getId());
            } catch (Exception e) {
                logger.error("Failed to clean up expired job " + job.getId() + ": " + e.getMessage(), e);
            }
        }
    }
}
