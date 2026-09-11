package com.paperforge.service;

import com.paperforge.dto.OcrRequestDto;
import com.paperforge.engine.OcrEngine;
import com.paperforge.util.OcrValidationUtils;
import com.paperforge.util.PdfValidationUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Service
public class OcrService {

    private final OcrEngine ocrEngine;
    private final TempFileService tempFileService;
    private final AuditLogService auditLogService;

    public OcrService(OcrEngine ocrEngine, TempFileService tempFileService, AuditLogService auditLogService) {
        this.ocrEngine = ocrEngine;
        this.tempFileService = tempFileService;
        this.auditLogService = auditLogService;
    }

    public byte[] performOcr(MultipartFile file, OcrRequestDto request, String clientIp) throws IOException {
        PdfValidationUtils.validatePdfFile(file);
        OcrValidationUtils.validateLanguages(request.getLanguages());

        String langString = request.getLanguages() != null ? String.join(",", request.getLanguages()) : "eng";
        auditLogService.logSecurityEvent("OCR_REQUEST", clientIp, "file=" + file.getOriginalFilename() + ", languages=" + langString);

        File tempInputPdf = null;
        File tempOutputFile = null;

        try {
            tempInputPdf = tempFileService.createTempFile("ocr_input", ".pdf");
            file.transferTo(tempInputPdf);

            tempOutputFile = tempFileService.createTempFile("ocr_output", ".pdf");

            ocrEngine.generateSearchablePdf(tempInputPdf, request, tempOutputFile, (prefix, suffix) -> {
                try {
                    return tempFileService.createTempFile(prefix, suffix);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to create temporary OCR work file", e);
                }
            });

            return Files.readAllBytes(tempOutputFile.toPath());
        } finally {
            tempFileService.deleteTempFile(tempInputPdf);
            tempFileService.deleteTempFile(tempOutputFile);
        }
    }
}
