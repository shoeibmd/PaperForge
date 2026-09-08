package com.paperforge.controller;

import com.paperforge.dto.ErrorResponseDto;
import com.paperforge.service.ConversionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/pdf/convert")
public class ConversionController {

    private final ConversionService conversionService;

    public ConversionController(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @PostMapping("/to-pdf")
    public ResponseEntity<byte[]> convertToPdf(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest httpRequest) throws IOException, InterruptedException {
        byte[] pdfBytes = conversionService.convertToPdf(file, httpRequest.getRemoteAddr());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"paperforge_converted.pdf\"")
                .body(pdfBytes);
    }

    @PostMapping("/from-pdf")
    public ResponseEntity<byte[]> convertFromPdf(
            @RequestParam("file") MultipartFile file,
            @RequestParam("targetFormat") String targetFormat,
            HttpServletRequest httpRequest) throws IOException, InterruptedException {
        byte[] outputBytes = conversionService.convertFromPdf(file, targetFormat, httpRequest.getRemoteAddr());

        String mimeType = getMimeTypeForFormat(targetFormat);
        String filename = "paperforge_converted." + targetFormat.toLowerCase();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mimeType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(outputBytes);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponseDto error = new ErrorResponseDto("Bad Request", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalStateException(IllegalStateException ex) {
        ErrorResponseDto error = new ErrorResponseDto("Conversion Engine Unavailable", ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }

    private String getMimeTypeForFormat(String format) {
        return switch (format.toLowerCase()) {
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            case "txt" -> "text/plain";
            default -> "application/octet-stream";
        };
    }
}
