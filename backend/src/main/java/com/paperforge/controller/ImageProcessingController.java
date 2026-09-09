package com.paperforge.controller;

import com.paperforge.dto.ErrorResponseDto;
import com.paperforge.dto.ImageToPdfRequestDto;
import com.paperforge.dto.PdfToImageRequestDto;
import com.paperforge.service.ImageProcessingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pdf/image")
public class ImageProcessingController {

    private final ImageProcessingService imageProcessingService;

    public ImageProcessingController(ImageProcessingService imageProcessingService) {
        this.imageProcessingService = imageProcessingService;
    }

    @PostMapping("/to-pdf")
    public ResponseEntity<byte[]> convertImagesToPdf(
            @RequestPart("files") List<MultipartFile> files,
            @RequestPart(value = "request", required = false) ImageToPdfRequestDto request,
            HttpServletRequest httpRequest) throws IOException {
        if (request == null) {
            request = new ImageToPdfRequestDto();
        }
        byte[] pdfBytes = imageProcessingService.convertImagesToPdf(files, request, httpRequest.getRemoteAddr());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"paperforge_images_merged.pdf\"")
                .body(pdfBytes);
    }

    @PostMapping("/from-pdf")
    public ResponseEntity<byte[]> convertPdfToImages(
            @RequestPart("file") MultipartFile file,
            @RequestPart(value = "request", required = false) PdfToImageRequestDto request,
            HttpServletRequest httpRequest) throws IOException {
        if (request == null) {
            request = new PdfToImageRequestDto();
        }
        ImageProcessingService.ProcessedImageResult result = imageProcessingService.convertPdfToImages(file, request, httpRequest.getRemoteAddr());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(result.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + result.filename() + "\"")
                .body(result.data());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponseDto error = new ErrorResponseDto("Bad Request", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
