package com.paperforge.service;

import com.paperforge.dto.AuthRequestDto;
import com.paperforge.dto.AuthResponseDto;
import com.paperforge.dto.RegisterRequestDto;
import com.paperforge.model.Role;
import com.paperforge.model.User;
import com.paperforge.repository.RoleRepository;
import com.paperforge.repository.UserRepository;
import com.paperforge.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    private JwtTokenProvider jwtTokenProvider;
    private AuthenticationManager authenticationManager;
    private AuditLogService auditLogService;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        roleRepository = mock(RoleRepository.class);
        passwordEncoder = mock(org.springframework.security.crypto.password.PasswordEncoder.class);
        jwtTokenProvider = mock(JwtTokenProvider.class);
        authenticationManager = mock(AuthenticationManager.class);
        auditLogService = mock(AuditLogService.class);
        com.paperforge.config.ResourceLimitsConfig resourceLimitsConfig = mock(com.paperforge.config.ResourceLimitsConfig.class);
        when(resourceLimitsConfig.getDefaultUserQuotaBytes()).thenReturn(524288000L);

        authService = new AuthService(userRepository, roleRepository, passwordEncoder, jwtTokenProvider, authenticationManager, auditLogService, resourceLimitsConfig);
    }

    @Test
    void testRegisterUserSuccess() {
        RegisterRequestDto req = new RegisterRequestDto("john", "john@example.com", "pass123");

        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(new Role("ROLE_USER")));
        when(passwordEncoder.encode("pass123")).thenReturn("hashedPass");
        when(jwtTokenProvider.generateToken(any())).thenReturn("mockJwtToken");

        User savedUser = new User("john", "john@example.com", "hashedPass");
        savedUser.setRoles(Set.of(new Role("ROLE_USER")));
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(savedUser));

        AuthResponseDto resp = authService.register(req, "127.0.0.1");

        assertNotNull(resp);
        assertEquals("john", resp.getUsername());
        assertEquals("mockJwtToken", resp.getToken());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testLoginSuccess() {
        AuthRequestDto req = new AuthRequestDto("john", "pass123");

        org.springframework.security.core.Authentication auth = mock(org.springframework.security.core.Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(jwtTokenProvider.generateToken(userDetails)).thenReturn("mockJwtToken");

        User user = new User("john", "john@example.com", "hashedPass");
        user.setRoles(Set.of(new Role("ROLE_USER")));
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        AuthResponseDto resp = authService.login(req, "127.0.0.1");

        assertNotNull(resp);
        assertEquals("john", resp.getUsername());
        assertEquals("mockJwtToken", resp.getToken());
    }
}
