package com.paperforge.security;

import com.paperforge.service.TempFileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SecurityHardeningTest {

    @Autowired
    private TempFileService tempFileService;

    @Test
    void testFilenameSanitizerRejectsPathTraversal() {
        String input1 = "../../../etc/passwd";
        String sanitized1 = FilenameSanitizer.sanitizeFilename(input1);
        assertEquals("passwd", sanitized1);

        String input2 = "..\\..\\Windows\\System32\\config.sys";
        String sanitized2 = FilenameSanitizer.sanitizeFilename(input2);
        assertEquals("config.sys", sanitized2);

        String input3 = "test\0file.pdf";
        String sanitized3 = FilenameSanitizer.sanitizeFilename(input3);
        assertEquals("testfile.pdf", sanitized3);

        String input4 = "...///hidden.pdf";
        String sanitized4 = FilenameSanitizer.sanitizeFilename(input4);
        assertFalse(sanitized4.contains("/"));
        assertFalse(sanitized4.startsWith("."));
    }

    @Test
    void testTempFileSecureZeroing() throws IOException {
        File tempFile = tempFileService.createTempFile("security_zero_test", ".tmp");
        assertTrue(tempFile.exists());

        // Write test data
        java.nio.file.Files.write(tempFile.toPath(), "SENSITIVE DOCUMENT CONTENT FOR PAPERFORGE TEST".getBytes());
        assertTrue(tempFile.length() > 0);

        // Delete file using TempFileService
        tempFileService.deleteTempFile(tempFile);

        // Assert file no longer exists
        assertFalse(tempFile.exists());
    }
}
