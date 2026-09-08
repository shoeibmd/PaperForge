package com.paperforge.controller;

import com.paperforge.dto.DecryptRequestDto;
import com.paperforge.dto.EncryptRequestDto;
import com.paperforge.dto.ErrorResponseDto;
import com.paperforge.service.PdfSecurityService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/pdf/security")
public class PdfSecurityController {

    private final PdfSecurityService pdfSecurityService;

    public PdfSecurityController(PdfSecurityService pdfSecurityService) {
        this.pdfSecurityService = pdfSecurityService;
    }

    @PostMapping("/encrypt")
    public ResponseEntity<byte[]> encryptPdf(
            @RequestPart("file") MultipartFile file,
            @RequestPart("request") EncryptRequestDto request,
            HttpServletRequest httpRequest) throws IOException {
        byte[] pdfBytes = pdfSecurityService.encryptPdf(file, request, httpRequest.getRemoteAddr());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"paperforge_protected.pdf\"")
                .body(pdfBytes);
    }

    @PostMapping("/decrypt")
    public ResponseEntity<byte[]> decryptPdf(
            @RequestPart("file") MultipartFile file,
            @RequestPart("request") DecryptRequestDto request,
            HttpServletRequest httpRequest) throws IOException {
        byte[] pdfBytes = pdfSecurityService.decryptPdf(file, request, httpRequest.getRemoteAddr());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"paperforge_unprotected.pdf\"")
                .body(pdfBytes);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponseDto error = new ErrorResponseDto("Bad Request", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
