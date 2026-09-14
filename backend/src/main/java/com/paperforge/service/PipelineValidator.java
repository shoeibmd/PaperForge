package com.paperforge.service;

import com.paperforge.model.FileType;
import com.paperforge.model.PipelineStep;
import com.paperforge.registry.ToolMetadata;
import com.paperforge.registry.ToolRegistry;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PipelineValidator {

    private final ToolRegistry toolRegistry;

    public PipelineValidator(ToolRegistry toolRegistry) {
        this.toolRegistry = toolRegistry;
    }

    public void validatePipelineSteps(List<PipelineStep> steps) {
        if (steps == null || steps.isEmpty()) {
            throw new IllegalArgumentException("Pipeline must contain at least one step");
        }

        FileType currentFileType = null;

        for (int i = 0; i < steps.size(); i++) {
            PipelineStep step = steps.get(i);
            String toolId = step.getToolId();

            final int stepNumber = i + 1;
            ToolMetadata tool = toolRegistry.getTool(toolId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid or unregistered tool ID at step " + stepNumber + ": " + toolId));

            if (i == 0) {
                currentFileType = tool.getProduces();
            } else {
                FileType accepts = tool.getAccepts();
                if (accepts != FileType.ANY && currentFileType != FileType.ANY && accepts != currentFileType) {
                    throw new IllegalArgumentException(String.format(
                            "Incompatible pipeline chain at step %d (%s): Tool requires input type %s, but previous step produces %s",
                            i + 1, tool.getName(), accepts, currentFileType
                    ));
                }
                currentFileType = tool.getProduces();
            }
        }
    }
}
