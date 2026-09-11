package com.paperforge.engine;

import com.paperforge.dto.OcrRequestDto;
import com.paperforge.manager.TesseractProcessManager;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class OcrEngineTest {

    private TesseractProcessManager tesseractProcessManager;
    private OcrEngine ocrEngine;

    @BeforeEach
    void setUp() {
        tesseractProcessManager = mock(TesseractProcessManager.class);
        ocrEngine = new OcrEngine(tesseractProcessManager);
    }

    private File createTestPdf(Path tempDir, String fileName, int pageCount) throws IOException {
        File file = tempDir.resolve(fileName).toFile();
        try (PDDocument doc = new PDDocument()) {
            for (int i = 0; i < pageCount; i++) {
                doc.addPage(new PDPage());
            }
            doc.save(file);
        }
        return file;
    }

    @Test
    void testFallbackWhenTesseractUnavailable(@TempDir Path tempDir) throws IOException {
        when(tesseractProcessManager.isTesseractAvailable()).thenReturn(false);

        File pdfInput = createTestPdf(tempDir, "input.pdf", 2);
        File pdfOutput = tempDir.resolve("output.pdf").toFile();

        OcrRequestDto request = new OcrRequestDto(List.of("eng"), List.of(1, 2), true, 150);
        ocrEngine.generateSearchablePdf(pdfInput, request, pdfOutput, (prefix, suffix) -> tempDir.resolve(prefix + suffix).toFile());

        assertTrue(pdfOutput.exists());
        try (PDDocument doc = Loader.loadPDF(pdfOutput)) {
            assertEquals(2, doc.getNumberOfPages());
        }
    }

    @Test
    void testTesseractExecutionSuccessMocked(@TempDir Path tempDir) throws IOException {
        when(tesseractProcessManager.isTesseractAvailable()).thenReturn(true);
        doAnswer(invocation -> {
            File ocrOutBase = invocation.getArgument(1);
            File ocrPdfOutput = new File(ocrOutBase.getAbsolutePath() + ".pdf");
            try (PDDocument doc = new PDDocument()) {
                doc.addPage(new PDPage());
                doc.save(ocrPdfOutput);
            }
            return null;
        }).when(tesseractProcessManager).executeOcr(any(), any(), anyString(), anyBoolean());

        File pdfInput = createTestPdf(tempDir, "input.pdf", 1);
        File pdfOutput = tempDir.resolve("output_searchable.pdf").toFile();

        OcrRequestDto request = new OcrRequestDto(List.of("eng", "spa"), List.of(1), true, 150);
        ocrEngine.generateSearchablePdf(pdfInput, request, pdfOutput, (prefix, suffix) -> tempDir.resolve(prefix + suffix).toFile());

        assertTrue(pdfOutput.exists());
        try (PDDocument doc = Loader.loadPDF(pdfOutput)) {
            assertEquals(1, doc.getNumberOfPages());
        }

        verify(tesseractProcessManager, times(1)).executeOcr(any(), any(), eq("eng+spa"), eq(true));
    }
}
