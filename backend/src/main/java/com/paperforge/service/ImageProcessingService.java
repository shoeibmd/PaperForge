package com.paperforge.service;

import com.paperforge.dto.ImageToPdfRequestDto;
import com.paperforge.dto.PdfToImageRequestDto;
import com.paperforge.engine.ImageProcessingEngine;
import com.paperforge.util.ImageValidationUtils;
import com.paperforge.util.PdfValidationUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ImageProcessingService {

    private final ImageProcessingEngine imageProcessingEngine;
    private final TempFileService tempFileService;
    private final AuditLogService auditLogService;

    public ImageProcessingService(ImageProcessingEngine imageProcessingEngine, TempFileService tempFileService, AuditLogService auditLogService) {
        this.imageProcessingEngine = imageProcessingEngine;
        this.tempFileService = tempFileService;
        this.auditLogService = auditLogService;
    }

    public byte[] convertImagesToPdf(List<MultipartFile> files, ImageToPdfRequestDto request, String clientIp) throws IOException {
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("At least one image file is required");
        }

        auditLogService.logSecurityEvent("IMAGE_TO_PDF_REQUEST", clientIp, "fileCount=" + files.size());

        List<File> tempImageFiles = new ArrayList<>();
        File tempOutputFile = null;

        try {
            for (MultipartFile file : files) {
                ImageValidationUtils.validateImageFile(file);
                File tempImage = tempFileService.createTempFile("img_in", getExtensionWithDot(file.getOriginalFilename()));
                file.transferTo(tempImage);
                tempImageFiles.add(tempImage);
            }

            tempOutputFile = tempFileService.createTempFile("img_to_pdf_out", ".pdf");

            imageProcessingEngine.imagesToPdf(tempImageFiles, request, tempOutputFile);

            return Files.readAllBytes(tempOutputFile.toPath());
        } finally {
            for (File file : tempImageFiles) {
                tempFileService.deleteTempFile(file);
            }
            tempFileService.deleteTempFile(tempOutputFile);
        }
    }

    public ProcessedImageResult convertPdfToImages(MultipartFile file, PdfToImageRequestDto request, String clientIp) throws IOException {
        PdfValidationUtils.validatePdfFile(file);

        auditLogService.logSecurityEvent("PDF_TO_IMAGE_REQUEST", clientIp, "format=" + request.getFormat() + ", dpi=" + request.getDpi());

        File tempInputPdf = null;
        List<File> renderedImages = new ArrayList<>();

        try {
            tempInputPdf = tempFileService.createTempFile("pdf_to_img_in", ".pdf");
            file.transferTo(tempInputPdf);

            renderedImages = imageProcessingEngine.pdfToImages(tempInputPdf, request, (prefix, suffix) -> {
                try {
                    return tempFileService.createTempFile(prefix, suffix);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to create temporary image file", e);
                }
            });

            if (renderedImages.isEmpty()) {
                throw new IOException("No page images rendered from PDF");
            }

            if (renderedImages.size() == 1) {
                byte[] bytes = Files.readAllBytes(renderedImages.get(0).toPath());
                String format = request.getFormat() != null ? request.getFormat().toLowerCase() : "png";
                return new ProcessedImageResult(bytes, "page_1." + format, getMimeType(format));
            } else {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try (ZipOutputStream zos = new ZipOutputStream(baos)) {
                    for (File imgFile : renderedImages) {
                        ZipEntry entry = new ZipEntry(imgFile.getName());
                        zos.putNextEntry(entry);
                        try (FileInputStream fis = new FileInputStream(imgFile)) {
                            fis.transferTo(zos);
                        }
                        zos.closeEntry();
                    }
                }
                return new ProcessedImageResult(baos.toByteArray(), "paperforge_pages_images.zip", "application/zip");
            }
        } finally {
            tempFileService.deleteTempFile(tempInputPdf);
            for (File imgFile : renderedImages) {
                tempFileService.deleteTempFile(imgFile);
            }
        }
    }

    private String getExtensionWithDot(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf('.'));
        }
        return ".png";
    }

    private String getMimeType(String format) {
        return switch (format.toLowerCase()) {
            case "jpeg", "jpg" -> "image/jpeg";
            case "webp" -> "image/webp";
            case "tiff" -> "image/tiff";
            default -> "image/png";
        };
    }

    public static record ProcessedImageResult(byte[] data, String filename, String contentType) {}
}
