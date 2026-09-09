package com.paperforge.util;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class ImageValidationUtils {

    private static final int MAX_DIMENSION = 10_000; // 10,000 px max width/height

    public static void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Image file cannot be empty");
        }

        try (InputStream is = file.getInputStream()) {
            byte[] header = new byte[12];
            int read = is.read(header);
            if (read < 4) {
                throw new IllegalArgumentException("Invalid image file header");
            }

            if (!isJpeg(header) && !isPng(header) && !isWebp(header) && !isTiff(header)) {
                throw new IllegalArgumentException("Invalid image magic bytes. Supported formats: JPEG, PNG, WEBP, TIFF");
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to read image file header", e);
        }

        // Validate image dimensions without loading full raster bitmap into heap
        try (InputStream is = file.getInputStream();
             ImageInputStream iis = ImageIO.createImageInputStream(is)) {
            if (iis == null) {
                throw new IllegalArgumentException("Unable to create ImageInputStream");
            }
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
            if (readers.hasNext()) {
                ImageReader reader = readers.next();
                try {
                    reader.setInput(iis, true, true);
                    int width = reader.getWidth(0);
                    int height = reader.getHeight(0);
                    if (width > MAX_DIMENSION || height > MAX_DIMENSION) {
                        throw new IllegalArgumentException("Image dimensions (" + width + "x" + height + ") exceed maximum allowed limit (" + MAX_DIMENSION + "x" + MAX_DIMENSION + ")");
                    }
                } finally {
                    reader.dispose();
                }
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to read image dimensions", e);
        }
    }

    private static boolean isJpeg(byte[] h) {
        return (h[0] & 0xFF) == 0xFF && (h[1] & 0xFF) == 0xD8 && (h[2] & 0xFF) == 0xFF;
    }

    private static boolean isPng(byte[] h) {
        return (h[0] & 0xFF) == 0x89 && h[1] == 'P' && h[2] == 'N' && h[3] == 'G';
    }

    private static boolean isWebp(byte[] h) {
        return h[0] == 'R' && h[1] == 'I' && h[2] == 'F' && h[3] == 'F' &&
               h[8] == 'W' && h[9] == 'E' && h[10] == 'B' && h[11] == 'P';
    }

    private static boolean isTiff(byte[] h) {
        return (h[0] == 0x49 && h[1] == 0x49) || (h[0] == 0x4D && h[1] == 0x4D);
    }
}
