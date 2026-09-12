package com.paperforge.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LocalStorageProviderTest {

    private LocalStorageProvider storageProvider;

    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        storageProvider = new LocalStorageProvider(tempDir.toString());
    }

    @Test
    void testSaveAndGetFileSuccess() throws IOException {
        String testData = "PaperForge storage test content";
        InputStream is = new ByteArrayInputStream(testData.getBytes());

        storageProvider.saveFile(StorageCategory.USERS, "1/test.txt", is);

        assertTrue(storageProvider.exists(StorageCategory.USERS, "1/test.txt"));

        try (InputStream retrievedIs = storageProvider.getFile(StorageCategory.USERS, "1/test.txt")) {
            String retrievedData = new String(retrievedIs.readAllBytes());
            assertEquals(testData, retrievedData);
        }
    }

    @Test
    void testPathTraversalRejection() {
        InputStream is = new ByteArrayInputStream("malicious".getBytes());

        assertThrows(SecurityException.class, () -> {
            storageProvider.saveFile(StorageCategory.USERS, "../../../etc/passwd", is);
        });
    }

    @Test
    void testListFilesAndStorageUsage() throws IOException {
        storageProvider.saveFile(StorageCategory.USERS, "2/doc1.txt", new ByteArrayInputStream("hello".getBytes()));
        storageProvider.saveFile(StorageCategory.USERS, "2/doc2.txt", new ByteArrayInputStream("world!".getBytes()));

        List<String> files = storageProvider.listFiles(StorageCategory.USERS, "2");
        assertEquals(2, files.size());

        long usage = storageProvider.getStorageUsageBytes(StorageCategory.USERS, "2");
        assertEquals(11L, usage);
    }
}
