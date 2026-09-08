package com.paperforge.engine;

import com.paperforge.dto.EncryptRequestDto;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class PdfSecurityEngineTest {

    private PdfSecurityEngine pdfSecurityEngine;

    @BeforeEach
    void setUp() {
        pdfSecurityEngine = new PdfSecurityEngine();
    }

    private File createTestPdf(Path tempDir, String fileName) throws IOException {
        File file = tempDir.resolve(fileName).toFile();
        try (PDDocument doc = new PDDocument()) {
            doc.addPage(new PDPage());
            doc.save(file);
        }
        return file;
    }

    @Test
    void testEncryptAndDecryptRoundTrip(@TempDir Path tempDir) throws IOException {
        File inputPdf = createTestPdf(tempDir, "original.pdf");
        File encryptedPdf = tempDir.resolve("encrypted.pdf").toFile();
        File decryptedPdf = tempDir.resolve("decrypted.pdf").toFile();

        EncryptRequestDto encryptReq = new EncryptRequestDto("user123", "owner123", 128, true, false, false, true);
        pdfSecurityEngine.encrypt(inputPdf, encryptReq, encryptedPdf);

        assertTrue(encryptedPdf.exists());

        // Opening without password throws InvalidPasswordException
        assertThrows(InvalidPasswordException.class, () -> Loader.loadPDF(encryptedPdf));

        // Decrypt with correct user password
        pdfSecurityEngine.decrypt(encryptedPdf, "user123", decryptedPdf);
        assertTrue(decryptedPdf.exists());

        try (PDDocument doc = Loader.loadPDF(decryptedPdf)) {
            assertFalse(doc.isEncrypted());
            assertEquals(1, doc.getNumberOfPages());
        }
    }

    @Test
    void testDecryptWithIncorrectPasswordThrowsException(@TempDir Path tempDir) throws IOException {
        File inputPdf = createTestPdf(tempDir, "original.pdf");
        File encryptedPdf = tempDir.resolve("encrypted.pdf").toFile();
        File decryptedPdf = tempDir.resolve("decrypted.pdf").toFile();

        EncryptRequestDto encryptReq = new EncryptRequestDto("secretPass", "ownerPass", 256, true, true, true, true);
        pdfSecurityEngine.encrypt(inputPdf, encryptReq, encryptedPdf);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                pdfSecurityEngine.decrypt(encryptedPdf, "wrongPass", decryptedPdf)
        );
        assertTrue(ex.getMessage().contains("Incorrect password"));
    }
}
