package com.paperforge.service;

import com.paperforge.model.User;
import com.paperforge.repository.UserRepository;
import com.paperforge.security.FilenameSanitizer;
import com.paperforge.storage.LocalStorageProvider;
import com.paperforge.storage.S3StorageProvider;
import com.paperforge.storage.StorageCategory;
import com.paperforge.storage.StorageProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Service
public class StorageService {

    private final StorageProvider storageProvider;
    private final TempFileService tempFileService;
    private final UserRepository userRepository;

    public StorageService(
            @Value("${paperforge.storage.type:LOCAL}") String storageType,
            LocalStorageProvider localStorageProvider,
            S3StorageProvider s3StorageProvider,
            TempFileService tempFileService,
            UserRepository userRepository) {

        if ("S3".equalsIgnoreCase(storageType)) {
            this.storageProvider = s3StorageProvider;
        } else {
            this.storageProvider = localStorageProvider;
        }
        this.tempFileService = tempFileService;
        this.userRepository = userRepository;
    }

    public void saveUserFile(Long userId, String filename, byte[] fileData) throws IOException {
        String sanitizedFilename = FilenameSanitizer.sanitizeFilename(filename);

        if (userId != null) {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                long currentUsage = storageProvider.getStorageUsageBytes(StorageCategory.USERS, String.valueOf(userId));
                long quota = user.getStorageQuotaBytes() != null ? user.getStorageQuotaBytes() : 524288000L;

                if (currentUsage + fileData.length > quota) {
                    throw new IllegalArgumentException("Storage quota exceeded. User quota: " + quota + " bytes, current usage: " + currentUsage + " bytes");
                }
            }
        }

        String userPath = userId + "/" + sanitizedFilename;
        try (InputStream is = new ByteArrayInputStream(fileData)) {
            storageProvider.saveFile(StorageCategory.USERS, userPath, is);
        }
    }

    public byte[] getUserFile(Long userId, String filename) throws IOException {
        String sanitizedFilename = FilenameSanitizer.sanitizeFilename(filename);
        String userPath = userId + "/" + sanitizedFilename;
        try (InputStream is = storageProvider.getFile(StorageCategory.USERS, userPath)) {
            return is.readAllBytes();
        }
    }

    public boolean deleteUserFile(Long userId, String filename) throws IOException {
        String sanitizedFilename = FilenameSanitizer.sanitizeFilename(filename);
        String userPath = userId + "/" + sanitizedFilename;
        return storageProvider.deleteFile(StorageCategory.USERS, userPath);
    }

    public long getUserStorageUsage(Long userId) throws IOException {
        return storageProvider.getStorageUsageBytes(StorageCategory.USERS, String.valueOf(userId));
    }

    public File createManagedTempFile(String prefix, String suffix) throws IOException {
        return tempFileService.createTempFile(prefix, suffix);
    }

    public void deleteManagedTempFile(File file) {
        tempFileService.deleteTempFile(file);
    }
}
