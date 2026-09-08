package com.paperforge.service;

import com.paperforge.dto.DecryptRequestDto;
import com.paperforge.dto.EncryptRequestDto;
import com.paperforge.engine.PdfSecurityEngine;
import com.paperforge.security.FilenameSanitizer;
import com.paperforge.util.PdfValidationUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.*;

@Service
public class PdfSecurityService {

    private final PdfSecurityEngine pdfSecurityEngine;
    private final TempFileService tempFileService;
    private final AuditLogService auditLogService;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public PdfSecurityService(PdfSecurityEngine pdfSecurityEngine, TempFileService tempFileService, AuditLogService auditLogService) {
        this.pdfSecurityEngine = pdfSecurityEngine;
        this.tempFileService = tempFileService;
        this.auditLogService = auditLogService;
    }

    public byte[] encryptPdf(MultipartFile file, EncryptRequestDto request, String clientIp) throws IOException {
        PdfValidationUtils.validatePdfFile(file);
        String safeName = FilenameSanitizer.sanitizeFilename(file.getOriginalFilename());
        auditLogService.logSecurityEvent("ENCRYPT_PDF_REQUEST", clientIp, "file=" + safeName + ", keyLength=" + request.getKeyLength());

        File tempInput = null;
        File tempOutput = null;

        try {
            tempInput = tempFileService.createTempFile("encrypt_input", ".pdf");
            file.transferTo(tempInput);

            tempOutput = tempFileService.createTempFile("encrypt_output", ".pdf");

            File finalTempInput = tempInput;
            File finalTempOutput = tempOutput;

            Future<byte[]> future = executorService.submit(() -> {
                pdfSecurityEngine.encrypt(finalTempInput, request, finalTempOutput);
                return Files.readAllBytes(finalTempOutput.toPath());
            });

            return future.get(120, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            auditLogService.logSecurityEvent("TIMEOUT_EXCEEDED", clientIp, "file=" + safeName);
            throw new IllegalArgumentException("PDF processing timed out");
        } catch (ExecutionException e) {
            if (e.getCause() instanceof IOException) {
                throw (IOException) e.getCause();
            } else if (e.getCause() instanceof IllegalArgumentException) {
                throw (IllegalArgumentException) e.getCause();
            }
            throw new IOException("Failed to encrypt PDF", e.getCause());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Encryption interrupted", e);
        } finally {
            tempFileService.deleteTempFile(tempInput);
            tempFileService.deleteTempFile(tempOutput);
        }
    }

    public byte[] decryptPdf(MultipartFile file, DecryptRequestDto request, String clientIp) throws IOException {
        PdfValidationUtils.validatePdfFile(file);
        String safeName = FilenameSanitizer.sanitizeFilename(file.getOriginalFilename());
        auditLogService.logSecurityEvent("DECRYPT_PDF_REQUEST", clientIp, "file=" + safeName);

        File tempInput = null;
        File tempOutput = null;

        try {
            tempInput = tempFileService.createTempFile("decrypt_input", ".pdf");
            file.transferTo(tempInput);

            tempOutput = tempFileService.createTempFile("decrypt_output", ".pdf");

            File finalTempInput = tempInput;
            File finalTempOutput = tempOutput;

            Future<byte[]> future = executorService.submit(() -> {
                pdfSecurityEngine.decrypt(finalTempInput, request.getPassword(), finalTempOutput);
                return Files.readAllBytes(finalTempOutput.toPath());
            });

            return future.get(120, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            auditLogService.logSecurityEvent("TIMEOUT_EXCEEDED", clientIp, "file=" + safeName);
            throw new IllegalArgumentException("PDF processing timed out");
        } catch (ExecutionException e) {
            if (e.getCause() instanceof IllegalArgumentException) {
                auditLogService.logSecurityEvent("DECRYPT_FAILED_INVALID_PASSWORD", clientIp, "file=" + safeName);
                throw (IllegalArgumentException) e.getCause();
            } else if (e.getCause() instanceof IOException) {
                throw (IOException) e.getCause();
            }
            throw new IOException("Failed to decrypt PDF", e.getCause());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Decryption interrupted", e);
        } finally {
            tempFileService.deleteTempFile(tempInput);
            tempFileService.deleteTempFile(tempOutput);
        }
    }
}
