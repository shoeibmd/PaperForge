package com.paperforge.service;

import com.paperforge.config.ResourceLimitsConfig;
import com.paperforge.dto.RegisterRequestDto;
import com.paperforge.dto.UserManagementDto;
import com.paperforge.model.Role;
import com.paperforge.model.User;
import com.paperforge.repository.RoleRepository;
import com.paperforge.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;
    private final ResourceLimitsConfig resourceLimitsConfig;

    @Value("${PAPERFORGE_ADMIN_USERNAME:admin}")
    private String defaultAdminUsername;

    public AdminService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder,
                        AuditLogService auditLogService, ResourceLimitsConfig resourceLimitsConfig) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService = auditLogService;
        this.resourceLimitsConfig = resourceLimitsConfig;
    }

    public List<UserManagementDto> getAllUsers() {
        return userRepository.findAll().stream().map(this::convertToDto).toList();
    }

    @Transactional
    public UserManagementDto createUser(RegisterRequestDto request, String clientIp) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User(request.getUsername(), request.getEmail(), passwordEncoder.encode(request.getPassword()));
        user.setStorageQuotaBytes(resourceLimitsConfig.getDefaultUserQuotaBytes());

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        userRepository.save(user);

        auditLogService.logSecurityEvent("ADMIN_CREATE_USER", clientIp, "createdUsername=" + user.getUsername());
        return convertToDto(user);
    }

    @Transactional
    public UserManagementDto setUserEnabled(Long userId, boolean enabled, String performingAdminUsername, String clientIp) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getUsername().equalsIgnoreCase(defaultAdminUsername) || user.getUsername().equalsIgnoreCase(performingAdminUsername)) {
            throw new IllegalArgumentException("Cannot disable the root admin account or your own account");
        }

        user.setEnabled(enabled);
        userRepository.save(user);

        auditLogService.logSecurityEvent("ADMIN_TOGGLE_USER_STATUS", clientIp, "targetUsername=" + user.getUsername() + ", enabled=" + enabled);
        return convertToDto(user);
    }

    @Transactional
    public void deleteUser(Long userId, String performingAdminUsername, String clientIp) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getUsername().equalsIgnoreCase(defaultAdminUsername) || user.getUsername().equalsIgnoreCase(performingAdminUsername)) {
            throw new IllegalArgumentException("Cannot delete the root admin account or your own account");
        }

        userRepository.delete(user);
        auditLogService.logSecurityEvent("ADMIN_DELETE_USER", clientIp, "deletedUsername=" + user.getUsername());
    }

    @Transactional
    public void resetUserPassword(Long userId, String newPassword, String clientIp) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setForceLoggedOutAt(LocalDateTime.now());
        userRepository.save(user);

        auditLogService.logSecurityEvent("ADMIN_RESET_PASSWORD", clientIp, "targetUsername=" + user.getUsername());
    }

    @Transactional
    public UserManagementDto toggleAdminRole(Long userId, String performingAdminUsername, String clientIp) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getUsername().equalsIgnoreCase(defaultAdminUsername) || user.getUsername().equalsIgnoreCase(performingAdminUsername)) {
            throw new IllegalArgumentException("Cannot change roles for the root admin account or your own account");
        }

        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));

        boolean isAdmin = user.getRoles().stream().anyMatch(r -> "ROLE_ADMIN".equals(r.getName()));

        if (isAdmin) {
            user.getRoles().removeIf(r -> "ROLE_ADMIN".equals(r.getName()));
        } else {
            user.getRoles().add(adminRole);
        }

        userRepository.save(user);
        auditLogService.logSecurityEvent("ADMIN_TOGGLE_ROLE", clientIp, "targetUsername=" + user.getUsername() + ", nowIsAdmin=" + !isAdmin);
        return convertToDto(user);
    }

    @Transactional
    public void forceLogoutUser(Long userId, String clientIp) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setForceLoggedOutAt(LocalDateTime.now());
        userRepository.save(user);

        auditLogService.logSecurityEvent("ADMIN_FORCE_LOGOUT", clientIp, "targetUsername=" + user.getUsername());
    }

    @Transactional
    public UserManagementDto setStorageQuota(Long userId, Long quotaBytes, String clientIp) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setStorageQuotaBytes(quotaBytes);
        userRepository.save(user);

        auditLogService.logSecurityEvent("ADMIN_SET_STORAGE_QUOTA", clientIp, "targetUsername=" + user.getUsername() + ", quotaBytes=" + quotaBytes);
        return convertToDto(user);
    }

    private UserManagementDto convertToDto(User user) {
        Set<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
        return new UserManagementDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.isEnabled(),
                user.getStorageQuotaBytes(),
                user.getCreatedAt(),
                roles
        );
    }
}
