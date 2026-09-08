package com.paperforge.engine;

import com.paperforge.dto.PdfMetadataDto;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PdfCoreEngineTest {

    private PdfCoreEngine pdfCoreEngine;

    @BeforeEach
    void setUp() {
        pdfCoreEngine = new PdfCoreEngine();
    }

    private File createTestPdf(Path tempDir, String fileName, int pageCount) throws IOException {
        File file = tempDir.resolve(fileName).toFile();
        try (PDDocument doc = new PDDocument()) {
            for (int i = 0; i < pageCount; i++) {
                PDPage page = new PDPage();
                doc.addPage(page);
                try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                    cs.beginText();
                    cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
                    cs.newLineAtOffset(100, 700);
                    cs.showText("Page " + (i + 1));
                    cs.endText();
                }
            }
            doc.save(file);
        }
        return file;
    }

    @Test
    void testMergePdfs(@TempDir Path tempDir) throws IOException {
        File pdf1 = createTestPdf(tempDir, "doc1.pdf", 2);
        File pdf2 = createTestPdf(tempDir, "doc2.pdf", 3);
        File mergedOutput = tempDir.resolve("merged.pdf").toFile();

        pdfCoreEngine.merge(List.of(pdf1, pdf2), mergedOutput);

        assertTrue(mergedOutput.exists());
        try (PDDocument mergedDoc = Loader.loadPDF(mergedOutput)) {
            assertEquals(5, mergedDoc.getNumberOfPages());
        }
    }

    @Test
    void testSplitPdf(@TempDir Path tempDir) throws IOException {
        File pdf = createTestPdf(tempDir, "doc.pdf", 4);

        List<PDDocument> splitDocs = pdfCoreEngine.split(pdf, 1);
        assertEquals(4, splitDocs.size());
        for (PDDocument doc : splitDocs) {
            assertEquals(1, doc.getNumberOfPages());
            doc.close();
        }
    }

    @Test
    void testRotatePdf(@TempDir Path tempDir) throws IOException {
        File pdf = createTestPdf(tempDir, "doc.pdf", 2);
        File rotatedOutput = tempDir.resolve("rotated.pdf").toFile();

        pdfCoreEngine.rotate(pdf, 90, List.of(1), rotatedOutput);

        try (PDDocument doc = Loader.loadPDF(rotatedOutput)) {
            assertEquals(90, doc.getPage(0).getRotation());
            assertEquals(0, doc.getPage(1).getRotation());
        }
    }

    @Test
    void testDeletePages(@TempDir Path tempDir) throws IOException {
        File pdf = createTestPdf(tempDir, "doc.pdf", 3);
        File deletedOutput = tempDir.resolve("deleted.pdf").toFile();

        pdfCoreEngine.deletePages(pdf, List.of(2), deletedOutput);

        try (PDDocument doc = Loader.loadPDF(deletedOutput)) {
            assertEquals(2, doc.getNumberOfPages());
        }
    }

    @Test
    void testExtractPages(@TempDir Path tempDir) throws IOException {
        File pdf = createTestPdf(tempDir, "doc.pdf", 5);
        File extractedOutput = tempDir.resolve("extracted.pdf").toFile();

        pdfCoreEngine.extractPages(pdf, List.of(1, 3, 5), extractedOutput);

        try (PDDocument doc = Loader.loadPDF(extractedOutput)) {
            assertEquals(3, doc.getNumberOfPages());
        }
    }

    @Test
    void testCropPdf(@TempDir Path tempDir) throws IOException {
        File pdf = createTestPdf(tempDir, "doc.pdf", 1);
        File croppedOutput = tempDir.resolve("cropped.pdf").toFile();

        pdfCoreEngine.crop(pdf, 10, 10, 400, 500, croppedOutput);

        try (PDDocument doc = Loader.loadPDF(croppedOutput)) {
            assertEquals(10, doc.getPage(0).getCropBox().getLowerLeftX());
            assertEquals(400, doc.getPage(0).getCropBox().getWidth());
        }
    }

    @Test
    void testMetadataReadAndWrite(@TempDir Path tempDir) throws IOException {
        File pdf = createTestPdf(tempDir, "doc.pdf", 1);
        File metadataOutput = tempDir.resolve("metadata.pdf").toFile();

        PdfMetadataDto inputMeta = new PdfMetadataDto("PaperForge Title", "PaperForge Author", "Testing Subject", "PDF,PaperForge", "PaperForge Studio", "PaperForge Producer");
        pdfCoreEngine.setMetadata(pdf, inputMeta, metadataOutput);

        PdfMetadataDto readMeta = pdfCoreEngine.getMetadata(metadataOutput);
        assertEquals("PaperForge Title", readMeta.getTitle());
        assertEquals("PaperForge Author", readMeta.getAuthor());
        assertEquals("Testing Subject", readMeta.getSubject());
        assertEquals("PDF,PaperForge", readMeta.getKeywords());
    }
}
