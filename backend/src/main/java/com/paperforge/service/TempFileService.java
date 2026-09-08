package com.paperforge.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class TempFileService {

    private final Path tempDir;

    public TempFileService() throws IOException {
        this.tempDir = Paths.get(System.getProperty("java.io.tmpdir"), "paperforge-temp");
        if (!Files.exists(tempDir)) {
            Files.createDirectories(tempDir);
        }
    }

    public File createTempFile(String prefix, String suffix) throws IOException {
        String fileName = prefix + "_" + UUID.randomUUID() + (suffix.startsWith(".") ? suffix : "." + suffix);
        Path path = tempDir.resolve(fileName);
        return Files.createFile(path).toFile();
    }

    public void deleteTempFile(File file) {
        if (file != null && file.exists()) {
            try {
                Files.deleteIfExists(file.toPath());
            } catch (IOException ignored) {
                file.deleteOnExit();
            }
        }
    }

    public Path getTempDir() {
        return tempDir;
    }
}
