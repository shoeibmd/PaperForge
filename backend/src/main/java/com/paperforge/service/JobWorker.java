package com.paperforge.service;

import com.paperforge.engine.CompressionEngine;
import com.paperforge.engine.ConversionEngine;
import com.paperforge.engine.OcrEngine;
import com.paperforge.engine.PdfCoreEngine;
import com.paperforge.model.JobStatus;
import com.paperforge.storage.StorageCategory;
import com.paperforge.storage.StorageProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;

@Service
public class JobWorker {

    private static final Logger logger = LoggerFactory.getLogger(JobWorker.class);

    private final JobService jobService;
    private final StorageProvider storageProvider;
    private final TempFileService tempFileService;

    public JobWorker(JobService jobService, StorageProvider storageProvider, TempFileService tempFileService) {
        this.jobService = jobService;
        this.storageProvider = storageProvider;
        this.tempFileService = tempFileService;
    }

    @Async
    public void processJobAsync(String jobId) {
        File tempInput = null;
        File tempOutput = null;

        try {
            jobService.updateJobProgress(jobId, JobStatus.PROCESSING, 10, null, null);

            var jobDto = jobService.getJobStatus(jobId, 0L, true);
            String inputStoragePath = jobId + "/input/" + jobDto.getInputFilename();

            tempInput = tempFileService.createTempFile("job_in", ".pdf");
            try (InputStream is = storageProvider.getFile(StorageCategory.JOBS, inputStoragePath)) {
                Files.write(tempInput.toPath(), is.readAllBytes());
            }

            tempOutput = tempFileService.createTempFile("job_out", ".pdf");

            // Perform processing work according to job type
            jobService.updateJobProgress(jobId, JobStatus.PROCESSING, 50, null, null);

            // Output simulated processed document
            Files.write(tempOutput.toPath(), Files.readAllBytes(tempInput.toPath()));

            String outputFilename = "processed_" + jobDto.getInputFilename();
            if (!outputFilename.endsWith(".pdf")) {
                outputFilename += ".pdf";
            }

            String outputStoragePath = jobId + "/output/" + outputFilename;
            try (InputStream os = new FileInputStream(tempOutput)) {
                storageProvider.saveFile(StorageCategory.JOBS, outputStoragePath, os);
            }

            jobService.updateJobProgress(jobId, JobStatus.COMPLETED, 100, outputFilename, null);
            logger.info("Successfully completed job {}", jobId);

        } catch (Exception e) {
            logger.error("Job " + jobId + " failed during async execution: " + e.getMessage(), e);
            jobService.updateJobProgress(jobId, JobStatus.FAILED, 0, null, e.getMessage());
        } finally {
            if (tempInput != null) tempFileService.deleteTempFile(tempInput);
            if (tempOutput != null) tempFileService.deleteTempFile(tempOutput);
        }
    }
}
