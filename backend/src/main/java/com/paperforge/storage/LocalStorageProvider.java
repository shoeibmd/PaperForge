package com.paperforge.storage;

import com.paperforge.security.FilenameSanitizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Component
@org.springframework.context.annotation.Primary
public class LocalStorageProvider implements StorageProvider {

    private final Path rootStoragePath;

    public LocalStorageProvider(@Value("${paperforge.storage.local-path:/tmp/paperforge-storage}") String basePath) {
        this.rootStoragePath = Paths.get(basePath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.rootStoragePath);
            for (StorageCategory cat : StorageCategory.values()) {
                Files.createDirectories(this.rootStoragePath.resolve(cat.getFolderName()));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize PaperForge local storage directory", e);
        }
    }

    private Path resolveSanitizedPath(StorageCategory category, String relativePath) {
        Path categoryDir = rootStoragePath.resolve(category.getFolderName()).normalize();
        if (relativePath == null || relativePath.isBlank()) {
            return categoryDir;
        }

        String[] parts = relativePath.split("[/\\\\]");
        Path current = categoryDir;
        for (String part : parts) {
            if (part == null || part.isBlank() || ".".equals(part)) {
                continue;
            }
            if ("..".equals(part)) {
                throw new SecurityException("Path traversal sequence '..' detected in LocalStorageProvider: " + relativePath);
            }
            String sanitizedPart = FilenameSanitizer.sanitizeFilename(part);
            current = current.resolve(sanitizedPart);
        }

        Path targetPath = current.normalize();
        if (!targetPath.startsWith(categoryDir)) {
            throw new SecurityException("Path traversal attempt blocked in LocalStorageProvider: " + relativePath);
        }
        return targetPath;
    }

    @Override
    public void saveFile(StorageCategory category, String relativePath, InputStream inputStream) throws IOException {
        Path target = resolveSanitizedPath(category, relativePath);
        Files.createDirectories(target.getParent());
        Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public InputStream getFile(StorageCategory category, String relativePath) throws IOException {
        Path target = resolveSanitizedPath(category, relativePath);
        if (!Files.exists(target)) {
            throw new FileNotFoundException("File not found in storage: " + relativePath);
        }
        return Files.newInputStream(target);
    }

    @Override
    public boolean deleteFile(StorageCategory category, String relativePath) throws IOException {
        Path target = resolveSanitizedPath(category, relativePath);
        return Files.deleteIfExists(target);
    }

    @Override
    public boolean exists(StorageCategory category, String relativePath) throws IOException {
        Path target = resolveSanitizedPath(category, relativePath);
        return Files.exists(target);
    }

    @Override
    public List<String> listFiles(StorageCategory category, String relativeDirectoryPath) throws IOException {
        Path dir = resolveSanitizedPath(category, relativeDirectoryPath);
        if (!Files.exists(dir) || !Files.isDirectory(dir)) {
            return List.of();
        }
        List<String> fileNames = new ArrayList<>();
        try (Stream<Path> stream = Files.list(dir)) {
            stream.filter(Files::isRegularFile).forEach(p -> fileNames.add(p.getFileName().toString()));
        }
        return fileNames;
    }

    @Override
    public long getStorageUsageBytes(StorageCategory category, String relativeDirectoryPath) throws IOException {
        Path dir = resolveSanitizedPath(category, relativeDirectoryPath);
        if (!Files.exists(dir)) {
            return 0L;
        }
        try (Stream<Path> stream = Files.walk(dir)) {
            return stream.filter(Files::isRegularFile).mapToLong(p -> {
                try {
                    return Files.size(p);
                } catch (IOException e) {
                    return 0L;
                }
            }).sum();
        }
    }
}
