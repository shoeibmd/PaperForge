package com.paperforge.controller;

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

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = "USER")
class ConversionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TempFileService tempFileService;

    private byte[] createValidPdfBytes() throws IOException {
        try (PDDocument doc = new PDDocument();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            doc.addPage(new PDPage());
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
    void testTextToPdfConversionEndpointSuccess() throws Exception {
        int initialTempFiles = getTempFileCount();
        byte[] txtBytes = "Hello PaperForge Document Studio!".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", "document.txt", "text/plain", txtBytes);

        mockMvc.perform(multipart("/api/v1/pdf/convert/to-pdf").file(file))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"paperforge_converted.pdf\""));

        assertEquals(initialTempFiles, getTempFileCount(), "No temp files leaked during conversion");
    }

    @Test
    void testPdfToTxtExportEndpointSuccess() throws Exception {
        int initialTempFiles = getTempFileCount();
        byte[] pdfBytes = createValidPdfBytes();
        MockMultipartFile file = new MockMultipartFile("file", "document.pdf", "application/pdf", pdfBytes);

        mockMvc.perform(multipart("/api/v1/pdf/convert/from-pdf")
                        .file(file)
                        .param("targetFormat", "txt"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "text/plain"));

        assertEquals(initialTempFiles, getTempFileCount(), "No temp files leaked during conversion");
    }

    @Test
    void testUnsupportedExtensionRejection() throws Exception {
        int initialTempFiles = getTempFileCount();
        byte[] exeBytes = "MZ... binary data".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", "malicious.exe", "application/x-msdownload", exeBytes);

        mockMvc.perform(multipart("/api/v1/pdf/convert/to-pdf").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("Unsupported input document format: exe")));

        assertEquals(initialTempFiles, getTempFileCount(), "No temp files leaked on error");
    }
}
