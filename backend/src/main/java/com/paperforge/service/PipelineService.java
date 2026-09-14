package com.paperforge.service;

import com.paperforge.dto.JobResponseDto;
import com.paperforge.model.Pipeline;
import com.paperforge.model.PipelineStep;
import com.paperforge.repository.PipelineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class PipelineService {

    private final PipelineRepository pipelineRepository;
    private final PipelineValidator pipelineValidator;
    private final JobService jobService;
    private final AuditLogService auditLogService;

    public PipelineService(PipelineRepository pipelineRepository, PipelineValidator pipelineValidator,
                           JobService jobService, AuditLogService auditLogService) {
        this.pipelineRepository = pipelineRepository;
        this.pipelineValidator = pipelineValidator;
        this.jobService = jobService;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public Pipeline createPipeline(Long userId, String name, String description, List<PipelineStep> steps, String clientIp) {
        pipelineValidator.validatePipelineSteps(steps);

        Pipeline pipeline = new Pipeline(userId, name, description);
        for (int i = 0; i < steps.size(); i++) {
            PipelineStep step = steps.get(i);
            step.setStepOrder(i + 1);
            pipeline.getSteps().add(step);
        }

        Pipeline saved = pipelineRepository.save(pipeline);
        auditLogService.logSecurityEvent("PIPELINE_CREATE", clientIp, "pipelineId=" + saved.getId() + ", name=" + name);
        return saved;
    }

    public List<Pipeline> getUserPipelines(Long userId) {
        return pipelineRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Pipeline getPipeline(Long id, Long requestingUserId, boolean isAdmin) {
        Pipeline pipeline = pipelineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pipeline not found: " + id));

        if (!isAdmin && !pipeline.getUserId().equals(requestingUserId)) {
            throw new SecurityException("Unauthorized access to pipeline " + id);
        }

        return pipeline;
    }

    @Transactional
    public void deletePipeline(Long id, Long requestingUserId, boolean isAdmin, String clientIp) {
        Pipeline pipeline = getPipeline(id, requestingUserId, isAdmin);
        pipelineRepository.delete(pipeline);
        auditLogService.logSecurityEvent("PIPELINE_DELETE", clientIp, "pipelineId=" + id + ", userId=" + requestingUserId);
    }

    @Transactional
    public JobResponseDto executePipeline(Long pipelineId, MultipartFile file, Long requestingUserId, boolean isAdmin, String clientIp) throws IOException {
        Pipeline pipeline = getPipeline(pipelineId, requestingUserId, isAdmin);
        pipelineValidator.validatePipelineSteps(pipeline.getSteps());

        JobResponseDto job = jobService.submitJob(requestingUserId, "PIPELINE:" + pipeline.getName(), file, clientIp);
        auditLogService.logSecurityEvent("PIPELINE_EXECUTE", clientIp, "pipelineId=" + pipelineId + ", jobId=" + job.getId());
        return job;
    }
}
