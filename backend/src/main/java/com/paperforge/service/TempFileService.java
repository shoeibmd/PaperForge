package com.paperforge.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
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
            secureZeroFile(file);
            try {
                Files.deleteIfExists(file.toPath());
            } catch (IOException ignored) {
                file.deleteOnExit();
            }
        }
    }

    private void secureZeroFile(File file) {
        try {
            long length = file.length();
            if (length > 0 && file.canWrite()) {
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    byte[] zeros = new byte[(int) Math.min(length, 8192)];
                    long remaining = length;
                    while (remaining > 0) {
                        int toWrite = (int) Math.min(zeros.length, remaining);
                        fos.write(zeros, 0, toWrite);
                        remaining -= toWrite;
                    }
                    fos.flush();
                }
            }
        } catch (Exception ignored) {
            // Ignore write exceptions during zeroing prior to unlink
        }
    }

    public Path getTempDir() {
        return tempDir;
    }
}
