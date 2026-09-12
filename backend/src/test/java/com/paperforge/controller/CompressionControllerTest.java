package com.paperforge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paperforge.dto.CompressionRequestDto;
import com.paperforge.service.TempFileService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = "USER")
class CompressionControllerTest {

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
    void testCompressEndpointSuccess() throws Exception {
        int initialTempFiles = getTempFileCount();
        byte[] pdfBytes = createValidPdfBytes(2);
        MockMultipartFile file = new MockMultipartFile("file", "document.pdf", "application/pdf", pdfBytes);

        CompressionRequestDto reqDto = new CompressionRequestDto("BALANCED", 150, 0.7f, true, true);
        MockMultipartFile jsonReq = new MockMultipartFile("request", "", "application/json", objectMapper.writeValueAsBytes(reqDto));

        mockMvc.perform(multipart("/api/v1/pdf/compress")
                        .file(file)
                        .file(jsonReq))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"paperforge_compressed.pdf\""))
                .andExpect(header().exists("X-PaperForge-Original-Size"))
                .andExpect(header().exists("X-PaperForge-Compressed-Size"))
                .andExpect(header().exists("X-PaperForge-Saved-Bytes"))
                .andExpect(header().exists("X-PaperForge-Compression-Ratio"));

        assertEquals(initialTempFiles, getTempFileCount(), "No temp files leaked after compression processing");
    }
}
