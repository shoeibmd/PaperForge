package com.paperforge.service;

import com.paperforge.model.FileType;
import com.paperforge.model.PipelineStep;
import com.paperforge.registry.ToolMetadata;
import com.paperforge.registry.ToolRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PipelineValidatorTest {

    private ToolRegistry toolRegistry;
    private PipelineValidator pipelineValidator;

    @BeforeEach
    void setUp() {
        toolRegistry = mock(ToolRegistry.class);
        pipelineValidator = new PipelineValidator(toolRegistry);
    }

    @Test
    void testValidChainSuccess() {
        when(toolRegistry.getTool("img-to-pdf")).thenReturn(Optional.of(new ToolMetadata("img-to-pdf", "Img2Pdf", "", FileType.IMAGE, FileType.PDF)));
        when(toolRegistry.getTool("ocr")).thenReturn(Optional.of(new ToolMetadata("ocr", "OCR", "", FileType.PDF, FileType.PDF)));
        when(toolRegistry.getTool("compress")).thenReturn(Optional.of(new ToolMetadata("compress", "Compress", "", FileType.PDF, FileType.PDF)));

        List<PipelineStep> steps = List.of(
                new PipelineStep(1, "img-to-pdf", null),
                new PipelineStep(2, "ocr", null),
                new PipelineStep(3, "compress", null)
        );

        assertDoesNotThrow(() -> pipelineValidator.validatePipelineSteps(steps));
    }

    @Test
    void testIncompatibleChainRejection() {
        when(toolRegistry.getTool("pdf-to-img")).thenReturn(Optional.of(new ToolMetadata("pdf-to-img", "Pdf2Img", "", FileType.PDF, FileType.IMAGE)));
        when(toolRegistry.getTool("compress")).thenReturn(Optional.of(new ToolMetadata("compress", "Compress", "", FileType.PDF, FileType.PDF)));

        List<PipelineStep> steps = List.of(
                new PipelineStep(1, "pdf-to-img", null),
                new PipelineStep(2, "compress", null) // Compress requires PDF, but previous produces IMAGE!
        );

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            pipelineValidator.validatePipelineSteps(steps);
        });

        assertTrue(ex.getMessage().contains("Incompatible pipeline chain"));
    }
}
