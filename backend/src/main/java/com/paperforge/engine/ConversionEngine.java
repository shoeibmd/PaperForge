package com.paperforge.engine;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

@Component
public class ConversionEngine {

    private static final Logger logger = LoggerFactory.getLogger(ConversionEngine.class);
    private static final Set<String> ALLOWED_TO_PDF_EXTENSIONS = Set.of(
            "docx", "doc", "odt", "xlsx", "xls", "pptx", "ppt", "html", "htm", "txt", "csv"
    );
    private static final Set<String> ALLOWED_FROM_PDF_FORMATS = Set.of(
            "docx", "xlsx", "pptx", "txt"
    );

    private final LibreOfficeProcessManager processManager;

    public ConversionEngine(LibreOfficeProcessManager processManager) {
        this.processManager = processManager;
    }

    public void convertToPdf(File inputFile, String inputExtension, File outputFile, Path tempDir) throws IOException, InterruptedException {
        String ext = inputExtension.toLowerCase().replace(".", "");
        if (!ALLOWED_TO_PDF_EXTENSIONS.contains(ext)) {
            throw new IllegalArgumentException("Unsupported input document format: " + ext);
        }

        if (processManager.isLibreOfficeAvailable()) {
            File converted = processManager.convertDocument(inputFile, "pdf", outputFile.getParentFile(), tempDir);
            if (!converted.getAbsolutePath().equals(outputFile.getAbsolutePath())) {
                Files.move(converted.toPath(), outputFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
        } else if (isTextBasedFormat(ext)) {
            logger.info("LibreOffice not found on PATH. Using pure-Java PDFBox text renderer fallback for extension: {}", ext);
            renderTextToPdf(inputFile, outputFile);
        } else {
            throw new IllegalStateException("LibreOffice is required to convert " + ext.toUpperCase() + " documents to PDF, but soffice binary was not found on system PATH.");
        }
    }

    public void convertFromPdf(File pdfFile, String targetFormat, File outputFile, Path tempDir) throws IOException, InterruptedException {
        String format = targetFormat.toLowerCase();
        if (!ALLOWED_FROM_PDF_FORMATS.contains(format)) {
            throw new IllegalArgumentException("Unsupported target export format: " + format);
        }

        if (processManager.isLibreOfficeAvailable()) {
            File converted = processManager.convertDocument(pdfFile, format, outputFile.getParentFile(), tempDir);
            if (!converted.getAbsolutePath().equals(outputFile.getAbsolutePath())) {
                Files.move(converted.toPath(), outputFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
        } else if ("txt".equalsIgnoreCase(format)) {
            // Fallback for PDF to TXT using PDFBox PDFTextStripper
            org.apache.pdfbox.text.PDFTextStripper stripper = new org.apache.pdfbox.text.PDFTextStripper();
            try (PDDocument doc = org.apache.pdfbox.Loader.loadPDF(pdfFile)) {
                String text = stripper.getText(doc);
                Files.writeString(outputFile.toPath(), text);
            }
        } else {
            throw new IllegalStateException("LibreOffice is required to export PDF to " + format.toUpperCase() + ", but soffice binary was not found on system PATH.");
        }
    }

    private boolean isTextBasedFormat(String ext) {
        return Set.of("txt", "html", "htm", "csv").contains(ext.toLowerCase());
    }

    private void renderTextToPdf(File textFile, File outputFile) throws IOException {
        List<String> lines = Files.readAllLines(textFile.toPath());
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);

            PDPageContentStream cs = new PDPageContentStream(doc, page);
            cs.beginText();
            cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
            cs.newLineAtOffset(50, 750);

            float y = 750;
            for (String line : lines) {
                if (y < 50) {
                    cs.endText();
                    cs.close();

                    page = new PDPage();
                    doc.addPage(page);
                    cs = new PDPageContentStream(doc, page);
                    cs.beginText();
                    cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
                    cs.newLineAtOffset(50, 750);
                    y = 750;
                }

                // Sanitize string for PDFBox WinAnsiEncoding
                String cleanLine = line.replaceAll("[^\\x20-\\x7E]", " ");
                cs.showText(cleanLine);
                cs.newLineAtOffset(0, -14);
                y -= 14;
            }

            cs.endText();
            cs.close();
            doc.save(outputFile);
        }
    }
}
