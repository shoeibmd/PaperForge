package com.paperforge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paperforge.dto.ImageToPdfRequestDto;
import com.paperforge.dto.PdfToImageRequestDto;
import com.paperforge.service.TempFileService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ImageProcessingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TempFileService tempFileService;

    private byte[] createValidPngBytes(int width, int height) throws IOException {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "png", baos);
        return baos.toByteArray();
    }

    private byte[] createValidPdfBytes(int pageCount) throws IOException {
        try (PDDocument doc = new PDDocument();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            for (int i = 0; i < pageCount; i++) {
                doc.addPage(new PDPage());
            }
            doc.save(baos);
            return baos.toByteArray();
        }
    }

    private int getTempFileCount() {
        File dir = tempFileService.getTempDir().toFile();
        File[] files = dir.listFiles();
        return files == null ? 0 : files.length;
    }

    @Test
    void testImageToPdfEndpointSuccess() throws Exception {
        int initialTempFiles = getTempFileCount();
        byte[] pngBytes = createValidPngBytes(100, 100);
        MockMultipartFile file = new MockMultipartFile("files", "photo.png", "image/png", pngBytes);

        ImageToPdfRequestDto reqDto = new ImageToPdfRequestDto("A4", "PORTRAIT", "FIT_PAGE", 10.0f);
        MockMultipartFile jsonReq = new MockMultipartFile("request", "", "application/json", objectMapper.writeValueAsBytes(reqDto));

        mockMvc.perform(multipart("/api/v1/pdf/image/to-pdf")
                        .file(file)
                        .file(jsonReq))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"paperforge_images_merged.pdf\""));

        assertEquals(initialTempFiles, getTempFileCount(), "No temp files leaked after image to PDF processing");
    }

    @Test
    void testPdfToImageEndpointSuccessSinglePage() throws Exception {
        int initialTempFiles = getTempFileCount();
        byte[] pdfBytes = createValidPdfBytes(1);
        MockMultipartFile file = new MockMultipartFile("file", "single_page.pdf", "application/pdf", pdfBytes);

        PdfToImageRequestDto reqDto = new PdfToImageRequestDto("png", 150, 0.9f, null);
        MockMultipartFile jsonReq = new MockMultipartFile("request", "", "application/json", objectMapper.writeValueAsBytes(reqDto));

        mockMvc.perform(multipart("/api/v1/pdf/image/from-pdf")
                        .file(file)
                        .file(jsonReq))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "image/png"));

        assertEquals(initialTempFiles, getTempFileCount(), "No temp files leaked after single page render");
    }

    @Test
    void testPdfToImageEndpointSuccessMultiPageZip() throws Exception {
        int initialTempFiles = getTempFileCount();
        byte[] pdfBytes = createValidPdfBytes(2);
        MockMultipartFile file = new MockMultipartFile("file", "multi_page.pdf", "application/pdf", pdfBytes);

        PdfToImageRequestDto reqDto = new PdfToImageRequestDto("png", 150, 0.9f, null);
        MockMultipartFile jsonReq = new MockMultipartFile("request", "", "application/json", objectMapper.writeValueAsBytes(reqDto));

        mockMvc.perform(multipart("/api/v1/pdf/image/from-pdf")
                        .file(file)
                        .file(jsonReq))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/zip"));

        assertEquals(initialTempFiles, getTempFileCount(), "No temp files leaked after multi-page render");
    }

    @Test
    void testInvalidImageMagicBytesRejection() throws Exception {
        int initialTempFiles = getTempFileCount();
        byte[] fakeBytes = "This is a fake image file".getBytes();
        MockMultipartFile file = new MockMultipartFile("files", "fake.png", "image/png", fakeBytes);

        mockMvc.perform(multipart("/api/v1/pdf/image/to-pdf").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("Invalid image magic bytes. Supported formats: JPEG, PNG, WEBP, TIFF")));

        assertEquals(initialTempFiles, getTempFileCount(), "No temp files leaked on error");
    }
}
