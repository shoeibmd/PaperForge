package com.paperforge.controller;

import com.paperforge.dto.AuthRequestDto;
import com.paperforge.dto.AuthResponseDto;
import com.paperforge.dto.RegisterRequestDto;
import com.paperforge.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@RequestBody RegisterRequestDto request, HttpServletRequest httpRequest) {
        String clientIp = httpRequest.getRemoteAddr();
        return ResponseEntity.ok(authService.register(request, clientIp));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthRequestDto request, HttpServletRequest httpRequest) {
        String clientIp = httpRequest.getRemoteAddr();
        return ResponseEntity.ok(authService.login(request, clientIp));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(401).body("Unauthenticated");
        }
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        return ResponseEntity.ok(userDetails);
    }
}
