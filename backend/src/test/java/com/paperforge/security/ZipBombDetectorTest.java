package com.paperforge.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.*;

class ZipBombDetectorTest {

    private ZipBombDetector detector;

    @BeforeEach
    void setUp() {
        // Strict limits for testing: 10:1 ratio, 1KB max size, 5 max entries, 3 max depth
        detector = new ZipBombDetector(10.0, 1024, 5, 3);
    }

    @Test
    void inspectZipStream_SafeZip_Success() throws Exception {
        byte[] zipBytes = createMockZip("hello.txt", "Hello World!".getBytes(StandardCharsets.UTF_8));
        ByteArrayInputStream bais = new ByteArrayInputStream(zipBytes);

        assertDoesNotThrow(() -> detector.inspectZipStream(bais, zipBytes.length));
    }

    @Test
    void inspectZipStream_ExceedsMaxEntries_ThrowsException() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (int i = 0; i < 6; i++) {
                zos.putNextEntry(new ZipEntry("file_" + i + ".txt"));
                zos.write("test".getBytes());
                zos.closeEntry();
            }
        }
        byte[] zipBytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(zipBytes);

        SecurityException ex = assertThrows(SecurityException.class, () ->
                detector.inspectZipStream(bais, zipBytes.length));
        assertTrue(ex.getMessage().contains("too many entries"));
    }

    @Test
    void inspectZipStream_ExceedsMaxDepth_ThrowsException() throws Exception {
        byte[] zipBytes = createMockZip("a/b/c/d/e/file.txt", "deep".getBytes());
        ByteArrayInputStream bais = new ByteArrayInputStream(zipBytes);

        SecurityException ex = assertThrows(SecurityException.class, () ->
                detector.inspectZipStream(bais, zipBytes.length));
        assertTrue(ex.getMessage().contains("depth exceeds limit"));
    }

    @Test
    void inspectZipStream_PathTraversal_ThrowsException() throws Exception {
        byte[] zipBytes = createMockZip("../malicious.txt", "payload".getBytes());
        ByteArrayInputStream bais = new ByteArrayInputStream(zipBytes);

        SecurityException ex = assertThrows(SecurityException.class, () ->
                detector.inspectZipStream(bais, zipBytes.length));
        assertTrue(ex.getMessage().contains("path traversal sequence"));
    }

    private byte[] createMockZip(String entryName, byte[] content) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            zos.putNextEntry(new ZipEntry(entryName));
            zos.write(content);
            zos.closeEntry();
        }
        return baos.toByteArray();
    }
}
