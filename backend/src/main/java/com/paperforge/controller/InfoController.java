package com.paperforge.controller;

import com.paperforge.dto.InfoResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/info")
public class InfoController {

    @GetMapping
    public ResponseEntity<InfoResponse> getInfo() {
        InfoResponse info = new InfoResponse(
                "PaperForge",
                "Forge your documents.",
                "0.0.1-SNAPSHOT",
                "development"
        );
        return ResponseEntity.ok(info);
    }
}
