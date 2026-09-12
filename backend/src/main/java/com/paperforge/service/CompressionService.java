package com.paperforge.service;

import com.paperforge.dto.CompressionRequestDto;
import com.paperforge.dto.CompressionResultDto;
import com.paperforge.engine.CompressionEngine;
import com.paperforge.util.PdfValidationUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Service
public class CompressionService {

    private final CompressionEngine compressionEngine;
    private final TempFileService tempFileService;
    private final AuditLogService auditLogService;

    public CompressionService(CompressionEngine compressionEngine, TempFileService tempFileService, AuditLogService auditLogService) {
        this.compressionEngine = compressionEngine;
        this.tempFileService = tempFileService;
        this.auditLogService = auditLogService;
    }

    public CompressionResultDto compressPdf(MultipartFile file, CompressionRequestDto request, String clientIp) throws IOException {
        PdfValidationUtils.validatePdfFile(file);

        long originalSize = file.getSize();
        String level = request.getLevel() != null ? request.getLevel() : "BALANCED";

        auditLogService.logSecurityEvent("COMPRESS_PDF_REQUEST", clientIp, "file=" + file.getOriginalFilename() + ", level=" + level + ", originalSize=" + originalSize);

        File tempInput = null;
        File tempOutput = null;

        try {
            tempInput = tempFileService.createTempFile("compress_in", ".pdf");
            file.transferTo(tempInput);

            tempOutput = tempFileService.createTempFile("compress_out", ".pdf");

            compressionEngine.compressPdf(tempInput, request, tempOutput);

            byte[] compressedBytes = Files.readAllBytes(tempOutput.toPath());
            long compressedSize = compressedBytes.length;

            long savedBytes = Math.max(0, originalSize - compressedSize);
            double compressionPercentage = originalSize > 0 ? ((double) savedBytes / originalSize) * 100.0 : 0.0;

            return new CompressionResultDto(compressedBytes, originalSize, compressedSize, savedBytes, compressionPercentage);
        } finally {
            tempFileService.deleteTempFile(tempInput);
            tempFileService.deleteTempFile(tempOutput);
        }
    }
}
