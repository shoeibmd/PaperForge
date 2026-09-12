package com.paperforge.service;

import com.paperforge.model.User;
import com.paperforge.repository.UserRepository;
import com.paperforge.storage.LocalStorageProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StorageServiceTest {

    private LocalStorageProvider localStorageProvider;
    private UserRepository userRepository;
    private TempFileService tempFileService;
    private StorageService storageService;

    @BeforeEach
    void setUp() throws IOException {
        localStorageProvider = mock(LocalStorageProvider.class);
        userRepository = mock(UserRepository.class);
        tempFileService = mock(TempFileService.class);

        storageService = new StorageService("LOCAL", localStorageProvider, null, tempFileService, userRepository);
    }

    @Test
    void testSaveUserFileQuotaExceeded() throws IOException {
        User user = new User("alice", "alice@example.com", "pass");
        user.setId(1L);
        user.setStorageQuotaBytes(100L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(localStorageProvider.getStorageUsageBytes(any(), anyString())).thenReturn(80L);

        byte[] largeData = new byte[30]; // 80 + 30 = 110 > 100 quota

        assertThrows(IllegalArgumentException.class, () -> {
            storageService.saveUserFile(1L, "overflow.txt", largeData);
        });

        verify(localStorageProvider, never()).saveFile(any(), anyString(), any());
    }

    @Test
    void testSaveUserFileSuccessWithinQuota() throws IOException {
        User user = new User("alice", "alice@example.com", "pass");
        user.setId(1L);
        user.setStorageQuotaBytes(1000L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(localStorageProvider.getStorageUsageBytes(any(), anyString())).thenReturn(100L);

        byte[] validData = "hello world".getBytes();

        assertDoesNotThrow(() -> {
            storageService.saveUserFile(1L, "hello.txt", validData);
        });

        verify(localStorageProvider, times(1)).saveFile(any(), anyString(), any());
    }
}
