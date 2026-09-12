package com.paperforge.service;

import com.paperforge.dto.UserManagementDto;
import com.paperforge.model.Role;
import com.paperforge.model.User;
import com.paperforge.repository.RoleRepository;
import com.paperforge.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AdminServiceTest {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private AuditLogService auditLogService;
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        roleRepository = mock(RoleRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        auditLogService = mock(AuditLogService.class);

        adminService = new AdminService(userRepository, roleRepository, passwordEncoder, auditLogService);
    }

    @Test
    void testPreventSelfDeletionOrRootAdminDeletion() {
        User rootAdmin = new User("admin", "admin@paperforge.local", "hashed");
        rootAdmin.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(rootAdmin));

        assertThrows(IllegalArgumentException.class, () -> {
            adminService.deleteUser(1L, "admin", "127.0.0.1");
        });

        verify(userRepository, never()).delete(any());
    }

    @Test
    void testSetUserEnabledSuccess() {
        User targetUser = new User("bob", "bob@example.com", "hashed");
        targetUser.setId(2L);
        targetUser.setEnabled(true);

        when(userRepository.findById(2L)).thenReturn(Optional.of(targetUser));

        UserManagementDto dto = adminService.setUserEnabled(2L, false, "admin", "127.0.0.1");

        assertFalse(dto.isEnabled());
        verify(userRepository, times(1)).save(targetUser);
    }
}
