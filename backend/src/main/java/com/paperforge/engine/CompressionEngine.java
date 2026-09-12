package com.paperforge.engine;

import com.paperforge.dto.CompressionRequestDto;
import com.paperforge.manager.QpdfProcessManager;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class CompressionEngine {

    private static final Logger logger = LoggerFactory.getLogger(CompressionEngine.class);
    private final QpdfProcessManager qpdfProcessManager;

    public CompressionEngine(QpdfProcessManager qpdfProcessManager) {
        this.qpdfProcessManager = qpdfProcessManager;
    }

    public void compressPdf(File inputFile, CompressionRequestDto request, File outputFile) throws IOException {
        boolean stripMetadata = Boolean.TRUE.equals(request.getStripMetadata());

        File intermediateFile = inputFile;

        if (stripMetadata) {
            try (PDDocument doc = Loader.loadPDF(inputFile)) {
                doc.setDocumentInformation(new PDDocumentInformation());
                doc.save(outputFile);
                intermediateFile = outputFile;
            }
        }

        if (qpdfProcessManager.isQpdfAvailable()) {
            File qpdfInput = intermediateFile;
            qpdfProcessManager.optimizePdf(qpdfInput, outputFile, request.getLevel(), request.getLinearize(), request.getStripMetadata());
        } else {
            logger.warn("QPDF not available on PATH. Executing pure-Java PDFBox stream compression fallback.");
            try (PDDocument doc = Loader.loadPDF(intermediateFile)) {
                doc.save(outputFile);
            }
        }
    }
}
