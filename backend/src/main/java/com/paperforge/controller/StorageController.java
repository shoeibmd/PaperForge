package com.paperforge.controller;

import com.paperforge.model.User;
import com.paperforge.repository.UserRepository;
import com.paperforge.service.StorageService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/storage")
public class StorageController {

    private final StorageService storageService;
    private final UserRepository userRepository;

    public StorageController(StorageService storageService, UserRepository userRepository) {
        this.storageService = storageService;
        this.userRepository = userRepository;
    }

    private Long getUserIdFromAuth(Authentication auth) {
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Authenticated user not found: " + username));
        return user.getId();
    }

    @GetMapping("/usage")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getUserStorageUsage(Authentication auth) throws IOException {
        Long userId = getUserIdFromAuth(auth);
        long usageBytes = storageService.getUserStorageUsage(userId);
        return ResponseEntity.ok(Map.of(
                "userId", userId,
                "usageBytes", usageBytes,
                "usageFormatted", (usageBytes / (1024 * 1024)) + " MB"
        ));
    }

    @GetMapping("/download/{filename}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> downloadUserFile(@PathVariable String filename, Authentication auth) throws IOException {
        Long userId = getUserIdFromAuth(auth);
        byte[] fileBytes = storageService.getUserFile(userId, filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileBytes);
    }
}
