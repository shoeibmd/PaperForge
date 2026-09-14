package com.paperforge.controller;

import com.paperforge.dto.JobResponseDto;
import com.paperforge.model.User;
import com.paperforge.repository.UserRepository;
import com.paperforge.service.JobService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/jobs")
@PreAuthorize("isAuthenticated()")
public class JobController {

    private final JobService jobService;
    private final UserRepository userRepository;

    public JobController(JobService jobService, UserRepository userRepository) {
        this.jobService = jobService;
        this.userRepository = userRepository;
    }

    private Long getUserIdFromAuth(Authentication auth) {
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Authenticated user not found: " + username));
        return user.getId();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<JobResponseDto> submitJob(
            @RequestParam("jobType") String jobType,
            @RequestPart("file") MultipartFile file,
            Authentication auth,
            HttpServletRequest httpRequest) throws IOException {

        Long userId = getUserIdFromAuth(auth);
        String clientIp = httpRequest.getRemoteAddr();
        return ResponseEntity.ok(jobService.submitJob(userId, jobType, file, clientIp));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobResponseDto> getJobStatus(@PathVariable String id, Authentication auth) {
        Long userId = getUserIdFromAuth(auth);
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        return ResponseEntity.ok(jobService.getJobStatus(id, userId, isAdmin));
    }

    @GetMapping
    public ResponseEntity<List<JobResponseDto>> getUserJobs(Authentication auth) {
        Long userId = getUserIdFromAuth(auth);
        return ResponseEntity.ok(jobService.getUserJobs(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelJob(@PathVariable String id, Authentication auth, HttpServletRequest httpRequest) {
        Long userId = getUserIdFromAuth(auth);
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        String clientIp = httpRequest.getRemoteAddr();
        jobService.cancelJob(id, userId, isAdmin, clientIp);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadJobOutput(@PathVariable String id, Authentication auth, HttpServletRequest httpRequest) throws IOException {
        Long userId = getUserIdFromAuth(auth);
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        String clientIp = httpRequest.getRemoteAddr();

        JobResponseDto job = jobService.getJobStatus(id, userId, isAdmin);
        byte[] fileBytes = jobService.downloadJobOutput(id, userId, isAdmin, clientIp);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + job.getOutputFilename() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(fileBytes);
    }
}
