package com.paperforge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paperforge.dto.DecryptRequestDto;
import com.paperforge.dto.EncryptRequestDto;
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
class PdfSecurityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    void testEncryptAndDecryptEndpointsSuccess() throws Exception {
        int initialTempFiles = getTempFileCount();
        byte[] pdfBytes = createValidPdfBytes();
        MockMultipartFile file = new MockMultipartFile("file", "document.pdf", "application/pdf", pdfBytes);

        EncryptRequestDto encryptDto = new EncryptRequestDto("userPass", "ownerPass", 128, true, false, true, true);
        MockMultipartFile jsonReq = new MockMultipartFile("request", "", "application/json", objectMapper.writeValueAsBytes(encryptDto));

        byte[] protectedPdf = mockMvc.perform(multipart("/api/v1/pdf/security/encrypt")
                        .file(file)
                        .file(jsonReq))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andReturn().getResponse().getContentAsByteArray();

        // Decrypt protected PDF
        MockMultipartFile protectedFile = new MockMultipartFile("file", "protected.pdf", "application/pdf", protectedPdf);
        DecryptRequestDto decryptDto = new DecryptRequestDto("userPass");
        MockMultipartFile decryptJsonReq = new MockMultipartFile("request", "", "application/json", objectMapper.writeValueAsBytes(decryptDto));

        mockMvc.perform(multipart("/api/v1/pdf/security/decrypt")
                        .file(protectedFile)
                        .file(decryptJsonReq))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"));

        assertEquals(initialTempFiles, getTempFileCount(), "No temp files leaked during security operations");
    }

    @Test
    void testDecryptWithInvalidPasswordReturns400() throws Exception {
        int initialTempFiles = getTempFileCount();
        byte[] pdfBytes = createValidPdfBytes();
        MockMultipartFile file = new MockMultipartFile("file", "document.pdf", "application/pdf", pdfBytes);

        EncryptRequestDto encryptDto = new EncryptRequestDto("correctPass", "ownerPass", 128, true, false, true, true);
        MockMultipartFile jsonReq = new MockMultipartFile("request", "", "application/json", objectMapper.writeValueAsBytes(encryptDto));

        byte[] protectedPdf = mockMvc.perform(multipart("/api/v1/pdf/security/encrypt")
                        .file(file)
                        .file(jsonReq))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();

        MockMultipartFile protectedFile = new MockMultipartFile("file", "protected.pdf", "application/pdf", protectedPdf);
        DecryptRequestDto decryptDto = new DecryptRequestDto("wrongPassword");
        MockMultipartFile decryptJsonReq = new MockMultipartFile("request", "", "application/json", objectMapper.writeValueAsBytes(decryptDto));

        mockMvc.perform(multipart("/api/v1/pdf/security/decrypt")
                        .file(protectedFile)
                        .file(decryptJsonReq))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("Incorrect password provided for PDF decryption")));

        assertEquals(initialTempFiles, getTempFileCount(), "No temp files leaked on decryption error");
    }
}
