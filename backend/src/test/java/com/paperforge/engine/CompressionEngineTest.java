package com.paperforge.engine;

import com.paperforge.dto.CompressionRequestDto;
import com.paperforge.manager.QpdfProcessManager;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class CompressionEngineTest {

    private QpdfProcessManager qpdfProcessManager;
    private CompressionEngine compressionEngine;

    @BeforeEach
    void setUp() {
        qpdfProcessManager = mock(QpdfProcessManager.class);
        compressionEngine = new CompressionEngine(qpdfProcessManager);
    }

    private File createTestPdf(Path tempDir, String fileName) throws IOException {
        File file = tempDir.resolve(fileName).toFile();
        try (PDDocument doc = new PDDocument()) {
            doc.addPage(new PDPage());
            doc.save(file);
        }
        return file;
    }

    @Test
    void testFallbackWhenQpdfUnavailable(@TempDir Path tempDir) throws IOException {
        when(qpdfProcessManager.isQpdfAvailable()).thenReturn(false);

        File pdfInput = createTestPdf(tempDir, "input.pdf");
        File pdfOutput = tempDir.resolve("output_compressed.pdf").toFile();

        CompressionRequestDto request = new CompressionRequestDto("BALANCED", 150, 0.7f, true, true);
        compressionEngine.compressPdf(pdfInput, request, pdfOutput);

        assertTrue(pdfOutput.exists());
        try (PDDocument doc = Loader.loadPDF(pdfOutput)) {
            assertEquals(1, doc.getNumberOfPages());
        }
    }

    @Test
    void testQpdfExecutionSuccessMocked(@TempDir Path tempDir) throws IOException {
        when(qpdfProcessManager.isQpdfAvailable()).thenReturn(true);
        doAnswer(invocation -> {
            File out = invocation.getArgument(1);
            try (PDDocument doc = new PDDocument()) {
                doc.addPage(new PDPage());
                doc.save(out);
            }
            return null;
        }).when(qpdfProcessManager).optimizePdf(any(), any(), anyString(), anyBoolean(), anyBoolean());

        File pdfInput = createTestPdf(tempDir, "input.pdf");
        File pdfOutput = tempDir.resolve("output_compressed.pdf").toFile();

        CompressionRequestDto request = new CompressionRequestDto("HIGH", 150, 0.5f, false, true);
        compressionEngine.compressPdf(pdfInput, request, pdfOutput);

        assertTrue(pdfOutput.exists());
        verify(qpdfProcessManager, times(1)).optimizePdf(any(), any(), eq("HIGH"), eq(true), eq(false));
    }
}
