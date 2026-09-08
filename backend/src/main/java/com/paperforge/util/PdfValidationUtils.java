package com.paperforge.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public class PdfValidationUtils {

    private static final byte[] PDF_HEADER = {'%', 'P', 'D', 'F', '-'};
    private static final long MAX_FILE_SIZE = 104_857_600L; // 100 MB

    public static void validatePdfFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum allowed limit (100MB)");
        }

        try (InputStream is = file.getInputStream()) {
            byte[] header = new byte[5];
            int bytesRead = is.read(header);
            if (bytesRead < 5) {
                throw new IllegalArgumentException("Invalid PDF file header");
            }
            for (int i = 0; i < PDF_HEADER.length; i++) {
                if (header[i] != PDF_HEADER[i]) {
                    throw new IllegalArgumentException("Invalid PDF file header");
                }
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to read file for validation", e);
        }
    }
}
