package com.paperforge.service;

import com.paperforge.dto.CropRequestDto;
import com.paperforge.dto.PdfMetadataDto;
import com.paperforge.dto.RotateRequestDto;
import com.paperforge.engine.PdfCoreEngine;
import com.paperforge.util.PdfValidationUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
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
public class PdfCoreService {

    private final PdfCoreEngine pdfCoreEngine;
    private final TempFileService tempFileService;

    public PdfCoreService(PdfCoreEngine pdfCoreEngine, TempFileService tempFileService) {
        this.pdfCoreEngine = pdfCoreEngine;
        this.tempFileService = tempFileService;
    }

    public byte[] mergePdfs(List<MultipartFile> files) throws IOException {
        if (files == null || files.size() < 2) {
            throw new IllegalArgumentException("At least two PDF files are required for merging");
        }

        List<File> tempInputFiles = new ArrayList<>();
        File tempOutputFile = null;

        try {
            for (MultipartFile file : files) {
                PdfValidationUtils.validatePdfFile(file);
                File tempInput = tempFileService.createTempFile("merge_input", ".pdf");
                file.transferTo(tempInput);
                tempInputFiles.add(tempInput);
            }

            tempOutputFile = tempFileService.createTempFile("merge_output", ".pdf");
            pdfCoreEngine.merge(tempInputFiles, tempOutputFile);

            return Files.readAllBytes(tempOutputFile.toPath());
        } finally {
            for (File file : tempInputFiles) {
                tempFileService.deleteTempFile(file);
            }
            tempFileService.deleteTempFile(tempOutputFile);
        }
    }

    public byte[] splitPdf(MultipartFile file, int splitFrequency) throws IOException {
        PdfValidationUtils.validatePdfFile(file);
        File tempInput = null;
        List<File> tempSplitFiles = new ArrayList<>();

        try {
            tempInput = tempFileService.createTempFile("split_input", ".pdf");
            file.transferTo(tempInput);

            List<PDDocument> splitDocs = pdfCoreEngine.split(tempInput, splitFrequency);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ZipOutputStream zos = new ZipOutputStream(baos)) {
                for (int i = 0; i < splitDocs.size(); i++) {
                    PDDocument doc = splitDocs.get(i);
                    File tempSplitOutput = tempFileService.createTempFile("split_part_" + (i + 1), ".pdf");
                    tempSplitFiles.add(tempSplitOutput);
                    doc.save(tempSplitOutput);
                    doc.close();

                    ZipEntry entry = new ZipEntry("split_page_" + (i + 1) + ".pdf");
                    zos.putNextEntry(entry);
                    try (FileInputStream fis = new FileInputStream(tempSplitOutput)) {
                        fis.transferTo(zos);
                    }
                    zos.closeEntry();
                }
            }

            return baos.toByteArray();
        } finally {
            tempFileService.deleteTempFile(tempInput);
            for (File tempFile : tempSplitFiles) {
                tempFileService.deleteTempFile(tempFile);
            }
        }
    }

    public byte[] rotatePdf(MultipartFile file, RotateRequestDto request) throws IOException {
        PdfValidationUtils.validatePdfFile(file);
        File tempInput = null;
        File tempOutput = null;

        try {
            tempInput = tempFileService.createTempFile("rotate_input", ".pdf");
            file.transferTo(tempInput);

            tempOutput = tempFileService.createTempFile("rotate_output", ".pdf");
            pdfCoreEngine.rotate(tempInput, request.getAngle(), request.getPageNumbers(), tempOutput);

            return Files.readAllBytes(tempOutput.toPath());
        } finally {
            tempFileService.deleteTempFile(tempInput);
            tempFileService.deleteTempFile(tempOutput);
        }
    }

    public byte[] deletePages(MultipartFile file, List<Integer> pageNumbers) throws IOException {
        PdfValidationUtils.validatePdfFile(file);
        File tempInput = null;
        File tempOutput = null;

        try {
            tempInput = tempFileService.createTempFile("delete_input", ".pdf");
            file.transferTo(tempInput);

            tempOutput = tempFileService.createTempFile("delete_output", ".pdf");
            pdfCoreEngine.deletePages(tempInput, pageNumbers, tempOutput);

            return Files.readAllBytes(tempOutput.toPath());
        } finally {
            tempFileService.deleteTempFile(tempInput);
            tempFileService.deleteTempFile(tempOutput);
        }
    }

    public byte[] extractPages(MultipartFile file, List<Integer> pageNumbers) throws IOException {
        PdfValidationUtils.validatePdfFile(file);
        File tempInput = null;
        File tempOutput = null;

        try {
            tempInput = tempFileService.createTempFile("extract_input", ".pdf");
            file.transferTo(tempInput);

            tempOutput = tempFileService.createTempFile("extract_output", ".pdf");
            pdfCoreEngine.extractPages(tempInput, pageNumbers, tempOutput);

            return Files.readAllBytes(tempOutput.toPath());
        } finally {
            tempFileService.deleteTempFile(tempInput);
            tempFileService.deleteTempFile(tempOutput);
        }
    }

    public byte[] reorderPages(MultipartFile file, List<Integer> pageOrder) throws IOException {
        PdfValidationUtils.validatePdfFile(file);
        File tempInput = null;
        File tempOutput = null;

        try {
            tempInput = tempFileService.createTempFile("reorder_input", ".pdf");
            file.transferTo(tempInput);

            tempOutput = tempFileService.createTempFile("reorder_output", ".pdf");
            pdfCoreEngine.reorderPages(tempInput, pageOrder, tempOutput);

            return Files.readAllBytes(tempOutput.toPath());
        } finally {
            tempFileService.deleteTempFile(tempInput);
            tempFileService.deleteTempFile(tempOutput);
        }
    }

    public byte[] cropPdf(MultipartFile file, CropRequestDto request) throws IOException {
        PdfValidationUtils.validatePdfFile(file);
        File tempInput = null;
        File tempOutput = null;

        try {
            tempInput = tempFileService.createTempFile("crop_input", ".pdf");
            file.transferTo(tempInput);

            tempOutput = tempFileService.createTempFile("crop_output", ".pdf");
            pdfCoreEngine.crop(tempInput, request.getX(), request.getY(), request.getWidth(), request.getHeight(), tempOutput);

            return Files.readAllBytes(tempOutput.toPath());
        } finally {
            tempFileService.deleteTempFile(tempInput);
            tempFileService.deleteTempFile(tempOutput);
        }
    }

    public PdfMetadataDto getMetadata(MultipartFile file) throws IOException {
        PdfValidationUtils.validatePdfFile(file);
        File tempInput = null;

        try {
            tempInput = tempFileService.createTempFile("metadata_input", ".pdf");
            file.transferTo(tempInput);

            return pdfCoreEngine.getMetadata(tempInput);
        } finally {
            tempFileService.deleteTempFile(tempInput);
        }
    }

    public byte[] setMetadata(MultipartFile file, PdfMetadataDto metadata) throws IOException {
        PdfValidationUtils.validatePdfFile(file);
        File tempInput = null;
        File tempOutput = null;

        try {
            tempInput = tempFileService.createTempFile("metadata_set_input", ".pdf");
            file.transferTo(tempInput);

            tempOutput = tempFileService.createTempFile("metadata_set_output", ".pdf");
            pdfCoreEngine.setMetadata(tempInput, metadata, tempOutput);

            return Files.readAllBytes(tempOutput.toPath());
        } finally {
            tempFileService.deleteTempFile(tempInput);
            tempFileService.deleteTempFile(tempOutput);
        }
    }
}
