package com.paperforge.service;

import com.paperforge.engine.ConversionEngine;
import com.paperforge.security.FilenameSanitizer;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Service
public class ConversionService {

    private final ConversionEngine conversionEngine;
    private final TempFileService tempFileService;
    private final AuditLogService auditLogService;
    private final com.paperforge.security.ZipBombDetector zipBombDetector;

    public ConversionService(ConversionEngine conversionEngine, TempFileService tempFileService,
                             AuditLogService auditLogService, com.paperforge.security.ZipBombDetector zipBombDetector) {
        this.conversionEngine = conversionEngine;
        this.tempFileService = tempFileService;
        this.auditLogService = auditLogService;
        this.zipBombDetector = zipBombDetector;
    }

    public byte[] convertToPdf(MultipartFile file, String clientIp) throws IOException, InterruptedException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        String rawName = file.getOriginalFilename();
        String safeName = FilenameSanitizer.sanitizeFilename(rawName);
        String ext = getExtension(safeName);

        auditLogService.logSecurityEvent("CONVERT_TO_PDF_REQUEST", clientIp, "file=" + safeName + ", ext=" + ext);

        File tempInput = null;
        File tempOutput = null;

        try {
            if ("zip".equalsIgnoreCase(ext) || "epub".equalsIgnoreCase(ext)) {
                zipBombDetector.inspectZipStream(file.getInputStream(), file.getSize());
            }

            tempInput = tempFileService.createTempFile("convert_in", "." + ext);
            file.transferTo(tempInput);

            tempOutput = tempFileService.createTempFile("convert_out", ".pdf");

            conversionEngine.convertToPdf(tempInput, ext, tempOutput, tempFileService.getTempDir());

            return Files.readAllBytes(tempOutput.toPath());
        } finally {
            tempFileService.deleteTempFile(tempInput);
            tempFileService.deleteTempFile(tempOutput);
        }
    }

    public byte[] convertFromPdf(MultipartFile file, String targetFormat, String clientIp) throws IOException, InterruptedException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        String rawName = file.getOriginalFilename();
        String safeName = FilenameSanitizer.sanitizeFilename(rawName);

        auditLogService.logSecurityEvent("CONVERT_FROM_PDF_REQUEST", clientIp, "file=" + safeName + ", targetFormat=" + targetFormat);

        File tempInput = null;
        File tempOutput = null;

        try {
            tempInput = tempFileService.createTempFile("convert_from_pdf_in", ".pdf");
            file.transferTo(tempInput);

            tempOutput = tempFileService.createTempFile("convert_from_pdf_out", "." + targetFormat.toLowerCase());

            conversionEngine.convertFromPdf(tempInput, targetFormat, tempOutput, tempFileService.getTempDir());

            return Files.readAllBytes(tempOutput.toPath());
        } finally {
            tempFileService.deleteTempFile(tempInput);
            tempFileService.deleteTempFile(tempOutput);
        }
    }

    private String getExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1);
        }
        return "";
    }
}
