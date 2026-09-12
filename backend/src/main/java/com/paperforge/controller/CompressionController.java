package com.paperforge.controller;

import com.paperforge.dto.CompressionRequestDto;
import com.paperforge.dto.CompressionResultDto;
import com.paperforge.service.CompressionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/pdf/compress")
public class CompressionController {

    private final CompressionService compressionService;

    public CompressionController(CompressionService compressionService) {
        this.compressionService = compressionService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> compressPdf(
            @RequestPart("file") MultipartFile file,
            @RequestPart(value = "request", required = false) CompressionRequestDto request,
            HttpServletRequest httpRequest) throws IOException {

        if (request == null) {
            request = new CompressionRequestDto();
        }

        String clientIp = httpRequest.getRemoteAddr();
        CompressionResultDto result = compressionService.compressPdf(file, request, clientIp);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"paperforge_compressed.pdf\"");
        headers.add("X-PaperForge-Original-Size", String.valueOf(result.getOriginalSizeBytes()));
        headers.add("X-PaperForge-Compressed-Size", String.valueOf(result.getCompressedSizeBytes()));
        headers.add("X-PaperForge-Saved-Bytes", String.valueOf(result.getSavedBytes()));
        headers.add("X-PaperForge-Compression-Ratio", String.format("%.2f%%", result.getCompressionPercentage()));

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(result.getPdfData());
    }
}
