package com.paperforge.engine;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ConversionEngineTest {

    private ConversionEngine conversionEngine;
    private LibreOfficeProcessManager processManager;

    @BeforeEach
    void setUp() {
        processManager = new LibreOfficeProcessManager();
        conversionEngine = new ConversionEngine(processManager);
    }

    @Test
    void testPureJavaTextToPdfFallback(@TempDir Path tempDir) throws IOException, InterruptedException {
        File txtFile = tempDir.resolve("sample.txt").toFile();
        Files.writeString(txtFile.toPath(), "PaperForge Document Studio\nLine 2 of sample text file.");

        File pdfOutput = tempDir.resolve("sample.pdf").toFile();

        // Convert using pure Java PDFBox fallback
        conversionEngine.convertToPdf(txtFile, "txt", pdfOutput, tempDir);

        assertTrue(pdfOutput.exists());
        try (PDDocument doc = Loader.loadPDF(pdfOutput)) {
            assertEquals(1, doc.getNumberOfPages());
        }
    }

    @Test
    void testUnsupportedExtensionThrowsException(@TempDir Path tempDir) {
        File fakeFile = tempDir.resolve("script.exe").toFile();
        File pdfOutput = tempDir.resolve("output.pdf").toFile();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                conversionEngine.convertToPdf(fakeFile, "exe", pdfOutput, tempDir)
        );
        assertTrue(ex.getMessage().contains("Unsupported input document format"));
    }

    @Test
    void testUnsupportedExportFormatThrowsException(@TempDir Path tempDir) {
        File pdfFile = tempDir.resolve("doc.pdf").toFile();
        File outputFile = tempDir.resolve("output.exe").toFile();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                conversionEngine.convertFromPdf(pdfFile, "exe", outputFile, tempDir)
        );
        assertTrue(ex.getMessage().contains("Unsupported target export format"));
    }
}
