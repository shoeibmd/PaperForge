package com.paperforge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paperforge.dto.OcrRequestDto;
import com.paperforge.service.TempFileService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OcrControllerTest {

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
    void testOcrEndpointSuccess() throws Exception {
        int initialTempFiles = getTempFileCount();
        byte[] pdfBytes = createValidPdfBytes(1);
        MockMultipartFile file = new MockMultipartFile("file", "scanned.pdf", "application/pdf", pdfBytes);

        OcrRequestDto reqDto = new OcrRequestDto(List.of("eng", "fra"), List.of(1), true, 150);
        MockMultipartFile jsonReq = new MockMultipartFile("request", "", "application/json", objectMapper.writeValueAsBytes(reqDto));

        mockMvc.perform(multipart("/api/v1/pdf/ocr")
                        .file(file)
                        .file(jsonReq))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"paperforge_ocr_searchable.pdf\""));

        assertEquals(initialTempFiles, getTempFileCount(), "No temp files leaked after OCR processing");
    }

    @Test
    void testOcrEndpointInvalidLanguageRejection() throws Exception {
        int initialTempFiles = getTempFileCount();
        byte[] pdfBytes = createValidPdfBytes(1);
        MockMultipartFile file = new MockMultipartFile("file", "scanned.pdf", "application/pdf", pdfBytes);

        OcrRequestDto reqDto = new OcrRequestDto(List.of("invalid_lang_code; rm -rf /"), List.of(1), false, 150);
        MockMultipartFile jsonReq = new MockMultipartFile("request", "", "application/json", objectMapper.writeValueAsBytes(reqDto));

        mockMvc.perform(multipart("/api/v1/pdf/ocr")
                        .file(file)
                        .file(jsonReq))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", containsString("Unsupported or invalid OCR language code")));

        assertEquals(initialTempFiles, getTempFileCount(), "No temp files leaked on error");
    }
}
