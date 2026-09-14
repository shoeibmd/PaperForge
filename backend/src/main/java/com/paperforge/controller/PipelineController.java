package com.paperforge.controller;

import com.paperforge.dto.JobResponseDto;
import com.paperforge.model.Pipeline;
import com.paperforge.model.PipelineStep;
import com.paperforge.model.User;
import com.paperforge.repository.UserRepository;
import com.paperforge.registry.ToolMetadata;
import com.paperforge.registry.ToolRegistry;
import com.paperforge.service.PipelineService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/pipelines")
@PreAuthorize("isAuthenticated()")
public class PipelineController {

    private final PipelineService pipelineService;
    private final ToolRegistry toolRegistry;
    private final UserRepository userRepository;

    public PipelineController(PipelineService pipelineService, ToolRegistry toolRegistry, UserRepository userRepository) {
        this.pipelineService = pipelineService;
        this.toolRegistry = toolRegistry;
        this.userRepository = userRepository;
    }

    private Long getUserIdFromAuth(Authentication auth) {
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Authenticated user not found: " + username));
        return user.getId();
    }

    @GetMapping("/tools")
    public ResponseEntity<List<ToolMetadata>> getAvailableTools() {
        return ResponseEntity.ok(toolRegistry.getAllTools());
    }

    @GetMapping
    public ResponseEntity<List<Pipeline>> getUserPipelines(Authentication auth) {
        Long userId = getUserIdFromAuth(auth);
        return ResponseEntity.ok(pipelineService.getUserPipelines(userId));
    }

    @PostMapping
    public ResponseEntity<Pipeline> createPipeline(@RequestBody Map<String, Object> body, Authentication auth, HttpServletRequest httpRequest) {
        Long userId = getUserIdFromAuth(auth);
        String name = (String) body.get("name");
        String description = (String) body.get("description");

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> stepsRaw = (List<Map<String, Object>>) body.get("steps");

        List<PipelineStep> steps = stepsRaw.stream().map(s -> {
            String toolId = (String) s.get("toolId");
            String params = (String) s.get("stepParamsJson");
            return new PipelineStep(0, toolId, params);
        }).toList();

        String clientIp = httpRequest.getRemoteAddr();
        return ResponseEntity.ok(pipelineService.createPipeline(userId, name, description, steps, clientIp));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePipeline(@PathVariable Long id, Authentication auth, HttpServletRequest httpRequest) {
        Long userId = getUserIdFromAuth(auth);
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        String clientIp = httpRequest.getRemoteAddr();
        pipelineService.deletePipeline(id, userId, isAdmin, clientIp);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/execute", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<JobResponseDto> executePipeline(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file,
            Authentication auth,
            HttpServletRequest httpRequest) throws IOException {

        Long userId = getUserIdFromAuth(auth);
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        String clientIp = httpRequest.getRemoteAddr();

        return ResponseEntity.ok(pipelineService.executePipeline(id, file, userId, isAdmin, clientIp));
    }
}
