package com.paperforge.controller;

import com.paperforge.dto.RegisterRequestDto;
import com.paperforge.dto.UserManagementDto;
import com.paperforge.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserManagementDto>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PostMapping("/users")
    public ResponseEntity<UserManagementDto> createUser(@RequestBody RegisterRequestDto request, HttpServletRequest httpRequest) {
        String clientIp = httpRequest.getRemoteAddr();
        return ResponseEntity.ok(adminService.createUser(request, clientIp));
    }

    @PutMapping("/users/{id}/enable")
    public ResponseEntity<UserManagementDto> enableUser(@PathVariable Long id, Authentication auth, HttpServletRequest httpRequest) {
        String clientIp = httpRequest.getRemoteAddr();
        return ResponseEntity.ok(adminService.setUserEnabled(id, true, auth.getName(), clientIp));
    }

    @PutMapping("/users/{id}/disable")
    public ResponseEntity<UserManagementDto> disableUser(@PathVariable Long id, Authentication auth, HttpServletRequest httpRequest) {
        String clientIp = httpRequest.getRemoteAddr();
        return ResponseEntity.ok(adminService.setUserEnabled(id, false, auth.getName(), clientIp));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, Authentication auth, HttpServletRequest httpRequest) {
        String clientIp = httpRequest.getRemoteAddr();
        adminService.deleteUser(id, auth.getName(), clientIp);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users/{id}/reset-password")
    public ResponseEntity<Void> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> body, HttpServletRequest httpRequest) {
        String newPassword = body.get("newPassword");
        if (newPassword == null || newPassword.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        String clientIp = httpRequest.getRemoteAddr();
        adminService.resetUserPassword(id, newPassword, clientIp);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/{id}/toggle-role")
    public ResponseEntity<UserManagementDto> toggleRole(@PathVariable Long id, Authentication auth, HttpServletRequest httpRequest) {
        String clientIp = httpRequest.getRemoteAddr();
        return ResponseEntity.ok(adminService.toggleAdminRole(id, auth.getName(), clientIp));
    }

    @PostMapping("/users/{id}/force-logout")
    public ResponseEntity<Void> forceLogout(@PathVariable Long id, HttpServletRequest httpRequest) {
        String clientIp = httpRequest.getRemoteAddr();
        adminService.forceLogoutUser(id, clientIp);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/{id}/quota")
    public ResponseEntity<UserManagementDto> setQuota(@PathVariable Long id, @RequestBody Map<String, Long> body, HttpServletRequest httpRequest) {
        Long quotaBytes = body.get("quotaBytes");
        if (quotaBytes == null || quotaBytes < 0) {
            return ResponseEntity.badRequest().build();
        }
        String clientIp = httpRequest.getRemoteAddr();
        return ResponseEntity.ok(adminService.setStorageQuota(id, quotaBytes, clientIp));
    }
}
