package com.paperforge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paperforge.dto.CropRequestDto;
import com.paperforge.dto.PdfMetadataDto;
import com.paperforge.dto.RotateRequestDto;
import com.paperforge.service.TempFileService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = "USER")
class PdfCoreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TempFileService tempFileService;

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
    void testMergePdfsEndpointSuccess() throws Exception {
        int initialTempFiles = getTempFileCount();
        byte[] pdf1 = createValidPdfBytes(1);
        byte[] pdf2 = createValidPdfBytes(2);

        MockMultipartFile file1 = new MockMultipartFile("files", "doc1.pdf", "application/pdf", pdf1);
        MockMultipartFile file2 = new MockMultipartFile("files", "doc2.pdf", "application/pdf", pdf2);

        mockMvc.perform(multipart("/api/v1/pdf/core/merge")
                        .file(file1)
                        .file(file2))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"paperforge_merged.pdf\""));

        assertEquals(initialTempFiles, getTempFileCount(), "Temporary files must be cleaned up");
    }

    @Test
    void testSplitPdfEndpointSuccess() throws Exception {
        int initialTempFiles = getTempFileCount();
        byte[] pdf = createValidPdfBytes(3);
        MockMultipartFile file = new MockMultipartFile("file", "doc.pdf", "application/pdf", pdf);

        mockMvc.perform(multipart("/api/v1/pdf/core/split")
                        .file(file)
                        .param("splitFrequency", "1"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/zip"));

        assertEquals(initialTempFiles, getTempFileCount(), "Temporary files must be cleaned up");
    }

    @Test
    void testRotatePdfEndpointSuccess() throws Exception {
        int initialTempFiles = getTempFileCount();
        byte[] pdf = createValidPdfBytes(2);
        MockMultipartFile file = new MockMultipartFile("file", "doc.pdf", "application/pdf", pdf);

        RotateRequestDto rotateReq = new RotateRequestDto(90, List.of(1));
        MockMultipartFile jsonReq = new MockMultipartFile("request", "", "application/json", objectMapper.writeValueAsBytes(rotateReq));

        mockMvc.perform(multipart("/api/v1/pdf/core/rotate")
                        .file(file)
                        .file(jsonReq))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"));

        assertEquals(initialTempFiles, getTempFileCount(), "Temporary files must be cleaned up");
    }

    @Test
    void testCropPdfEndpointSuccess() throws Exception {
        int initialTempFiles = getTempFileCount();
        byte[] pdf = createValidPdfBytes(1);
        MockMultipartFile file = new MockMultipartFile("file", "doc.pdf", "application/pdf", pdf);

        CropRequestDto cropReq = new CropRequestDto(0, 0, 300, 400);
        MockMultipartFile jsonReq = new MockMultipartFile("request", "", "application/json", objectMapper.writeValueAsBytes(cropReq));

        mockMvc.perform(multipart("/api/v1/pdf/core/crop")
                        .file(file)
                        .file(jsonReq))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"));

        assertEquals(initialTempFiles, getTempFileCount(), "Temporary files must be cleaned up");
    }

    @Test
    void testGetAndSetMetadataEndpoints() throws Exception {
        int initialTempFiles = getTempFileCount();
        byte[] pdf = createValidPdfBytes(1);
        MockMultipartFile file = new MockMultipartFile("file", "doc.pdf", "application/pdf", pdf);

        // Get initial
        mockMvc.perform(multipart("/api/v1/pdf/core/metadata/get").file(file))
                .andExpect(status().isOk());

        // Set metadata
        PdfMetadataDto metaDto = new PdfMetadataDto("Title", "Author", "Subject", "Keys", "Creator", "Producer");
        MockMultipartFile jsonReq = new MockMultipartFile("metadata", "", "application/json", objectMapper.writeValueAsBytes(metaDto));

        byte[] updatedPdf = mockMvc.perform(multipart("/api/v1/pdf/core/metadata/set")
                        .file(file)
                        .file(jsonReq))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andReturn().getResponse().getContentAsByteArray();

        // Verify updated metadata
        MockMultipartFile updatedFile = new MockMultipartFile("file", "doc_updated.pdf", "application/pdf", updatedPdf);
        mockMvc.perform(multipart("/api/v1/pdf/core/metadata/get").file(updatedFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Title")))
                .andExpect(jsonPath("$.author", is("Author")));

        assertEquals(initialTempFiles, getTempFileCount(), "Temporary files must be cleaned up");
    }

    @Test
    void testInvalidPdfHeaderRejection() throws Exception {
        int initialTempFiles = getTempFileCount();
        byte[] invalidContent = "This is not a PDF file".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", "fake.pdf", "application/pdf", invalidContent);

        mockMvc.perform(multipart("/api/v1/pdf/core/split").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("Invalid PDF file header")));

        assertEquals(initialTempFiles, getTempFileCount(), "No temp files leaked on error");
    }

    @Test
    void testEmptyFileRejection() throws Exception {
        int initialTempFiles = getTempFileCount();
        MockMultipartFile file = new MockMultipartFile("file", "empty.pdf", "application/pdf", new byte[0]);

        mockMvc.perform(multipart("/api/v1/pdf/core/split").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("File cannot be empty")));

        assertEquals(initialTempFiles, getTempFileCount(), "No temp files leaked on error");
    }
}
