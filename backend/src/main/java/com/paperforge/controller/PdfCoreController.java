package com.paperforge.controller;

import com.paperforge.dto.CropRequestDto;
import com.paperforge.dto.ErrorResponseDto;
import com.paperforge.dto.PdfMetadataDto;
import com.paperforge.dto.RotateRequestDto;
import com.paperforge.service.PdfCoreService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pdf/core")
public class PdfCoreController {

    private final PdfCoreService pdfCoreService;

    public PdfCoreController(PdfCoreService pdfCoreService) {
        this.pdfCoreService = pdfCoreService;
    }

    @PostMapping("/merge")
    public ResponseEntity<byte[]> mergePdfs(@RequestParam("files") List<MultipartFile> files) throws IOException {
        byte[] pdfBytes = pdfCoreService.mergePdfs(files);
        return createPdfResponse(pdfBytes, "paperforge_merged.pdf");
    }

    @PostMapping("/split")
    public ResponseEntity<byte[]> splitPdf(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "splitFrequency", defaultValue = "1") int splitFrequency) throws IOException {
        byte[] zipBytes = pdfCoreService.splitPdf(file, splitFrequency);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/zip"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"paperforge_split.zip\"")
                .body(zipBytes);
    }

    @PostMapping("/rotate")
    public ResponseEntity<byte[]> rotatePdf(
            @RequestPart("file") MultipartFile file,
            @RequestPart("request") RotateRequestDto request) throws IOException {
        byte[] pdfBytes = pdfCoreService.rotatePdf(file, request);
        return createPdfResponse(pdfBytes, "paperforge_rotated.pdf");
    }

    @PostMapping("/delete-pages")
    public ResponseEntity<byte[]> deletePages(
            @RequestParam("file") MultipartFile file,
            @RequestParam("pageNumbers") List<Integer> pageNumbers) throws IOException {
        byte[] pdfBytes = pdfCoreService.deletePages(file, pageNumbers);
        return createPdfResponse(pdfBytes, "paperforge_deleted.pdf");
    }

    @PostMapping("/extract-pages")
    public ResponseEntity<byte[]> extractPages(
            @RequestParam("file") MultipartFile file,
            @RequestParam("pageNumbers") List<Integer> pageNumbers) throws IOException {
        byte[] pdfBytes = pdfCoreService.extractPages(file, pageNumbers);
        return createPdfResponse(pdfBytes, "paperforge_extracted.pdf");
    }

    @PostMapping("/reorder-pages")
    public ResponseEntity<byte[]> reorderPages(
            @RequestParam("file") MultipartFile file,
            @RequestParam("pageOrder") List<Integer> pageOrder) throws IOException {
        byte[] pdfBytes = pdfCoreService.reorderPages(file, pageOrder);
        return createPdfResponse(pdfBytes, "paperforge_reordered.pdf");
    }

    @PostMapping("/crop")
    public ResponseEntity<byte[]> cropPdf(
            @RequestPart("file") MultipartFile file,
            @RequestPart("request") CropRequestDto request) throws IOException {
        byte[] pdfBytes = pdfCoreService.cropPdf(file, request);
        return createPdfResponse(pdfBytes, "paperforge_cropped.pdf");
    }

    @PostMapping("/metadata/get")
    public ResponseEntity<PdfMetadataDto> getMetadata(@RequestParam("file") MultipartFile file) throws IOException {
        PdfMetadataDto metadata = pdfCoreService.getMetadata(file);
        return ResponseEntity.ok(metadata);
    }

    @PostMapping("/metadata/set")
    public ResponseEntity<byte[]> setMetadata(
            @RequestPart("file") MultipartFile file,
            @RequestPart("metadata") PdfMetadataDto metadata) throws IOException {
        byte[] pdfBytes = pdfCoreService.setMetadata(file, metadata);
        return createPdfResponse(pdfBytes, "paperforge_metadata_updated.pdf");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponseDto error = new ErrorResponseDto("Bad Request", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    private ResponseEntity<byte[]> createPdfResponse(byte[] bytes, String filename) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(bytes);
    }
}
