package com.paperforge.engine;

import com.paperforge.dto.ImageToPdfRequestDto;
import com.paperforge.dto.PdfToImageRequestDto;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ImageProcessingEngineTest {

    private ImageProcessingEngine imageEngine;

    @BeforeEach
    void setUp() {
        imageEngine = new ImageProcessingEngine();
    }

    private File createTestImageFile(Path tempDir, String fileName, String format, int width, int height) throws IOException {
        File file = tempDir.resolve(fileName).toFile();
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.BLUE);
        g.fillRect(0, 0, width, height);
        g.dispose();

        ImageIO.write(img, format, file);
        return file;
    }

    private File createTestPdf(Path tempDir, String fileName, int pageCount) throws IOException {
        File file = tempDir.resolve(fileName).toFile();
        try (PDDocument doc = new PDDocument()) {
            for (int i = 0; i < pageCount; i++) {
                doc.addPage(new PDPage());
            }
            doc.save(file);
        }
        return file;
    }

    @Test
    void testImagesToPdfConversion(@TempDir Path tempDir) throws IOException {
        File img1 = createTestImageFile(tempDir, "test1.png", "png", 200, 300);
        File img2 = createTestImageFile(tempDir, "test2.jpg", "jpg", 400, 400);

        File pdfOutput = tempDir.resolve("images_merged.pdf").toFile();

        ImageToPdfRequestDto request = new ImageToPdfRequestDto("A4", "PORTRAIT", "FIT_PAGE", 10.0f);
        imageEngine.imagesToPdf(List.of(img1, img2), request, pdfOutput);

        assertTrue(pdfOutput.exists());
        try (PDDocument doc = Loader.loadPDF(pdfOutput)) {
            assertEquals(2, doc.getNumberOfPages());
        }
    }

    @Test
    void testPdfToImagesRendering(@TempDir Path tempDir) throws IOException {
        File pdfFile = createTestPdf(tempDir, "test.pdf", 2);
        File outputDir = tempDir.resolve("img_out").toFile();
        outputDir.mkdirs();

        PdfToImageRequestDto request = new PdfToImageRequestDto("png", 150, 0.9f, List.of(1, 2));
        List<File> images = imageEngine.pdfToImages(pdfFile, request, (prefix, suffix) -> new File(outputDir, prefix + suffix));

        assertEquals(2, images.size());
        assertTrue(images.get(0).exists());
        assertTrue(images.get(1).exists());
    }
}
