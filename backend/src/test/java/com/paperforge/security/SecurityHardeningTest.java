package com.paperforge.security;

import com.paperforge.util.OcrValidationUtils;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SecurityHardeningTest {

    @Test
    void filenameSanitizer_PreventsPathTraversalAndCommandInjection() {
        String malicious = "passwd; rm -rf file.pdf";
        String sanitized = FilenameSanitizer.sanitizeFilename(malicious);

        assertFalse(sanitized.contains(".."));
        assertFalse(sanitized.contains(";"));
        assertFalse(sanitized.contains("/"));
        assertEquals("passwd__rm_-rf_file.pdf", sanitized);
    }

    @Test
    void ocrLanguageValidation_RejectsCommandInjection() {
        List<String> maliciousLangs = List.of("eng; cat /etc/shadow");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                OcrValidationUtils.buildLanguageParam(maliciousLangs));

        assertTrue(ex.getMessage().contains("Unsupported or invalid OCR language code"));
    }
}
