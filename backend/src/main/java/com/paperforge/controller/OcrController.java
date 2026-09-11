package com.paperforge.controller;

import com.paperforge.dto.OcrRequestDto;
import com.paperforge.service.OcrService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/pdf/ocr")
public class OcrController {

    private final OcrService ocrService;

    public OcrController(OcrService ocrService) {
        this.ocrService = ocrService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> processOcr(
            @RequestPart("file") MultipartFile file,
            @RequestPart(value = "request", required = false) OcrRequestDto request,
            HttpServletRequest httpRequest) throws IOException {

        if (request == null) {
            request = new OcrRequestDto();
        }

        String clientIp = httpRequest.getRemoteAddr();
        byte[] pdfBytes = ocrService.performOcr(file, request, clientIp);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"paperforge_ocr_searchable.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
